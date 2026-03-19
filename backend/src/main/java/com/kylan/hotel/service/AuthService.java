package com.kylan.hotel.service;

import com.kylan.hotel.domain.dto.LogoutRequest;
import com.kylan.hotel.domain.dto.LoginRequest;
import com.kylan.hotel.domain.dto.RefreshTokenRequest;
import com.kylan.hotel.domain.vo.CurrentUserVO;
import com.kylan.hotel.domain.vo.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);

    void logout(String accessToken, LogoutRequest request);

    CurrentUserVO currentUser();

    LoginResponse switchProperty(Long propertyId);
}
