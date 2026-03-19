package com.kylan.hotel.domain.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CampaignVO {
    private Long id;
    private Long propertyId;
    private String propertyName;
    private String campaignCode;
    private String campaignName;
    private String campaignType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
