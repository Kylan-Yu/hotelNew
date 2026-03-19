package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FinanceRefundRecord extends BaseEntity {
    private Long paymentId;
    private Long orderId;
    private BigDecimal refundAmount;
    private String refundReason;
    private String refundStatus;
}
