package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.OtaCallbackRetryTask;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OtaCallbackRetryTaskMapper {

    @Insert("""
            INSERT INTO ota_callback_retry_task(idempotent_key, channel_code, event_type, external_request_no,
                                                signature, headers_json, payload, task_status, retry_count,
                                                max_retry_count, last_error, next_retry_time)
            VALUES(#{idempotentKey}, #{channelCode}, #{eventType}, #{externalRequestNo},
                   #{signature}, #{headersJson}, #{payload}, #{taskStatus}, #{retryCount},
                   #{maxRetryCount}, #{lastError}, #{nextRetryTime})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OtaCallbackRetryTask entity);

    @Select("""
            SELECT id, idempotent_key AS idempotentKey, channel_code AS channelCode, event_type AS eventType,
                   external_request_no AS externalRequestNo, signature, headers_json AS headersJson,
                   payload, task_status AS taskStatus, retry_count AS retryCount, max_retry_count AS maxRetryCount,
                   last_error AS lastError, next_retry_time AS nextRetryTime, created_at AS createdAt, updated_at AS updatedAt
            FROM ota_callback_retry_task
            WHERE id = #{id}
            LIMIT 1
            """)
    OtaCallbackRetryTask findById(Long id);

    @Select("""
            SELECT id, idempotent_key AS idempotentKey, channel_code AS channelCode, event_type AS eventType,
                   external_request_no AS externalRequestNo, signature, headers_json AS headersJson,
                   payload, task_status AS taskStatus, retry_count AS retryCount, max_retry_count AS maxRetryCount,
                   last_error AS lastError, next_retry_time AS nextRetryTime, created_at AS createdAt, updated_at AS updatedAt
            FROM ota_callback_retry_task
            WHERE idempotent_key = #{idempotentKey}
            ORDER BY id DESC
            LIMIT 1
            """)
    OtaCallbackRetryTask findByIdempotentKey(String idempotentKey);

    @Update("""
            UPDATE ota_callback_retry_task
            SET task_status = #{taskStatus},
                retry_count = #{retryCount},
                last_error = #{lastError},
                next_retry_time = #{nextRetryTime},
                updated_at = NOW()
            WHERE id = #{id}
            """)
    int updateProgress(@Param("id") Long id,
                       @Param("taskStatus") String taskStatus,
                       @Param("retryCount") Integer retryCount,
                       @Param("lastError") String lastError,
                       @Param("nextRetryTime") java.time.LocalDateTime nextRetryTime);
}
