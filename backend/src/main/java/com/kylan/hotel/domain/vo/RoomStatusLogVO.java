package com.kylan.hotel.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoomStatusLogVO {
    private Long id;
    private Long roomId;
    private String oldStatus;
    private String newStatus;
    private String reason;
    private String operator;
    private LocalDateTime createdAt;
}
