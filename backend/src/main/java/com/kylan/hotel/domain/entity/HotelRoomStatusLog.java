package com.kylan.hotel.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HotelRoomStatusLog {
    private Long id;
    private Long roomId;
    private String oldStatus;
    private String newStatus;
    private String reason;
    private String operator;
    private LocalDateTime createdAt;
}
