package com.fridgemate.api.dto.recipe;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "레시피 재료")
public class RecipeIngredient {

    @Schema(description = "재료명", example = "김치")
    private String name;

    @Schema(description = "필요량", example = "200")
    private String amount;

    @Schema(description = "단위", example = "g")
    private String unit;

    @Schema(description = "보유 여부", example = "true")
    private boolean available;
}