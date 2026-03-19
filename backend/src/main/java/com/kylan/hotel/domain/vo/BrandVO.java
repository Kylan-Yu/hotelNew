package com.kylan.hotel.domain.vo;

import lombok.Data;

@Data
public class BrandVO {
    private Long id;
    private Long groupId;
    private String groupName;
    private String brandCode;
    private String brandName;
    private Integer status;
}
