package com.kylan.hotel.service.ota.sign.impl;

import com.kylan.hotel.domain.dto.OtaCallbackRequest;
import com.kylan.hotel.service.ota.sign.ChannelSignatureVerifier;
import org.springframework.stereotype.Component;

@Component
public class MeituanSignatureVerifier implements ChannelSignatureVerifier {
    @Override
    public String channelCode() {
        return "MEITUAN";
    }

    @Override
    public boolean verify(OtaCallbackRequest request) {
        return request.getSignature() != null && !request.getSignature().isBlank();
    }
}
