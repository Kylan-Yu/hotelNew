package com.kylan.hotel.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLogItemVO {
    private Long id;
    private String operation;
    private String requestUri;
    private String requestMethod;
    private String operator;
    private String successFlag;
    private String message;
    private LocalDateTime createdAt;
}
