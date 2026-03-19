package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PricePlanUpsertRequest {
    @NotNull(message = "propertyId is required")
    private Long propertyId;

    @NotNull(message = "roomTypeId is required")
    private Long roomTypeId;

    @NotNull(message = "bizDate is required")
    private LocalDate bizDate;

    @NotNull(message = "salePrice is required")
    @DecimalMin(value = "0", inclusive = true, message = "salePrice must be >= 0")
    private BigDecimal salePrice;

    @NotNull(message = "sellableInventory is required")
    private Integer sellableInventory;

    private Integer overbookLimit;
    private String priceTag;
}
