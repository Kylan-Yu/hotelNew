package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChannelMappingCreateRequest {
    @NotNull(message = "propertyId is required")
    private Long propertyId;

    @NotBlank(message = "channelCode is required")
    private String channelCode;

    @NotBlank(message = "mappingType is required")
    private String mappingType;

    @NotNull(message = "localBizId is required")
    private Long localBizId;

    @NotBlank(message = "channelBizId is required")
    private String channelBizId;

    private String remark;
}
