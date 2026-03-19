package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

@Data
public class HotelGuestProfile extends BaseEntity {
    private Long orderId;
    private Long stayRecordId;
    private String guestName;
    private String guestMobile;
    private String certificateType;
    private String certificateNo;
    private String isPrimary;
}
