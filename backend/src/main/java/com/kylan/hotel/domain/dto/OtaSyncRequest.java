package com.kylan.hotel.domain.dto;

import lombok.Data;

@Data
public class OtaSyncRequest {
    private Long propertyId;
    private String channelCode;
    private String syncType;
}
