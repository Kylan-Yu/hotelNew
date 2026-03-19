package com.kylan.hotel.domain.vo;

import lombok.Data;

@Data
public class SystemUserVO {
    private Long id;
    private String username;
    private String nickname;
    private String mobile;
    private String email;
    private Integer status;
}
