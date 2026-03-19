package com.kylan.hotel.service;

import com.kylan.hotel.domain.dto.RoomCreateRequest;
import com.kylan.hotel.domain.dto.RoomStatusUpdateRequest;
import com.kylan.hotel.domain.dto.RoomUpdateRequest;
import com.kylan.hotel.domain.vo.RoomStatusLogVO;
import com.kylan.hotel.domain.vo.RoomVO;

import java.util.List;

public interface RoomService {
    Long create(RoomCreateRequest request);

    List<RoomVO> list();

    void update(Long id, RoomUpdateRequest request);

    void updateStatus(Long id, RoomStatusUpdateRequest request);

    List<RoomStatusLogVO> listStatusLogs(Long roomId);
}
