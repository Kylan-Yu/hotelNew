package com.kylan.hotel.domain.vo;

import lombok.Data;

@Data
public class SystemMenuVO {
    private Long id;
    private Long parentId;
    private String menuName;
    private String permissionCode;
    private String menuType;
    private String path;
    private Integer status;
}
