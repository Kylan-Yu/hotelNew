package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemUserCreateRequest {
    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "nickname is required")
    private String nickname;

    private String mobile;
    private String email;
    private Integer status;
}
