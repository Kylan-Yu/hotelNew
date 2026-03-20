package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MemberCreateRequest {
    @NotNull(message = "propertyId is required")
    private Long propertyId;

    @NotBlank(message = "memberName is required")
    private String memberName;

    @NotBlank(message = "mobile is required")
    private String mobile;

    private String gender;

    private Integer levelCode;
}
