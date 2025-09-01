package com.fridgemate.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "재고 소비 요청")
public class ConsumeReq {

    @NotNull(message = "소비량은 필수입니다")
    @Positive(message = "소비량은 0보다 커야 합니다")
    @Schema(description = "소비할 수량", example = "2")
    private BigDecimal amount;
}
