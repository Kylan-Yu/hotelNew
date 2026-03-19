package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemUserUpdateRequest {
    @NotBlank(message = "nickname is required")
    private String nickname;

    private String mobile;
    private String email;
    private Integer status;
}
