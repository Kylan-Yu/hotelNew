package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderCancelRequest {
    @NotNull(message = "orderId is required")
    private Long orderId;

    private String reason;
}
