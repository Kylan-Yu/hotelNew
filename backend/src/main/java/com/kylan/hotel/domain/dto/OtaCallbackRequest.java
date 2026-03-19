package com.kylan.hotel.domain.dto;

import java.util.Map;

public class OtaCallbackRequest {
    private String channelCode;
    private String eventType;
    private String externalRequestNo;
    private Boolean retryProcess;
    private String signature;
    private String timestamp;
    private String nonce;
    private String body;
    private Map<String, String> headers;

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getExternalRequestNo() {
        return externalRequestNo;
    }

    public void setExternalRequestNo(String externalRequestNo) {
        this.externalRequestNo = externalRequestNo;
    }

    public Boolean getRetryProcess() {
        return retryProcess;
    }

    public void setRetryProcess(Boolean retryProcess) {
        this.retryProcess = retryProcess;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
