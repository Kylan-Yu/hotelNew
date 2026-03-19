package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.ChannelSyncLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChannelSyncLogMapper {

    @Insert("""
            INSERT INTO channel_sync_log(property_id, channel_code, biz_type, biz_id, idempotent_key, sync_status,
                                         request_payload, response_payload)
            VALUES(#{propertyId}, #{channelCode}, #{bizType}, #{bizId}, #{idempotentKey}, #{syncStatus},
                   #{requestPayload}, #{responsePayload})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChannelSyncLog entity);

    @Select("""
            SELECT id, property_id AS propertyId, channel_code AS channelCode, biz_type AS bizType, biz_id AS bizId,
                   idempotent_key AS idempotentKey, sync_status AS syncStatus,
                   request_payload AS requestPayload, response_payload AS responsePayload, created_at AS createdAt
            FROM channel_sync_log
            WHERE 1=1
              AND /*DS_PROPERTY:property_id*/ 1=1
            ORDER BY id DESC
            LIMIT 500
            """)
    List<ChannelSyncLog> findAll();
}
