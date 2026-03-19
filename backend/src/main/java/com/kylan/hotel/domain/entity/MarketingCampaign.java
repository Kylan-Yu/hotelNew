package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MarketingCampaign extends BaseEntity {
    private Long propertyId;
    private String campaignCode;
    private String campaignName;
    private String campaignType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
