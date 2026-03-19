package com.kylan.hotel.domain.entity;

import com.kylan.hotel.common.BaseEntity;
import lombok.Data;

@Data
public class MaintenanceTicket extends BaseEntity {
    private Long propertyId;
    private Long roomId;
    private String issueType;
    private String issueDescription;
    private String ticketStatus;
    private String assignee;
}
