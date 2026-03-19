package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HotelRoomType extends BaseEntity {
    private Long propertyId;
    private String roomTypeCode;
    private String roomTypeName;
    private Integer maxGuestCount;
    private String bedType;
    private BigDecimal basePrice;
    private Integer status;
}
