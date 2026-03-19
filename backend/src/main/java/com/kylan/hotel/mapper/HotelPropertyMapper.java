package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.HotelProperty;
import com.kylan.hotel.domain.vo.PropertyVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HotelPropertyMapper {

    @Insert("""
            INSERT INTO hotel_property(group_id, brand_id, property_code, property_name, business_mode, contact_phone, province, city, district, address, status, created_by, updated_by, deleted)
            VALUES(#{groupId}, #{brandId}, #{propertyCode}, #{propertyName}, #{businessMode}, #{contactPhone}, #{province}, #{city}, #{district}, #{address}, #{status}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HotelProperty entity);

    @Select("""
            SELECT p.id,
                   p.group_id AS groupId,
                   g.group_name AS groupName,
                   p.brand_id AS brandId,
                   b.brand_name AS brandName,
                   p.property_code AS propertyCode,
                   p.property_name AS propertyName,
                   p.business_mode AS businessMode,
                   p.contact_phone AS contactPhone,
                   p.city,
                   p.address,
                   p.status
            FROM hotel_property p
            LEFT JOIN hotel_group g ON g.id = p.group_id AND g.deleted = 0
            LEFT JOIN hotel_brand b ON b.id = p.brand_id AND b.deleted = 0
            WHERE p.deleted = 0
            ORDER BY p.id DESC
            """)
    List<PropertyVO> findAll();

    @Select("""
            SELECT id, group_id AS groupId, brand_id AS brandId, property_code AS propertyCode, property_name AS propertyName,
                   business_mode AS businessMode, contact_phone AS contactPhone, province, city, district, address, status,
                   created_by AS createdBy, created_at AS createdAt, updated_by AS updatedBy, updated_at AS updatedAt, deleted
            FROM hotel_property
            WHERE id = #{id} AND deleted = 0
            LIMIT 1
            """)
    HotelProperty findById(Long id);

    @Select("SELECT COUNT(1) FROM hotel_property WHERE property_code = #{propertyCode} AND deleted = 0")
    int countByPropertyCode(String propertyCode);

    @Update("""
            UPDATE hotel_property
            SET group_id = #{groupId},
                brand_id = #{brandId},
                property_name = #{propertyName},
                business_mode = #{businessMode},
                contact_phone = #{contactPhone},
                province = #{province},
                city = #{city},
                district = #{district},
                address = #{address},
                updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int updateById(HotelProperty entity);

    @Update("""
            UPDATE hotel_property
            SET status = #{status}, updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int updateStatusById(@Param("id") Long id, @Param("status") Integer status, @Param("updatedBy") String updatedBy);

    @Select("""
            <script>
            SELECT DISTINCT brand_id
            FROM hotel_property
            WHERE deleted = 0
              AND id IN
              <foreach collection='propertyIds' item='id' open='(' separator=',' close=')'>
                #{id}
              </foreach>
            </script>
            """)
    List<Long> findBrandIdsByPropertyIds(@Param("propertyIds") List<Long> propertyIds);

    @Select("""
            <script>
            SELECT DISTINCT group_id
            FROM hotel_property
            WHERE deleted = 0
              AND id IN
              <foreach collection='propertyIds' item='id' open='(' separator=',' close=')'>
                #{id}
              </foreach>
            </script>
            """)
    List<Long> findGroupIdsByPropertyIds(@Param("propertyIds") List<Long> propertyIds);
}
