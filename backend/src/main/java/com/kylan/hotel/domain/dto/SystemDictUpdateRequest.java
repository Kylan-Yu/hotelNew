package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemDictUpdateRequest {
    @NotBlank(message = "dictName is required")
    private String dictName;

    private String remark;
}
