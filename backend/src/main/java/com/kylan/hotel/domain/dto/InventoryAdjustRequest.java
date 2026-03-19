package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InventoryAdjustRequest {
    @NotNull(message = "propertyId is required")
    private Long propertyId;

    @NotNull(message = "roomTypeId is required")
    private Long roomTypeId;

    @NotNull(message = "bizDate is required")
    private LocalDate bizDate;

    @NotNull(message = "occupiedDelta is required")
    private Integer occupiedDelta;
}
