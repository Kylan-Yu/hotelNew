package com.kylan.hotel.service.impl;

import com.kylan.hotel.common.BusinessException;
import com.kylan.hotel.config.UserPrincipal;
import com.kylan.hotel.domain.dto.BrandCreateRequest;
import com.kylan.hotel.domain.dto.BrandUpdateRequest;
import com.kylan.hotel.domain.entity.HotelBrand;
import com.kylan.hotel.domain.entity.HotelGroup;
import com.kylan.hotel.domain.vo.BrandVO;
import com.kylan.hotel.mapper.HotelBrandMapper;
import com.kylan.hotel.mapper.HotelGroupMapper;
import com.kylan.hotel.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final HotelBrandMapper hotelBrandMapper;
    private final HotelGroupMapper hotelGroupMapper;

    @Override
    public Long create(BrandCreateRequest request) {
        HotelGroup group = hotelGroupMapper.findById(request.getGroupId());
        if (group == null) {
            throw new BusinessException("group not found");
        }
        if (hotelBrandMapper.countByBrandCode(request.getBrandCode()) > 0) {
            throw new BusinessException("brandCode already exists");
        }
        HotelBrand entity = new HotelBrand();
        entity.setGroupId(request.getGroupId());
        entity.setBrandCode(request.getBrandCode());
        entity.setBrandName(request.getBrandName());
        entity.setStatus(1);
        entity.setDeleted(0);
        entity.setCreatedBy(currentOperator());
        entity.setUpdatedBy(currentOperator());
        hotelBrandMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public List<BrandVO> list() {
        return hotelBrandMapper.findAll();
    }

    @Override
    public void update(Long id, BrandUpdateRequest request) {
        HotelBrand brand = hotelBrandMapper.findById(id);
        if (brand == null) {
            throw new BusinessException("brand not found");
        }
        HotelGroup group = hotelGroupMapper.findById(request.getGroupId());
        if (group == null) {
            throw new BusinessException("group not found");
        }
        brand.setGroupId(request.getGroupId());
        brand.setBrandName(request.getBrandName());
        brand.setUpdatedBy(currentOperator());
        hotelBrandMapper.updateById(brand);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        HotelBrand brand = hotelBrandMapper.findById(id);
        if (brand == null) {
            throw new BusinessException("brand not found");
        }
        hotelBrandMapper.updateStatusById(id, status, currentOperator());
    }

    private String currentOperator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getUsername();
        }
        return "system";
    }
}
