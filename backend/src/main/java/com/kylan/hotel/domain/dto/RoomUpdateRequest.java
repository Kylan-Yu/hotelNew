package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomUpdateRequest {
    @NotNull(message = "roomTypeId is required")
    private Long roomTypeId;

    private String floorNo;
}
