package com.kylan.hotel.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemParamVO {
    private String paramKey;
    private String paramValue;
    private String remark;
}
