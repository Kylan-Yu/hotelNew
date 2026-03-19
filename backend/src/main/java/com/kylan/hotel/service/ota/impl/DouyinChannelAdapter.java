package com.kylan.hotel.service.ota.impl;

import com.kylan.hotel.domain.dto.OtaSyncRequest;
import com.kylan.hotel.service.ota.ChannelAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DouyinChannelAdapter implements ChannelAdapter {
    @Override
    public String channelCode() {
        return "DOUYIN";
    }

    @Override
    public void pushInventory(OtaSyncRequest request) {
        log.info("[DOUYIN] push inventory: {}", request);
    }

    @Override
    public void pushPrice(OtaSyncRequest request) {
        log.info("[DOUYIN] push price: {}", request);
    }

    @Override
    public void pullOrders(OtaSyncRequest request) {
        log.info("[DOUYIN] pull orders: {}", request);
    }

    @Override
    public void handleCallback(String payload) {
        log.info("[DOUYIN] callback payload: {}", payload);
    }
}
