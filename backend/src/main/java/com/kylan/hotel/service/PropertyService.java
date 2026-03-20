package com.kylan.hotel.service;

import com.kylan.hotel.domain.dto.PropertyCreateRequest;
import com.kylan.hotel.domain.dto.PropertyUpdateRequest;
import com.kylan.hotel.domain.vo.PropertyVO;

import java.util.List;

public interface PropertyService {
    Long create(PropertyCreateRequest request);

    List<PropertyVO> list();

    List<PropertyVO> listScopeOptions();

    void update(Long id, PropertyUpdateRequest request);

    void updateStatus(Long id, Integer status);
}
