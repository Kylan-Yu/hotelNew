package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemRoleUpdateRequest {
    @NotBlank(message = "roleName is required")
    private String roleName;

    private Integer status;
}
