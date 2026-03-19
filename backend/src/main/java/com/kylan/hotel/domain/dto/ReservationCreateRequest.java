package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReservationCreateRequest {
    @NotNull(message = "propertyId is required")
    private Long propertyId;

    @NotNull(message = "roomTypeId is required")
    private Long roomTypeId;

    @NotBlank(message = "contactName is required")
    private String contactName;

    @NotBlank(message = "contactMobile is required")
    private String contactMobile;

    @NotNull(message = "guestCount is required")
    private Integer guestCount;

    @NotNull(message = "checkInDate is required")
    private LocalDate checkInDate;

    @NotNull(message = "checkOutDate is required")
    private LocalDate checkOutDate;

    private String channelCode;
    private BigDecimal estimatedAmount;
    private String remark;
}
