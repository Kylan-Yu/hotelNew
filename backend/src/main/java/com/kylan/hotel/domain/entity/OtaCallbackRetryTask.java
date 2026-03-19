package com.kylan.hotel.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OtaCallbackRetryTask {
    private Long id;
    private String idempotentKey;
    private String channelCode;
    private String eventType;
    private String externalRequestNo;
    private String signature;
    private String headersJson;
    private String payload;
    private String taskStatus;
    private Integer retryCount;
    private Integer maxRetryCount;
    private String lastError;
    private LocalDateTime nextRetryTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
