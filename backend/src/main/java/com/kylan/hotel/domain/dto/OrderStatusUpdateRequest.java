package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {
    @NotNull(message = "orderId is required")
    private Long orderId;

    @NotNull(message = "targetStatus is required")
    private String targetStatus;

    private String remark;
}
