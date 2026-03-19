package com.kylan.hotel.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kylan.hotel.common.BusinessException;
import com.kylan.hotel.domain.dto.OtaCallbackRequest;
import com.kylan.hotel.domain.entity.OtaCallbackRetryTask;
import com.kylan.hotel.domain.vo.OtaCallbackRetryMessage;
import com.kylan.hotel.service.OtaCallbackService;
import com.kylan.hotel.service.OtaCallbackRetryTaskService;
import com.kylan.hotel.service.OtaRetryPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OtaCallbackRetryConsumer {

    private final OtaCallbackRetryTaskService retryTaskService;
    private final OtaCallbackService otaCallbackService;
    private final OtaRetryPublisher otaRetryPublisher;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "hms.ota.callback.retry.process.queue")
    public void consumeRetry(OtaCallbackRetryMessage message) {
        OtaCallbackRetryTask task = retryTaskService.findById(message.getTaskId());
        if (task == null) {
            return;
        }
        if ("SUCCESS".equals(task.getTaskStatus()) || "FINAL_FAILED".equals(task.getTaskStatus())) {
            return;
        }
        if (task.getNextRetryTime() != null && task.getNextRetryTime().isAfter(LocalDateTime.now())) {
            long delaySeconds = java.time.Duration.between(LocalDateTime.now(), task.getNextRetryTime()).toSeconds();
            otaRetryPublisher.publishRetry(OtaCallbackRetryMessage.builder()
                    .taskId(task.getId())
                    .idempotentKey(task.getIdempotentKey())
                    .retryCount(task.getRetryCount())
                    .build(), Math.max(delaySeconds, 1));
            return;
        }

        try {
            OtaCallbackRequest request = new OtaCallbackRequest();
            request.setChannelCode(task.getChannelCode());
            request.setEventType(task.getEventType());
            request.setExternalRequestNo(task.getExternalRequestNo());
            request.setSignature(task.getSignature());
            request.setBody(task.getPayload());
            request.setHeaders(parseHeaders(task.getHeadersJson()));
            request.setRetryProcess(true);
            otaCallbackService.handleCallback(request);
            retryTaskService.markSuccess(task.getId(), task.getRetryCount());
        } catch (Exception ex) {
            int nextCount = (task.getRetryCount() == null ? 0 : task.getRetryCount()) + 1;
            if (nextCount >= (task.getMaxRetryCount() == null ? 3 : task.getMaxRetryCount())) {
                retryTaskService.markFinalFailed(task.getId(), nextCount, ex.getMessage());
                otaRetryPublisher.publishDeadLetter(OtaCallbackRetryMessage.builder()
                        .taskId(task.getId())
                        .idempotentKey(task.getIdempotentKey())
                        .retryCount(nextCount)
                        .build());
                log.error("ota callback retry final failed, taskId={}, error={}", task.getId(), ex.getMessage());
                return;
            }
            long delaySeconds = (long) Math.pow(2, nextCount) * 30;
            LocalDateTime nextRetryTime = LocalDateTime.now().plusSeconds(delaySeconds);
            retryTaskService.markRetrying(task.getId(), nextCount, ex.getMessage(), nextRetryTime);
            otaRetryPublisher.publishRetry(OtaCallbackRetryMessage.builder()
                    .taskId(task.getId())
                    .idempotentKey(task.getIdempotentKey())
                    .retryCount(nextCount)
                    .build(), delaySeconds);
        }
    }

    private Map<String, String> parseHeaders(String headersJson) {
        if (headersJson == null || headersJson.isBlank()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(headersJson, new TypeReference<Map<String, String>>() {
            });
        } catch (Exception ex) {
            throw new BusinessException("invalid retry headers json");
        }
    }
}
