package com.kylan.hotel.service;

import com.kylan.hotel.domain.dto.ChannelMappingCreateRequest;
import com.kylan.hotel.domain.dto.OtaSyncRequest;
import com.kylan.hotel.domain.entity.ChannelCallbackLog;
import com.kylan.hotel.domain.entity.ChannelSyncLog;
import com.kylan.hotel.domain.vo.ChannelMappingVO;

import java.util.List;

public interface OtaChannelService {
    Long createMapping(ChannelMappingCreateRequest request);

    List<ChannelMappingVO> listMappings();

    void pushInventory(OtaSyncRequest request);

    void pushPrice(OtaSyncRequest request);

    void pullOrders(OtaSyncRequest request);

    void handleCallback(String channelCode, String payload);

    List<ChannelSyncLog> listSyncLogs();

    List<ChannelCallbackLog> listCallbackLogs();
}
