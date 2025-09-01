package com.fridgemate.api.dto.recipe;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "레시피 추천 요청")
public class RecipeSuggestionReq {

    @Schema(description = "인분 수", example = "2")
    private Integer servings;

    @Schema(description = "식단 타입", example = "vegetarian", allowableValues = {"vegetarian", "vegan", "low-carb", "high-protein"})
    private String diet;
}
