package com.kylan.hotel.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoomStatusTimelineVO {
    private String nodeCode;
    private String actionCode;
    private String remarkCode;
    private String remarkText;
    private LocalDateTime operationTime;
    private String operator;
    private Long roomId;
    private String roomNo;
}
