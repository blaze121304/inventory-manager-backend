package com.fridgemate.api.service.recipe;

import com.fridgemate.api.domain.Item;
import com.fridgemate.api.dto.recipe.Recipe;
import com.fridgemate.api.dto.recipe.RecipeSuggestionReq;

import java.util.List;

/**
 * 레시피 추천 인터페이스
 */
public interface Recommender {
    
    /**
     * 현재 보유 재료를 기반으로 레시피를 추천한다
     * 
     * @param availableItems 보유 재료 목록
     * @param request 추천 요청 조건
     * @return 추천 레시피 목록 (점수 순으로 정렬)
     */
    List<Recipe> recommend(List<Item> availableItems, RecipeSuggestionReq request);
}
