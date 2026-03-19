package com.kylan.hotel.service;

import com.kylan.hotel.domain.dto.InventoryAdjustRequest;
import com.kylan.hotel.domain.dto.PricePlanUpsertRequest;
import com.kylan.hotel.domain.dto.PriceRuleCreateRequest;
import com.kylan.hotel.domain.entity.HotelPriceRule;
import com.kylan.hotel.domain.vo.InventoryVO;
import com.kylan.hotel.domain.vo.PricePlanVO;

import java.util.List;

public interface PricingInventoryService {
    void upsertPricePlan(PricePlanUpsertRequest request);

    List<PricePlanVO> listPricePlans();

    Long createPriceRule(PriceRuleCreateRequest request);

    List<HotelPriceRule> listPriceRules();

    void adjustInventory(InventoryAdjustRequest request);

    List<InventoryVO> listInventories();
}
