package com.kylan.hotel.domain.vo;

import lombok.Data;

@Data
public class ChannelMappingVO {
    private Long id;
    private Long propertyId;
    private String propertyName;
    private String channelCode;
    private String mappingType;
    private Long localBizId;
    private String channelBizId;
    private String remark;
}
