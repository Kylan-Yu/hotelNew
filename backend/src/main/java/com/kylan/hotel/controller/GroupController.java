package com.kylan.hotel.controller;

import com.kylan.hotel.common.ApiResponse;
import com.kylan.hotel.domain.dto.GroupCreateRequest;
import com.kylan.hotel.domain.dto.GroupUpdateRequest;
import com.kylan.hotel.domain.dto.StatusUpdateRequest;
import com.kylan.hotel.domain.vo.GroupVO;
import com.kylan.hotel.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @PreAuthorize("hasAuthority('group:write')")
    public ApiResponse<Long> create(@Valid @RequestBody GroupCreateRequest request) {
        return ApiResponse.success(groupService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('group:read')")
    public ApiResponse<List<GroupVO>> list() {
        return ApiResponse.success(groupService.list());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('group:write')")
    public ApiResponse<Void> update(@PathVariable("id") Long id, @Valid @RequestBody GroupUpdateRequest request) {
        groupService.update(id, request);
        return ApiResponse.success(null);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('group:write')")
    public ApiResponse<Void> updateStatus(@PathVariable("id") Long id, @Valid @RequestBody StatusUpdateRequest request) {
        groupService.updateStatus(id, request.getStatus());
        return ApiResponse.success(null);
    }
}

