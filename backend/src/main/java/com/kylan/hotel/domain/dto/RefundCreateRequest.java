package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundCreateRequest {
    @NotNull(message = "paymentId is required")
    private Long paymentId;

    @NotNull(message = "orderId is required")
    private Long orderId;

    @NotNull(message = "refundAmount is required")
    @DecimalMin(value = "0", inclusive = false, message = "refundAmount must be > 0")
    private BigDecimal refundAmount;

    private String refundReason;
}
