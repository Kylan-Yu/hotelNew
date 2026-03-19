package com.kylan.hotel.config;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class UserPrincipal {
    private Long userId;
    private String username;
    private String nickname;
    private List<String> permissions;
    private List<Long> propertyScopes;
    private Long currentPropertyId;

    public Collection<? extends GrantedAuthority> toAuthorities() {
        return permissions == null ? List.of() : permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
