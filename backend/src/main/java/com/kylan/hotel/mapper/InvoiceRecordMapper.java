package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.InvoiceRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InvoiceRecordMapper {
    @Insert("""
            INSERT INTO invoice_record(order_id, property_id, invoice_type, invoice_title, tax_no, amount, invoice_status,
                                       created_by, updated_by, deleted)
            VALUES(#{orderId}, #{propertyId}, #{invoiceType}, #{invoiceTitle}, #{taxNo}, #{amount}, #{invoiceStatus},
                   #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(InvoiceRecord entity);

    @Select("""
            SELECT id, order_id AS orderId, property_id AS propertyId, invoice_type AS invoiceType,
                   invoice_title AS invoiceTitle, tax_no AS taxNo, amount, invoice_status AS invoiceStatus,
                   created_by AS createdBy, created_at AS createdAt, updated_by AS updatedBy, updated_at AS updatedAt, deleted
            FROM invoice_record
            WHERE deleted = 0
            ORDER BY id DESC
            """)
    List<InvoiceRecord> findAll();
}
