package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.ChannelCallbackLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChannelCallbackLogMapper {
    @Insert("""
            INSERT INTO channel_callback_log(idempotent_key, channel_code, event_type, external_request_no,
                                             signature, callback_status, payload, message, processed_flag)
            VALUES(#{idempotentKey}, #{channelCode}, #{eventType}, #{externalRequestNo},
                   #{signature}, #{callbackStatus}, #{payload}, #{message}, #{processedFlag})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChannelCallbackLog entity);

    @Select("""
            SELECT id, idempotent_key AS idempotentKey, channel_code AS channelCode, event_type AS eventType,
                   external_request_no AS externalRequestNo, signature, callback_status AS callbackStatus,
                   payload, message, processed_flag AS processedFlag, created_at AS createdAt
            FROM channel_callback_log
            WHERE idempotent_key = #{idempotentKey}
            ORDER BY id DESC
            LIMIT 1
            """)
    ChannelCallbackLog findByIdempotentKey(String idempotentKey);

    @Select("""
            SELECT id, idempotent_key AS idempotentKey, channel_code AS channelCode, event_type AS eventType,
                   external_request_no AS externalRequestNo, signature, callback_status AS callbackStatus,
                   payload, message, processed_flag AS processedFlag, created_at AS createdAt
            FROM channel_callback_log
            ORDER BY id DESC
            LIMIT 500
            """)
    List<ChannelCallbackLog> findAll();
}
