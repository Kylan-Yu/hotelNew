package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HotelOrderExt extends BaseEntity {
    private String orderNo;
    private Long propertyId;
    private Long roomTypeId;
    private String sourceChannel;
    private String guestName;
    private String guestMobile;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String channelOrderNo;
}
