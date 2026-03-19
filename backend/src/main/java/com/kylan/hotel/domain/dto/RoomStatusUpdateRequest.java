package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoomStatusUpdateRequest {
    @NotBlank(message = "status is required")
    private String status;

    private String reason;
}
