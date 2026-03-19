package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeRoomRequest {
    @NotNull(message = "stayRecordId is required")
    private Long stayRecordId;

    @NotNull(message = "newRoomId is required")
    private Long newRoomId;

    private String reason;
}
