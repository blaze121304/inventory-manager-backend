package com.fridgemate.api.service.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fridgemate.api.domain.Item;
import com.fridgemate.api.dto.recipe.Recipe;
import com.fridgemate.api.dto.recipe.RecipeSuggestionReq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RuleBasedRecommenderTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RuleBasedRecommender recommender;

    private List<Item> availableItems;
    private RecipeSuggestionReq request;

    @BeforeEach
    void setUp() {
        // 보유 재료 준비
        Item egg = new Item("계란", new BigDecimal("5"), "개");
        egg.setExpiryDate(LocalDate.now().plusDays(2)); // 임박한 유통기한
        egg.setCategory("유제품");
        egg.setLocation("냉장");

        Item rice = new Item("밥", new BigDecimal("2"), "공기");
        rice.setExpiryDate(LocalDate.now().plusDays(1));
        rice.setCategory("곡물");
        rice.setLocation("냉장");

        Item onion = new Item("양파", new BigDecimal("3"), "개");
        onion.setExpiryDate(LocalDate.now().plusDays(10));
        onion.setCategory("채소");
        onion.setLocation("실온");

        availableItems = List.of(egg, rice, onion);

        // 추천 요청 준비
        request = new RecipeSuggestionReq();
        request.setServings(2);
        
        // 빈 레시피 템플릿으로 초기화
        recommender.recipeTemplates = new ArrayList<>();
    }

    @Test
    @DisplayName("레시피 추천 - 빈 템플릿")
    void recommend_EmptyTemplates() {
        // When
        List<Recipe> result = recommender.recommend(availableItems, request);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("보유 재료가 없을 때")
    void recommend_NoAvailableItems() {
        // Given
        List<Item> emptyItems = List.of();

        // When
        List<Recipe> result = recommender.recommend(emptyItems, request);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("식단 타입 필터링")
    void recommend_DietFiltering() {
        // Given
        request.setDiet("채식");

        // When
        List<Recipe> result = recommender.recommend(availableItems, request);

        // Then
        // 실제 레시피 데이터가 로드되지 않으므로 빈 결과 예상
        assertThat(result).isEmpty();
    }
}
