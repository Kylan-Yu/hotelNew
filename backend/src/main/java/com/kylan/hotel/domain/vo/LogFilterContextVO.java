package com.kylan.hotel.domain.vo;

import lombok.Data;

@Data
public class LogFilterContextVO {
    private Long currentPropertyId;
    private Long currentBrandId;
    private Long currentGroupId;
    private Boolean allowWideRangeSwitch;
}
