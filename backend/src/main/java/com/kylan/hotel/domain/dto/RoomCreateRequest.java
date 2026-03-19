package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomCreateRequest {
    @NotNull(message = "propertyId is required")
    private Long propertyId;

    @NotNull(message = "roomTypeId is required")
    private Long roomTypeId;

    @NotBlank(message = "roomNo is required")
    private String roomNo;

    private String floorNo;
}
