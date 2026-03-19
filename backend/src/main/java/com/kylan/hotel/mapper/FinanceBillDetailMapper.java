package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.FinanceBillDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FinanceBillDetailMapper {
    @Insert("""
            INSERT INTO finance_bill_detail(property_id, order_id, bill_type, bill_item, amount,
                                            created_by, updated_by, deleted)
            VALUES(#{propertyId}, #{orderId}, #{billType}, #{billItem}, #{amount}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FinanceBillDetail entity);

    @Select("""
            SELECT id, property_id AS propertyId, order_id AS orderId, bill_type AS billType, bill_item AS billItem, amount,
                   created_by AS createdBy, created_at AS createdAt, updated_by AS updatedBy, updated_at AS updatedAt, deleted
            FROM finance_bill_detail
            WHERE deleted = 0
            ORDER BY id DESC
            """)
    List<FinanceBillDetail> findAll();
}
