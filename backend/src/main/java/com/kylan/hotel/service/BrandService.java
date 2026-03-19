package com.kylan.hotel.service;

import com.kylan.hotel.domain.dto.BrandCreateRequest;
import com.kylan.hotel.domain.dto.BrandUpdateRequest;
import com.kylan.hotel.domain.vo.BrandVO;

import java.util.List;

public interface BrandService {
    Long create(BrandCreateRequest request);

    List<BrandVO> list();

    void update(Long id, BrandUpdateRequest request);

    void updateStatus(Long id, Integer status);
}
