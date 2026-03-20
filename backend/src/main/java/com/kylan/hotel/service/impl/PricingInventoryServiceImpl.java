package com.kylan.hotel.service.impl;

import com.kylan.hotel.common.BusinessException;
import com.kylan.hotel.common.SecurityUtils;
import com.kylan.hotel.domain.dto.InventoryAdjustRequest;
import com.kylan.hotel.domain.dto.PricePlanUpsertRequest;
import com.kylan.hotel.domain.dto.PriceRuleCreateRequest;
import com.kylan.hotel.domain.entity.*;
import com.kylan.hotel.domain.vo.BizEventVO;
import com.kylan.hotel.domain.vo.InventoryVO;
import com.kylan.hotel.domain.vo.PricePlanVO;
import com.kylan.hotel.mapper.*;
import com.kylan.hotel.service.EventPublisher;
import com.kylan.hotel.service.PricingInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PricingInventoryServiceImpl implements PricingInventoryService {

    private final HotelPricePlanMapper hotelPricePlanMapper;
    private final HotelPriceRuleMapper hotelPriceRuleMapper;
    private final HotelInventoryRecordMapper hotelInventoryRecordMapper;
    private final HotelPropertyMapper hotelPropertyMapper;
    private final HotelRoomTypeMapper hotelRoomTypeMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsertPricePlan(PricePlanUpsertRequest request) {
        SecurityUtils.assertPropertyAccessible(request.getPropertyId());
        validatePropertyAndRoomType(request.getPropertyId(), request.getRoomTypeId());

        HotelPricePlan entity = new HotelPricePlan();
        entity.setPropertyId(request.getPropertyId());
        entity.setRoomTypeId(request.getRoomTypeId());
        entity.setBizDate(request.getBizDate());
        entity.setSalePrice(request.getSalePrice());
        entity.setSellableInventory(request.getSellableInventory());
        entity.setOverbookLimit(request.getOverbookLimit() == null ? 0 : request.getOverbookLimit());
        entity.setPriceTag(request.getPriceTag());
        entity.setUpdatedBy(SecurityUtils.currentUsername());

        if (hotelPricePlanMapper.countByUk(request.getPropertyId(), request.getRoomTypeId(), request.getBizDate()) > 0) {
            hotelPricePlanMapper.updateByUk(entity);
        } else {
            entity.setCreatedBy(SecurityUtils.currentUsername());
            entity.setDeleted(0);
            hotelPricePlanMapper.insert(entity);
        }

        String cacheKey = priceCacheKey(request.getPropertyId(), request.getRoomTypeId(), request.getBizDate().toString());
        stringRedisTemplate.opsForValue().set(cacheKey, request.getSalePrice().toPlainString(), 30, TimeUnit.MINUTES);
    }

    @Override
    public List<PricePlanVO> listPricePlans() {
        return hotelPricePlanMapper.findAll().stream()
                .filter(item -> canAccessProperty(item.getPropertyId()))
                .toList();
    }

    @Override
    public Long createPriceRule(PriceRuleCreateRequest request) {
        SecurityUtils.assertPropertyAccessible(request.getPropertyId());
        HotelPriceRule entity = new HotelPriceRule();
        entity.setPropertyId(request.getPropertyId());
        entity.setRuleName(request.getRuleName());
        entity.setRuleType(request.getRuleType());
        entity.setRuleValue(request.getRuleValue());
        entity.setDeltaAmount(request.getDeltaAmount());
        entity.setEnabled(request.getEnabled() == null ? 1 : request.getEnabled());
        entity.setCreatedBy(SecurityUtils.currentUsername());
        entity.setUpdatedBy(SecurityUtils.currentUsername());
        entity.setDeleted(0);
        hotelPriceRuleMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public List<HotelPriceRule> listPriceRules() {
        return hotelPriceRuleMapper.findAll().stream()
                .filter(item -> canAccessProperty(item.getPropertyId()))
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustInventory(InventoryAdjustRequest request) {
        SecurityUtils.assertPropertyAccessible(request.getPropertyId());
        validatePropertyAndRoomType(request.getPropertyId(), request.getRoomTypeId());

        HotelInventoryRecord inventory = hotelInventoryRecordMapper.findByUk(request.getPropertyId(), request.getRoomTypeId(), request.getBizDate());
        if (inventory == null) {
            inventory = new HotelInventoryRecord();
            inventory.setPropertyId(request.getPropertyId());
            inventory.setRoomTypeId(request.getRoomTypeId());
            inventory.setBizDate(request.getBizDate());
            inventory.setTotalInventory(0);
            inventory.setOccupiedInventory(0);
            inventory.setAvailableInventory(0);
            inventory.setWarningThreshold(2);
            inventory.setCreatedBy(SecurityUtils.currentUsername());
            inventory.setUpdatedBy(SecurityUtils.currentUsername());
            inventory.setDeleted(0);
            hotelInventoryRecordMapper.insert(inventory);
        }

        int newOccupied = inventory.getOccupiedInventory() + request.getOccupiedDelta();
        if (newOccupied < 0) {
            newOccupied = 0;
        }
        int newAvailable = inventory.getTotalInventory() - newOccupied;
        inventory.setOccupiedInventory(newOccupied);
        inventory.setAvailableInventory(newAvailable);
        inventory.setUpdatedBy(SecurityUtils.currentUsername());
        hotelInventoryRecordMapper.updateById(inventory);

        if (newAvailable <= inventory.getWarningThreshold()) {
            eventPublisher.publishInventoryWarning(BizEventVO.builder()
                    .eventType("INVENTORY_WARNING")
                    .propertyId(inventory.getPropertyId())
                    .bizId(inventory.getId())
                    .content("available inventory is low")
                    .build());
        }
    }

    @Override
    public List<InventoryVO> listInventories() {
        return hotelInventoryRecordMapper.findAll().stream()
                .filter(item -> canAccessProperty(item.getPropertyId()))
                .peek(item -> item.setWarning(item.getAvailableInventory() <= item.getWarningThreshold()))
                .toList();
    }

    private void validatePropertyAndRoomType(Long propertyId, Long roomTypeId) {
        HotelProperty property = hotelPropertyMapper.findById(propertyId);
        if (property == null) {
            throw new BusinessException("property not found");
        }
        HotelRoomType roomType = hotelRoomTypeMapper.findById(roomTypeId);
        if (roomType == null) {
            throw new BusinessException("room type not found");
        }
        if (!propertyId.equals(roomType.getPropertyId())) {
            throw new BusinessException("room type does not belong to property");
        }
    }

    private boolean canAccessProperty(Long propertyId) {
        if (propertyId == null) {
            return false;
        }
        return SecurityUtils.propertyScopes().contains(propertyId);
    }

    private String priceCacheKey(Long propertyId, Long roomTypeId, String bizDate) {
        return "pricing:plan:" + propertyId + ":" + roomTypeId + ":" + bizDate;
    }
}
