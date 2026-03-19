package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.ChannelMapping;
import com.kylan.hotel.domain.vo.ChannelMappingVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChannelMappingMapper {

    @Insert("""
            INSERT INTO channel_mapping(property_id, channel_code, mapping_type, local_biz_id, channel_biz_id, remark,
                                        created_by, updated_by, deleted)
            VALUES(#{propertyId}, #{channelCode}, #{mappingType}, #{localBizId}, #{channelBizId}, #{remark},
                   #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChannelMapping entity);

    @Select("""
            SELECT cm.id, cm.property_id AS propertyId, p.property_name AS propertyName,
                   cm.channel_code AS channelCode, cm.mapping_type AS mappingType,
                   cm.local_biz_id AS localBizId, cm.channel_biz_id AS channelBizId, cm.remark
            FROM channel_mapping cm
            LEFT JOIN hotel_property p ON p.id = cm.property_id
            WHERE cm.deleted = 0
            ORDER BY cm.id DESC
            """)
    List<ChannelMappingVO> findAll();
}
