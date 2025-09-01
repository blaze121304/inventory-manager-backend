package com.fridgemate.api.service.recipe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fridgemate.api.domain.Item;
import com.fridgemate.api.dto.recipe.Recipe;
import com.fridgemate.api.dto.recipe.RecipeIngredient;
import com.fridgemate.api.dto.recipe.RecipeSuggestionReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RuleBasedRecommender implements Recommender {

    private final ObjectMapper objectMapper;
    private List<RecipeTemplate> recipeTemplates;

    @PostConstruct
    public void loadRecipes() {
        try {
            ClassPathResource resource = new ClassPathResource("recipes.json");
            this.recipeTemplates = objectMapper.readValue(
                resource.getInputStream(), 
                new TypeReference<List<RecipeTemplate>>() {}
            );
            log.info("레시피 템플릿 {} 개 로드됨", recipeTemplates.size());
        } catch (IOException e) {
            log.error("레시피 데이터 로드 실패", e);
            this.recipeTemplates = new ArrayList<>();
        }
    }

    @Override
    public List<Recipe> recommend(List<Item> availableItems, RecipeSuggestionReq request) {
        if (recipeTemplates.isEmpty()) {
            log.warn("레시피 템플릿이 없습니다.");
            return new ArrayList<>();
        }

        // 보유 재료를 맵으로 변환 (이름을 키로)
        Map<String, Item> availableItemMap = availableItems.stream()
                .collect(Collectors.toMap(
                    item -> item.getName().toLowerCase().trim(),
                    item -> item,
                    (existing, replacement) -> existing
                ));

        List<Recipe> recommendations = new ArrayList<>();

        for (RecipeTemplate template : recipeTemplates) {
            // 식단 타입 필터링
            if (StringUtils.hasText(request.getDiet()) && 
                !template.getDiet().equalsIgnoreCase(request.getDiet()) &&
                !"일반".equals(template.getDiet())) {
                continue;
            }

            Recipe recipe = evaluateRecipe(template, availableItemMap, request);
            if (recipe.getScore().compareTo(BigDecimal.ZERO) > 0) {
                recommendations.add(recipe);
            }
        }

        // 점수 순으로 정렬하고 상위 5개만 반환
        return recommendations.stream()
                .sorted((r1, r2) -> r2.getScore().compareTo(r1.getScore()))
                .limit(5)
                .collect(Collectors.toList());
    }

    private Recipe evaluateRecipe(RecipeTemplate template, Map<String, Item> availableItems, RecipeSuggestionReq request) {
        List<RecipeIngredient> usedIngredients = new ArrayList<>();
        List<RecipeIngredient> missingIngredients = new ArrayList<>();
        
        int totalIngredients = template.getIngredients().size();
        int availableIngredientCount = 0;
        BigDecimal freshnessBonus = BigDecimal.ZERO;

        // 각 재료별로 보유 여부 확인
        for (RecipeIngredient ingredient : template.getIngredients()) {
            String ingredientName = ingredient.getName().toLowerCase().trim();
            Item availableItem = availableItems.get(ingredientName);

            if (availableItem != null && availableItem.getQuantity().compareTo(ingredient.getAmount()) >= 0) {
                // 보유 재료
                usedIngredients.add(ingredient);
                availableIngredientCount++;
                
                // 신선도 보너스 계산 (유통기한 임박할수록 가점)
                if (availableItem.getExpiryDate() != null && availableItem.isExpiringSoon(3)) {
                    freshnessBonus = freshnessBonus.add(new BigDecimal("0.1"));
                }
            } else {
                // 부족한 재료
                missingIngredients.add(ingredient);
            }
        }

        // 점수 계산: (보유재료 일치율 * 0.7) + (신선도 가중치 * 0.3)
        BigDecimal matchRatio = BigDecimal.valueOf(availableIngredientCount)
                .divide(BigDecimal.valueOf(totalIngredients), 2, RoundingMode.HALF_UP);
        
        BigDecimal freshnessWeight = freshnessBonus.min(new BigDecimal("0.3")); // 최대 0.3점
        BigDecimal totalScore = matchRatio.multiply(new BigDecimal("0.7"))
                .add(freshnessWeight);

        // 인분 수에 따른 재료량 조정
        if (request.getServings() != null && request.getServings() > 1) {
            BigDecimal servingMultiplier = BigDecimal.valueOf(request.getServings());
            usedIngredients = adjustIngredientAmounts(usedIngredients, servingMultiplier);
            missingIngredients = adjustIngredientAmounts(missingIngredients, servingMultiplier);
        }

        return new Recipe(
            template.getTitle(),
            totalScore,
            usedIngredients,
            missingIngredients,
            template.getSteps(),
            template.getEstimatedTimeMin()
        );
    }

    private List<RecipeIngredient> adjustIngredientAmounts(List<RecipeIngredient> ingredients, BigDecimal multiplier) {
        return ingredients.stream()
                .map(ingredient -> new RecipeIngredient(
                    ingredient.getName(),
                    ingredient.getAmount().multiply(multiplier),
                    ingredient.getUnit()
                ))
                .collect(Collectors.toList());
    }

    // 내부 템플릿 클래스
    private static class RecipeTemplate {
        private String title;
        private List<RecipeIngredient> ingredients;
        private List<String> steps;
        private Integer estimatedTimeMin;
        private String category;
        private String diet;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public List<RecipeIngredient> getIngredients() { return ingredients; }
        public void setIngredients(List<RecipeIngredient> ingredients) { this.ingredients = ingredients; }
        
        public List<String> getSteps() { return steps; }
        public void setSteps(List<String> steps) { this.steps = steps; }
        
        public Integer getEstimatedTimeMin() { return estimatedTimeMin; }
        public void setEstimatedTimeMin(Integer estimatedTimeMin) { this.estimatedTimeMin = estimatedTimeMin; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getDiet() { return diet; }
        public void setDiet(String diet) { this.diet = diet; }
    }
}
