package com.kylan.hotel.controller;

import com.kylan.hotel.common.ApiResponse;
import com.kylan.hotel.domain.dto.RoomTypeCreateRequest;
import com.kylan.hotel.domain.dto.RoomTypeUpdateRequest;
import com.kylan.hotel.domain.dto.StatusUpdateRequest;
import com.kylan.hotel.domain.vo.RoomTypeVO;
import com.kylan.hotel.service.RoomTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room-types")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @PostMapping
    @PreAuthorize("hasAuthority('roomType:write')")
    public ApiResponse<Long> create(@Valid @RequestBody RoomTypeCreateRequest request) {
        return ApiResponse.success(roomTypeService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('roomType:read')")
    public ApiResponse<List<RoomTypeVO>> list() {
        return ApiResponse.success(roomTypeService.list());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('roomType:write')")
    public ApiResponse<Void> update(@PathVariable("id") Long id, @Valid @RequestBody RoomTypeUpdateRequest request) {
        roomTypeService.update(id, request);
        return ApiResponse.success(null);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('roomType:write')")
    public ApiResponse<Void> updateStatus(@PathVariable("id") Long id, @Valid @RequestBody StatusUpdateRequest request) {
        roomTypeService.updateStatus(id, request.getStatus());
        return ApiResponse.success(null);
    }
}

