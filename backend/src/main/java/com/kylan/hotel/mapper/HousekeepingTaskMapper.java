package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.HousekeepingTask;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface HousekeepingTaskMapper {
    @Insert("""
            INSERT INTO housekeeping_task(property_id, room_id, biz_date, task_status, assignee, remark,
                                          created_by, updated_by, deleted)
            VALUES(#{propertyId}, #{roomId}, #{bizDate}, #{taskStatus}, #{assignee}, #{remark}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HousekeepingTask entity);

    @Update("""
            UPDATE housekeeping_task
            SET task_status = #{taskStatus}, updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int updateStatus(HousekeepingTask entity);

    @Select("""
            SELECT id, property_id AS propertyId, room_id AS roomId, biz_date AS bizDate, task_status AS taskStatus,
                   assignee, remark, created_by AS createdBy, created_at AS createdAt, updated_by AS updatedBy, updated_at AS updatedAt, deleted
            FROM housekeeping_task
            WHERE deleted = 0
            ORDER BY id DESC
            """)
    List<HousekeepingTask> findAll();
}
