package com.kylan.hotel.service.ota.impl;

import com.kylan.hotel.domain.dto.OtaSyncRequest;
import com.kylan.hotel.service.ota.ChannelAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CtripChannelAdapter implements ChannelAdapter {
    @Override
    public String channelCode() {
        return "CTRIP";
    }

    @Override
    public void pushInventory(OtaSyncRequest request) {
        log.info("[CTRIP] push inventory: {}", request);
    }

    @Override
    public void pushPrice(OtaSyncRequest request) {
        log.info("[CTRIP] push price: {}", request);
    }

    @Override
    public void pullOrders(OtaSyncRequest request) {
        log.info("[CTRIP] pull orders: {}", request);
    }

    @Override
    public void handleCallback(String payload) {
        log.info("[CTRIP] callback payload: {}", payload);
    }
}
