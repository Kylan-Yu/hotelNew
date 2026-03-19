package com.kylan.hotel.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long propertyId;
    private String propertyName;
    private Long roomTypeId;
    private String roomTypeName;
    private String sourceChannel;
    private String guestName;
    private String guestMobile;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalAmount;
    private String orderStatus;
}
