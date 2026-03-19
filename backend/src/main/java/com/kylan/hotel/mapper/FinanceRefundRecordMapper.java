package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.FinanceRefundRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FinanceRefundRecordMapper {
    @Insert("""
            INSERT INTO finance_refund_record(payment_id, order_id, refund_amount, refund_reason, refund_status,
                                              created_by, updated_by, deleted)
            VALUES(#{paymentId}, #{orderId}, #{refundAmount}, #{refundReason}, #{refundStatus},
                   #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FinanceRefundRecord entity);

    @Select("""
            SELECT id, payment_id AS paymentId, order_id AS orderId, refund_amount AS refundAmount,
                   refund_reason AS refundReason, refund_status AS refundStatus,
                   created_by AS createdBy, created_at AS createdAt, updated_by AS updatedBy, updated_at AS updatedAt, deleted
            FROM finance_refund_record
            WHERE deleted = 0
            ORDER BY id DESC
            """)
    List<FinanceRefundRecord> findAll();
}
