package com.fridgemate.api.dto;

import com.fridgemate.api.domain.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Schema(description = "재고 아이템 응답")
public class ItemResp {

    @Schema(description = "아이템 ID", example = "1")
    private Long id;

    @Schema(description = "상품명", example = "계란")
    private String name;

    @Schema(description = "수량", example = "10")
    private BigDecimal quantity;

    @Schema(description = "단위", example = "개")
    private String unit;

    @Schema(description = "유통기한", example = "2024-12-31")
    private LocalDate expiryDate;

    @Schema(description = "카테고리", example = "유제품")
    private String category;

    @Schema(description = "보관 위치", example = "냉장")
    private String location;

    @Schema(description = "메모", example = "신선한 계란")
    private String memo;

    @Schema(description = "생성 시간")
    private Instant createdAt;

    @Schema(description = "수정 시간")
    private Instant updatedAt;

    @Schema(description = "유통기한까지 남은 일수")
    private Integer daysUntilExpiry;

    public static ItemResp from(Item item) {
        ItemResp resp = new ItemResp();
        resp.id = item.getId();
        resp.name = item.getName();
        resp.quantity = item.getQuantity();
        resp.unit = item.getUnit();
        resp.expiryDate = item.getExpiryDate();
        resp.category = item.getCategory();
        resp.location = item.getLocation();
        resp.memo = item.getMemo();
        resp.createdAt = item.getCreatedAt();
        resp.updatedAt = item.getUpdatedAt();
        resp.daysUntilExpiry = item.getDaysUntilExpiry();
        return resp;
    }
}
