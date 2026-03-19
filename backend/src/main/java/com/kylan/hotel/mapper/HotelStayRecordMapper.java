package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.HotelStayRecord;
import com.kylan.hotel.domain.vo.StayRecordVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HotelStayRecordMapper {

    @Insert("""
            INSERT INTO hotel_stay_record(stay_no, order_id, reservation_id, property_id, room_id, stay_type, stay_status,
                                          check_in_date, check_out_date, actual_check_in_time, actual_check_out_time,
                                          created_by, updated_by, deleted)
            VALUES(#{stayNo}, #{orderId}, #{reservationId}, #{propertyId}, #{roomId}, #{stayType}, #{stayStatus},
                   #{checkInDate}, #{checkOutDate}, #{actualCheckInTime}, #{actualCheckOutTime}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HotelStayRecord entity);

    @Select("""
            SELECT s.id, s.stay_no AS stayNo, s.order_id AS orderId, o.order_no AS orderNo,
                   s.property_id AS propertyId, p.property_name AS propertyName, s.room_id AS roomId,
                   r.room_no AS roomNo, s.stay_type AS stayType, s.stay_status AS stayStatus,
                   s.check_in_date AS checkInDate, s.check_out_date AS checkOutDate,
                   s.actual_check_in_time AS actualCheckInTime, s.actual_check_out_time AS actualCheckOutTime
            FROM hotel_stay_record s
            LEFT JOIN hotel_order o ON o.id = s.order_id
            LEFT JOIN hotel_property p ON p.id = s.property_id
            LEFT JOIN hotel_room r ON r.id = s.room_id
            WHERE s.deleted = 0
              AND /*DS_PROPERTY:property_id*/ 1=1
            ORDER BY s.id DESC
            """)
    List<StayRecordVO> findAll();

    @Select("""
            SELECT s.id, s.stay_no AS stayNo, s.order_id AS orderId, o.order_no AS orderNo,
                   s.property_id AS propertyId, p.property_name AS propertyName, s.room_id AS roomId,
                   r.room_no AS roomNo, s.stay_type AS stayType, s.stay_status AS stayStatus,
                   s.check_in_date AS checkInDate, s.check_out_date AS checkOutDate,
                   s.actual_check_in_time AS actualCheckInTime, s.actual_check_out_time AS actualCheckOutTime
            FROM hotel_stay_record s
            LEFT JOIN hotel_order o ON o.id = s.order_id
            LEFT JOIN hotel_property p ON p.id = s.property_id
            LEFT JOIN hotel_room r ON r.id = s.room_id
            WHERE s.deleted = 0 AND s.order_id = #{orderId}
            ORDER BY s.id DESC
            """)
    List<StayRecordVO> findByOrderId(Long orderId);

    @Select("""
            SELECT id, stay_no AS stayNo, order_id AS orderId, reservation_id AS reservationId, property_id AS propertyId,
                   room_id AS roomId, stay_type AS stayType, stay_status AS stayStatus,
                   check_in_date AS checkInDate, check_out_date AS checkOutDate,
                   actual_check_in_time AS actualCheckInTime, actual_check_out_time AS actualCheckOutTime,
                   created_by AS createdBy, created_at AS createdAt, updated_by AS updatedBy, updated_at AS updatedAt, deleted
            FROM hotel_stay_record
            WHERE id = #{id} AND deleted = 0
            LIMIT 1
            """)
    HotelStayRecord findById(Long id);

    @Update("""
            UPDATE hotel_stay_record
            SET check_out_date = #{checkOutDate}, updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int extendStay(@Param("id") Long id, @Param("checkOutDate") LocalDate checkOutDate, @Param("updatedBy") String updatedBy);

    @Update("""
            UPDATE hotel_stay_record
            SET room_id = #{roomId}, updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int changeRoom(@Param("id") Long id, @Param("roomId") Long roomId, @Param("updatedBy") String updatedBy);

    @Update("""
            UPDATE hotel_stay_record
            SET stay_status = #{stayStatus}, actual_check_out_time = NOW(), updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int checkout(@Param("id") Long id, @Param("stayStatus") String stayStatus, @Param("updatedBy") String updatedBy);
}
