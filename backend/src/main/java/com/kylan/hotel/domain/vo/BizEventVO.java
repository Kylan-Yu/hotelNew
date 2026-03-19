package com.kylan.hotel.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BizEventVO {
    private String eventType;
    private Long propertyId;
    private Long bizId;
    private String content;
}
