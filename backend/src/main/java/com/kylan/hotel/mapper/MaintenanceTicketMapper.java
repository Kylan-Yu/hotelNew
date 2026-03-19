package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.MaintenanceTicket;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MaintenanceTicketMapper {
    @Insert("""
            INSERT INTO maintenance_ticket(property_id, room_id, issue_type, issue_description, ticket_status, assignee,
                                           created_by, updated_by, deleted)
            VALUES(#{propertyId}, #{roomId}, #{issueType}, #{issueDescription}, #{ticketStatus}, #{assignee},
                   #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MaintenanceTicket entity);

    @Update("""
            UPDATE maintenance_ticket
            SET ticket_status = #{ticketStatus}, updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int updateStatus(MaintenanceTicket entity);

    @Select("""
            SELECT id, property_id AS propertyId, room_id AS roomId, issue_type AS issueType,
                   issue_description AS issueDescription, ticket_status AS ticketStatus, assignee,
                   created_by AS createdBy, created_at AS createdAt, updated_by AS updatedBy, updated_at AS updatedAt, deleted
            FROM maintenance_ticket
            WHERE deleted = 0
            ORDER BY id DESC
            """)
    List<MaintenanceTicket> findAll();
}
