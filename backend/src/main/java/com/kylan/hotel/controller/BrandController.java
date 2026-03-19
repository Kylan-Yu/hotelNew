package com.kylan.hotel.controller;

import com.kylan.hotel.common.ApiResponse;
import com.kylan.hotel.domain.dto.BrandCreateRequest;
import com.kylan.hotel.domain.dto.BrandUpdateRequest;
import com.kylan.hotel.domain.dto.StatusUpdateRequest;
import com.kylan.hotel.domain.vo.BrandVO;
import com.kylan.hotel.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    @PreAuthorize("hasAuthority('brand:write')")
    public ApiResponse<Long> create(@Valid @RequestBody BrandCreateRequest request) {
        return ApiResponse.success(brandService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('brand:read')")
    public ApiResponse<List<BrandVO>> list() {
        return ApiResponse.success(brandService.list());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('brand:write')")
    public ApiResponse<Void> update(@PathVariable("id") Long id, @Valid @RequestBody BrandUpdateRequest request) {
        brandService.update(id, request);
        return ApiResponse.success(null);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('brand:write')")
    public ApiResponse<Void> updateStatus(@PathVariable("id") Long id, @Valid @RequestBody StatusUpdateRequest request) {
        brandService.updateStatus(id, request.getStatus());
        return ApiResponse.success(null);
    }
}

