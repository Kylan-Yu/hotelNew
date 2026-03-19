package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

@Data
public class MemberPointLedger extends BaseEntity {
    private Long memberId;
    private Integer pointDelta;
    private String bizType;
    private String bizNo;
    private String remark;
}
