package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

@Data
public class HotelBrand extends BaseEntity {
    private Long groupId;
    private String brandCode;
    private String brandName;
    private Integer status;
}
