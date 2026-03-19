package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemParamCreateRequest {
    @NotBlank(message = "paramKey is required")
    private String paramKey;

    @NotBlank(message = "paramValue is required")
    private String paramValue;

    private String remark;
}
