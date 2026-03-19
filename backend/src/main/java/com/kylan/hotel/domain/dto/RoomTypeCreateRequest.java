package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomTypeCreateRequest {
    @NotNull(message = "propertyId is required")
    private Long propertyId;

    @NotBlank(message = "roomTypeCode is required")
    private String roomTypeCode;

    @NotBlank(message = "roomTypeName is required")
    private String roomTypeName;

    @NotNull(message = "maxGuestCount is required")
    private Integer maxGuestCount;

    private String bedType;

    @NotNull(message = "basePrice is required")
    @DecimalMin(value = "0", inclusive = true, message = "basePrice cannot be negative")
    private BigDecimal basePrice;
}
