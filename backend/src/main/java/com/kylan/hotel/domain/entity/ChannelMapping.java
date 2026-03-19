package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

@Data
public class ChannelMapping extends BaseEntity {
    private Long propertyId;
    private String channelCode;
    private String mappingType;
    private Long localBizId;
    private String channelBizId;
    private String remark;
}
