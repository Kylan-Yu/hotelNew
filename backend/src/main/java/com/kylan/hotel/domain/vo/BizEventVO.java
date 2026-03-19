package com.kylan.hotel.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class BizEventVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String eventType;
    private Long propertyId;
    private Long bizId;
    private String content;
}
