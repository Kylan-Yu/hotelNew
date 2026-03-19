package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.HotelOrderExt;
import com.kylan.hotel.domain.vo.OrderVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HotelOrderMapper {

    @Insert("""
            INSERT INTO hotel_order(order_no, property_id, room_type_id, source_channel, guest_name, guest_mobile,
                                    check_in_date, check_out_date, total_amount, order_status, channel_order_no,
                                    created_by, updated_by, deleted)
            VALUES(#{orderNo}, #{propertyId}, #{roomTypeId}, #{sourceChannel}, #{guestName}, #{guestMobile},
                   #{checkInDate}, #{checkOutDate}, #{totalAmount}, #{orderStatus}, #{channelOrderNo},
                   #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HotelOrderExt entity);

    @Select("""
            SELECT o.id, o.order_no AS orderNo, o.property_id AS propertyId, p.property_name AS propertyName,
                   o.room_type_id AS roomTypeId, rt.room_type_name AS roomTypeName, o.source_channel AS sourceChannel,
                   o.guest_name AS guestName, o.guest_mobile AS guestMobile, o.check_in_date AS checkInDate,
                   o.check_out_date AS checkOutDate, o.total_amount AS totalAmount, o.order_status AS orderStatus
            FROM hotel_order o
            LEFT JOIN hotel_property p ON p.id = o.property_id
            LEFT JOIN hotel_room_type rt ON rt.id = o.room_type_id
            WHERE o.deleted = 0
              AND /*DS_PROPERTY:property_id*/ 1=1
            ORDER BY o.id DESC
            """)
    List<OrderVO> findAll();

    @Select("""
            SELECT o.id, o.order_no AS orderNo, o.property_id AS propertyId, p.property_name AS propertyName,
                   o.room_type_id AS roomTypeId, rt.room_type_name AS roomTypeName, o.source_channel AS sourceChannel,
                   o.guest_name AS guestName, o.guest_mobile AS guestMobile, o.check_in_date AS checkInDate,
                   o.check_out_date AS checkOutDate, o.total_amount AS totalAmount, o.order_status AS orderStatus
            FROM hotel_order o
            LEFT JOIN hotel_property p ON p.id = o.property_id
            LEFT JOIN hotel_room_type rt ON rt.id = o.room_type_id
            WHERE o.deleted = 0 AND o.id = #{id}
            LIMIT 1
            """)
    OrderVO findVOById(Long id);

    @Select("""
            SELECT id, order_no AS orderNo, property_id AS propertyId, room_type_id AS roomTypeId,
                   source_channel AS sourceChannel, guest_name AS guestName, guest_mobile AS guestMobile,
                   check_in_date AS checkInDate, check_out_date AS checkOutDate, total_amount AS totalAmount,
                   order_status AS orderStatus, channel_order_no AS channelOrderNo,
                   created_by AS createdBy, created_at AS createdAt, updated_by AS updatedBy, updated_at AS updatedAt, deleted
            FROM hotel_order
            WHERE id = #{id} AND deleted = 0
            LIMIT 1
            """)
    HotelOrderExt findById(Long id);

    @Update("""
            UPDATE hotel_order
            SET order_status = #{orderStatus}, updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int updateStatus(@Param("id") Long id, @Param("orderStatus") String orderStatus, @Param("updatedBy") String updatedBy);
}
