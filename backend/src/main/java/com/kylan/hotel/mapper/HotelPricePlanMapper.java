package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.HotelPricePlan;
import com.kylan.hotel.domain.vo.PricePlanVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HotelPricePlanMapper {

    @Insert("""
            INSERT INTO hotel_price_plan(property_id, room_type_id, biz_date, sale_price, sellable_inventory, overbook_limit,
                                         price_tag, created_by, updated_by, deleted)
            VALUES(#{propertyId}, #{roomTypeId}, #{bizDate}, #{salePrice}, #{sellableInventory}, #{overbookLimit},
                   #{priceTag}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HotelPricePlan entity);

    @Update("""
            UPDATE hotel_price_plan
            SET sale_price = #{salePrice}, sellable_inventory = #{sellableInventory},
                overbook_limit = #{overbookLimit}, price_tag = #{priceTag}, updated_by = #{updatedBy}
            WHERE property_id = #{propertyId} AND room_type_id = #{roomTypeId} AND biz_date = #{bizDate} AND deleted = 0
            """)
    int updateByUk(HotelPricePlan entity);

    @Select("SELECT COUNT(1) FROM hotel_price_plan WHERE property_id = #{propertyId} AND room_type_id = #{roomTypeId} AND biz_date = #{bizDate} AND deleted = 0")
    int countByUk(@Param("propertyId") Long propertyId, @Param("roomTypeId") Long roomTypeId, @Param("bizDate") LocalDate bizDate);

    @Select("""
            SELECT pp.id, pp.property_id AS propertyId, p.property_name AS propertyName,
                   pp.room_type_id AS roomTypeId, rt.room_type_name AS roomTypeName,
                   pp.biz_date AS bizDate, pp.sale_price AS salePrice, pp.sellable_inventory AS sellableInventory,
                   pp.overbook_limit AS overbookLimit, pp.price_tag AS priceTag
            FROM hotel_price_plan pp
            LEFT JOIN hotel_property p ON p.id = pp.property_id
            LEFT JOIN hotel_room_type rt ON rt.id = pp.room_type_id
            WHERE pp.deleted = 0
            ORDER BY pp.biz_date DESC, pp.id DESC
            """)
    List<PricePlanVO> findAll();
}
