package com.kylan.hotel.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomTypeVO {
    private Long id;
    private Long propertyId;
    private String propertyName;
    private String roomTypeCode;
    private String roomTypeName;
    private Integer maxGuestCount;
    private String bedType;
    private BigDecimal basePrice;
    private Integer status;
}
