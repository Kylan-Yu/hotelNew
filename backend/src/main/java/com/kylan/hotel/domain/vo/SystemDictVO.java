package com.kylan.hotel.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemDictVO {
    private String dictCode;
    private String dictName;
    private String remark;
}
