package com.kylan.hotel.domain.vo;

import lombok.Data;

@Data
public class PropertyVO {
    private Long id;
    private Long groupId;
    private String groupName;
    private Long brandId;
    private String brandName;
    private String propertyCode;
    private String propertyName;
    private String businessMode;
    private String contactPhone;
    private String city;
    private String address;
    private Integer status;
}
