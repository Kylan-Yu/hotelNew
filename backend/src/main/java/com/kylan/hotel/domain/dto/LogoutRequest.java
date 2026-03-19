package com.kylan.hotel.domain.dto;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}
