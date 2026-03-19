package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CheckInRequest {
    @NotNull(message = "orderId is required")
    private Long orderId;

    @NotNull(message = "roomId is required")
    private Long roomId;

    @NotBlank(message = "stayType is required")
    private String stayType;

    @NotNull(message = "checkInDate is required")
    private LocalDate checkInDate;

    @NotNull(message = "checkOutDate is required")
    private LocalDate checkOutDate;
}
