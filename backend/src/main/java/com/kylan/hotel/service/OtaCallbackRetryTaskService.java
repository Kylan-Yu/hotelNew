package com.kylan.hotel.service;

import com.kylan.hotel.domain.dto.OtaCallbackRequest;
import com.kylan.hotel.domain.entity.OtaCallbackRetryTask;

import java.time.LocalDateTime;

public interface OtaCallbackRetryTaskService {
    OtaCallbackRetryTask findById(Long id);

    OtaCallbackRetryTask findByIdempotentKey(String idempotentKey);

    OtaCallbackRetryTask createTaskIfAbsent(OtaCallbackRequest request, String idempotentKey, String errorMessage);

    void markSuccess(Long taskId, Integer retryCount);

    void markRetrying(Long taskId, Integer retryCount, String errorMessage, LocalDateTime nextRetryTime);

    void markFinalFailed(Long taskId, Integer retryCount, String errorMessage);
}
