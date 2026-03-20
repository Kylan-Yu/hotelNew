package com.kylan.hotel.controller;

import com.kylan.hotel.common.ApiResponse;
import com.kylan.hotel.domain.dto.LoginRequest;
import com.kylan.hotel.domain.vo.LoginResponse;
import com.kylan.hotel.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/final-auth")
@RequiredArgsConstructor
public class FinalAuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }
}
