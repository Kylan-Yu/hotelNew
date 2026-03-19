package com.kylan.hotel.service.impl;

import com.kylan.hotel.common.BusinessException;
import com.kylan.hotel.common.SecurityUtils;
import com.kylan.hotel.config.UserPrincipal;
import com.kylan.hotel.domain.dto.PropertyCreateRequest;
import com.kylan.hotel.domain.dto.PropertyUpdateRequest;
import com.kylan.hotel.domain.entity.HotelBrand;
import com.kylan.hotel.domain.entity.HotelGroup;
import com.kylan.hotel.domain.entity.HotelProperty;
import com.kylan.hotel.domain.vo.PropertyVO;
import com.kylan.hotel.mapper.HotelBrandMapper;
import com.kylan.hotel.mapper.HotelGroupMapper;
import com.kylan.hotel.mapper.HotelPropertyMapper;
import com.kylan.hotel.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final HotelGroupMapper hotelGroupMapper;
    private final HotelBrandMapper hotelBrandMapper;
    private final HotelPropertyMapper hotelPropertyMapper;

    @Override
    public Long create(PropertyCreateRequest request) {
        ResolvedOrg resolvedOrg = resolveGroupBrand(request.getGroupId(), request.getBrandId());
        if (hotelPropertyMapper.countByPropertyCode(request.getPropertyCode()) > 0) {
            throw new BusinessException("propertyCode already exists");
        }
        HotelProperty entity = new HotelProperty();
        entity.setGroupId(resolvedOrg.groupId);
        entity.setBrandId(resolvedOrg.brandId);
        entity.setPropertyCode(request.getPropertyCode());
        entity.setPropertyName(request.getPropertyName());
        entity.setBusinessMode(request.getBusinessMode());
        entity.setContactPhone(request.getContactPhone());
        entity.setProvince(request.getProvince());
        entity.setCity(request.getCity());
        entity.setDistrict(request.getDistrict());
        entity.setAddress(request.getAddress());
        entity.setStatus(1);
        entity.setCreatedBy(currentOperator());
        entity.setUpdatedBy(currentOperator());
        entity.setDeleted(0);
        hotelPropertyMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public List<PropertyVO> list() {
        List<PropertyVO> list = hotelPropertyMapper.findAll();
        if (SecurityUtils.hasPermission("scope:all")) {
            return list;
        }
        List<Long> scopes = SecurityUtils.propertyScopes();
        return list.stream().filter(item -> scopes.contains(item.getId())).toList();
    }

    @Override
    public void update(Long id, PropertyUpdateRequest request) {
        HotelProperty existing = hotelPropertyMapper.findById(id);
        if (existing == null) {
            throw new BusinessException("property not found");
        }
        ResolvedOrg resolvedOrg = resolveGroupBrand(request.getGroupId(), request.getBrandId());

        existing.setGroupId(resolvedOrg.groupId);
        existing.setBrandId(resolvedOrg.brandId);
        existing.setPropertyName(request.getPropertyName());
        existing.setBusinessMode(request.getBusinessMode());
        existing.setContactPhone(request.getContactPhone());
        existing.setProvince(request.getProvince());
        existing.setCity(request.getCity());
        existing.setDistrict(request.getDistrict());
        existing.setAddress(request.getAddress());
        existing.setUpdatedBy(currentOperator());
        hotelPropertyMapper.updateById(existing);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        HotelProperty existing = hotelPropertyMapper.findById(id);
        if (existing == null) {
            throw new BusinessException("property not found");
        }
        hotelPropertyMapper.updateStatusById(id, status, currentOperator());
    }

    private ResolvedOrg resolveGroupBrand(Long groupId, Long brandId) {
        if (groupId == null && brandId == null) {
            return new ResolvedOrg(null, null);
        }

        if (brandId != null) {
            HotelBrand brand = hotelBrandMapper.findById(brandId);
            if (brand == null) {
                throw new BusinessException("brand not found");
            }
            Long resolvedGroupId = groupId == null ? brand.getGroupId() : groupId;
            HotelGroup group = hotelGroupMapper.findById(resolvedGroupId);
            if (group == null) {
                throw new BusinessException("group not found");
            }
            if (!resolvedGroupId.equals(brand.getGroupId())) {
                throw new BusinessException("brand does not belong to group");
            }
            return new ResolvedOrg(resolvedGroupId, brandId);
        }

        HotelGroup group = hotelGroupMapper.findById(groupId);
        if (group == null) {
            throw new BusinessException("group not found");
        }
        return new ResolvedOrg(groupId, null);
    }

    private String currentOperator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getUsername();
        }
        return "system";
    }

    private static class ResolvedOrg {
        private final Long groupId;
        private final Long brandId;

        private ResolvedOrg(Long groupId, Long brandId) {
            this.groupId = groupId;
            this.brandId = brandId;
        }
    }
}
