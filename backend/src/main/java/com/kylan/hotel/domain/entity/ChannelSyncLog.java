package com.kylan.hotel.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChannelSyncLog {
    private Long id;
    private Long propertyId;
    private String channelCode;
    private String bizType;
    private String bizId;
    private String idempotentKey;
    private String syncStatus;
    private String requestPayload;
    private String responsePayload;
    private LocalDateTime createdAt;
}
