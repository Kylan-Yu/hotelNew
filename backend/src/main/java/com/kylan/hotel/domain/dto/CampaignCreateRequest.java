package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CampaignCreateRequest {
    @NotNull(message = "propertyId is required")
    private Long propertyId;

    @NotBlank(message = "campaignCode is required")
    private String campaignCode;

    @NotBlank(message = "campaignName is required")
    private String campaignName;

    @NotBlank(message = "campaignType is required")
    private String campaignType;

    private LocalDate startDate;
    private LocalDate endDate;
}
