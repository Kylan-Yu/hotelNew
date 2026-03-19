package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExtendStayRequest {
    @NotNull(message = "stayRecordId is required")
    private Long stayRecordId;

    @NotNull(message = "newCheckOutDate is required")
    private LocalDate newCheckOutDate;
}
