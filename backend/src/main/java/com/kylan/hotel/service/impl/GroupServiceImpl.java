package com.kylan.hotel.service.impl;

import com.kylan.hotel.common.BusinessException;
import com.kylan.hotel.config.UserPrincipal;
import com.kylan.hotel.domain.dto.GroupCreateRequest;
import com.kylan.hotel.domain.dto.GroupUpdateRequest;
import com.kylan.hotel.domain.entity.HotelGroup;
import com.kylan.hotel.domain.vo.GroupVO;
import com.kylan.hotel.mapper.HotelGroupMapper;
import com.kylan.hotel.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final HotelGroupMapper hotelGroupMapper;

    @Override
    public Long create(GroupCreateRequest request) {
        if (hotelGroupMapper.countByGroupCode(request.getGroupCode()) > 0) {
            throw new BusinessException("groupCode already exists");
        }
        HotelGroup entity = new HotelGroup();
        entity.setGroupCode(request.getGroupCode());
        entity.setGroupName(request.getGroupName());
        entity.setStatus(1);
        entity.setDeleted(0);
        entity.setCreatedBy(currentOperator());
        entity.setUpdatedBy(currentOperator());
        hotelGroupMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public List<GroupVO> list() {
        return hotelGroupMapper.findAll();
    }

    @Override
    public void update(Long id, GroupUpdateRequest request) {
        HotelGroup existing = hotelGroupMapper.findById(id);
        if (existing == null) {
            throw new BusinessException("group not found");
        }
        existing.setGroupName(request.getGroupName());
        existing.setUpdatedBy(currentOperator());
        hotelGroupMapper.updateById(existing);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        HotelGroup existing = hotelGroupMapper.findById(id);
        if (existing == null) {
            throw new BusinessException("group not found");
        }
        hotelGroupMapper.updateStatusById(id, status, currentOperator());
    }

    private String currentOperator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getUsername();
        }
        return "system";
    }
}
