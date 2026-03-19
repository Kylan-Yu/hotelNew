package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.HotelGroup;
import com.kylan.hotel.domain.vo.GroupVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HotelGroupMapper {

    @Insert("""
            INSERT INTO hotel_group(group_code, group_name, status, created_by, updated_by, deleted)
            VALUES(#{groupCode}, #{groupName}, #{status}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HotelGroup entity);

    @Select("""
            SELECT id, group_code AS groupCode, group_name AS groupName, status
            FROM hotel_group
            WHERE deleted = 0
            ORDER BY id DESC
            """)
    List<GroupVO> findAll();

    @Select("""
            SELECT id, group_code AS groupCode, group_name AS groupName, status,
                   created_by AS createdBy, created_at AS createdAt, updated_by AS updatedBy, updated_at AS updatedAt, deleted
            FROM hotel_group
            WHERE id = #{id} AND deleted = 0
            LIMIT 1
            """)
    HotelGroup findById(Long id);

    @Select("SELECT COUNT(1) FROM hotel_group WHERE group_code = #{groupCode} AND deleted = 0")
    int countByGroupCode(String groupCode);

    @Update("""
            UPDATE hotel_group
            SET group_name = #{groupName}, updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int updateById(HotelGroup entity);

    @Update("""
            UPDATE hotel_group
            SET status = #{status}, updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int updateStatusById(@Param("id") Long id, @Param("status") Integer status, @Param("updatedBy") String updatedBy);
}
