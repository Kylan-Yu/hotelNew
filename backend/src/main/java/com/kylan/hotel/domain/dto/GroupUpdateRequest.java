package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GroupUpdateRequest {
    @NotBlank(message = "groupName is required")
    private String groupName;
}
