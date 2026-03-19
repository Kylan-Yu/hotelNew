package com.kylan.hotel.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CouponVO {
    private Long id;
    private Long propertyId;
    private String propertyName;
    private String couponCode;
    private String couponName;
    private BigDecimal amount;
    private BigDecimal threshold;
    private String status;
}
