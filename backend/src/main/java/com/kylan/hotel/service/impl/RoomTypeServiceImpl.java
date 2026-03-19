package com.kylan.hotel.service.impl;

import com.kylan.hotel.common.BusinessException;
import com.kylan.hotel.common.SecurityUtils;
import com.kylan.hotel.config.UserPrincipal;
import com.kylan.hotel.domain.dto.RoomTypeCreateRequest;
import com.kylan.hotel.domain.dto.RoomTypeUpdateRequest;
import com.kylan.hotel.domain.entity.HotelProperty;
import com.kylan.hotel.domain.entity.HotelRoomType;
import com.kylan.hotel.domain.vo.RoomTypeVO;
import com.kylan.hotel.mapper.HotelPropertyMapper;
import com.kylan.hotel.mapper.HotelRoomTypeMapper;
import com.kylan.hotel.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {

    private final HotelRoomTypeMapper hotelRoomTypeMapper;
    private final HotelPropertyMapper hotelPropertyMapper;

    @Override
    public Long create(RoomTypeCreateRequest request) {
        SecurityUtils.assertPropertyAccessible(request.getPropertyId());
        validateProperty(request.getPropertyId());
        if (hotelRoomTypeMapper.countByRoomTypeCode(request.getRoomTypeCode()) > 0) {
            throw new BusinessException("roomTypeCode already exists");
        }
        HotelRoomType entity = new HotelRoomType();
        entity.setPropertyId(request.getPropertyId());
        entity.setRoomTypeCode(request.getRoomTypeCode());
        entity.setRoomTypeName(request.getRoomTypeName());
        entity.setMaxGuestCount(request.getMaxGuestCount());
        entity.setBedType(request.getBedType());
        entity.setBasePrice(request.getBasePrice());
        entity.setStatus(1);
        entity.setDeleted(0);
        entity.setCreatedBy(currentOperator());
        entity.setUpdatedBy(currentOperator());
        hotelRoomTypeMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public List<RoomTypeVO> list() {
        List<RoomTypeVO> list = hotelRoomTypeMapper.findAll();
        if (SecurityUtils.hasPermission("scope:all")) {
            return list;
        }
        List<Long> scopes = SecurityUtils.propertyScopes();
        return list.stream().filter(item -> scopes.contains(item.getPropertyId())).toList();
    }

    @Override
    public void update(Long id, RoomTypeUpdateRequest request) {
        HotelRoomType existing = hotelRoomTypeMapper.findById(id);
        if (existing == null) {
            throw new BusinessException("room type not found");
        }
        SecurityUtils.assertPropertyAccessible(existing.getPropertyId());
        SecurityUtils.assertPropertyAccessible(request.getPropertyId());
        validateProperty(request.getPropertyId());
        existing.setPropertyId(request.getPropertyId());
        existing.setRoomTypeName(request.getRoomTypeName());
        existing.setMaxGuestCount(request.getMaxGuestCount());
        existing.setBedType(request.getBedType());
        existing.setBasePrice(request.getBasePrice());
        existing.setUpdatedBy(currentOperator());
        hotelRoomTypeMapper.updateById(existing);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        HotelRoomType existing = hotelRoomTypeMapper.findById(id);
        if (existing == null) {
            throw new BusinessException("room type not found");
        }
        SecurityUtils.assertPropertyAccessible(existing.getPropertyId());
        hotelRoomTypeMapper.updateStatusById(id, status, currentOperator());
    }

    private void validateProperty(Long propertyId) {
        HotelProperty property = hotelPropertyMapper.findById(propertyId);
        if (property == null) {
            throw new BusinessException("property not found");
        }
    }

    private String currentOperator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getUsername();
        }
        return "system";
    }
}
