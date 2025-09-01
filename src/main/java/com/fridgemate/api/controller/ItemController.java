package com.fridgemate.api.controller;

import com.fridgemate.api.dto.*;
import com.fridgemate.api.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "아이템 관리", description = "냉장고 아이템 관리 API")
public class ItemController {

    private final ItemService itemService;

    @Operation(
            summary = "아이템 목록 조회",
            description = "카테고리, 위치, 정렬 조건으로 아이템 목록을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "아이템 목록 조회 성공")
    @GetMapping
    public ResponseEntity<Page<ItemResp>> getItems(
            @Parameter(description = "카테고리")
            @RequestParam(required = false) String category,

            @Parameter(description = "보관 위치")
            @RequestParam(required = false) String location,

            @Parameter(description = "정렬 기준 (name, category, quantity, expiry, location, created, updated)")
            @RequestParam(required = false, defaultValue = "created") String sortBy,

            @Parameter(description = "정렬 방향 (asc, desc)")
            @RequestParam(required = false, defaultValue = "desc") String sortDir,

            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "페이지 크기")
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ItemResp> items = itemService.getItems(category, location, sortBy, sortDir, pageable);
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "아이템 상세 조회")
    @ApiResponse(responseCode = "200", description = "아이템 상세 조회 성공")
    @ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음")
    @GetMapping("/{id}")
    public ResponseEntity<ItemResp> getItem(
            @PathVariable Long id
    ) {
        ItemResp item = itemService.getItem(id);
        return ResponseEntity.ok(item);
    }

    @Operation(summary = "아이템 생성")
    @ApiResponse(responseCode = "201", description = "아이템 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @PostMapping
    public ResponseEntity<ItemResp> createItem(
            @Valid @RequestBody ItemCreateReq request
    ) {
        ItemResp createdItem = itemService.createItem(request);
        return ResponseEntity.ok(createdItem);
    }

    @Operation(summary = "아이템 수정")
    @ApiResponse(responseCode = "200", description = "아이템 수정 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음")
    @PutMapping("/{id}")
    public ResponseEntity<ItemResp> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemUpdateReq request
    ) {
        ItemResp updatedItem = itemService.updateItem(id, request);
        return ResponseEntity.ok(updatedItem);
    }

    @Operation(summary = "아이템 삭제")
    @ApiResponse(responseCode = "204", description = "아이템 삭제 성공")
    @ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "아이템 소비")
    @ApiResponse(responseCode = "200", description = "아이템 소비 성공")
    @ApiResponse(responseCode = "400", description = "재고 부족 또는 잘못된 요청")
    @ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음")
    @PostMapping("/{id}/consume")
    public ResponseEntity<ItemResp> consumeItem(
            @PathVariable Long id,
            @Valid @RequestBody ConsumeReq request
    ) {
        ItemResp updatedItem = itemService.consumeItem(id, request);
        return ResponseEntity.ok(updatedItem);
    }

    @Operation(summary = "카테고리 목록 조회")
    @ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공")
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = itemService.getCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "보관 위치 목록 조회")
    @ApiResponse(responseCode = "200", description = "보관 위치 목록 조회 성공")
    @GetMapping("/locations")
    public ResponseEntity<List<String>> getLocations() {
        List<String> locations = itemService.getLocations();
        return ResponseEntity.ok(locations);
    }

    @Operation(summary = "곧 만료될 아이템 조회")
    @ApiResponse(responseCode = "200", description = "곧 만료될 아이템 목록 조회 성공")
    @GetMapping("/expiring")
    public ResponseEntity<List<ItemResp>> getExpiringItems(
            @Parameter(description = "몇 일 후까지 조회할지")
            @RequestParam(defaultValue = "7") int days
    ) {
        List<ItemResp> items = itemService.getExpiringItems(days);
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "재고 부족 아이템 조회")
    @ApiResponse(responseCode = "200", description = "재고 부족 아이템 목록 조회 성공")
    @GetMapping("/low-stock")
    public ResponseEntity<List<ItemResp>> getLowStockItems(
            @Parameter(description = "재고 부족 기준 수량")
            @RequestParam(defaultValue = "5") double threshold
    ) {
        List<ItemResp> items = itemService.getLowStockItems(threshold);
        return ResponseEntity.ok(items);
    }
}