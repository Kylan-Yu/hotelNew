package com.kylan.hotel.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropertyStatVO {
    private Long propertyId;
    private String propertyName;
    private Integer roomCount;
    private Integer occupiedRoomCount;
    private BigDecimal occupancyRate;
}
