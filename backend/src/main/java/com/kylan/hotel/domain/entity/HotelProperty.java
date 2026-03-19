package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

@Data
public class HotelProperty extends BaseEntity {
    private Long groupId;
    private Long brandId;
    private String propertyCode;
    private String propertyName;
    private String businessMode;
    private String contactPhone;
    private String province;
    private String city;
    private String district;
    private String address;
    private Integer status;
}
