package com.kylan.hotel.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardVO {
    private Integer todayOrderCount;
    private BigDecimal todayRevenue;
    private Integer inHouseCount;
    private Integer warningInventoryCount;
}
