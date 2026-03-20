package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

@Data
public class SysDictItem extends BaseEntity {
    private String dictCode;
    private String itemCode;
    private String itemName;
    private String itemValue;
    private Integer status;
    private Integer sortNo;
    private String remark;
}
