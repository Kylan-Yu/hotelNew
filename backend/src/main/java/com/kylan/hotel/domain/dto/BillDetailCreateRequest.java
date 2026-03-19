package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillDetailCreateRequest {
    @NotNull(message = "propertyId is required")
    private Long propertyId;

    @NotNull(message = "orderId is required")
    private Long orderId;

    @NotBlank(message = "billType is required")
    private String billType;

    @NotBlank(message = "billItem is required")
    private String billItem;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0", inclusive = false, message = "amount must be > 0")
    private BigDecimal amount;
}
