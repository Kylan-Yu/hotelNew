package com.kylan.hotel.domain.vo;

import lombok.Data;

@Data
public class SystemDictVO {
    private String dictCode;
    private String dictName;
    private Integer status;
    private Integer sortNo;
    private Integer itemCount;
    private String remark;
}
