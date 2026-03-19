package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.HotelReservation;
import com.kylan.hotel.domain.vo.ReservationVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HotelReservationMapper {

    @Insert("""
            INSERT INTO hotel_reservation(reservation_no, property_id, room_type_id, channel_code, contact_name, contact_mobile,
                                         guest_count, check_in_date, check_out_date, reservation_status, estimated_amount, remark,
                                         created_by, updated_by, deleted)
            VALUES(#{reservationNo}, #{propertyId}, #{roomTypeId}, #{channelCode}, #{contactName}, #{contactMobile}, #{guestCount},
                   #{checkInDate}, #{checkOutDate}, #{reservationStatus}, #{estimatedAmount}, #{remark}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HotelReservation entity);

    @Select("""
            SELECT r.id, r.reservation_no AS reservationNo, r.property_id AS propertyId, p.property_name AS propertyName,
                   r.room_type_id AS roomTypeId, rt.room_type_name AS roomTypeName, r.contact_name AS contactName,
                   r.contact_mobile AS contactMobile, r.guest_count AS guestCount, r.check_in_date AS checkInDate,
                   r.check_out_date AS checkOutDate, r.reservation_status AS reservationStatus, r.estimated_amount AS estimatedAmount
            FROM hotel_reservation r
            LEFT JOIN hotel_property p ON p.id = r.property_id
            LEFT JOIN hotel_room_type rt ON rt.id = r.room_type_id
            WHERE r.deleted = 0
            ORDER BY r.id DESC
            """)
    List<ReservationVO> findAll();

    @Select("""
            SELECT id, reservation_no AS reservationNo, property_id AS propertyId, room_type_id AS roomTypeId,
                   channel_code AS channelCode, contact_name AS contactName, contact_mobile AS contactMobile,
                   guest_count AS guestCount, check_in_date AS checkInDate, check_out_date AS checkOutDate,
                   reservation_status AS reservationStatus, estimated_amount AS estimatedAmount, remark,
                   created_by AS createdBy, created_at AS createdAt, updated_by AS updatedBy, updated_at AS updatedAt, deleted
            FROM hotel_reservation
            WHERE id = #{id} AND deleted = 0
            LIMIT 1
            """)
    HotelReservation findById(Long id);

    @Update("""
            UPDATE hotel_reservation
            SET reservation_status = #{status}, updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("updatedBy") String updatedBy);
}
