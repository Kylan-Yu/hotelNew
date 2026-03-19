package com.kylan.hotel.controller;

import com.kylan.hotel.common.ApiResponse;
import com.kylan.hotel.common.PageResult;
import com.kylan.hotel.domain.dto.LogQueryRequest;
import com.kylan.hotel.domain.entity.AuditLogRecord;
import com.kylan.hotel.domain.entity.OperationLogRecord;
import com.kylan.hotel.domain.vo.BrandVO;
import com.kylan.hotel.domain.vo.GroupVO;
import com.kylan.hotel.domain.vo.LogFilterContextVO;
import com.kylan.hotel.domain.vo.PropertyVO;
import com.kylan.hotel.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @PostMapping("/audit/search")
    @PreAuthorize("hasAuthority('report:read')")
    public ApiResponse<PageResult<AuditLogRecord>> auditLogs(@RequestBody LogQueryRequest request) {
        return ApiResponse.success(logService.searchAudit(request));
    }

    @PostMapping("/operation/search")
    @PreAuthorize("hasAuthority('report:read')")
    public ApiResponse<PageResult<OperationLogRecord>> operationLogs(@RequestBody LogQueryRequest request) {
        return ApiResponse.success(logService.searchOperation(request));
    }

    @GetMapping("/options/groups")
    @PreAuthorize("hasAuthority('report:read')")
    public ApiResponse<List<GroupVO>> groupOptions(
            @RequestParam(value = "preferCurrent", required = false, defaultValue = "true") Boolean preferCurrent) {
        return ApiResponse.success(logService.groupOptions(preferCurrent));
    }

    @GetMapping("/options/context")
    @PreAuthorize("hasAuthority('report:read')")
    public ApiResponse<LogFilterContextVO> filterContext() {
        return ApiResponse.success(logService.filterContext());
    }

    @GetMapping("/options/brands")
    @PreAuthorize("hasAuthority('report:read')")
    public ApiResponse<List<BrandVO>> brandOptions(
            @RequestParam(value = "groupId", required = false) Long groupId,
            @RequestParam(value = "preferCurrent", required = false, defaultValue = "true") Boolean preferCurrent) {
        return ApiResponse.success(logService.brandOptions(groupId, preferCurrent));
    }

    @GetMapping("/options/properties")
    @PreAuthorize("hasAuthority('report:read')")
    public ApiResponse<List<PropertyVO>> propertyOptions(
            @RequestParam(value = "groupId", required = false) Long groupId,
            @RequestParam(value = "brandId", required = false) Long brandId,
            @RequestParam(value = "preferCurrent", required = false, defaultValue = "true") Boolean preferCurrent) {
        return ApiResponse.success(logService.propertyOptions(groupId, brandId, preferCurrent));
    }
}
