package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CouponCreateRequest {
    @NotNull(message = "propertyId is required")
    private Long propertyId;

    @NotBlank(message = "couponCode is required")
    private String couponCode;

    @NotBlank(message = "couponName is required")
    private String couponName;

    @NotNull(message = "amount is required")
    private BigDecimal amount;

    private BigDecimal threshold;
}
