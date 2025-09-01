package com.fridgemate.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "재고 아이템 수정 요청")
public class ItemUpdateReq {

    @NotBlank(message = "상품명은 필수입니다")
    @Schema(description = "상품명", example = "계란")
    private String name;

    @NotNull(message = "수량은 필수입니다")
    @PositiveOrZero(message = "수량은 0 이상이어야 합니다")
    @Schema(description = "수량", example = "8")
    private BigDecimal quantity;

    @NotBlank(message = "단위는 필수입니다")
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
}
