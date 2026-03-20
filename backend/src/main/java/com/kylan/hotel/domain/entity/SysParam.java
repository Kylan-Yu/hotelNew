package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

@Data
public class SysParam extends BaseEntity {
    private String paramKey;
    private String paramValue;
    private String remark;
}
