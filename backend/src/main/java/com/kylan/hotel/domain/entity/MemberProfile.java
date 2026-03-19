package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

@Data
public class MemberProfile extends BaseEntity {
    private Long propertyId;
    private String memberNo;
    private String memberName;
    private String mobile;
    private Integer levelCode;
    private Integer pointBalance;
    private String status;
}
