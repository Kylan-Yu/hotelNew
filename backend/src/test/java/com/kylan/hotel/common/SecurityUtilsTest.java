package com.kylan.hotel.common;

import com.kylan.hotel.config.UserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilsTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void propertyScopesShouldReturnCurrentPropertyWhenSelected() {
        UserPrincipal principal = UserPrincipal.builder()
                .userId(1L)
                .username("admin")
                .permissions(List.of("scope:all"))
                .propertyScopes(List.of(101L, 102L))
                .currentPropertyId(102L)
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.toAuthorities())
        );

        List<Long> scopes = SecurityUtils.propertyScopes();
        assertEquals(1, scopes.size());
        assertEquals(102L, scopes.get(0));
    }

    @Test
    void assertPropertyAccessibleShouldRejectOutOfScopeProperty() {
        UserPrincipal principal = UserPrincipal.builder()
                .userId(2L)
                .username("operator")
                .permissions(List.of("order:read"))
                .propertyScopes(List.of(201L))
                .currentPropertyId(null)
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.toAuthorities())
        );

        assertThrows(BusinessException.class, () -> SecurityUtils.assertPropertyAccessible(202L));
        assertDoesNotThrow(() -> SecurityUtils.assertPropertyAccessible(201L));
    }
}
