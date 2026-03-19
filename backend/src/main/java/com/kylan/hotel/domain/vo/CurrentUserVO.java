package com.kylan.hotel.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CurrentUserVO {
    private Long userId;
    private String username;
    private String nickname;
    private List<String> permissions;
    private List<Long> propertyScopes;
    private Long currentPropertyId;
}
