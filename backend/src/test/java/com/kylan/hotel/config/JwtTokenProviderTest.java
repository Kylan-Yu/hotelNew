package com.kylan.hotel.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(
                tokenProvider,
                "secret",
                "0123456789abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz"
        );
        ReflectionTestUtils.setField(tokenProvider, "accessTokenExpireSeconds", 600L);
        ReflectionTestUtils.setField(tokenProvider, "refreshTokenExpireSeconds", 3600L);
        tokenProvider.init();
    }

    @Test
    void shouldGenerateAndParseAccessToken() {
        String accessToken = tokenProvider.generateAccessToken(
                1L,
                "admin",
                "Administrator",
                List.of("order:read", "scope:all"),
                List.of(1001L, 1002L),
                1001L
        );

        assertTrue(tokenProvider.isTokenValid(accessToken, "access"));
        Authentication authentication = tokenProvider.toAuthentication(accessToken);
        assertNotNull(authentication);
        assertTrue(authentication.getPrincipal() instanceof UserPrincipal);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        assertEquals(1L, principal.getUserId());
        assertEquals(1001L, principal.getCurrentPropertyId());
        assertEquals(2, principal.getPropertyScopes().size());
        assertTrue(principal.getPermissions().contains("order:read"));
    }

    @Test
    void shouldValidateTokenType() {
        String refreshToken = tokenProvider.generateRefreshToken(1L, "admin");
        assertTrue(tokenProvider.isTokenValid(refreshToken, "refresh"));
        assertFalse(tokenProvider.isTokenValid(refreshToken, "access"));
    }
}
