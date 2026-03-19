package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.HotelPriceRule;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HotelPriceRuleMapper {

    @Insert("""
            INSERT INTO hotel_price_rule(property_id, rule_name, rule_type, rule_value, delta_amount, enabled,
                                         created_by, updated_by, deleted)
            VALUES(#{propertyId}, #{ruleName}, #{ruleType}, #{ruleValue}, #{deltaAmount}, #{enabled},
                   #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HotelPriceRule entity);

    @Select("""
            SELECT id, property_id AS propertyId, rule_name AS ruleName, rule_type AS ruleType,
                   rule_value AS ruleValue, delta_amount AS deltaAmount, enabled
            FROM hotel_price_rule
            WHERE deleted = 0
            ORDER BY id DESC
            """)
    List<HotelPriceRule> findAll();
}
