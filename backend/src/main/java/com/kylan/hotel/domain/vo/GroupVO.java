package com.kylan.hotel.domain.vo;

import lombok.Data;

@Data
public class GroupVO {
    private Long id;
    private String groupCode;
    private String groupName;
    private Integer status;
}
