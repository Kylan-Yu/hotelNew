package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.HotelRoomType;
import com.kylan.hotel.domain.vo.RoomTypeVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HotelRoomTypeMapper {

    @Insert("""
            INSERT INTO hotel_room_type(property_id, room_type_code, room_type_name, max_guest_count, bed_type, base_price, status, created_by, updated_by, deleted)
            VALUES(#{propertyId}, #{roomTypeCode}, #{roomTypeName}, #{maxGuestCount}, #{bedType}, #{basePrice}, #{status}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HotelRoomType entity);

    @Select("""
            SELECT rt.id, rt.property_id AS propertyId, p.property_name AS propertyName,
                   rt.room_type_code AS roomTypeCode, rt.room_type_name AS roomTypeName,
                   rt.max_guest_count AS maxGuestCount, rt.bed_type AS bedType,
                   rt.base_price AS basePrice, rt.status
            FROM hotel_room_type rt
            LEFT JOIN hotel_property p ON p.id = rt.property_id AND p.deleted = 0
            WHERE rt.deleted = 0
            ORDER BY rt.id DESC
            """)
    List<RoomTypeVO> findAll();

    @Select("""
            SELECT id, property_id AS propertyId, room_type_code AS roomTypeCode, room_type_name AS roomTypeName,
                   max_guest_count AS maxGuestCount, bed_type AS bedType, base_price AS basePrice, status,
                   created_by AS createdBy, created_at AS createdAt, updated_by AS updatedBy, updated_at AS updatedAt, deleted
            FROM hotel_room_type
            WHERE id = #{id} AND deleted = 0
            LIMIT 1
            """)
    HotelRoomType findById(Long id);

    @Select("SELECT COUNT(1) FROM hotel_room_type WHERE room_type_code = #{roomTypeCode} AND deleted = 0")
    int countByRoomTypeCode(String roomTypeCode);

    @Update("""
            UPDATE hotel_room_type
            SET property_id = #{propertyId},
                room_type_name = #{roomTypeName},
                max_guest_count = #{maxGuestCount},
                bed_type = #{bedType},
                base_price = #{basePrice},
                updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int updateById(HotelRoomType entity);

    @Update("""
            UPDATE hotel_room_type
            SET status = #{status}, updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int updateStatusById(@Param("id") Long id, @Param("status") Integer status, @Param("updatedBy") String updatedBy);
}
