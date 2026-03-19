package com.kylan.hotel.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberPointLedgerVO {
    private Long id;
    private Long memberId;
    private Long propertyId;
    private Integer pointDelta;
    private String bizType;
    private String bizNo;
    private String remark;
    private LocalDateTime createdAt;
}
