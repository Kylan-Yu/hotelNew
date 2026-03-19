package com.kylan.hotel.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditLogRecord {
    private Long id;
    private String moduleCode;
    private String bizNo;
    private String actionType;
    private String content;
    private String operator;
    private Long groupId;
    private Long brandId;
    private Long propertyId;
    private LocalDateTime createdAt;
}
