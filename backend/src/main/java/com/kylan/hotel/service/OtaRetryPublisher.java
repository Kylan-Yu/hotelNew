package com.kylan.hotel.service;

import com.kylan.hotel.domain.vo.OtaCallbackRetryMessage;

public interface OtaRetryPublisher {
    void publishRetry(OtaCallbackRetryMessage message, long delaySeconds);

    void publishDeadLetter(OtaCallbackRetryMessage message);
}
