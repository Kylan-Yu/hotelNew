package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

@Data
public class HotelGroup extends BaseEntity {
    private String groupCode;
    private String groupName;
    private Integer status;
}
