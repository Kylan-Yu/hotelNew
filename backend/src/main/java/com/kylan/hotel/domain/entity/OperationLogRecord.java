package com.kylan.hotel.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLogRecord {
    private Long id;
    private String moduleCode;
    private String operation;
    private String requestUri;
    private String requestMethod;
    private String operator;
    private String successFlag;
    private String message;
    private Long groupId;
    private Long brandId;
    private Long propertyId;
    private LocalDateTime createdAt;
}
