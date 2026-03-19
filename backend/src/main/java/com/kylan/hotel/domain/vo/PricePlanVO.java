package com.kylan.hotel.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PricePlanVO {
    private Long id;
    private Long propertyId;
    private String propertyName;
    private Long roomTypeId;
    private String roomTypeName;
    private LocalDate bizDate;
    private BigDecimal salePrice;
    private Integer sellableInventory;
    private Integer overbookLimit;
    private String priceTag;
}
