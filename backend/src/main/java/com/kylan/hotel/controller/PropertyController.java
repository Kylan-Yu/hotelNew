package com.kylan.hotel.controller;

import com.kylan.hotel.common.ApiResponse;
import com.kylan.hotel.domain.dto.PropertyCreateRequest;
import com.kylan.hotel.domain.dto.PropertyUpdateRequest;
import com.kylan.hotel.domain.dto.StatusUpdateRequest;
import com.kylan.hotel.domain.vo.PropertyVO;
import com.kylan.hotel.service.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    @PreAuthorize("hasAuthority('property:write')")
    public ApiResponse<Long> create(@Valid @RequestBody PropertyCreateRequest request) {
        return ApiResponse.success(propertyService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('property:read')")
    public ApiResponse<List<PropertyVO>> list() {
        return ApiResponse.success(propertyService.list());
    }

    @GetMapping("/scope-options")
    @PreAuthorize("hasAuthority('property:read')")
    public ApiResponse<List<PropertyVO>> listScopeOptions() {
        return ApiResponse.success(propertyService.listScopeOptions());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('property:write')")
    public ApiResponse<Void> update(@PathVariable("id") Long id, @Valid @RequestBody PropertyUpdateRequest request) {
        propertyService.update(id, request);
        return ApiResponse.success(null);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('property:write')")
    public ApiResponse<Void> updateStatus(@PathVariable("id") Long id, @Valid @RequestBody StatusUpdateRequest request) {
        propertyService.updateStatus(id, request.getStatus());
        return ApiResponse.success(null);
    }
}

