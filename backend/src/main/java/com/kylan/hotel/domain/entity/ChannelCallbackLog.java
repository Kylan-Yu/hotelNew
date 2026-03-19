package com.kylan.hotel.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChannelCallbackLog {
    private Long id;
    private String idempotentKey;
    private String channelCode;
    private String eventType;
    private String externalRequestNo;
    private String signature;
    private String callbackStatus;
    private String payload;
    private String message;
    private String processedFlag;
    private LocalDateTime createdAt;
}
