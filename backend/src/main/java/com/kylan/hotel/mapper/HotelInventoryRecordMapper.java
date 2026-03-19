package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.HotelInventoryRecord;
import com.kylan.hotel.domain.vo.InventoryVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HotelInventoryRecordMapper {

    @Insert("""
            INSERT INTO hotel_inventory_record(property_id, room_type_id, biz_date, total_inventory, occupied_inventory,
                                               available_inventory, warning_threshold, created_by, updated_by, deleted)
            VALUES(#{propertyId}, #{roomTypeId}, #{bizDate}, #{totalInventory}, #{occupiedInventory},
                   #{availableInventory}, #{warningThreshold}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HotelInventoryRecord entity);

    @Select("""
            SELECT id, property_id AS propertyId, room_type_id AS roomTypeId, biz_date AS bizDate,
                   total_inventory AS totalInventory, occupied_inventory AS occupiedInventory,
                   available_inventory AS availableInventory, warning_threshold AS warningThreshold,
                   created_by AS createdBy, created_at AS createdAt, updated_by AS updatedBy, updated_at AS updatedAt, deleted
            FROM hotel_inventory_record
            WHERE property_id = #{propertyId} AND room_type_id = #{roomTypeId} AND biz_date = #{bizDate} AND deleted = 0
            LIMIT 1
            """)
    HotelInventoryRecord findByUk(@Param("propertyId") Long propertyId, @Param("roomTypeId") Long roomTypeId, @Param("bizDate") LocalDate bizDate);

    @Update("""
            UPDATE hotel_inventory_record
            SET occupied_inventory = #{occupiedInventory}, available_inventory = #{availableInventory},
                warning_threshold = #{warningThreshold}, updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int updateById(HotelInventoryRecord entity);

    @Select("""
            SELECT i.id, i.property_id AS propertyId, p.property_name AS propertyName,
                   i.room_type_id AS roomTypeId, rt.room_type_name AS roomTypeName,
                   i.biz_date AS bizDate, i.total_inventory AS totalInventory,
                   i.occupied_inventory AS occupiedInventory, i.available_inventory AS availableInventory,
                   i.warning_threshold AS warningThreshold
            FROM hotel_inventory_record i
            LEFT JOIN hotel_property p ON p.id = i.property_id
            LEFT JOIN hotel_room_type rt ON rt.id = i.room_type_id
            WHERE i.deleted = 0
            ORDER BY i.biz_date DESC, i.id DESC
            """)
    List<InventoryVO> findAll();
}
