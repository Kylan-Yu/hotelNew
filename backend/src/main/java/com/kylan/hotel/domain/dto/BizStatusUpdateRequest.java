package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BizStatusUpdateRequest {
    @NotNull(message = "id is required")
    private Long id;

    @NotBlank(message = "status is required")
    private String status;
}
