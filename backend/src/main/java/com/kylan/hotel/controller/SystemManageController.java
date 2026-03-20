package com.kylan.hotel.controller;

import com.kylan.hotel.common.ApiResponse;
import com.kylan.hotel.domain.dto.SystemDictCreateRequest;
import com.kylan.hotel.domain.dto.SystemDictItemCreateRequest;
import com.kylan.hotel.domain.dto.SystemDictItemUpdateRequest;
import com.kylan.hotel.domain.dto.SystemDictUpdateRequest;
import com.kylan.hotel.domain.dto.SystemParamCreateRequest;
import com.kylan.hotel.domain.dto.SystemParamUpdateRequest;
import com.kylan.hotel.domain.dto.SystemRoleCreateRequest;
import com.kylan.hotel.domain.dto.SystemRoleUpdateRequest;
import com.kylan.hotel.domain.dto.SystemUserCreateRequest;
import com.kylan.hotel.domain.dto.SystemUserUpdateRequest;
import com.kylan.hotel.domain.vo.SystemDictVO;
import com.kylan.hotel.domain.vo.SystemDictItemVO;
import com.kylan.hotel.domain.vo.SystemMenuVO;
import com.kylan.hotel.domain.vo.SystemParamVO;
import com.kylan.hotel.domain.vo.SystemPermissionVO;
import com.kylan.hotel.domain.vo.SystemRoleVO;
import com.kylan.hotel.domain.vo.SystemUserVO;
import com.kylan.hotel.service.SystemManageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemManageController {

    private final SystemManageService systemManageService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('sys:user:read')")
    public ApiResponse<List<SystemUserVO>> users() {
        return ApiResponse.success(systemManageService.users());
    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('sys:user:write') or hasAuthority('*')")
    public ApiResponse<Long> createUser(@Valid @RequestBody SystemUserCreateRequest request) {
        return ApiResponse.success(systemManageService.createUser(request));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasAuthority('sys:user:write') or hasAuthority('*')")
    public ApiResponse<Void> updateUser(@PathVariable("id") Long id, @Valid @RequestBody SystemUserUpdateRequest request) {
        systemManageService.updateUser(id, request);
        return ApiResponse.success(null);
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('sys:role:read')")
    public ApiResponse<List<SystemRoleVO>> roles() {
        return ApiResponse.success(systemManageService.roles());
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('sys:role:write') or hasAuthority('*')")
    public ApiResponse<Long> createRole(@Valid @RequestBody SystemRoleCreateRequest request) {
        return ApiResponse.success(systemManageService.createRole(request));
    }

    @PutMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('sys:role:write') or hasAuthority('*')")
    public ApiResponse<Void> updateRole(@PathVariable("id") Long id, @Valid @RequestBody SystemRoleUpdateRequest request) {
        systemManageService.updateRole(id, request);
        return ApiResponse.success(null);
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('sys:permission:read')")
    public ApiResponse<List<SystemPermissionVO>> permissions() {
        return ApiResponse.success(systemManageService.permissions());
    }

    @GetMapping("/menus")
    @PreAuthorize("hasAuthority('sys:menu:read')")
    public ApiResponse<List<SystemMenuVO>> menus() {
        return ApiResponse.success(systemManageService.menus());
    }

    @GetMapping("/dicts")
    @PreAuthorize("hasAuthority('sys:dict:read')")
    public ApiResponse<List<SystemDictVO>> dicts() {
        return ApiResponse.success(systemManageService.dicts());
    }

    @PostMapping("/dicts")
    @PreAuthorize("hasAuthority('sys:dict:write') or hasAuthority('*')")
    public ApiResponse<Long> createDict(@Valid @RequestBody SystemDictCreateRequest request) {
        return ApiResponse.success(systemManageService.createDict(request));
    }

    @GetMapping("/dicts/{dictCode}/items")
    @PreAuthorize("hasAuthority('sys:dict:read')")
    public ApiResponse<List<SystemDictItemVO>> dictItems(@PathVariable("dictCode") String dictCode) {
        return ApiResponse.success(systemManageService.dictItems(dictCode, false));
    }

    @PostMapping("/dicts/{dictCode}/items")
    @PreAuthorize("hasAuthority('sys:dict:write') or hasAuthority('*')")
    public ApiResponse<Long> createDictItem(@PathVariable("dictCode") String dictCode,
                                            @Valid @RequestBody SystemDictItemCreateRequest request) {
        return ApiResponse.success(systemManageService.createDictItem(dictCode, request));
    }

    @PutMapping("/dict-items/{id}")
    @PreAuthorize("hasAuthority('sys:dict:write') or hasAuthority('*')")
    public ApiResponse<Void> updateDictItem(@PathVariable("id") Long id,
                                            @Valid @RequestBody SystemDictItemUpdateRequest request) {
        systemManageService.updateDictItem(id, request);
        return ApiResponse.success(null);
    }

    @PutMapping("/dicts/{dictCode}")
    @PreAuthorize("hasAuthority('sys:dict:write') or hasAuthority('*')")
    public ApiResponse<Void> updateDict(@PathVariable("dictCode") String dictCode, @Valid @RequestBody SystemDictUpdateRequest request) {
        systemManageService.updateDict(dictCode, request);
        return ApiResponse.success(null);
    }

    @GetMapping("/params")
    @PreAuthorize("hasAuthority('sys:param:read')")
    public ApiResponse<List<SystemParamVO>> params() {
        return ApiResponse.success(systemManageService.params());
    }

    @PostMapping("/params")
    @PreAuthorize("hasAuthority('sys:param:write') or hasAuthority('*')")
    public ApiResponse<Long> createParam(@Valid @RequestBody SystemParamCreateRequest request) {
        return ApiResponse.success(systemManageService.createParam(request));
    }

    @PutMapping("/params/{paramKey}")
    @PreAuthorize("hasAuthority('sys:param:write') or hasAuthority('*')")
    public ApiResponse<Void> updateParam(@PathVariable("paramKey") String paramKey, @Valid @RequestBody SystemParamUpdateRequest request) {
        systemManageService.updateParam(paramKey, request);
        return ApiResponse.success(null);
    }
}

