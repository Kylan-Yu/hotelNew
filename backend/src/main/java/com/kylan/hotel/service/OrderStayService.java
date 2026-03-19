package com.kylan.hotel.service;

import com.kylan.hotel.domain.dto.*;
import com.kylan.hotel.domain.vo.OrderVO;
import com.kylan.hotel.domain.vo.OrderDetailVO;
import com.kylan.hotel.domain.vo.PaymentRecordVO;
import com.kylan.hotel.domain.vo.ReservationVO;
import com.kylan.hotel.domain.vo.RoomStatusTimelineVO;
import com.kylan.hotel.domain.vo.StayRecordVO;

import java.util.List;

public interface OrderStayService {
    Long createReservation(ReservationCreateRequest request);

    Long createOrderByReservation(Long reservationId);

    List<ReservationVO> listReservations();

    List<OrderVO> listOrders();

    OrderDetailVO orderDetail(Long orderId);

    List<StayRecordVO> listStayRecords();

    Long checkIn(CheckInRequest request);

    void extendStay(ExtendStayRequest request);

    void changeRoom(ChangeRoomRequest request);

    void checkout(CheckoutRequest request);

    void cancelOrder(OrderCancelRequest request);

    void updateOrderStatus(OrderStatusUpdateRequest request);

    Long registerGuest(GuestRegisterRequest request);

    Long createPayment(PaymentCreateRequest request);

    List<PaymentRecordVO> listPayments();

    byte[] exportOrdersExcel();

    List<RoomStatusTimelineVO> roomStatusTimeline(Long orderId);
}
