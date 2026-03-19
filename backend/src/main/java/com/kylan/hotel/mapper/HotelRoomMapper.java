package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.HotelRoom;
import com.kylan.hotel.domain.vo.RoomVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HotelRoomMapper {

    @Insert("""
            INSERT INTO hotel_room(property_id, room_type_id, room_no, floor_no, status, created_by, updated_by, deleted)
            VALUES(#{propertyId}, #{roomTypeId}, #{roomNo}, #{floorNo}, #{status}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HotelRoom entity);

    @Select("""
            SELECT r.id, r.property_id AS propertyId, p.property_name AS propertyName,
                   r.room_type_id AS roomTypeId, rt.room_type_name AS roomTypeName,
                   r.room_no AS roomNo, r.floor_no AS floorNo, r.status
            FROM hotel_room r
            LEFT JOIN hotel_property p ON p.id = r.property_id AND p.deleted = 0
            LEFT JOIN hotel_room_type rt ON rt.id = r.room_type_id AND rt.deleted = 0
            WHERE r.deleted = 0
            ORDER BY r.id DESC
            """)
    List<RoomVO> findAll();

    @Select("""
            SELECT id, property_id AS propertyId, room_type_id AS roomTypeId, room_no AS roomNo, floor_no AS floorNo, status,
                   created_by AS createdBy, created_at AS createdAt, updated_by AS updatedBy, updated_at AS updatedAt, deleted
            FROM hotel_room
            WHERE id = #{id} AND deleted = 0
            LIMIT 1
            """)
    HotelRoom findById(Long id);

    @Select("SELECT COUNT(1) FROM hotel_room WHERE property_id = #{propertyId} AND room_no = #{roomNo} AND deleted = 0")
    int countByPropertyAndRoomNo(@Param("propertyId") Long propertyId, @Param("roomNo") String roomNo);

    @Update("""
            UPDATE hotel_room
            SET room_type_id = #{roomTypeId}, floor_no = #{floorNo}, updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int updateById(HotelRoom entity);

    @Update("""
            UPDATE hotel_room
            SET status = #{status}, updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int updateStatusById(@Param("id") Long id, @Param("status") String status, @Param("updatedBy") String updatedBy);
}
