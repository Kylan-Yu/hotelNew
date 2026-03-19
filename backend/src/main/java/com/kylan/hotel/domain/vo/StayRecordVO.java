package com.kylan.hotel.domain.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StayRecordVO {
    private Long id;
    private String stayNo;
    private Long orderId;
    private String orderNo;
    private Long propertyId;
    private String propertyName;
    private Long roomId;
    private String roomNo;
    private String stayType;
    private String stayStatus;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime actualCheckInTime;
    private LocalDateTime actualCheckOutTime;
}
