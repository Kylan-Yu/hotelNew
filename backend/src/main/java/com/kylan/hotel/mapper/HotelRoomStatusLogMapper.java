package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.HotelRoomStatusLog;
import com.kylan.hotel.domain.vo.RoomStatusLogVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HotelRoomStatusLogMapper {

    @Insert("""
            INSERT INTO hotel_room_status_log(room_id, old_status, new_status, reason, operator)
            VALUES(#{roomId}, #{oldStatus}, #{newStatus}, #{reason}, #{operator})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HotelRoomStatusLog entity);

    @Select("""
            SELECT id, room_id AS roomId, old_status AS oldStatus, new_status AS newStatus,
                   reason, operator, created_at AS createdAt
            FROM hotel_room_status_log
            WHERE room_id = #{roomId}
            ORDER BY id DESC
            """)
    List<RoomStatusLogVO> findByRoomId(Long roomId);
}
