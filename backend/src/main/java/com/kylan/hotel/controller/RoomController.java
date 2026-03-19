package com.kylan.hotel.controller;

import com.kylan.hotel.common.ApiResponse;
import com.kylan.hotel.domain.dto.RoomCreateRequest;
import com.kylan.hotel.domain.dto.RoomStatusUpdateRequest;
import com.kylan.hotel.domain.dto.RoomUpdateRequest;
import com.kylan.hotel.domain.vo.RoomStatusLogVO;
import com.kylan.hotel.domain.vo.RoomVO;
import com.kylan.hotel.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasAuthority('room:write')")
    public ApiResponse<Long> create(@Valid @RequestBody RoomCreateRequest request) {
        return ApiResponse.success(roomService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('room:read')")
    public ApiResponse<List<RoomVO>> list() {
        return ApiResponse.success(roomService.list());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('room:write')")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody RoomUpdateRequest request) {
        roomService.update(id, request);
        return ApiResponse.success(null);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('room:write')")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody RoomStatusUpdateRequest request) {
        roomService.updateStatus(id, request);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}/status-logs")
    @PreAuthorize("hasAuthority('room:read')")
    public ApiResponse<List<RoomStatusLogVO>> listStatusLogs(@PathVariable Long id) {
        return ApiResponse.success(roomService.listStatusLogs(id));
    }
}
