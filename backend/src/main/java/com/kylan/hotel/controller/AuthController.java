package com.kylan.hotel.controller;

import com.kylan.hotel.common.ApiResponse;
import com.kylan.hotel.domain.dto.LoginRequest;
import com.kylan.hotel.domain.dto.LogoutRequest;
import com.kylan.hotel.domain.dto.RefreshTokenRequest;
import com.kylan.hotel.domain.dto.SwitchPropertyRequest;
import com.kylan.hotel.domain.vo.CurrentUserVO;
import com.kylan.hotel.domain.vo.LoginResponse;
import com.kylan.hotel.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody(required = false) LogoutRequest request) {
        String accessToken = extractToken(authorization);
        authService.logout(accessToken, request);
        return ApiResponse.success(null);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<CurrentUserVO> me() {
        return ApiResponse.success(authService.currentUser());
    }

    @PostMapping("/switch-property")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<LoginResponse> switchProperty(@Valid @RequestBody SwitchPropertyRequest request) {
        return ApiResponse.success(authService.switchProperty(request.getPropertyId()));
    }

    private String extractToken(String authorization) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring("Bearer ".length());
    }
}
