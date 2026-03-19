package com.kylan.hotel.service.impl;

import com.kylan.hotel.common.BusinessException;
import com.kylan.hotel.common.RedisKeys;
import com.kylan.hotel.domain.dto.OtaCallbackRequest;
import com.kylan.hotel.domain.entity.ChannelCallbackLog;
import com.kylan.hotel.mapper.ChannelCallbackLogMapper;
import com.kylan.hotel.service.OtaCallbackService;
import com.kylan.hotel.service.OtaCallbackRetryTaskService;
import com.kylan.hotel.service.ota.ChannelAdapter;
import com.kylan.hotel.service.ota.sign.ChannelSignatureVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OtaCallbackServiceImpl implements OtaCallbackService {

    private static final long IDEMPOTENT_PROCESSING_SECONDS = 10 * 60;
    private static final long IDEMPOTENT_DONE_SECONDS = 24 * 60 * 60;

    private final List<ChannelSignatureVerifier> verifiers;
    private final List<ChannelAdapter> adapters;
    private final ChannelCallbackLogMapper channelCallbackLogMapper;
    private final OtaCallbackRetryTaskService otaCallbackRetryTaskService;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void handleCallback(OtaCallbackRequest request) {
        String channelCode = normalizeChannel(request.getChannelCode());
        String eventType = StringUtils.hasText(request.getEventType()) ? request.getEventType().toUpperCase() : "UNKNOWN";
        String externalRequestNo = StringUtils.hasText(request.getExternalRequestNo())
                ? request.getExternalRequestNo()
                : digestPayload(request.getBody());
        String idempotentKey = buildIdempotentKey(channelCode, eventType, externalRequestNo);
        String redisKey = RedisKeys.otaCallbackIdempotent(idempotentKey);

        ChannelCallbackLog existed = channelCallbackLogMapper.findByIdempotentKey(idempotentKey);
        if (existed != null && "SUCCESS".equals(existed.getCallbackStatus())) {
            return;
        }

        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent(
                redisKey, "PROCESSING", IDEMPOTENT_PROCESSING_SECONDS, TimeUnit.SECONDS
        );
        if (Boolean.FALSE.equals(lock)) {
            ChannelCallbackLog duplicate = channelCallbackLogMapper.findByIdempotentKey(idempotentKey);
            if (duplicate != null && "SUCCESS".equals(duplicate.getCallbackStatus())) {
                return;
            }
            throw new BusinessException("callback duplicated and being processed");
        }

        boolean verified = verifierMap().containsKey(channelCode) && verifierMap().get(channelCode).verify(request);
        ChannelCallbackLog log = new ChannelCallbackLog();
        log.setIdempotentKey(idempotentKey);
        log.setChannelCode(channelCode);
        log.setEventType(eventType);
        log.setExternalRequestNo(externalRequestNo);
        log.setSignature(request.getSignature());
        log.setPayload(request.getBody());

        if (!verified) {
            log.setCallbackStatus("REJECTED");
            log.setMessage("signature verify failed");
            log.setProcessedFlag("N");
            insertLogIgnoreDuplicate(log);
            throw new BusinessException("invalid callback signature");
        }

        if (!adapterMap().containsKey(channelCode)) {
            log.setCallbackStatus("FAILED");
            log.setMessage("channel adapter not found");
            log.setProcessedFlag("N");
            insertLogIgnoreDuplicate(log);
            throw new BusinessException("channel adapter not found");
        }

        try {
            adapterMap().get(channelCode).handleCallback(request.getBody());
            log.setCallbackStatus("SUCCESS");
            log.setMessage("accepted");
            log.setProcessedFlag("Y");
            insertLogIgnoreDuplicate(log);
            stringRedisTemplate.opsForValue().set(redisKey, "DONE", IDEMPOTENT_DONE_SECONDS, TimeUnit.SECONDS);
        } catch (RuntimeException ex) {
            log.setCallbackStatus("FAILED");
            log.setMessage(ex.getMessage());
            log.setProcessedFlag("N");
            insertLogIgnoreDuplicate(log);
            if (!Boolean.TRUE.equals(request.getRetryProcess())) {
                scheduleRetryTask(request, idempotentKey, ex.getMessage());
            }
            throw ex;
        }
    }

    private void scheduleRetryTask(OtaCallbackRequest request, String idempotentKey, String error) {
        otaCallbackRetryTaskService.createTaskIfAbsent(request, idempotentKey, error);
    }

    private void insertLogIgnoreDuplicate(ChannelCallbackLog log) {
        try {
            channelCallbackLogMapper.insert(log);
        } catch (DuplicateKeyException ignore) {
            // ignore duplicate callback insertion, idempotent key already recorded
        }
    }

    private String buildIdempotentKey(String channelCode, String eventType, String externalRequestNo) {
        return channelCode + ":" + eventType + ":" + externalRequestNo;
    }

    private String normalizeChannel(String channelCode) {
        if (!StringUtils.hasText(channelCode)) {
            throw new BusinessException("channelCode is required");
        }
        return channelCode.toUpperCase();
    }

    private String digestPayload(String payload) {
        if (!StringUtils.hasText(payload)) {
            return "EMPTY";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte value : bytes) {
                builder.append(String.format("%02x", value));
            }
            return builder.substring(0, 32);
        } catch (NoSuchAlgorithmException ex) {
            return String.valueOf(payload.hashCode());
        }
    }

    private Map<String, ChannelSignatureVerifier> verifierMap() {
        return verifiers.stream().collect(Collectors.toMap(ChannelSignatureVerifier::channelCode, Function.identity(), (a, b) -> a));
    }

    private Map<String, ChannelAdapter> adapterMap() {
        return adapters.stream().collect(Collectors.toMap(ChannelAdapter::channelCode, Function.identity(), (a, b) -> a));
    }
}
