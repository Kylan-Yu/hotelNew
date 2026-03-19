package com.kylan.hotel.domain.vo;

import lombok.Data;

@Data
public class GuestVO {
    private Long id;
    private String guestName;
    private String guestMobile;
    private String certificateType;
    private String certificateNo;
    private String isPrimary;
}
