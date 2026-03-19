package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.MarketingCampaign;
import com.kylan.hotel.domain.vo.CampaignVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MarketingCampaignMapper {

    @Insert("""
            INSERT INTO marketing_campaign(property_id, campaign_code, campaign_name, campaign_type,
                                           start_date, end_date, status, created_by, updated_by, deleted)
            VALUES(#{propertyId}, #{campaignCode}, #{campaignName}, #{campaignType},
                   #{startDate}, #{endDate}, #{status}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MarketingCampaign entity);

    @Select("""
            SELECT c.id, c.property_id AS propertyId, p.property_name AS propertyName,
                   c.campaign_code AS campaignCode, c.campaign_name AS campaignName,
                   c.campaign_type AS campaignType, c.start_date AS startDate, c.end_date AS endDate, c.status
            FROM marketing_campaign c
            LEFT JOIN hotel_property p ON p.id = c.property_id
            WHERE c.deleted = 0
            ORDER BY c.id DESC
            """)
    List<CampaignVO> findAll();
}
