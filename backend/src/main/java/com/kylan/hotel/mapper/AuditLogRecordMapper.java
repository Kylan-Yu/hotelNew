package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.dto.LogQueryRequest;
import com.kylan.hotel.domain.entity.AuditLogRecord;
import com.kylan.hotel.domain.vo.AuditLogItemVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AuditLogRecordMapper {
    @Insert("""
            INSERT INTO audit_log_record(module_code, biz_no, action_type, content, operator, group_id, brand_id, property_id)
            VALUES(#{moduleCode}, #{bizNo}, #{actionType}, #{content}, #{operator}, #{groupId}, #{brandId}, #{propertyId})
            """)
    int insert(AuditLogRecord entity);

    @Select("""
            <script>
            SELECT id, module_code AS moduleCode, biz_no AS bizNo, action_type AS actionType,
                   content, operator, group_id AS groupId, brand_id AS brandId, property_id AS propertyId,
                   created_at AS createdAt
            FROM audit_log_record
            WHERE 1=1
              <if test='q.groupId != null'> AND group_id = #{q.groupId} </if>
              <if test='q.brandId != null'> AND brand_id = #{q.brandId} </if>
              <if test='q.propertyId != null'> AND property_id = #{q.propertyId} </if>
              <if test='q.moduleCode != null and q.moduleCode != ""'> AND module_code = #{q.moduleCode} </if>
              <if test='q.operator != null and q.operator != ""'> AND operator LIKE CONCAT('%', #{q.operator}, '%') </if>
              <if test='q.startTime != null'> AND created_at <![CDATA[>=]]> #{q.startTime} </if>
              <if test='q.endTime != null'> AND created_at <![CDATA[<=]]> #{q.endTime} </if>
              AND /*DS_GROUP:group_id*/ 1=1
              AND /*DS_BRAND:brand_id*/ 1=1
              AND /*DS_PROPERTY:property_id*/ 1=1
            ORDER BY id DESC
            LIMIT #{q.offset}, #{q.limit}
            </script>
            """)
    List<AuditLogRecord> search(@Param("q") LogQueryRequest q);

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM audit_log_record
            WHERE 1=1
              <if test='q.groupId != null'> AND group_id = #{q.groupId} </if>
              <if test='q.brandId != null'> AND brand_id = #{q.brandId} </if>
              <if test='q.propertyId != null'> AND property_id = #{q.propertyId} </if>
              <if test='q.moduleCode != null and q.moduleCode != ""'> AND module_code = #{q.moduleCode} </if>
              <if test='q.operator != null and q.operator != ""'> AND operator LIKE CONCAT('%', #{q.operator}, '%') </if>
              <if test='q.startTime != null'> AND created_at <![CDATA[>=]]> #{q.startTime} </if>
              <if test='q.endTime != null'> AND created_at <![CDATA[<=]]> #{q.endTime} </if>
              AND /*DS_GROUP:group_id*/ 1=1
              AND /*DS_BRAND:brand_id*/ 1=1
              AND /*DS_PROPERTY:property_id*/ 1=1
            </script>
            """)
    Long count(@Param("q") LogQueryRequest q);

    @Select("""
            SELECT id, action_type AS actionType, content, operator, created_at AS createdAt
            FROM audit_log_record
            WHERE module_code = #{moduleCode} AND biz_no = #{bizNo}
            ORDER BY id DESC
            LIMIT 100
            """)
    List<AuditLogItemVO> findByBiz(@Param("moduleCode") String moduleCode, @Param("bizNo") String bizNo);

    @Select("""
            SELECT id, module_code AS moduleCode, biz_no AS bizNo, action_type AS actionType,
                   content, operator, group_id AS groupId, brand_id AS brandId, property_id AS propertyId,
                   created_at AS createdAt
            FROM audit_log_record
            WHERE module_code = #{moduleCode} AND biz_no = #{bizNo}
            ORDER BY id ASC
            """)
    List<AuditLogRecord> findAllByBiz(@Param("moduleCode") String moduleCode, @Param("bizNo") String bizNo);
}
