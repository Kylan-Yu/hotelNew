package com.kylan.hotel.service.impl;

import com.kylan.hotel.common.BusinessException;
import com.kylan.hotel.common.RedisKeys;
import com.kylan.hotel.common.SecurityUtils;
import com.kylan.hotel.config.JwtTokenProvider;
import com.kylan.hotel.config.UserPrincipal;
import com.kylan.hotel.domain.dto.LoginRequest;
import com.kylan.hotel.domain.dto.LogoutRequest;
import com.kylan.hotel.domain.dto.RefreshTokenRequest;
import com.kylan.hotel.domain.entity.SysUser;
import com.kylan.hotel.domain.vo.CurrentUserVO;
import com.kylan.hotel.domain.vo.LoginResponse;
import com.kylan.hotel.mapper.SysUserMapper;
import com.kylan.hotel.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException("user not found");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("user is disabled");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("invalid username or password");
        }
        return buildTokenResponse(user);
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtTokenProvider.isTokenValid(refreshToken, "refresh")) {
            throw new BusinessException("invalid refresh token");
        }
        Long userId = jwtTokenProvider.getUserId(refreshToken);
        String oldJti = jwtTokenProvider.getTokenId(refreshToken);
        String tokenValue = stringRedisTemplate.opsForValue().get(RedisKeys.refreshToken(userId, oldJti));
        if (!StringUtils.hasText(tokenValue)) {
            throw new BusinessException("refresh token has expired");
        }

        SysUser user = sysUserMapper.findById(userId);
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("user unavailable");
        }

        stringRedisTemplate.delete(RedisKeys.refreshToken(userId, oldJti));
        return buildTokenResponse(user);
    }

    @Override
    public void logout(String accessToken, LogoutRequest request) {
        if (StringUtils.hasText(accessToken) && jwtTokenProvider.isTokenValid(accessToken, "access")) {
            String accessTokenId = jwtTokenProvider.getTokenId(accessToken);
            long remaining = jwtTokenProvider.getRemainingTtlSeconds(accessToken);
            if (remaining > 0) {
                stringRedisTemplate.opsForValue()
                        .set(RedisKeys.accessTokenBlacklist(accessTokenId), "1", remaining, TimeUnit.SECONDS);
            }
        }

        if (request != null && StringUtils.hasText(request.getRefreshToken())
                && jwtTokenProvider.isTokenValid(request.getRefreshToken(), "refresh")) {
            Long userId = jwtTokenProvider.getUserId(request.getRefreshToken());
            String refreshJti = jwtTokenProvider.getTokenId(request.getRefreshToken());
            stringRedisTemplate.delete(RedisKeys.refreshToken(userId, refreshJti));
        }
    }

    @Override
    public CurrentUserVO currentUser() {
        UserPrincipal principal = SecurityUtils.currentPrincipal();
        return CurrentUserVO.builder()
                .userId(principal.getUserId())
                .username(principal.getUsername())
                .nickname(principal.getNickname())
                .permissions(principal.getPermissions())
                .propertyScopes(principal.getPropertyScopes())
                .currentPropertyId(principal.getCurrentPropertyId())
                .build();
    }

    @Override
    public LoginResponse switchProperty(Long propertyId) {
        UserPrincipal principal = SecurityUtils.currentPrincipal();
        List<Long> scopes = principal.getPropertyScopes() == null ? List.of() : principal.getPropertyScopes();
        if (!principal.getPermissions().contains("scope:all") && !scopes.contains(propertyId)) {
            throw new BusinessException("property scope denied");
        }
        SysUser user = sysUserMapper.findById(principal.getUserId());
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("user unavailable");
        }
        return buildTokenResponse(user, propertyId);
    }

    private LoginResponse buildTokenResponse(SysUser user) {
        return buildTokenResponse(user, null);
    }

    private LoginResponse buildTokenResponse(SysUser user, Long forceCurrentPropertyId) {
        List<String> permissions;
        List<Long> propertyScopes;

        try {
            permissions = sysUserMapper.findPermissionCodesByUserId(user.getId());
        } catch (Exception e) {
            permissions = List.of();
        }
        permissions = mergeBuiltInAdminPermissions(user, permissions);

        try {
            propertyScopes = sysUserMapper.findPropertyScopesByUserId(user.getId());
        } catch (Exception e) {
            propertyScopes = List.of();
        }

        Long currentPropertyId = forceCurrentPropertyId != null
                ? forceCurrentPropertyId
                : (propertyScopes.isEmpty() ? null : propertyScopes.get(0));
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getUsername(), user.getNickname(), permissions, propertyScopes, currentPropertyId
        );
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getUsername());

        String refreshTokenKey = RedisKeys.refreshToken(user.getId(), jwtTokenProvider.getTokenId(refreshToken));
        stringRedisTemplate.opsForValue()
                .set(refreshTokenKey, "1", jwtTokenProvider.getRefreshTokenExpireSeconds(), TimeUnit.SECONDS);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpireSeconds())
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .permissions(permissions)
                .propertyScopes(propertyScopes)
                .currentPropertyId(currentPropertyId)
                .build();
    }

    private List<String> mergeBuiltInAdminPermissions(SysUser user, List<String> permissions) {
        if (user == null || user.getUsername() == null || !"admin".equalsIgnoreCase(user.getUsername())) {
            return permissions;
        }
        Set<String> merged = new LinkedHashSet<>(permissions == null ? List.of() : permissions);
        merged.addAll(List.of(
                "ops:read",
                "sys:user:read",
                "sys:role:read",
                "sys:permission:read",
                "sys:menu:read",
                "sys:dict:read",
                "sys:param:read",
                "sys:user:write",
                "sys:role:write",
                "sys:dict:write",
                "sys:param:write"
        ));
        return new ArrayList<>(merged);
    }
}
