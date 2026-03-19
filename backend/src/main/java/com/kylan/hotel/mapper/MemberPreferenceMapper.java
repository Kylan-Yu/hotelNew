package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.MemberPreference;
import com.kylan.hotel.domain.vo.MemberPreferenceVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MemberPreferenceMapper {

    @Insert("""
            INSERT INTO member_preference(member_id, preference_type, preference_value,
                                          created_by, updated_by, deleted)
            VALUES(#{memberId}, #{preferenceType}, #{preferenceValue}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MemberPreference entity);

    @Select("""
            <script>
            SELECT p.id, p.member_id AS memberId, m.property_id AS propertyId,
                   p.preference_type AS preferenceType, p.preference_value AS preferenceValue,
                   p.created_at AS createdAt
            FROM member_preference p
            LEFT JOIN member_profile m ON m.id = p.member_id AND m.deleted = 0
            WHERE p.deleted = 0
            <if test='memberId != null'>
              AND p.member_id = #{memberId}
            </if>
            ORDER BY p.id DESC
            </script>
            """)
    List<MemberPreferenceVO> findAll(@Param("memberId") Long memberId);
}
