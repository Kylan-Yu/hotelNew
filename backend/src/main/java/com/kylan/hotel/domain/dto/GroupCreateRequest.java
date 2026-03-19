package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GroupCreateRequest {
    @NotBlank(message = "groupCode is required")
    private String groupCode;

    @NotBlank(message = "groupName is required")
    private String groupName;
}
