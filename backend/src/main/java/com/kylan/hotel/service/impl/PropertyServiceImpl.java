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
import com.kylan.hotel.mapper.SysDictItemMapper;
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
    private final SysDictItemMapper sysDictItemMapper;

    @Override
    public Long create(PropertyCreateRequest request) {
        ResolvedOrg resolvedOrg = resolveGroupBrand(request.getGroupId(), request.getBrandId());
        validateBusinessMode(request.getBusinessMode());
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
        List<Long> scopes = SecurityUtils.propertyScopes();
        return list.stream().filter(item -> scopes.contains(item.getId())).toList();
    }

    @Override
    public List<PropertyVO> listScopeOptions() {
        List<PropertyVO> list = hotelPropertyMapper.findAll();
        UserPrincipal principal = SecurityUtils.currentPrincipal();
        List<String> permissions = principal.getPermissions() == null ? List.of() : principal.getPermissions();
        if (permissions.contains("scope:all")) {
            return list.stream()
                    .filter(item -> item.getStatus() != null && item.getStatus() == 1)
                    .toList();
        }
        List<Long> scopes = principal.getPropertyScopes() == null ? List.of() : principal.getPropertyScopes();
        if (scopes.isEmpty()) {
            return list.stream()
                    .filter(item -> item.getStatus() != null && item.getStatus() == 1)
                    .toList();
        }
        return list.stream()
                .filter(item -> item.getStatus() != null && item.getStatus() == 1)
                .filter(item -> scopes.contains(item.getId()))
                .toList();
    }

    @Override
    public void update(Long id, PropertyUpdateRequest request) {
        HotelProperty existing = hotelPropertyMapper.findById(id);
        if (existing == null) {
            throw new BusinessException("property not found");
        }
        validateBusinessMode(request.getBusinessMode());
        ResolvedOrg resolvedOrg = (request.getGroupId() == null && request.getBrandId() == null)
                ? new ResolvedOrg(existing.getGroupId(), existing.getBrandId())
                : resolveGroupBrand(request.getGroupId(), request.getBrandId());

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
            return resolveDefaultOrg();
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

    /**
     * Compatibility fallback:
     * Older databases may still enforce non-null group_id/brand_id on hotel_property.
     * To keep homestay creation UX simple, we inherit org fields from current property
     * or the earliest property with org fields.
     */
    private ResolvedOrg resolveDefaultOrg() {
        try {
            Long currentPropertyId = SecurityUtils.currentPropertyId();
            if (currentPropertyId != null) {
                HotelProperty currentProperty = hotelPropertyMapper.findById(currentPropertyId);
                if (currentProperty != null && currentProperty.getGroupId() != null) {
                    return new ResolvedOrg(currentProperty.getGroupId(), currentProperty.getBrandId());
                }
            }
        } catch (Exception ignore) {
        }

        HotelProperty fallback = hotelPropertyMapper.findFirstWithOrg();
        if (fallback != null && fallback.getGroupId() != null) {
            return new ResolvedOrg(fallback.getGroupId(), fallback.getBrandId());
        }
        return new ResolvedOrg(null, null);
    }

    private String currentOperator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getUsername();
        }
        return "system";
    }

    private void validateBusinessMode(String businessMode) {
        if (sysDictItemMapper.countEnabledByValue("BUSINESS_MODE", businessMode) <= 0) {
            throw new BusinessException("unsupported business mode");
        }
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
