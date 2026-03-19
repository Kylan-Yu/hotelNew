package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.dto.LogQueryRequest;
import com.kylan.hotel.domain.entity.OperationLogRecord;
import com.kylan.hotel.domain.vo.OperationLogItemVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OperationLogRecordMapper {
    @Insert("""
            INSERT INTO operation_log_record(module_code, operation, request_uri, request_method, operator,
                                             success_flag, message, group_id, brand_id, property_id)
            VALUES(#{moduleCode}, #{operation}, #{requestUri}, #{requestMethod}, #{operator},
                   #{successFlag}, #{message}, #{groupId}, #{brandId}, #{propertyId})
            """)
    int insert(OperationLogRecord entity);

    @Select("""
            <script>
            SELECT id, module_code AS moduleCode, operation, request_uri AS requestUri, request_method AS requestMethod,
                   operator, success_flag AS successFlag, message,
                   group_id AS groupId, brand_id AS brandId, property_id AS propertyId,
                   created_at AS createdAt
            FROM operation_log_record
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
    List<OperationLogRecord> search(@Param("q") LogQueryRequest q);

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM operation_log_record
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
            <script>
            SELECT id, operation, request_uri AS requestUri, request_method AS requestMethod,
                   operator, success_flag AS successFlag, message, created_at AS createdAt
            FROM operation_log_record
            WHERE property_id = #{propertyId}
              <if test='moduleCode != null and moduleCode != \"\"'>AND module_code = #{moduleCode}</if>
            ORDER BY id DESC
            LIMIT 200
            </script>
            """)
    List<OperationLogItemVO> findByProperty(@Param("propertyId") Long propertyId, @Param("moduleCode") String moduleCode);
}
