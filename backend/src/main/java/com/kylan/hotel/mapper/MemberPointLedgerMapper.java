package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.MemberPointLedger;
import com.kylan.hotel.domain.vo.MemberPointLedgerVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MemberPointLedgerMapper {
    @Insert("""
            INSERT INTO member_point_ledger(member_id, point_delta, biz_type, biz_no, remark,
                                            created_by, updated_by, deleted)
            VALUES(#{memberId}, #{pointDelta}, #{bizType}, #{bizNo}, #{remark}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MemberPointLedger entity);

    @Select("""
            SELECT l.id, l.member_id AS memberId, m.property_id AS propertyId, l.point_delta AS pointDelta,
                   l.biz_type AS bizType, l.biz_no AS bizNo, l.remark, l.created_at AS createdAt
            FROM member_point_ledger l
            LEFT JOIN member_profile m ON m.id = l.member_id AND m.deleted = 0
            WHERE l.deleted = 0
            ORDER BY l.id DESC
            """)
    List<MemberPointLedgerVO> findAll();
}
