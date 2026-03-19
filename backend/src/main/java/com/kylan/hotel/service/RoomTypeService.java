package com.kylan.hotel.service;

import com.kylan.hotel.domain.dto.RoomTypeCreateRequest;
import com.kylan.hotel.domain.dto.RoomTypeUpdateRequest;
import com.kylan.hotel.domain.vo.RoomTypeVO;

import java.util.List;

public interface RoomTypeService {
    Long create(RoomTypeCreateRequest request);

    List<RoomTypeVO> list();

    void update(Long id, RoomTypeUpdateRequest request);

    void updateStatus(Long id, Integer status);
}
