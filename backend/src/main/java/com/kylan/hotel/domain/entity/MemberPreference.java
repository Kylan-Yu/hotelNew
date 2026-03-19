package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

@Data
public class MemberPreference extends BaseEntity {
    private Long memberId;
    private String preferenceType;
    private String preferenceValue;
}
