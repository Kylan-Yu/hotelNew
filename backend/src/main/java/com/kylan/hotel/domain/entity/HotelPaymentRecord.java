package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HotelPaymentRecord extends BaseEntity {
    private Long orderId;
    private Long stayRecordId;
    private String paymentType;
    private String paymentMethod;
    private BigDecimal amount;
    private String paymentStatus;
    private String externalTradeNo;
    private String remark;
}
