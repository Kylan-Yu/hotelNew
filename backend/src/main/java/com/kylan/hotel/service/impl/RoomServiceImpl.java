package com.kylan.hotel.service.impl;

import com.kylan.hotel.common.BusinessException;
import com.kylan.hotel.common.RoomStatus;
import com.kylan.hotel.common.SecurityUtils;
import com.kylan.hotel.config.UserPrincipal;
import com.kylan.hotel.domain.dto.RoomCreateRequest;
import com.kylan.hotel.domain.dto.RoomStatusUpdateRequest;
import com.kylan.hotel.domain.dto.RoomUpdateRequest;
import com.kylan.hotel.domain.entity.HotelProperty;
import com.kylan.hotel.domain.entity.HotelRoom;
import com.kylan.hotel.domain.entity.HotelRoomStatusLog;
import com.kylan.hotel.domain.entity.HotelRoomType;
import com.kylan.hotel.domain.vo.RoomStatusLogVO;
import com.kylan.hotel.domain.vo.RoomVO;
import com.kylan.hotel.mapper.HotelPropertyMapper;
import com.kylan.hotel.mapper.HotelRoomMapper;
import com.kylan.hotel.mapper.HotelRoomStatusLogMapper;
import com.kylan.hotel.mapper.HotelRoomTypeMapper;
import com.kylan.hotel.mapper.SysDictItemMapper;
import com.kylan.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final HotelRoomMapper hotelRoomMapper;
    private final HotelPropertyMapper hotelPropertyMapper;
    private final HotelRoomTypeMapper hotelRoomTypeMapper;
    private final HotelRoomStatusLogMapper hotelRoomStatusLogMapper;
    private final SysDictItemMapper sysDictItemMapper;

    @Override
    public Long create(RoomCreateRequest request) {
        SecurityUtils.assertPropertyAccessible(request.getPropertyId());
        validatePropertyAndRoomType(request.getPropertyId(), request.getRoomTypeId());
        if (hotelRoomMapper.countByPropertyAndRoomNo(request.getPropertyId(), request.getRoomNo()) > 0) {
            throw new BusinessException("roomNo already exists in property");
        }
        HotelRoom room = new HotelRoom();
        room.setPropertyId(request.getPropertyId());
        room.setRoomTypeId(request.getRoomTypeId());
        room.setRoomNo(request.getRoomNo());
        room.setFloorNo(request.getFloorNo());
        room.setStatus(RoomStatus.VACANT_CLEAN);
        room.setDeleted(0);
        room.setCreatedBy(currentOperator());
        room.setUpdatedBy(currentOperator());
        hotelRoomMapper.insert(room);
        return room.getId();
    }

    @Override
    public List<RoomVO> list() {
        List<RoomVO> list = hotelRoomMapper.findAll();
        List<Long> scopes = SecurityUtils.propertyScopes();
        return list.stream().filter(item -> scopes.contains(item.getPropertyId())).toList();
    }

    @Override
    public void update(Long id, RoomUpdateRequest request) {
        HotelRoom room = hotelRoomMapper.findById(id);
        if (room == null) {
            throw new BusinessException("room not found");
        }
        SecurityUtils.assertPropertyAccessible(room.getPropertyId());
        validatePropertyAndRoomType(room.getPropertyId(), request.getRoomTypeId());
        room.setRoomTypeId(request.getRoomTypeId());
        room.setFloorNo(request.getFloorNo());
        room.setUpdatedBy(currentOperator());
        hotelRoomMapper.updateById(room);
    }

    @Override
    public void updateStatus(Long id, RoomStatusUpdateRequest request) {
        HotelRoom room = hotelRoomMapper.findById(id);
        if (room == null) {
            throw new BusinessException("room not found");
        }
        SecurityUtils.assertPropertyAccessible(room.getPropertyId());
        if (sysDictItemMapper.countEnabledByValue("ROOM_STATUS", request.getStatus()) <= 0) {
            throw new BusinessException("unsupported room status");
        }
        if (room.getStatus().equals(request.getStatus())) {
            return;
        }
        hotelRoomMapper.updateStatusById(id, request.getStatus(), currentOperator());

        HotelRoomStatusLog log = new HotelRoomStatusLog();
        log.setRoomId(id);
        log.setOldStatus(room.getStatus());
        log.setNewStatus(request.getStatus());
        log.setReason(request.getReason());
        log.setOperator(currentOperator());
        hotelRoomStatusLogMapper.insert(log);
    }

    @Override
    public List<RoomStatusLogVO> listStatusLogs(Long roomId) {
        HotelRoom room = hotelRoomMapper.findById(roomId);
        if (room == null) {
            throw new BusinessException("room not found");
        }
        SecurityUtils.assertPropertyAccessible(room.getPropertyId());
        return hotelRoomStatusLogMapper.findByRoomId(roomId);
    }

    private void validatePropertyAndRoomType(Long propertyId, Long roomTypeId) {
        HotelProperty property = hotelPropertyMapper.findById(propertyId);
        if (property == null) {
            throw new BusinessException("property not found");
        }
        HotelRoomType roomType = hotelRoomTypeMapper.findById(roomTypeId);
        if (roomType == null) {
            throw new BusinessException("room type not found");
        }
        if (!propertyId.equals(roomType.getPropertyId())) {
            throw new BusinessException("room type does not belong to property");
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
