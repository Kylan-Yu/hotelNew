package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemDictCreateRequest {
    @NotBlank(message = "dictCode is required")
    private String dictCode;

    @NotBlank(message = "dictName is required")
    private String dictName;

    private String remark;
}
