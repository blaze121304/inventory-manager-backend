package com.fridgemate.api.controller;

import com.fridgemate.api.dto.recipe.RecipeSuggestionReq;
import com.fridgemate.api.dto.recipe.RecipeSuggestionResp;
import com.fridgemate.api.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "레시피 추천", description = "AI 기반 레시피 추천 API")
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping("/suggest")
    @Operation(summary = "레시피 추천", description = "현재 보유 재료를 기반으로 레시피를 추천합니다")
    @ApiResponse(responseCode = "200", description = "추천 성공")
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패")
    public ResponseEntity<RecipeSuggestionResp> suggestRecipes(@RequestBody RecipeSuggestionReq request) {
        RecipeSuggestionResp response = recipeService.suggestRecipes(request);
        return ResponseEntity.ok(response);
    }
}
