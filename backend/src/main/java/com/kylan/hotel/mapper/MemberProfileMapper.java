package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.MemberProfile;
import com.kylan.hotel.domain.vo.MemberVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MemberProfileMapper {

    @Insert("""
            INSERT INTO member_profile(property_id, member_no, member_name, mobile, level_code, point_balance, status,
                                       created_by, updated_by, deleted)
            VALUES(#{propertyId}, #{memberNo}, #{memberName}, #{mobile}, #{levelCode}, #{pointBalance}, #{status},
                   #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MemberProfile entity);

    @Select("""
            SELECT m.id, m.property_id AS propertyId, p.property_name AS propertyName,
                   m.member_no AS memberNo, m.member_name AS memberName, m.mobile,
                   m.level_code AS levelCode, m.point_balance AS pointBalance, m.status
            FROM member_profile m
            LEFT JOIN hotel_property p ON p.id = m.property_id
            WHERE m.deleted = 0
            ORDER BY m.id DESC
            """)
    List<MemberVO> findAll();

    @Select("""
            SELECT id, property_id AS propertyId, member_no AS memberNo, member_name AS memberName, mobile,
                   level_code AS levelCode, point_balance AS pointBalance, status,
                   created_by AS createdBy, created_at AS createdAt, updated_by AS updatedBy, updated_at AS updatedAt, deleted
            FROM member_profile
            WHERE id = #{id} AND deleted = 0
            LIMIT 1
            """)
    MemberProfile findById(Long id);

    @Update("""
            UPDATE member_profile
            SET point_balance = #{pointBalance}, updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int updatePoint(@Param("id") Long id, @Param("pointBalance") Integer pointBalance, @Param("updatedBy") String updatedBy);
}
