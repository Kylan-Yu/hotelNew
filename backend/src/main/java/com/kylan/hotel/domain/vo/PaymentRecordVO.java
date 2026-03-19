package com.kylan.hotel.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRecordVO {
    private Long id;
    private Long orderId;
    private Long stayRecordId;
    private String paymentType;
    private String paymentMethod;
    private BigDecimal amount;
    private String paymentStatus;
    private String externalTradeNo;
    private String remark;
}
