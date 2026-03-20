package com.kylan.hotel.domain.vo;

import lombok.Data;

@Data
public class SystemDictItemVO {
    private Long id;
    private String dictCode;
    private String itemCode;
    private String itemName;
    private String itemValue;
    private Integer status;
    private Integer sortNo;
    private String remark;
}
