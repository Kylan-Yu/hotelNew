package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.HotelGuestProfile;
import com.kylan.hotel.domain.vo.GuestVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HotelGuestProfileMapper {

    @Insert("""
            INSERT INTO hotel_guest_profile(order_id, stay_record_id, guest_name, guest_mobile, certificate_type, certificate_no,
                                           is_primary, created_by, updated_by, deleted)
            VALUES(#{orderId}, #{stayRecordId}, #{guestName}, #{guestMobile}, #{certificateType}, #{certificateNo},
                   #{isPrimary}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HotelGuestProfile entity);

    @Select("""
            SELECT id, guest_name AS guestName, guest_mobile AS guestMobile,
                   certificate_type AS certificateType, certificate_no AS certificateNo,
                   is_primary AS isPrimary
            FROM hotel_guest_profile
            WHERE order_id = #{orderId} AND deleted = 0
            ORDER BY id ASC
            """)
    List<GuestVO> findByOrderId(Long orderId);
}
