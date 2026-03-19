package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PropertyCreateRequest {
    private Long groupId;

    private Long brandId;

    @NotBlank(message = "propertyCode is required")
    private String propertyCode;

    @NotBlank(message = "propertyName is required")
    private String propertyName;

    @NotBlank(message = "businessMode is required")
    private String businessMode;

    private String contactPhone;
    private String province;
    private String city;
    private String district;
    private String address;
}
