package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class HotelStayRecord extends BaseEntity {
    private String stayNo;
    private Long orderId;
    private Long reservationId;
    private Long propertyId;
    private Long roomId;
    private String stayType;
    private String stayStatus;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime actualCheckInTime;
    private LocalDateTime actualCheckOutTime;
}
