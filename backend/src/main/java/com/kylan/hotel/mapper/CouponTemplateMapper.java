package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.CouponTemplate;
import com.kylan.hotel.domain.vo.CouponVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CouponTemplateMapper {

    @Insert("""
            INSERT INTO coupon_template(property_id, coupon_code, coupon_name, amount, threshold_amount, status,
                                        created_by, updated_by, deleted)
            VALUES(#{propertyId}, #{couponCode}, #{couponName}, #{amount}, #{threshold}, #{status},
                   #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CouponTemplate entity);

    @Select("""
            SELECT c.id, c.property_id AS propertyId, p.property_name AS propertyName,
                   c.coupon_code AS couponCode, c.coupon_name AS couponName,
                   c.amount, c.threshold_amount AS threshold, c.status
            FROM coupon_template c
            LEFT JOIN hotel_property p ON p.id = c.property_id
            WHERE c.deleted = 0
            ORDER BY c.id DESC
            """)
    List<CouponVO> findAll();
}
