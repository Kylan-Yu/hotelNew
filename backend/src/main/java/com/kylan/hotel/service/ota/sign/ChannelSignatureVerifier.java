package com.kylan.hotel.service.ota.sign;

import com.kylan.hotel.domain.dto.OtaCallbackRequest;

public interface ChannelSignatureVerifier {
    String channelCode();

    boolean verify(OtaCallbackRequest request);
}
