package com.kylan.hotel.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kylan.hotel.domain.dto.OtaCallbackRequest;
import com.kylan.hotel.domain.entity.OtaCallbackRetryTask;
import com.kylan.hotel.mapper.OtaCallbackRetryTaskMapper;
import com.kylan.hotel.service.OtaCallbackRetryTaskService;
import com.kylan.hotel.service.OtaRetryPublisher;
import com.kylan.hotel.domain.vo.OtaCallbackRetryMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtaCallbackRetryTaskServiceImpl implements OtaCallbackRetryTaskService {

    private final OtaCallbackRetryTaskMapper retryTaskMapper;
    private final OtaRetryPublisher otaRetryPublisher;
    private final ObjectMapper objectMapper;

    @Override
    public OtaCallbackRetryTask findById(Long id) {
        return retryTaskMapper.findById(id);
    }

    @Override
    public OtaCallbackRetryTask findByIdempotentKey(String idempotentKey) {
        return retryTaskMapper.findByIdempotentKey(idempotentKey);
    }

    @Override
    public OtaCallbackRetryTask createTaskIfAbsent(OtaCallbackRequest request, String idempotentKey, String errorMessage) {
        OtaCallbackRetryTask existing = retryTaskMapper.findByIdempotentKey(idempotentKey);
        if (existing != null && ("PENDING".equals(existing.getTaskStatus()) || "RETRYING".equals(existing.getTaskStatus()))) {
            return existing;
        }
        OtaCallbackRetryTask task = new OtaCallbackRetryTask();
        task.setIdempotentKey(idempotentKey);
        task.setChannelCode(request.getChannelCode() == null ? null : request.getChannelCode().toUpperCase());
        task.setEventType(StringUtils.hasText(request.getEventType()) ? request.getEventType().toUpperCase() : "UNKNOWN");
        task.setExternalRequestNo(request.getExternalRequestNo());
        task.setSignature(request.getSignature());
        task.setHeadersJson(toJson(request.getHeaders()));
        task.setPayload(request.getBody());
        task.setTaskStatus("PENDING");
        task.setRetryCount(0);
        task.setMaxRetryCount(5);
        task.setLastError(errorMessage);
        task.setNextRetryTime(LocalDateTime.now().plusSeconds(30));
        retryTaskMapper.insert(task);
        otaRetryPublisher.publishRetry(OtaCallbackRetryMessage.builder()
                .taskId(task.getId())
                .idempotentKey(task.getIdempotentKey())
                .retryCount(0)
                .build(), 30);
        return task;
    }

    @Override
    public void markSuccess(Long taskId, Integer retryCount) {
        retryTaskMapper.updateProgress(taskId, "SUCCESS", retryCount, null, null);
    }

    @Override
    public void markRetrying(Long taskId, Integer retryCount, String errorMessage, LocalDateTime nextRetryTime) {
        retryTaskMapper.updateProgress(taskId, "RETRYING", retryCount, errorMessage, nextRetryTime);
    }

    @Override
    public void markFinalFailed(Long taskId, Integer retryCount, String errorMessage) {
        retryTaskMapper.updateProgress(taskId, "FINAL_FAILED", retryCount, errorMessage, null);
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            return "{}";
        }
    }
}
