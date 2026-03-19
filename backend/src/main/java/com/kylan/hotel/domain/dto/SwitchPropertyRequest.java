package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SwitchPropertyRequest {
    @NotNull(message = "propertyId is required")
    private Long propertyId;
}
