package com.kylan.hotel.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditLogItemVO {
    private Long id;
    private String actionType;
    private String content;
    private String operator;
    private LocalDateTime createdAt;
}
