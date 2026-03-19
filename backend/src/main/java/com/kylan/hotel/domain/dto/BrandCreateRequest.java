package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BrandCreateRequest {
    @NotNull(message = "groupId is required")
    private Long groupId;

    @NotBlank(message = "brandCode is required")
    private String brandCode;

    @NotBlank(message = "brandName is required")
    private String brandName;
}
