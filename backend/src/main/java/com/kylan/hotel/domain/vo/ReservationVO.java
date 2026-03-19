package com.kylan.hotel.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReservationVO {
    private Long id;
    private String reservationNo;
    private Long propertyId;
    private String propertyName;
    private Long roomTypeId;
    private String roomTypeName;
    private String contactName;
    private String contactMobile;
    private Integer guestCount;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String reservationStatus;
    private BigDecimal estimatedAmount;
}
