package com.kylan.hotel.service.impl;

import com.kylan.hotel.common.BusinessException;
import com.kylan.hotel.common.SecurityUtils;
import com.kylan.hotel.domain.dto.ChannelMappingCreateRequest;
import com.kylan.hotel.domain.dto.OtaSyncRequest;
import com.kylan.hotel.domain.entity.ChannelCallbackLog;
import com.kylan.hotel.domain.entity.ChannelMapping;
import com.kylan.hotel.domain.entity.ChannelSyncLog;
import com.kylan.hotel.domain.vo.ChannelMappingVO;
import com.kylan.hotel.mapper.ChannelCallbackLogMapper;
import com.kylan.hotel.mapper.ChannelMappingMapper;
import com.kylan.hotel.mapper.ChannelSyncLogMapper;
import com.kylan.hotel.service.OtaChannelService;
import com.kylan.hotel.service.ota.ChannelAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OtaChannelServiceImpl implements OtaChannelService {

    private final ChannelMappingMapper channelMappingMapper;
    private final ChannelSyncLogMapper channelSyncLogMapper;
    private final ChannelCallbackLogMapper channelCallbackLogMapper;
    private final List<ChannelAdapter> adapters;

    @Override
    public Long createMapping(ChannelMappingCreateRequest request) {
        SecurityUtils.assertPropertyAccessible(request.getPropertyId());
        ChannelMapping entity = new ChannelMapping();
        entity.setPropertyId(request.getPropertyId());
        entity.setChannelCode(request.getChannelCode());
        entity.setMappingType(request.getMappingType());
        entity.setLocalBizId(request.getLocalBizId());
        entity.setChannelBizId(request.getChannelBizId());
        entity.setRemark(request.getRemark());
        entity.setCreatedBy(SecurityUtils.currentUsername());
        entity.setUpdatedBy(SecurityUtils.currentUsername());
        entity.setDeleted(0);
        channelMappingMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public List<ChannelMappingVO> listMappings() {
        return channelMappingMapper.findAll().stream()
                .filter(item -> canAccessProperty(item.getPropertyId()))
                .toList();
    }

    @Override
    public void pushInventory(OtaSyncRequest request) {
        String channelCode = resolveChannelCode(request);
        try {
            adapterMap().get(channelCode).pushInventory(request);
            insertSyncLog(request, channelCode, "INVENTORY", "SUCCESS", null);
        } catch (RuntimeException ex) {
            insertSyncLog(request, channelCode, "INVENTORY", "FAILED", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void pushPrice(OtaSyncRequest request) {
        String channelCode = resolveChannelCode(request);
        try {
            adapterMap().get(channelCode).pushPrice(request);
            insertSyncLog(request, channelCode, "PRICE", "SUCCESS", null);
        } catch (RuntimeException ex) {
            insertSyncLog(request, channelCode, "PRICE", "FAILED", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void pullOrders(OtaSyncRequest request) {
        String channelCode = resolveChannelCode(request);
        try {
            adapterMap().get(channelCode).pullOrders(request);
            insertSyncLog(request, channelCode, "ORDER", "SUCCESS", null);
        } catch (RuntimeException ex) {
            insertSyncLog(request, channelCode, "ORDER", "FAILED", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void handleCallback(String channelCode, String payload) {
        adapterMap().get(channelCode.toUpperCase()).handleCallback(payload);
    }

    @Override
    public List<ChannelSyncLog> listSyncLogs() {
        return channelSyncLogMapper.findAll();
    }

    @Override
    public List<ChannelCallbackLog> listCallbackLogs() {
        return channelCallbackLogMapper.findAll();
    }

    private String resolveChannelCode(OtaSyncRequest request) {
        if (request.getPropertyId() != null) {
            SecurityUtils.assertPropertyAccessible(request.getPropertyId());
        }
        if (request.getChannelCode() == null || request.getChannelCode().isBlank()) {
            throw new BusinessException("channelCode is required");
        }
        String channelCode = request.getChannelCode().toUpperCase();
        if (!adapterMap().containsKey(channelCode)) {
            throw new BusinessException("unsupported channelCode");
        }
        return channelCode;
    }

    private Map<String, ChannelAdapter> adapterMap() {
        return adapters.stream().collect(Collectors.toMap(ChannelAdapter::channelCode, Function.identity(), (a, b) -> a));
    }

    private boolean canAccessProperty(Long propertyId) {
        if (propertyId == null) {
            return false;
        }
        return SecurityUtils.propertyScopes().contains(propertyId);
    }

    private void insertSyncLog(OtaSyncRequest request, String channelCode, String bizType, String status, String response) {
        ChannelSyncLog log = new ChannelSyncLog();
        log.setPropertyId(request.getPropertyId());
        log.setChannelCode(channelCode);
        log.setBizType(bizType);
        log.setBizId(String.valueOf(request.getPropertyId() == null ? 0L : request.getPropertyId()));
        log.setIdempotentKey(channelCode + ":" + bizType + ":" + UUID.randomUUID());
        log.setSyncStatus(status);
        log.setRequestPayload(String.valueOf(request));
        log.setResponsePayload(response);
        channelSyncLogMapper.insert(log);
    }
}
