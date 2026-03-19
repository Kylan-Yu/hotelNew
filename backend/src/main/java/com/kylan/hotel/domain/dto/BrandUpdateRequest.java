package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BrandUpdateRequest {
    @NotNull(message = "groupId is required")
    private Long groupId;

    @NotBlank(message = "brandName is required")
    private String brandName;
}
