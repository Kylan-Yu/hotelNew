package com.kylan.hotel.config;

import com.kylan.hotel.common.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Component
public class JwtTokenProvider {
    private static final String CLAIM_UID = "uid";
    private static final String CLAIM_NICK = "nick";
    private static final String CLAIM_PERMS = "perms";
    private static final String CLAIM_PROP_SCOPES = "propScopes";
    private static final String CLAIM_CURRENT_PROP = "currentProp";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.access-token-expire-seconds:7200}")
    @Getter
    private long accessTokenExpireSeconds;

    @Value("${security.jwt.refresh-token-expire-seconds:604800}")
    @Getter
    private long refreshTokenExpireSeconds;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Long userId, String username, String nickname, List<String> permissions,
                                      List<Long> propertyScopes, Long currentPropertyId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_UID, userId);
        claims.put(CLAIM_NICK, nickname);
        claims.put(CLAIM_PERMS, permissions == null ? List.of() : permissions);
        claims.put(CLAIM_PROP_SCOPES, propertyScopes == null ? List.of() : propertyScopes);
        claims.put(CLAIM_CURRENT_PROP, currentPropertyId);
        claims.put(CLAIM_TYPE, TYPE_ACCESS);
        return buildToken(username, claims, accessTokenExpireSeconds);
    }

    public String generateRefreshToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_UID, userId);
        claims.put(CLAIM_TYPE, TYPE_REFRESH);
        return buildToken(username, claims, refreshTokenExpireSeconds);
    }

    public Authentication toAuthentication(String token) {
        Claims claims = parseClaims(token);
        Object rawPerms = claims.get(CLAIM_PERMS);
        List<String> permissions;
        if (rawPerms instanceof List<?> list) {
            permissions = list.stream().map(String::valueOf).toList();
        } else {
            permissions = List.of();
        }
        Object rawScopes = claims.get(CLAIM_PROP_SCOPES);
        List<Long> propertyScopes;
        if (rawScopes instanceof List<?> list) {
            propertyScopes = list.stream().map(item -> Long.valueOf(String.valueOf(item))).toList();
        } else {
            propertyScopes = List.of();
        }
        Object currentProperty = claims.get(CLAIM_CURRENT_PROP);
        Long currentPropertyId = null;
        if (currentProperty != null && !"null".equals(String.valueOf(currentProperty))) {
            currentPropertyId = Long.valueOf(String.valueOf(currentProperty));
        }
        UserPrincipal principal = UserPrincipal.builder()
                .userId(Long.valueOf(String.valueOf(claims.get(CLAIM_UID))))
                .username(claims.getSubject())
                .nickname(String.valueOf(claims.getOrDefault(CLAIM_NICK, "")))
                .permissions(permissions)
                .propertyScopes(propertyScopes)
                .currentPropertyId(currentPropertyId)
                .build();
        return new UsernamePasswordAuthenticationToken(principal, null, principal.toAuthorities());
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new BusinessException("invalid token");
        }
    }

    public boolean isTokenValid(String token, String expectedType) {
        try {
            Claims claims = parseClaims(token);
            return expectedType.equals(claims.get(CLAIM_TYPE));
        } catch (BusinessException ex) {
            return false;
        }
    }

    public String getTokenId(String token) {
        return parseClaims(token).getId();
    }

    public Long getUserId(String token) {
        return Long.valueOf(String.valueOf(parseClaims(token).get(CLAIM_UID)));
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public long getRemainingTtlSeconds(String token) {
        Claims claims = parseClaims(token);
        long seconds = Duration.between(new Date().toInstant(), claims.getExpiration().toInstant()).toSeconds();
        return Math.max(seconds, 0L);
    }

    private String buildToken(String subject, Map<String, Object> claims, long expireSeconds) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expireSeconds * 1000);
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }
}
