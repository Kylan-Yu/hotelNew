package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.vo.DailyReportVO;
import com.kylan.hotel.domain.vo.PropertyStatVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReportMapper {

    @Select("""
            SELECT p.id AS propertyId,
                   p.property_name AS propertyName,
                   IFNULL(o.order_count, 0) AS orderCount,
                   IFNULL(pay.payment_amount, 0) AS paymentAmount,
                   IFNULL(ref.refund_amount, 0) AS refundAmount,
                   (IFNULL(pay.payment_amount, 0) - IFNULL(ref.refund_amount, 0)) AS netRevenue
            FROM hotel_property p
            LEFT JOIN (
                SELECT property_id, COUNT(1) AS order_count
                FROM hotel_order
                WHERE deleted = 0 AND DATE(created_at) = CURRENT_DATE
                GROUP BY property_id
            ) o ON o.property_id = p.id
            LEFT JOIN (
                SELECT ho.property_id, SUM(pr.amount) AS payment_amount
                FROM hotel_payment_record pr
                INNER JOIN hotel_order ho ON ho.id = pr.order_id
                WHERE pr.deleted = 0 AND DATE(pr.created_at) = CURRENT_DATE
                GROUP BY ho.property_id
            ) pay ON pay.property_id = p.id
            LEFT JOIN (
                SELECT ho.property_id, SUM(rr.refund_amount) AS refund_amount
                FROM finance_refund_record rr
                INNER JOIN hotel_order ho ON ho.id = rr.order_id
                WHERE rr.deleted = 0 AND DATE(rr.created_at) = CURRENT_DATE
                GROUP BY ho.property_id
            ) ref ON ref.property_id = p.id
            WHERE p.deleted = 0
            ORDER BY p.id
            """)
    List<DailyReportVO> selectDailyReport();

    @Select("""
            SELECT p.id AS propertyId,
                   p.property_name AS propertyName,
                   IFNULL(room_stat.room_count, 0) AS roomCount,
                   IFNULL(room_stat.occupied_room_count, 0) AS occupiedRoomCount,
                   IFNULL(
                     CASE WHEN IFNULL(room_stat.room_count, 0) = 0 THEN 0
                          ELSE ROUND(room_stat.occupied_room_count / room_stat.room_count, 4)
                     END,
                     0
                   ) AS occupancyRate
            FROM hotel_property p
            LEFT JOIN (
                SELECT property_id,
                       COUNT(1) AS room_count,
                       SUM(CASE WHEN status = 'OCCUPIED' THEN 1 ELSE 0 END) AS occupied_room_count
                FROM hotel_room
                WHERE deleted = 0
                GROUP BY property_id
            ) room_stat ON room_stat.property_id = p.id
            WHERE p.deleted = 0
            ORDER BY p.id
            """)
    List<PropertyStatVO> selectPropertyStat();
}
