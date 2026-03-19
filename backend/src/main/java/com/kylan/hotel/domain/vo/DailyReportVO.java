package com.kylan.hotel.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DailyReportVO {
    private Long propertyId;
    private String propertyName;
    private Integer orderCount;
    private BigDecimal paymentAmount;
    private BigDecimal refundAmount;
    private BigDecimal netRevenue;
}
