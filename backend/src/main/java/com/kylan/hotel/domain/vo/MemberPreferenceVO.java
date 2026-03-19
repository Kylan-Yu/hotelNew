package com.kylan.hotel.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberPreferenceVO {
    private Long id;
    private Long memberId;
    private Long propertyId;
    private String preferenceType;
    private String preferenceValue;
    private LocalDateTime createdAt;
}
