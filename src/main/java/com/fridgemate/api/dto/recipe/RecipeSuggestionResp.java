package com.fridgemate.api.dto.recipe;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "레시피 추천 응답")
public class RecipeSuggestionResp {

    @Schema(description = "사용 가능한 재료 수", example = "8")
    private int availableIngredients;

    @Schema(description = "추천 레시피 목록")
    private List<Recipe> suggestedRecipes;
}