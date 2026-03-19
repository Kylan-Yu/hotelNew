package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FinanceBillDetail extends BaseEntity {
    private Long propertyId;
    private Long orderId;
    private String billType;
    private String billItem;
    private BigDecimal amount;
}
