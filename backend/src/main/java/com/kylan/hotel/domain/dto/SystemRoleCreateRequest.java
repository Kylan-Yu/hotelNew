package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemRoleCreateRequest {
    @NotBlank(message = "roleCode is required")
    private String roleCode;

    @NotBlank(message = "roleName is required")
    private String roleName;

    private Integer status;
}
