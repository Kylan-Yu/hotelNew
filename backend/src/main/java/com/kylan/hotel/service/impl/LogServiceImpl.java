package com.kylan.hotel.service.impl;

import com.kylan.hotel.common.PageResult;
import com.kylan.hotel.common.SecurityUtils;
import com.kylan.hotel.domain.dto.LogQueryRequest;
import com.kylan.hotel.domain.entity.AuditLogRecord;
import com.kylan.hotel.domain.entity.OperationLogRecord;
import com.kylan.hotel.domain.vo.BrandVO;
import com.kylan.hotel.domain.vo.GroupVO;
import com.kylan.hotel.domain.vo.LogFilterContextVO;
import com.kylan.hotel.domain.vo.PropertyVO;
import com.kylan.hotel.mapper.AuditLogRecordMapper;
import com.kylan.hotel.mapper.OperationLogRecordMapper;
import com.kylan.hotel.service.BrandService;
import com.kylan.hotel.service.GroupService;
import com.kylan.hotel.service.LogService;
import com.kylan.hotel.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final OperationLogRecordMapper operationLogRecordMapper;
    private final AuditLogRecordMapper auditLogRecordMapper;
    private final GroupService groupService;
    private final BrandService brandService;
    private final PropertyService propertyService;

    @Override
    public PageResult<OperationLogRecord> searchOperation(LogQueryRequest request) {
        applyCurrentPropertyDefault(request);
        Long total = operationLogRecordMapper.count(request);
        List<OperationLogRecord> records = operationLogRecordMapper.search(request);
        return PageResult.of(total, request.getPageNo(), request.getPageSize(), records);
    }

    @Override
    public PageResult<AuditLogRecord> searchAudit(LogQueryRequest request) {
        applyCurrentPropertyDefault(request);
        Long total = auditLogRecordMapper.count(request);
        List<AuditLogRecord> records = auditLogRecordMapper.search(request);
        return PageResult.of(total, request.getPageNo(), request.getPageSize(), records);
    }

    @Override
    public LogFilterContextVO filterContext() {
        List<PropertyVO> visibleProperties = propertyService.list();
        PropertyVO currentProperty = findCurrentProperty(visibleProperties);
        LogFilterContextVO context = new LogFilterContextVO();
        context.setCurrentPropertyId(currentProperty == null ? null : currentProperty.getId());
        context.setCurrentBrandId(currentProperty == null ? null : currentProperty.getBrandId());
        context.setCurrentGroupId(currentProperty == null ? null : currentProperty.getGroupId());
        context.setAllowWideRangeSwitch(SecurityUtils.hasPermission("scope:all"));
        return context;
    }

    @Override
    public List<GroupVO> groupOptions(Boolean preferCurrent) {
        List<PropertyVO> visibleProperties = propertyService.list();
        PropertyVO currentProperty = Boolean.TRUE.equals(preferCurrent) ? findCurrentProperty(visibleProperties) : null;
        Long effectiveGroupId = currentProperty == null ? null : currentProperty.getGroupId();
        Set<Long> groupIds = visibleProperties.stream().map(PropertyVO::getGroupId).collect(Collectors.toSet());
        return groupService.list().stream()
                .filter(item -> groupIds.contains(item.getId()))
                .filter(item -> effectiveGroupId == null || effectiveGroupId.equals(item.getId()))
                .sorted(currentFirstComparator(
                        currentProperty == null ? null : currentProperty.getGroupId(),
                        GroupVO::getId
                ))
                .toList();
    }

    @Override
    public List<BrandVO> brandOptions(Long groupId, Boolean preferCurrent) {
        List<PropertyVO> visibleProperties = propertyService.list();
        PropertyVO currentProperty = Boolean.TRUE.equals(preferCurrent) ? findCurrentProperty(visibleProperties) : null;
        Long effectiveGroupId = groupId != null ? groupId : (currentProperty == null ? null : currentProperty.getGroupId());
        Set<Long> brandIds = visibleProperties.stream().map(PropertyVO::getBrandId).collect(Collectors.toSet());
        return brandService.list().stream()
                .filter(item -> brandIds.contains(item.getId()))
                .filter(item -> effectiveGroupId == null || effectiveGroupId.equals(item.getGroupId()))
                .sorted(currentFirstComparator(
                        currentProperty == null ? null : currentProperty.getBrandId(),
                        BrandVO::getId
                ))
                .toList();
    }

    @Override
    public List<PropertyVO> propertyOptions(Long groupId, Long brandId, Boolean preferCurrent) {
        List<PropertyVO> visibleProperties = propertyService.list();
        PropertyVO currentProperty = Boolean.TRUE.equals(preferCurrent) ? findCurrentProperty(visibleProperties) : null;
        Long effectiveGroupId = groupId != null ? groupId : (currentProperty == null ? null : currentProperty.getGroupId());
        Long effectiveBrandId = brandId != null ? brandId : (currentProperty == null ? null : currentProperty.getBrandId());

        return visibleProperties.stream()
                .filter(item -> effectiveGroupId == null || effectiveGroupId.equals(item.getGroupId()))
                .filter(item -> effectiveBrandId == null || effectiveBrandId.equals(item.getBrandId()))
                .sorted(currentFirstComparator(
                        currentProperty == null ? null : currentProperty.getId(),
                        PropertyVO::getId
                ))
                .toList();
    }

    private PropertyVO findCurrentProperty(List<PropertyVO> properties) {
        Long currentPropertyId = SecurityUtils.currentPropertyId();
        if (currentPropertyId == null) {
            return null;
        }
        return properties.stream()
                .filter(item -> currentPropertyId.equals(item.getId()))
                .findFirst()
                .orElse(null);
    }

    private <T> Comparator<T> currentFirstComparator(Long currentId, java.util.function.Function<T, Long> idGetter) {
        return (a, b) -> {
            if (currentId != null) {
                boolean aCurrent = currentId.equals(idGetter.apply(a));
                boolean bCurrent = currentId.equals(idGetter.apply(b));
                if (aCurrent && !bCurrent) {
                    return -1;
                }
                if (!aCurrent && bCurrent) {
                    return 1;
                }
            }
            return Long.compare(idGetter.apply(a), idGetter.apply(b));
        };
    }

    private void applyCurrentPropertyDefault(LogQueryRequest request) {
        if (request == null) {
            return;
        }
        if (request.getPropertyId() != null) {
            return;
        }
        if (!Boolean.TRUE.equals(request.getPreferCurrent())) {
            return;
        }
        Long currentPropertyId = SecurityUtils.currentPropertyId();
        if (currentPropertyId != null) {
            request.setPropertyId(currentPropertyId);
        }
    }

}
