package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.HotelPaymentRecord;
import com.kylan.hotel.domain.vo.PaymentRecordVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HotelPaymentRecordMapper {

    @Insert("""
            INSERT INTO hotel_payment_record(order_id, stay_record_id, payment_type, payment_method, amount, payment_status,
                                             external_trade_no, remark, created_by, updated_by, deleted)
            VALUES(#{orderId}, #{stayRecordId}, #{paymentType}, #{paymentMethod}, #{amount}, #{paymentStatus},
                   #{externalTradeNo}, #{remark}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HotelPaymentRecord entity);

    @Select("""
            SELECT id, order_id AS orderId, stay_record_id AS stayRecordId, payment_type AS paymentType,
                   payment_method AS paymentMethod, amount, payment_status AS paymentStatus,
                   external_trade_no AS externalTradeNo, remark
            FROM hotel_payment_record
            WHERE deleted = 0
            ORDER BY id DESC
            """)
    List<PaymentRecordVO> findAll();

    @Select("""
            SELECT id, order_id AS orderId, stay_record_id AS stayRecordId, payment_type AS paymentType,
                   payment_method AS paymentMethod, amount, payment_status AS paymentStatus,
                   external_trade_no AS externalTradeNo, remark
            FROM hotel_payment_record
            WHERE deleted = 0 AND order_id = #{orderId}
            ORDER BY id DESC
            """)
    List<PaymentRecordVO> findByOrderId(Long orderId);
}
