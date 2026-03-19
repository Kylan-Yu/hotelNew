package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HotelReservation extends BaseEntity {
    private String reservationNo;
    private Long propertyId;
    private Long roomTypeId;
    private String channelCode;
    private String contactName;
    private String contactMobile;
    private Integer guestCount;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String reservationStatus;
    private BigDecimal estimatedAmount;
    private String remark;
}
