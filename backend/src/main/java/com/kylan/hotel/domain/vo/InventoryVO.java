package com.kylan.hotel.domain.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InventoryVO {
    private Long id;
    private Long propertyId;
    private String propertyName;
    private Long roomTypeId;
    private String roomTypeName;
    private LocalDate bizDate;
    private Integer totalInventory;
    private Integer occupiedInventory;
    private Integer availableInventory;
    private Integer warningThreshold;
    private Boolean warning;
}
