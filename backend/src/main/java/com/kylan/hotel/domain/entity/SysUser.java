package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

@Data
public class SysUser extends BaseEntity {
    private String username;
    private String passwordHash;
    private String nickname;
    private String mobile;
    private String email;
    private Integer status;
}
