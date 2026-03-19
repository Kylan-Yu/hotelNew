package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.HotelBrand;
import com.kylan.hotel.domain.vo.BrandVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HotelBrandMapper {

    @Insert("""
            INSERT INTO hotel_brand(group_id, brand_code, brand_name, status, created_by, updated_by, deleted)
            VALUES(#{groupId}, #{brandCode}, #{brandName}, #{status}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HotelBrand entity);

    @Select("""
            SELECT b.id, b.group_id AS groupId, g.group_name AS groupName, b.brand_code AS brandCode, b.brand_name AS brandName, b.status
            FROM hotel_brand b
            LEFT JOIN hotel_group g ON g.id = b.group_id AND g.deleted = 0
            WHERE b.deleted = 0
            ORDER BY b.id DESC
            """)
    List<BrandVO> findAll();

    @Select("""
            SELECT id, group_id AS groupId, brand_code AS brandCode, brand_name AS brandName, status,
                   created_by AS createdBy, created_at AS createdAt, updated_by AS updatedBy, updated_at AS updatedAt, deleted
            FROM hotel_brand
            WHERE id = #{id} AND deleted = 0
            LIMIT 1
            """)
    HotelBrand findById(Long id);

    @Select("SELECT COUNT(1) FROM hotel_brand WHERE brand_code = #{brandCode} AND deleted = 0")
    int countByBrandCode(String brandCode);

    @Update("""
            UPDATE hotel_brand
            SET group_id = #{groupId}, brand_name = #{brandName}, updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int updateById(HotelBrand entity);

    @Update("""
            UPDATE hotel_brand
            SET status = #{status}, updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int updateStatusById(@Param("id") Long id, @Param("status") Integer status, @Param("updatedBy") String updatedBy);
}
