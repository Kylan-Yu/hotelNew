package com.kylan.hotel.service;

import com.kylan.hotel.domain.dto.OtaCallbackRequest;

public interface OtaCallbackService {
    void handleCallback(OtaCallbackRequest request);
}
