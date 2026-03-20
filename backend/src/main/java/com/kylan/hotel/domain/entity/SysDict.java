package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

@Data
public class SysDict extends BaseEntity {
    private String dictCode;
    private String dictName;
    private Integer status;
    private Integer sortNo;
    private String remark;
}
