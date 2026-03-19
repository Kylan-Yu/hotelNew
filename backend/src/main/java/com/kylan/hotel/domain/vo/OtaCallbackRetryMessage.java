package com.kylan.hotel.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtaCallbackRetryMessage {
    private Long taskId;
    private String idempotentKey;
    private Integer retryCount;
}
