package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemParamUpdateRequest {
    @NotBlank(message = "paramValue is required")
    private String paramValue;

    private String remark;
}
