package com.fridgemate.api.service;

import com.fridgemate.api.dto.ItemResp;
import com.fridgemate.api.dto.recipe.Recipe;
import com.fridgemate.api.dto.recipe.RecipeIngredient;
import com.fridgemate.api.dto.recipe.RecipeSuggestionReq;
import com.fridgemate.api.dto.recipe.RecipeSuggestionResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final ItemService itemService;

    /**
     * 현재 재고 기반 레시피 추천
     */
    public RecipeSuggestionResp suggestRecipes(RecipeSuggestionReq request) {
        // 현재 재고가 있는 아이템들 조회
        List<ItemResp> availableItems = itemService.getAvailableItems();

        log.info("사용 가능한 재료 수: {}", availableItems.size());

        // 간단한 레시피 매칭 로직 (실제로는 외부 API나 ML 모델 사용)
        List<Recipe> suggestedRecipes = generateSimpleRecipes(availableItems, request);

        return RecipeSuggestionResp.builder()
                .availableIngredients(availableItems.size())
                .suggestedRecipes(suggestedRecipes)
                .build();
    }

    /**
     * 간단한 레시피 생성 로직
     * 실제 구현에서는 외부 레시피 API나 ML 모델을 사용
     */
    private List<Recipe> generateSimpleRecipes(List<ItemResp> availableItems, RecipeSuggestionReq request) {
        List<Recipe> recipes = new ArrayList<>();

        // 카테고리별로 재료 분류
        List<ItemResp> vegetables = filterByCategory(availableItems, "야채");
        List<ItemResp> meat = filterByCategory(availableItems, "육류");
        List<ItemResp> dairy = filterByCategory(availableItems, "유제품");
        List<ItemResp> grains = filterByCategory(availableItems, "곡물");

        // 볶음밥 레시피
        if (!grains.isEmpty() && !vegetables.isEmpty()) {
            recipes.add(createFriedRiceRecipe(grains, vegetables, meat));
        }

        // 샐러드 레시피
        if (!vegetables.isEmpty()) {
            recipes.add(createSaladRecipe(vegetables, dairy));
        }

        // 스튜 레시피
        if (!meat.isEmpty() && !vegetables.isEmpty()) {
            recipes.add(createStewRecipe(meat, vegetables));
        }

        // 요청된 개수만큼 반환 (최대 10개)
        //int maxRecipes = Math.min(request.getMaxRecipes() != null ? request.getMaxRecipes() : 5, 10);
        return recipes.stream().limit(10).collect(Collectors.toList());
    }

    private List<ItemResp> filterByCategory(List<ItemResp> items, String category) {
        return items.stream()
                .filter(item -> category.equals(item.getCategory()))
                .collect(Collectors.toList());
    }

    private Recipe createFriedRiceRecipe(List<ItemResp> grains, List<ItemResp> vegetables, List<ItemResp> meat) {
        List<RecipeIngredient> ingredients = new ArrayList<>();

        // 곡물 추가
        if (!grains.isEmpty()) {
            ItemResp grain = grains.get(0);
            ingredients.add(RecipeIngredient.builder()
                    .name(grain.getName())
                    .amount("1")
                    .unit("공기")
                    .available(true)
                    .build());
        }

        // 야채 추가
        vegetables.stream().limit(2).forEach(vegetable -> {
            ingredients.add(RecipeIngredient.builder()
                    .name(vegetable.getName())
                    .amount("100")
                    .unit("g")
                    .available(true)
                    .build());
        });

        // 육류 추가 (있는 경우)
        if (!meat.isEmpty()) {
            ItemResp meatItem = meat.get(0);
            ingredients.add(RecipeIngredient.builder()
                    .name(meatItem.getName())
                    .amount("50")
                    .unit("g")
                    .available(true)
                    .build());
        }

        return Recipe.builder()
                .id(1L)
                .name("간단 볶음밥")
                .description("냉장고 재료로 만드는 간단한 볶음밥")
                .cookingTime(15)
                .difficulty("쉬움")
                .servings(2)
                .ingredients(ingredients)
                .instructions(List.of(
                        "밥을 준비하고 계란을 풀어둡니다.",
                        "팬에 기름을 두르고 계란을 먼저 볶습니다.",
                        "야채를 추가하여 볶습니다.",
                        "밥을 넣고 함께 볶아 완성합니다."
                ))
                .build();
    }

    private Recipe createSaladRecipe(List<ItemResp> vegetables, List<ItemResp> dairy) {
        List<RecipeIngredient> ingredients = new ArrayList<>();

        // 야채 추가
        vegetables.stream().limit(3).forEach(vegetable -> {
            ingredients.add(RecipeIngredient.builder()
                    .name(vegetable.getName())
                    .amount("50")
                    .unit("g")
                    .available(true)
                    .build());
        });

        // 유제품 추가 (있는 경우)
        if (!dairy.isEmpty()) {
            ItemResp dairyItem = dairy.get(0);
            ingredients.add(RecipeIngredient.builder()
                    .name(dairyItem.getName())
                    .amount("2")
                    .unit("큰술")
                    .available(true)
                    .build());
        }

        return Recipe.builder()
                .id(2L)
                .name("신선한 샐러드")
                .description("신선한 야채로 만드는 건강 샐러드")
                .cookingTime(10)
                .difficulty("쉬움")
                .servings(1)
                .ingredients(ingredients)
                .instructions(List.of(
                        "야채를 깨끗이 씻어 준비합니다.",
                        "적당한 크기로 자릅니다.",
                        "드레싱과 함께 버무려 완성합니다."
                ))
                .build();
    }

    private Recipe createStewRecipe(List<ItemResp> meat, List<ItemResp> vegetables) {
        List<RecipeIngredient> ingredients = new ArrayList<>();

        // 육류 추가
        if (!meat.isEmpty()) {
            ItemResp meatItem = meat.get(0);
            ingredients.add(RecipeIngredient.builder()
                    .name(meatItem.getName())
                    .amount("200")
                    .unit("g")
                    .available(true)
                    .build());
        }

        // 야채 추가
        vegetables.stream().limit(3).forEach(vegetable -> {
            ingredients.add(RecipeIngredient.builder()
                    .name(vegetable.getName())
                    .amount("100")
                    .unit("g")
                    .available(true)
                    .build());
        });

        return Recipe.builder()
                .id(3L)
                .name("푸짐한 스튜")
                .description("고기와 야채로 만드는 든든한 스튜")
                .cookingTime(45)
                .difficulty("보통")
                .servings(4)
                .ingredients(ingredients)
                .instructions(List.of(
                        "고기를 적당한 크기로 자릅니다.",
                        "야채도 큼직하게 자릅니다.",
                        "냄비에 고기를 먼저 볶습니다.",
                        "야채를 넣고 물을 부어 끓입니다.",
                        "약불에서 30분간 끓여 완성합니다."
                ))
                .build();
    }
}