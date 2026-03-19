package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HotelPriceRule extends BaseEntity {
    private Long propertyId;
    private String ruleName;
    private String ruleType;
    private String ruleValue;
    private BigDecimal deltaAmount;
    private Integer enabled;
}
