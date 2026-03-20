package com.kylan.hotel.service.impl;

import com.kylan.hotel.common.*;
import com.kylan.hotel.domain.dto.*;
import com.kylan.hotel.domain.entity.*;
import com.kylan.hotel.domain.vo.AuditLogItemVO;
import com.kylan.hotel.domain.vo.BizEventVO;
import com.kylan.hotel.domain.vo.OrderDetailVO;
import com.kylan.hotel.domain.vo.OperationLogItemVO;
import com.kylan.hotel.domain.vo.OrderVO;
import com.kylan.hotel.domain.vo.PaymentRecordVO;
import com.kylan.hotel.domain.vo.ReservationVO;
import com.kylan.hotel.domain.vo.RoomStatusTimelineVO;
import com.kylan.hotel.domain.vo.StayRecordVO;
import com.kylan.hotel.mapper.*;
import com.kylan.hotel.service.OrderStayService;
import com.kylan.hotel.service.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderStayServiceImpl implements OrderStayService {

    private final HotelReservationMapper hotelReservationMapper;
    private final HotelOrderMapper hotelOrderMapper;
    private final HotelStayRecordMapper hotelStayRecordMapper;
    private final HotelGuestProfileMapper hotelGuestProfileMapper;
    private final HotelPaymentRecordMapper hotelPaymentRecordMapper;
    private final HotelRoomMapper hotelRoomMapper;
    private final HotelRoomTypeMapper hotelRoomTypeMapper;
    private final HotelPropertyMapper hotelPropertyMapper;
    private final HotelRoomStatusLogMapper hotelRoomStatusLogMapper;
    private final AuditLogRecordMapper auditLogRecordMapper;
    private final OperationLogRecordMapper operationLogRecordMapper;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReservation(ReservationCreateRequest request) {
        SecurityUtils.assertPropertyAccessible(request.getPropertyId());
        validatePropertyAndRoomType(request.getPropertyId(), request.getRoomTypeId());

        HotelReservation entity = new HotelReservation();
        entity.setReservationNo("RSV" + System.currentTimeMillis());
        entity.setPropertyId(request.getPropertyId());
        entity.setRoomTypeId(request.getRoomTypeId());
        entity.setChannelCode(request.getChannelCode() == null ? "DIRECT" : request.getChannelCode());
        entity.setContactName(request.getContactName());
        entity.setContactMobile(request.getContactMobile());
        entity.setGuestCount(request.getGuestCount());
        entity.setCheckInDate(request.getCheckInDate());
        entity.setCheckOutDate(request.getCheckOutDate());
        entity.setReservationStatus(OrderStatus.PENDING_CONFIRM);
        entity.setEstimatedAmount(request.getEstimatedAmount() == null ? BigDecimal.ZERO : request.getEstimatedAmount());
        entity.setRemark(request.getRemark());
        entity.setCreatedBy(SecurityUtils.currentUsername());
        entity.setUpdatedBy(SecurityUtils.currentUsername());
        entity.setDeleted(0);
        hotelReservationMapper.insert(entity);
        AuditLogRecord reservationLog = new AuditLogRecord();
        reservationLog.setModuleCode("RESERVATION");
        reservationLog.setBizNo(String.valueOf(entity.getId()));
        reservationLog.setActionType("CREATE");
        reservationLog.setContent("reservation created");
        reservationLog.setOperator(SecurityUtils.currentUsername());
        reservationLog.setPropertyId(entity.getPropertyId());
        HotelProperty property = hotelPropertyMapper.findById(entity.getPropertyId());
        if (property != null) {
            reservationLog.setBrandId(property.getBrandId());
            reservationLog.setGroupId(property.getGroupId());
        }
        auditLogRecordMapper.insert(reservationLog);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrderByReservation(Long reservationId) {
        HotelReservation reservation = hotelReservationMapper.findById(reservationId);
        if (reservation == null) {
            throw new BusinessException("reservation not found");
        }
        SecurityUtils.assertPropertyAccessible(reservation.getPropertyId());

        HotelOrderExt order = new HotelOrderExt();
        order.setOrderNo("ORD" + System.currentTimeMillis());
        order.setPropertyId(reservation.getPropertyId());
        order.setRoomTypeId(reservation.getRoomTypeId());
        order.setSourceChannel(reservation.getChannelCode());
        order.setGuestName(reservation.getContactName());
        order.setGuestMobile(reservation.getContactMobile());
        order.setCheckInDate(reservation.getCheckInDate());
        order.setCheckOutDate(reservation.getCheckOutDate());
        order.setTotalAmount(reservation.getEstimatedAmount());
        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setChannelOrderNo(null);
        order.setCreatedBy(SecurityUtils.currentUsername());
        order.setUpdatedBy(SecurityUtils.currentUsername());
        order.setDeleted(0);
        hotelOrderMapper.insert(order);

        hotelReservationMapper.updateStatus(reservationId, OrderStatus.CONFIRMED, SecurityUtils.currentUsername());
        writeOrderAudit(order.getId(), "GENERATE_ORDER", "generate order from reservation");
        eventPublisher.publishOrderCreated(BizEventVO.builder()
                .eventType("ORDER_CREATED")
                .propertyId(order.getPropertyId())
                .bizId(order.getId())
                .content("order created from reservation")
                .build());
        return order.getId();
    }

    @Override
    public List<ReservationVO> listReservations() {
        return hotelReservationMapper.findAll().stream()
                .filter(item -> canAccessProperty(item.getPropertyId()))
                .toList();
    }

    @Override
    public List<OrderVO> listOrders() {
        return hotelOrderMapper.findAll().stream()
                .filter(item -> canAccessProperty(item.getPropertyId()))
                .toList();
    }

    @Override
    public OrderDetailVO orderDetail(Long orderId) {
        HotelOrderExt orderEntity = hotelOrderMapper.findById(orderId);
        if (orderEntity == null) {
            throw new BusinessException("order not found");
        }
        SecurityUtils.assertPropertyAccessible(orderEntity.getPropertyId());

        OrderVO order = hotelOrderMapper.findVOById(orderId);
        List<StayRecordVO> stays = hotelStayRecordMapper.findByOrderId(orderId);
        List<PaymentRecordVO> payments = hotelPaymentRecordMapper.findByOrderId(orderId);
        List<AuditLogItemVO> auditLogs = auditLogRecordMapper.findByBiz("ORDER", String.valueOf(orderId));
        List<OperationLogItemVO> operationLogs = operationLogRecordMapper.findByProperty(orderEntity.getPropertyId(), "ORDERS");
        List<RoomStatusTimelineVO> timeline = roomStatusTimeline(orderId);

        OrderDetailVO detail = new OrderDetailVO();
        detail.setOrder(order);
        detail.setReservations(List.of());
        detail.setStays(stays);
        detail.setGuests(hotelGuestProfileMapper.findByOrderId(orderId));
        detail.setPayments(payments);
        detail.setAuditLogs(auditLogs);
        detail.setOperationLogs(operationLogs);
        detail.setRoomStatusTimeline(timeline);
        return detail;
    }

    @Override
    public List<StayRecordVO> listStayRecords() {
        return hotelStayRecordMapper.findAll().stream()
                .filter(item -> canAccessProperty(item.getPropertyId()))
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long checkIn(CheckInRequest request) {
        HotelOrderExt order = hotelOrderMapper.findById(request.getOrderId());
        if (order == null) {
            throw new BusinessException("order not found");
        }
        SecurityUtils.assertPropertyAccessible(order.getPropertyId());

        HotelRoom room = hotelRoomMapper.findById(request.getRoomId());
        if (room == null) {
            throw new BusinessException("room not found");
        }
        if (!order.getPropertyId().equals(room.getPropertyId())) {
            throw new BusinessException("room does not belong to order property");
        }

        HotelStayRecord stayRecord = new HotelStayRecord();
        stayRecord.setStayNo("STAY" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        stayRecord.setOrderId(order.getId());
        stayRecord.setReservationId(null);
        stayRecord.setPropertyId(order.getPropertyId());
        stayRecord.setRoomId(request.getRoomId());
        stayRecord.setStayType(request.getStayType());
        stayRecord.setStayStatus("IN_HOUSE");
        stayRecord.setCheckInDate(request.getCheckInDate());
        stayRecord.setCheckOutDate(request.getCheckOutDate());
        stayRecord.setActualCheckInTime(LocalDateTime.now());
        stayRecord.setCreatedBy(SecurityUtils.currentUsername());
        stayRecord.setUpdatedBy(SecurityUtils.currentUsername());
        stayRecord.setDeleted(0);
        hotelStayRecordMapper.insert(stayRecord);

        hotelOrderMapper.updateStatus(order.getId(), OrderStatus.CHECKED_IN, SecurityUtils.currentUsername());
        updateRoomStatusWithLog(room.getId(), room.getStatus(), RoomStatus.OCCUPIED, "check in");
        writeOrderAudit(order.getId(), "CHECK_IN", "check in completed, room=" + room.getRoomNo());
        return stayRecord.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void extendStay(ExtendStayRequest request) {
        HotelStayRecord stayRecord = hotelStayRecordMapper.findById(request.getStayRecordId());
        if (stayRecord == null) {
            throw new BusinessException("stay record not found");
        }
        SecurityUtils.assertPropertyAccessible(stayRecord.getPropertyId());
        if (request.getNewCheckOutDate().isBefore(stayRecord.getCheckOutDate())) {
            throw new BusinessException("new checkOutDate must be greater than old one");
        }
        hotelStayRecordMapper.extendStay(stayRecord.getId(), request.getNewCheckOutDate(), SecurityUtils.currentUsername());
        writeOrderAudit(stayRecord.getOrderId(), "EXTEND_STAY", "extend stay to " + request.getNewCheckOutDate());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeRoom(ChangeRoomRequest request) {
        HotelStayRecord stayRecord = hotelStayRecordMapper.findById(request.getStayRecordId());
        if (stayRecord == null) {
            throw new BusinessException("stay record not found");
        }
        SecurityUtils.assertPropertyAccessible(stayRecord.getPropertyId());

        HotelRoom oldRoom = hotelRoomMapper.findById(stayRecord.getRoomId());
        HotelRoom newRoom = hotelRoomMapper.findById(request.getNewRoomId());
        if (newRoom == null) {
            throw new BusinessException("new room not found");
        }
        if (!stayRecord.getPropertyId().equals(newRoom.getPropertyId())) {
            throw new BusinessException("new room does not belong to same property");
        }

        hotelStayRecordMapper.changeRoom(stayRecord.getId(), newRoom.getId(), SecurityUtils.currentUsername());
        if (oldRoom != null) {
            updateRoomStatusWithLog(oldRoom.getId(), oldRoom.getStatus(), RoomStatus.VACANT_DIRTY, "change room - old room");
        }
        updateRoomStatusWithLog(newRoom.getId(), newRoom.getStatus(), RoomStatus.OCCUPIED, "change room - new room");
        writeOrderAudit(stayRecord.getOrderId(), "CHANGE_ROOM", "change room to " + newRoom.getRoomNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkout(CheckoutRequest request) {
        HotelStayRecord stayRecord = hotelStayRecordMapper.findById(request.getStayRecordId());
        if (stayRecord == null) {
            throw new BusinessException("stay record not found");
        }
        SecurityUtils.assertPropertyAccessible(stayRecord.getPropertyId());

        HotelRoom room = hotelRoomMapper.findById(stayRecord.getRoomId());
        hotelStayRecordMapper.checkout(stayRecord.getId(), "CHECKED_OUT", SecurityUtils.currentUsername());
        hotelOrderMapper.updateStatus(stayRecord.getOrderId(), OrderStatus.CHECKED_OUT, SecurityUtils.currentUsername());
        if (room != null) {
            updateRoomStatusWithLog(room.getId(), room.getStatus(), RoomStatus.VACANT_DIRTY, "checkout");
        }
        writeOrderAudit(stayRecord.getOrderId(), "CHECKOUT",
                room == null ? "checkout completed" : "checkout completed, room=" + room.getRoomNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(OrderCancelRequest request) {
        HotelOrderExt order = hotelOrderMapper.findById(request.getOrderId());
        if (order == null) {
            throw new BusinessException("order not found");
        }
        SecurityUtils.assertPropertyAccessible(order.getPropertyId());
        if (OrderStatus.CHECKED_OUT.equals(order.getOrderStatus()) || OrderStatus.CHECKED_IN.equals(order.getOrderStatus())) {
            throw new BusinessException("checked in/out order cannot be canceled");
        }
        hotelOrderMapper.updateStatus(order.getId(), OrderStatus.CANCELED, SecurityUtils.currentUsername());
        writeOrderAudit(order.getId(), "CANCEL", request.getReason() == null ? "cancel order" : request.getReason());
    }

    @Override
    public void updateOrderStatus(OrderStatusUpdateRequest request) {
        HotelOrderExt order = hotelOrderMapper.findById(request.getOrderId());
        if (order == null) {
            throw new BusinessException("order not found");
        }
        SecurityUtils.assertPropertyAccessible(order.getPropertyId());
        hotelOrderMapper.updateStatus(order.getId(), request.getTargetStatus(), SecurityUtils.currentUsername());
        writeOrderAudit(order.getId(), "UPDATE_STATUS", "status => " + request.getTargetStatus());
    }

    @Override
    public Long registerGuest(GuestRegisterRequest request) {
        HotelOrderExt order = hotelOrderMapper.findById(request.getOrderId());
        if (order == null) {
            throw new BusinessException("order not found");
        }
        SecurityUtils.assertPropertyAccessible(order.getPropertyId());

        HotelGuestProfile entity = new HotelGuestProfile();
        entity.setOrderId(request.getOrderId());
        entity.setStayRecordId(null);
        entity.setGuestName(request.getGuestName());
        entity.setGuestMobile(request.getGuestMobile());
        entity.setCertificateType(request.getCertificateType());
        entity.setCertificateNo(request.getCertificateNo());
        entity.setIsPrimary(Boolean.TRUE.equals(request.getPrimaryGuest()) ? "Y" : "N");
        entity.setCreatedBy(SecurityUtils.currentUsername());
        entity.setUpdatedBy(SecurityUtils.currentUsername());
        entity.setDeleted(0);
        hotelGuestProfileMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public Long createPayment(PaymentCreateRequest request) {
        HotelOrderExt order = hotelOrderMapper.findById(request.getOrderId());
        if (order == null) {
            throw new BusinessException("order not found");
        }
        SecurityUtils.assertPropertyAccessible(order.getPropertyId());

        HotelPaymentRecord payment = new HotelPaymentRecord();
        payment.setOrderId(request.getOrderId());
        payment.setStayRecordId(request.getStayRecordId());
        payment.setPaymentType(request.getPaymentType());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAmount(request.getAmount());
        payment.setPaymentStatus("PAID");
        payment.setExternalTradeNo(request.getExternalTradeNo());
        payment.setRemark(request.getRemark());
        payment.setCreatedBy(SecurityUtils.currentUsername());
        payment.setUpdatedBy(SecurityUtils.currentUsername());
        payment.setDeleted(0);
        hotelPaymentRecordMapper.insert(payment);
        writeOrderAudit(order.getId(), "PAYMENT", "payment " + request.getAmount());
        return payment.getId();
    }

    @Override
    public List<PaymentRecordVO> listPayments() {
        return hotelPaymentRecordMapper.findAll();
    }

    @Override
    public byte[] exportOrdersExcel() {
        List<OrderVO> list = listOrders();
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("orders");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Order No");
            header.createCell(1).setCellValue("Property");
            header.createCell(2).setCellValue("Room Type");
            header.createCell(3).setCellValue("Guest");
            header.createCell(4).setCellValue("CheckIn Date");
            header.createCell(5).setCellValue("CheckOut Date");
            header.createCell(6).setCellValue("Amount");
            header.createCell(7).setCellValue("Status");

            int rowIndex = 1;
            for (OrderVO item : list) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(item.getOrderNo());
                row.createCell(1).setCellValue(item.getPropertyName());
                row.createCell(2).setCellValue(item.getRoomTypeName());
                row.createCell(3).setCellValue(item.getGuestName());
                row.createCell(4).setCellValue(String.valueOf(item.getCheckInDate()));
                row.createCell(5).setCellValue(String.valueOf(item.getCheckOutDate()));
                row.createCell(6).setCellValue(item.getTotalAmount() == null ? 0D : item.getTotalAmount().doubleValue());
                row.createCell(7).setCellValue(item.getOrderStatus());
            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new BusinessException("export order list failed");
        }
    }

    @Override
    public List<RoomStatusTimelineVO> roomStatusTimeline(Long orderId) {
        HotelOrderExt order = hotelOrderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException("order not found");
        }
        SecurityUtils.assertPropertyAccessible(order.getPropertyId());

        List<StayRecordVO> stays = hotelStayRecordMapper.findByOrderId(orderId);
        Map<Long, String> roomNoMap = new HashMap<>();
        for (StayRecordVO stay : stays) {
            if (stay.getRoomId() != null && stay.getRoomNo() != null) {
                roomNoMap.put(stay.getRoomId(), stay.getRoomNo());
            }
        }

        List<RoomStatusTimelineVO> timeline = new ArrayList<>();
        timeline.add(buildTimelineNode(
                RoomTimelineCodes.NODE_RESERVED,
                RoomTimelineCodes.ACTION_CREATE,
                RoomTimelineCodes.REMARK_ORDER_CREATED,
                order.getCreatedAt(),
                order.getCreatedBy(),
                "order created",
                null,
                null
        ));
        timeline.add(buildTimelineNode(
                RoomTimelineCodes.NODE_PENDING_CHECKIN,
                RoomTimelineCodes.ACTION_WAITING,
                RoomTimelineCodes.REMARK_WAITING_CHECKIN,
                order.getCreatedAt(),
                order.getCreatedBy(),
                "waiting for check-in",
                null,
                null
        ));

        List<AuditLogRecord> orderAudits = auditLogRecordMapper.findAllByBiz("ORDER", String.valueOf(orderId));
        for (AuditLogRecord audit : orderAudits) {
            RoomStatusTimelineVO node = mapAuditToTimelineNode(audit, stays, roomNoMap);
            if (node != null) {
                timeline.add(node);
            }
        }

        boolean hasCheckout = timeline.stream().anyMatch(item -> RoomTimelineCodes.NODE_CHECKED_OUT.equals(item.getNodeCode()));
        boolean hasCanceled = timeline.stream().anyMatch(item -> RoomTimelineCodes.NODE_CANCELED.equals(item.getNodeCode()));
        if (!hasCheckout && !hasCanceled && !stays.isEmpty()) {
            StayRecordVO latest = stays.get(0);
            timeline.add(buildTimelineNode(
                    RoomTimelineCodes.NODE_PENDING_CHECKOUT,
                    RoomTimelineCodes.ACTION_WAITING,
                    RoomTimelineCodes.REMARK_PENDING_CHECKOUT,
                    latest.getActualCheckInTime(),
                    null,
                    "checked-in and waiting checkout",
                    latest.getRoomId(),
                    latest.getRoomNo()
            ));
        }

        return timeline.stream()
                .sorted(Comparator.comparing(RoomStatusTimelineVO::getOperationTime,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    private void validatePropertyAndRoomType(Long propertyId, Long roomTypeId) {
        HotelProperty property = hotelPropertyMapper.findById(propertyId);
        if (property == null) {
            throw new BusinessException("property not found");
        }
        HotelRoomType roomType = hotelRoomTypeMapper.findById(roomTypeId);
        if (roomType == null) {
            throw new BusinessException("room type not found");
        }
        if (!propertyId.equals(roomType.getPropertyId())) {
            throw new BusinessException("room type does not belong to property");
        }
    }

    private void updateRoomStatusWithLog(Long roomId, String oldStatus, String newStatus, String reason) {
        hotelRoomMapper.updateStatusById(roomId, newStatus, SecurityUtils.currentUsername());
        HotelRoomStatusLog log = new HotelRoomStatusLog();
        log.setRoomId(roomId);
        log.setOldStatus(oldStatus);
        log.setNewStatus(newStatus);
        log.setReason(reason);
        log.setOperator(SecurityUtils.currentUsername());
        hotelRoomStatusLogMapper.insert(log);
    }

    private boolean canAccessProperty(Long propertyId) {
        if (propertyId == null) {
            return false;
        }
        return SecurityUtils.propertyScopes().contains(propertyId);
    }

    private void writeOrderAudit(Long orderId, String actionType, String content) {
        HotelOrderExt order = hotelOrderMapper.findById(orderId);
        AuditLogRecord log = new AuditLogRecord();
        log.setModuleCode("ORDER");
        log.setBizNo(String.valueOf(orderId));
        log.setActionType(actionType);
        log.setContent(content);
        log.setOperator(SecurityUtils.currentUsername());
        if (order != null) {
            log.setPropertyId(order.getPropertyId());
            HotelProperty property = hotelPropertyMapper.findById(order.getPropertyId());
            if (property != null) {
                log.setBrandId(property.getBrandId());
                log.setGroupId(property.getGroupId());
            }
        }
        auditLogRecordMapper.insert(log);
    }

    private RoomStatusTimelineVO mapAuditToTimelineNode(AuditLogRecord audit, List<StayRecordVO> stays, Map<Long, String> roomNoMap) {
        if (audit == null || audit.getActionType() == null) {
            return null;
        }
        String action = audit.getActionType();
        return switch (action) {
            case "CHECK_IN" -> {
                StayRecordVO stay = stays.stream()
                        .filter(item -> item.getActualCheckInTime() != null)
                        .max(Comparator.comparing(StayRecordVO::getActualCheckInTime))
                        .orElse(stays.isEmpty() ? null : stays.get(0));
                yield buildTimelineNode(
                        RoomTimelineCodes.NODE_CHECKED_IN,
                        RoomTimelineCodes.ACTION_CHECK_IN,
                        RoomTimelineCodes.REMARK_CHECKIN_COMPLETED,
                        audit.getCreatedAt(),
                        audit.getOperator(),
                        audit.getContent(),
                        stay == null ? null : stay.getRoomId(),
                        stay == null ? null : stay.getRoomNo()
                );
            }
            case "EXTEND_STAY" -> buildTimelineNode(
                    RoomTimelineCodes.NODE_EXTENDED,
                    RoomTimelineCodes.ACTION_EXTEND_STAY,
                    RoomTimelineCodes.REMARK_STAY_EXTENDED,
                    audit.getCreatedAt(),
                    audit.getOperator(),
                    audit.getContent(),
                    null,
                    null
            );
            case "CHANGE_ROOM" -> {
                String roomNo = extractRoomNo(audit.getContent());
                yield buildTimelineNode(
                        RoomTimelineCodes.NODE_CHANGE_ROOM,
                        RoomTimelineCodes.ACTION_CHANGE_ROOM,
                        RoomTimelineCodes.REMARK_ROOM_CHANGED,
                        audit.getCreatedAt(),
                        audit.getOperator(),
                        audit.getContent(),
                        null,
                        roomNo
                );
            }
            case "CHECKOUT" -> {
                StayRecordVO stay = stays.stream()
                        .filter(item -> item.getActualCheckOutTime() != null)
                        .max(Comparator.comparing(StayRecordVO::getActualCheckOutTime))
                        .orElse(stays.isEmpty() ? null : stays.get(0));
                yield buildTimelineNode(
                        RoomTimelineCodes.NODE_CHECKED_OUT,
                        RoomTimelineCodes.ACTION_CHECKOUT,
                        RoomTimelineCodes.REMARK_CHECKOUT_COMPLETED,
                        audit.getCreatedAt(),
                        audit.getOperator(),
                        audit.getContent(),
                        stay == null ? null : stay.getRoomId(),
                        stay == null ? null : roomNoMap.getOrDefault(stay.getRoomId(), stay.getRoomNo())
                );
            }
            case "CANCEL" -> buildTimelineNode(
                    RoomTimelineCodes.NODE_CANCELED,
                    RoomTimelineCodes.ACTION_CANCEL,
                    RoomTimelineCodes.REMARK_ORDER_CANCELED,
                    audit.getCreatedAt(),
                    audit.getOperator(),
                    audit.getContent(),
                    null,
                    null
            );
            default -> null;
        };
    }

    private RoomStatusTimelineVO buildTimelineNode(String nodeCode, String actionCode, String remarkCode,
                                                   LocalDateTime time, String operator, String remarkText,
                                                   Long roomId, String roomNo) {
        RoomStatusTimelineVO node = new RoomStatusTimelineVO();
        node.setNodeCode(nodeCode);
        node.setActionCode(actionCode);
        node.setRemarkCode(remarkCode);
        node.setOperationTime(time);
        node.setOperator(operator);
        node.setRemarkText(remarkText);
        node.setRoomId(roomId);
        node.setRoomNo(roomNo);
        return node;
    }

    private String extractRoomNo(String content) {
        if (content == null) {
            return null;
        }
        int idx = content.indexOf("to ");
        if (idx < 0 || idx + 3 >= content.length()) {
            return null;
        }
        return content.substring(idx + 3).trim();
    }
}
