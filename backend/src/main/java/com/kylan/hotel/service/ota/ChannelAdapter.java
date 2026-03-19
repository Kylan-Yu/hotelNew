package com.kylan.hotel.service.ota;

import com.kylan.hotel.domain.dto.OtaSyncRequest;

public interface ChannelAdapter {
    String channelCode();

    void pushInventory(OtaSyncRequest request);

    void pushPrice(OtaSyncRequest request);

    void pullOrders(OtaSyncRequest request);

    void handleCallback(String payload);
}
