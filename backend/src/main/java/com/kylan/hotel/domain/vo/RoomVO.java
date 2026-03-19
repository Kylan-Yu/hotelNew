package com.kylan.hotel.domain.vo;

import lombok.Data;

@Data
public class RoomVO {
    private Long id;
    private Long propertyId;
    private String propertyName;
    private Long roomTypeId;
    private String roomTypeName;
    private String roomNo;
    private String floorNo;
    private String status;
}
