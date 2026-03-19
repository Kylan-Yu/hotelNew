package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HousekeepingTaskCreateRequest {
    @NotNull(message = "propertyId is required")
    private Long propertyId;

    @NotNull(message = "roomId is required")
    private Long roomId;

    @NotNull(message = "bizDate is required")
    private LocalDate bizDate;

    private String assignee;
    private String remark;
}
