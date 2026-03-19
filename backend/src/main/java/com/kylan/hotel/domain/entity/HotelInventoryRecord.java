package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HotelInventoryRecord extends BaseEntity {
    private Long propertyId;
    private Long roomTypeId;
    private LocalDate bizDate;
    private Integer totalInventory;
    private Integer occupiedInventory;
    private Integer availableInventory;
    private Integer warningThreshold;
}
