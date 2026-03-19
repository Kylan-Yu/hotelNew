package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceCreateRequest {
    @NotNull(message = "orderId is required")
    private Long orderId;

    @NotNull(message = "propertyId is required")
    private Long propertyId;

    private String invoiceType;
    private String invoiceTitle;
    private String taxNo;
    private BigDecimal amount;
}
