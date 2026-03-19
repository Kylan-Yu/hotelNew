package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GuestRegisterRequest {
    @NotNull(message = "orderId is required")
    private Long orderId;

    @NotBlank(message = "guestName is required")
    private String guestName;

    @NotBlank(message = "guestMobile is required")
    private String guestMobile;

    private String certificateType;
    private String certificateNo;
    private Boolean primaryGuest;
}
