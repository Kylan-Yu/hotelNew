package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentCreateRequest {
    @NotNull(message = "orderId is required")
    private Long orderId;

    private Long stayRecordId;

    @NotBlank(message = "paymentType is required")
    private String paymentType;

    @NotBlank(message = "paymentMethod is required")
    private String paymentMethod;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0", inclusive = false, message = "amount must be greater than 0")
    private BigDecimal amount;

    private String externalTradeNo;
    private String remark;
}
