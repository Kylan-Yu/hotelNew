package com.kylan.hotel.common;

import com.kylan.hotel.config.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UserPrincipal currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BusinessException("unauthorized");
        }
        return principal;
    }

    public static String currentUsername() {
        try {
            return currentPrincipal().getUsername();
        } catch (Exception ex) {
            return "system";
        }
    }

    public static Long currentPropertyId() {
        return currentPrincipal().getCurrentPropertyId();
    }

    public static List<Long> propertyScopes() {
        UserPrincipal principal = currentPrincipal();
        Long currentPropertyId = principal.getCurrentPropertyId();
        if (currentPropertyId != null) {
            return List.of(currentPropertyId);
        }
        List<Long> scopes = principal.getPropertyScopes();
        return scopes == null ? List.of() : scopes;
    }

    public static boolean hasPermission(String permission) {
        List<String> permissions = currentPrincipal().getPermissions();
        return permissions != null && permissions.contains(permission);
    }

    public static void assertPropertyAccessible(Long propertyId) {
        if (propertyId == null) {
            return;
        }
        List<Long> scopes = propertyScopes();
        if (scopes.isEmpty() || !scopes.contains(propertyId)) {
            throw new BusinessException("property scope denied");
        }
    }
}
