package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MemberPreferenceCreateRequest {
    @NotNull(message = "memberId is required")
    private Long memberId;

    @NotBlank(message = "preferenceType is required")
    private String preferenceType;

    @NotBlank(message = "preferenceValue is required")
    private String preferenceValue;
}
