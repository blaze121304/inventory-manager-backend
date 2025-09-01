package com.fridgemate.api.dto.recipe;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.util.PartialOrder;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "레시피 정보")
public class Recipe {

    @Schema(description = "레시피 ID", example = "1")
    private Long id;

    @Schema(description = "레시피 이름", example = "김치볶음밥")
    private String name;

    @Schema(description = "레시피 설명", example = "간단하고 맛있는 김치볶음밥")
    private String description;

    @Schema(description = "조리 시간 (분)", example = "15")
    private int cookingTime;

    @Schema(description = "난이도", example = "쉬움")
    private String difficulty;

    @Schema(description = "인분", example = "2")
    private int servings;

    @Schema(description = "필요한 재료 목록")
    private List<RecipeIngredient> ingredients;

    @Schema(description = "조리 과정")
    private List<String> instructions;

    public PartialOrder.PartialComparable getScore() {

        return null;
    }
}