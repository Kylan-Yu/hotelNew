package com.kylan.hotel.domain.vo;

import lombok.Data;

@Data
public class MemberVO {
    private Long id;
    private Long propertyId;
    private String propertyName;
    private String memberNo;
    private String memberName;
    private String mobile;
    private Integer levelCode;
    private Integer pointBalance;
    private String status;
}
