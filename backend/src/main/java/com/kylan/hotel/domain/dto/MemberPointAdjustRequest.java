package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MemberPointAdjustRequest {
    @NotNull(message = "memberId is required")
    private Long memberId;

    @NotNull(message = "pointDelta is required")
    private Integer pointDelta;

    private String bizType;
    private String bizNo;
    private String remark;
}
