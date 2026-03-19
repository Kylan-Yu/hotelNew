package com.kylan.hotel.service;

import com.kylan.hotel.domain.dto.GroupCreateRequest;
import com.kylan.hotel.domain.dto.GroupUpdateRequest;
import com.kylan.hotel.domain.vo.GroupVO;

import java.util.List;

public interface GroupService {
    Long create(GroupCreateRequest request);

    List<GroupVO> list();

    void update(Long id, GroupUpdateRequest request);

    void updateStatus(Long id, Integer status);
}
