package com.kylan.hotel.service;

import com.kylan.hotel.common.PageResult;
import com.kylan.hotel.domain.dto.LogQueryRequest;
import com.kylan.hotel.domain.entity.AuditLogRecord;
import com.kylan.hotel.domain.entity.OperationLogRecord;
import com.kylan.hotel.domain.vo.BrandVO;
import com.kylan.hotel.domain.vo.GroupVO;
import com.kylan.hotel.domain.vo.LogFilterContextVO;
import com.kylan.hotel.domain.vo.PropertyVO;

import java.util.List;

public interface LogService {
    PageResult<OperationLogRecord> searchOperation(LogQueryRequest request);

    PageResult<AuditLogRecord> searchAudit(LogQueryRequest request);

    LogFilterContextVO filterContext();

    List<GroupVO> groupOptions(Boolean preferCurrent);

    List<BrandVO> brandOptions(Long groupId, Boolean preferCurrent);

    List<PropertyVO> propertyOptions(Long groupId, Long brandId, Boolean preferCurrent);
}
