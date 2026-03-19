package com.kylan.hotel.controller;

import com.kylan.hotel.common.ApiResponse;
import com.kylan.hotel.domain.dto.OtaCallbackRequest;
import com.kylan.hotel.service.OtaCallbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ota/callback")
@RequiredArgsConstructor
public class OtaCallbackController {

    private final OtaCallbackService otaCallbackService;

    @PostMapping("/douyin")
    public ApiResponse<Void> douyinCallback(
            @RequestHeader(value = "X-Signature", required = false) String signature,
            @RequestHeader Map<String, String> headers,
            @RequestBody String body) {
        OtaCallbackRequest request = new OtaCallbackRequest();
        request.setChannelCode("DOUYIN");
        request.setSignature(signature);
        request.setEventType(firstHeader(headers, "X-Event-Type", "event-type", "event_type", "eventType"));
        request.setExternalRequestNo(firstHeader(headers, "X-Request-Id", "request-id", "request_id", "requestId"));
        request.setHeaders(headers);
        request.setBody(body);
        otaCallbackService.handleCallback(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/meituan")
    public ApiResponse<Void> meituanCallback(
            @RequestHeader(value = "X-Signature", required = false) String signature,
            @RequestHeader Map<String, String> headers,
            @RequestBody String body) {
        OtaCallbackRequest request = new OtaCallbackRequest();
        request.setChannelCode("MEITUAN");
        request.setSignature(signature);
        request.setEventType(firstHeader(headers, "X-Event-Type", "event-type", "event_type", "eventType"));
        request.setExternalRequestNo(firstHeader(headers, "X-Request-Id", "request-id", "request_id", "requestId"));
        request.setHeaders(headers);
        request.setBody(body);
        otaCallbackService.handleCallback(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/ctrip")
    public ApiResponse<Void> ctripCallback(
            @RequestHeader(value = "X-Signature", required = false) String signature,
            @RequestHeader Map<String, String> headers,
            @RequestBody String body) {
        OtaCallbackRequest request = new OtaCallbackRequest();
        request.setChannelCode("CTRIP");
        request.setSignature(signature);
        request.setEventType(firstHeader(headers, "X-Event-Type", "event-type", "event_type", "eventType"));
        request.setExternalRequestNo(firstHeader(headers, "X-Request-Id", "request-id", "request_id", "requestId"));
        request.setHeaders(headers);
        request.setBody(body);
        otaCallbackService.handleCallback(request);
        return ApiResponse.success(null);
    }

    private String firstHeader(Map<String, String> headers, String... names) {
        if (headers == null || headers.isEmpty()) {
            return null;
        }
        for (String name : names) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(name)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }
}
