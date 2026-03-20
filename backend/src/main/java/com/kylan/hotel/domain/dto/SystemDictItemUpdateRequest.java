package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemDictItemUpdateRequest {

    @NotBlank(message = "itemName is required")
    private String itemName;

    private String itemValue;

    private Integer status;

    private Integer sortNo;

    private String remark;
}
