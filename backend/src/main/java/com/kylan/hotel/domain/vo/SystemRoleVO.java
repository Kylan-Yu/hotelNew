package com.kylan.hotel.domain.vo;

import lombok.Data;

@Data
public class SystemRoleVO {
    private Long id;
    private String roleCode;
    private String roleName;
    private Integer status;
}
