package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HotelPricePlan extends BaseEntity {
    private Long propertyId;
    private Long roomTypeId;
    private LocalDate bizDate;
    private BigDecimal salePrice;
    private Integer sellableInventory;
    private Integer overbookLimit;
    private String priceTag;
}
