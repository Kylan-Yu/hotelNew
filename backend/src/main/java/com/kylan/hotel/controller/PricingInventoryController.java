package com.kylan.hotel.controller;

import com.kylan.hotel.common.ApiResponse;
import com.kylan.hotel.domain.dto.InventoryAdjustRequest;
import com.kylan.hotel.domain.dto.PricePlanUpsertRequest;
import com.kylan.hotel.domain.dto.PriceRuleCreateRequest;
import com.kylan.hotel.domain.entity.HotelPriceRule;
import com.kylan.hotel.domain.vo.InventoryVO;
import com.kylan.hotel.domain.vo.PricePlanVO;
import com.kylan.hotel.service.PricingInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
public class PricingInventoryController {

    private final PricingInventoryService pricingInventoryService;

    @PostMapping("/plans")
    @PreAuthorize("hasAuthority('pricing:write')")
    public ApiResponse<Void> upsertPlan(@Valid @RequestBody PricePlanUpsertRequest request) {
        pricingInventoryService.upsertPricePlan(request);
        return ApiResponse.success(null);
    }

    @GetMapping("/plans")
    @PreAuthorize("hasAuthority('pricing:read')")
    public ApiResponse<List<PricePlanVO>> listPlans() {
        return ApiResponse.success(pricingInventoryService.listPricePlans());
    }

    @PostMapping("/rules")
    @PreAuthorize("hasAuthority('pricing:write')")
    public ApiResponse<Long> createRule(@Valid @RequestBody PriceRuleCreateRequest request) {
        return ApiResponse.success(pricingInventoryService.createPriceRule(request));
    }

    @GetMapping("/rules")
    @PreAuthorize("hasAuthority('pricing:read')")
    public ApiResponse<List<HotelPriceRule>> listRules() {
        return ApiResponse.success(pricingInventoryService.listPriceRules());
    }

    @PatchMapping("/inventories/adjust")
    @PreAuthorize("hasAuthority('inventory:write')")
    public ApiResponse<Void> adjustInventory(@Valid @RequestBody InventoryAdjustRequest request) {
        pricingInventoryService.adjustInventory(request);
        return ApiResponse.success(null);
    }

    @GetMapping("/inventories")
    @PreAuthorize("hasAuthority('inventory:read')")
    public ApiResponse<List<InventoryVO>> listInventories() {
        return ApiResponse.success(pricingInventoryService.listInventories());
    }
}
