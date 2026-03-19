package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CouponTemplate extends BaseEntity {
    private Long propertyId;
    private String couponCode;
    private String couponName;
    private BigDecimal amount;
    private BigDecimal threshold;
    private String status;
}
