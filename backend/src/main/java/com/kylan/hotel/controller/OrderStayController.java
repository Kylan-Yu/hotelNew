package com.kylan.hotel.controller;

import com.kylan.hotel.common.ApiResponse;
import com.kylan.hotel.domain.dto.*;
import com.kylan.hotel.domain.vo.OrderDetailVO;
import com.kylan.hotel.domain.vo.OrderVO;
import com.kylan.hotel.domain.vo.PaymentRecordVO;
import com.kylan.hotel.domain.vo.ReservationVO;
import com.kylan.hotel.domain.vo.RoomStatusTimelineVO;
import com.kylan.hotel.domain.vo.StayRecordVO;
import com.kylan.hotel.service.OrderStayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderStayController {

    private final OrderStayService orderStayService;

    @PostMapping("/reservations")
    @PreAuthorize("hasAuthority('order:write')")
    public ApiResponse<Long> createReservation(@Valid @RequestBody ReservationCreateRequest request) {
        return ApiResponse.success(orderStayService.createReservation(request));
    }

    @PostMapping("/reservations/{id}/generate-order")
    @PreAuthorize("hasAuthority('order:write')")
    public ApiResponse<Long> createOrderByReservation(@PathVariable Long id) {
        return ApiResponse.success(orderStayService.createOrderByReservation(id));
    }

    @GetMapping("/reservations")
    @PreAuthorize("hasAuthority('order:read')")
    public ApiResponse<List<ReservationVO>> listReservations() {
        return ApiResponse.success(orderStayService.listReservations());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('order:read')")
    public ApiResponse<List<OrderVO>> listOrders() {
        return ApiResponse.success(orderStayService.listOrders());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('order:read')")
    public ApiResponse<OrderDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(orderStayService.orderDetail(id));
    }

    @GetMapping("/{id}/timeline")
    @PreAuthorize("hasAuthority('order:read')")
    public ApiResponse<List<RoomStatusTimelineVO>> timeline(@PathVariable Long id) {
        return ApiResponse.success(orderStayService.roomStatusTimeline(id));
    }

    @PatchMapping("/cancel")
    @PreAuthorize("hasAuthority('order:write')")
    public ApiResponse<Void> cancelOrder(@Valid @RequestBody OrderCancelRequest request) {
        orderStayService.cancelOrder(request);
        return ApiResponse.success(null);
    }

    @PatchMapping("/status")
    @PreAuthorize("hasAuthority('order:write')")
    public ApiResponse<Void> updateOrderStatus(@Valid @RequestBody OrderStatusUpdateRequest request) {
        orderStayService.updateOrderStatus(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/stays/check-in")
    @PreAuthorize("hasAuthority('checkin:write')")
    public ApiResponse<Long> checkIn(@Valid @RequestBody CheckInRequest request) {
        return ApiResponse.success(orderStayService.checkIn(request));
    }

    @GetMapping("/stays")
    @PreAuthorize("hasAuthority('checkin:read')")
    public ApiResponse<List<StayRecordVO>> listStays() {
        return ApiResponse.success(orderStayService.listStayRecords());
    }

    @PatchMapping("/stays/extend")
    @PreAuthorize("hasAuthority('checkin:write')")
    public ApiResponse<Void> extendStay(@Valid @RequestBody ExtendStayRequest request) {
        orderStayService.extendStay(request);
        return ApiResponse.success(null);
    }

    @PatchMapping("/stays/change-room")
    @PreAuthorize("hasAuthority('checkin:write')")
    public ApiResponse<Void> changeRoom(@Valid @RequestBody ChangeRoomRequest request) {
        orderStayService.changeRoom(request);
        return ApiResponse.success(null);
    }

    @PatchMapping("/stays/checkout")
    @PreAuthorize("hasAuthority('checkin:write')")
    public ApiResponse<Void> checkout(@Valid @RequestBody CheckoutRequest request) {
        orderStayService.checkout(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/guests")
    @PreAuthorize("hasAuthority('checkin:write')")
    public ApiResponse<Long> registerGuest(@Valid @RequestBody GuestRegisterRequest request) {
        return ApiResponse.success(orderStayService.registerGuest(request));
    }

    @PostMapping("/payments")
    @PreAuthorize("hasAuthority('finance:write')")
    public ApiResponse<Long> createPayment(@Valid @RequestBody PaymentCreateRequest request) {
        return ApiResponse.success(orderStayService.createPayment(request));
    }

    @GetMapping("/payments")
    @PreAuthorize("hasAuthority('finance:read')")
    public ApiResponse<List<PaymentRecordVO>> listPayments() {
        return ApiResponse.success(orderStayService.listPayments());
    }

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('order:export')")
    public ResponseEntity<byte[]> exportOrders() {
        byte[] bytes = orderStayService.exportOrdersExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("orders.xlsx").build());
        return ResponseEntity.ok().headers(headers).body(bytes);
    }
}
