package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotNull;
public class CheckoutRequest {
    @NotNull(message = "stayRecordId is required")
    private Long stayRecordId;

    private String remark;

    public Long getStayRecordId() {
        return stayRecordId;
    }

    public void setStayRecordId(Long stayRecordId) {
        this.stayRecordId = stayRecordId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
