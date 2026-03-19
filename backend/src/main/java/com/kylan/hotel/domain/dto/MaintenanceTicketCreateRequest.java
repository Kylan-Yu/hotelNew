package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MaintenanceTicketCreateRequest {
    @NotNull(message = "propertyId is required")
    private Long propertyId;

    @NotNull(message = "roomId is required")
    private Long roomId;

    @NotBlank(message = "issueType is required")
    private String issueType;

    @NotBlank(message = "issueDescription is required")
    private String issueDescription;

    private String assignee;
}
