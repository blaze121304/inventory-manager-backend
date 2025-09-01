package com.fridgemate.api.service.recipe;

import com.fridgemate.api.domain.Item;
import com.fridgemate.api.dto.recipe.Recipe;
import com.fridgemate.api.dto.recipe.RecipeSuggestionReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LLM 기반 레시피 추천기
 * 현재는 기본 구현만 제공하고, 실제 LLM 호출 부분은 TODO로 남겨둠
 */
@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "llm")
@Slf4j
public class LlmRecommender implements Recommender {

    @Value("${ai.llm.api-key:}")
    private String apiKey;
    
    @Value("${ai.llm.base-url:}")
    private String baseUrl;

    @Override
    public List<Recipe> recommend(List<Item> availableItems, RecipeSuggestionReq request) {
        log.info("LLM 레시피 추천 요청 - 보유 재료 수: {}", availableItems.size());
        
        if (apiKey.isEmpty()) {
            log.warn("LLM API 키가 설정되지 않았습니다. 기본 레시피를 반환합니다.");
            return getDefaultRecipes();
        }

        try {
            // TODO: 실제 LLM API 호출 구현
            return callLlmApi(availableItems, request);
        } catch (Exception e) {
            log.error("LLM API 호출 실패, 기본 레시피로 대체", e);
            return getDefaultRecipes();
        }
    }

    private List<Recipe> callLlmApi(List<Item> availableItems, RecipeSuggestionReq request) {
        // TODO: 실제 LLM API 호출 로직 구현
        // 1. 보유 재료 목록을 프롬프트로 구성
        // 2. LLM API 호출 (OpenAI, Claude, etc.)
        // 3. 응답을 Recipe 객체로 파싱
        // 4. 점수 계산 및 정렬
        
        log.info("LLM API 호출 - BaseURL: {}, 재료 수: {}", baseUrl, availableItems.size());
        
        String prompt = buildPrompt(availableItems, request);
        log.debug("LLM 프롬프트: {}", prompt);
        
        // 실제 구현 예시:
        // String response = httpClient.post(baseUrl + "/v1/chat/completions")
        //     .header("Authorization", "Bearer " + apiKey)
        //     .body(buildRequestBody(prompt))
        //     .execute();
        // return parseResponse(response);
        
        // 현재는 모의 응답 반환
        return getDefaultRecipes();
    }

    private String buildPrompt(List<Item> availableItems, RecipeSuggestionReq request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("냉장고에 다음 재료들이 있습니다:\n");
        
        for (Item item : availableItems) {
            prompt.append("- ").append(item.getName())
                  .append(" ").append(item.getQuantity())
                  .append(item.getUnit());
            if (item.getExpiryDate() != null) {
                prompt.append(" (유통기한: ").append(item.getExpiryDate()).append(")");
            }
            prompt.append("\n");
        }
        
        prompt.append("\n이 재료들로 만들 수 있는 레시피 3-5개를 추천해주세요.");
        
        if (request.getServings() != null) {
            prompt.append(" ").append(request.getServings()).append("인분으로 조리법을 알려주세요.");
        }
        
        if (request.getDiet() != null) {
            prompt.append(" ").append(request.getDiet()).append(" 식단에 맞는 레시피로 부탁합니다.");
        }
        
        prompt.append("\n\n응답은 다음 JSON 형식으로 해주세요:\n");
        prompt.append("{\n");
        prompt.append("  \"recipes\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"title\": \"레시피 이름\",\n");
        prompt.append("      \"score\": 0.85,\n");
        prompt.append("      \"used\": [{\"name\":\"재료명\",\"amount\":2,\"unit\":\"개\"}],\n");
        prompt.append("      \"missing\": [{\"name\":\"부족재료\",\"amount\":1,\"unit\":\"개\"}],\n");
        prompt.append("      \"steps\": [\"조리단계1\", \"조리단계2\"],\n");
        prompt.append("      \"estimated_time_min\": 15\n");
        prompt.append("    }\n");
        prompt.append("  ]\n");
        prompt.append("}\n");
        
        return prompt.toString();
    }

    private List<Recipe> getDefaultRecipes() {
        // LLM을 사용할 수 없을 때 반환할 기본 레시피
//        Recipe defaultRecipe = new Recipe();
//        defaultRecipe.setTitle("LLM 추천 레시피 (준비 중)");
//        defaultRecipe.setScore(new BigDecimal("0.5"));
//        defaultRecipe.setUsed(new ArrayList<>());
//        defaultRecipe.setMissing(new ArrayList<>());
//        defaultRecipe.setSteps(Arrays.asList(
//            "LLM 기반 레시피 추천 기능을 준비 중입니다.",
//            "현재는 규칙 기반 추천을 이용해주세요."
//        ));
//        defaultRecipe.setEstimatedTimeMin(0);
//
//        return Arrays.asList(defaultRecipe);
        return null;
    }
}
