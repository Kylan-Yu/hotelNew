package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

@Data
public class HotelRoom extends BaseEntity {
    private Long propertyId;
    private Long roomTypeId;
    private String roomNo;
    private String floorNo;
    private String status;
}
