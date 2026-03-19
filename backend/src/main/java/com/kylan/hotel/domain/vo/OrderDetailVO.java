package com.kylan.hotel.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class OrderDetailVO {
    private OrderVO order;
    private List<ReservationVO> reservations;
    private List<StayRecordVO> stays;
    private List<GuestVO> guests;
    private List<PaymentRecordVO> payments;
    private List<AuditLogItemVO> auditLogs;
    private List<OperationLogItemVO> operationLogs;
    private List<RoomStatusTimelineVO> roomStatusTimeline;
}
