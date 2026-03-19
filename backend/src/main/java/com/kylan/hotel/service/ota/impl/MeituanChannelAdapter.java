package com.kylan.hotel.service.ota.impl;

import com.kylan.hotel.domain.dto.OtaSyncRequest;
import com.kylan.hotel.service.ota.ChannelAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MeituanChannelAdapter implements ChannelAdapter {
    @Override
    public String channelCode() {
        return "MEITUAN";
    }

    @Override
    public void pushInventory(OtaSyncRequest request) {
        log.info("[MEITUAN] push inventory: {}", request);
    }

    @Override
    public void pushPrice(OtaSyncRequest request) {
        log.info("[MEITUAN] push price: {}", request);
    }

    @Override
    public void pullOrders(OtaSyncRequest request) {
        log.info("[MEITUAN] pull orders: {}", request);
    }

    @Override
    public void handleCallback(String payload) {
        log.info("[MEITUAN] callback payload: {}", payload);
    }
}
