package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HousekeepingTask extends BaseEntity {
    private Long propertyId;
    private Long roomId;
    private LocalDate bizDate;
    private String taskStatus;
    private String assignee;
    private String remark;
}
