package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceRecord extends BaseEntity {
    private Long orderId;
    private Long propertyId;
    private String invoiceType;
    private String invoiceTitle;
    private String taxNo;
    private BigDecimal amount;
    private String invoiceStatus;
}
