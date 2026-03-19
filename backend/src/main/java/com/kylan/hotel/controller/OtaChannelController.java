package com.kylan.hotel.controller;

import com.kylan.hotel.common.ApiResponse;
import com.kylan.hotel.domain.dto.ChannelMappingCreateRequest;
import com.kylan.hotel.domain.dto.OtaSyncRequest;
import com.kylan.hotel.domain.entity.ChannelCallbackLog;
import com.kylan.hotel.domain.entity.ChannelSyncLog;
import com.kylan.hotel.domain.vo.ChannelMappingVO;
import com.kylan.hotel.service.OtaChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ota")
@RequiredArgsConstructor
public class OtaChannelController {

    private final OtaChannelService otaChannelService;

    @PostMapping("/mappings")
    @PreAuthorize("hasAuthority('ota:write')")
    public ApiResponse<Long> createMapping(@Valid @RequestBody ChannelMappingCreateRequest request) {
        return ApiResponse.success(otaChannelService.createMapping(request));
    }

    @GetMapping("/mappings")
    @PreAuthorize("hasAuthority('ota:read')")
    public ApiResponse<List<ChannelMappingVO>> listMappings() {
        return ApiResponse.success(otaChannelService.listMappings());
    }

    @PostMapping("/sync/inventory")
    @PreAuthorize("hasAuthority('ota:write')")
    public ApiResponse<Void> pushInventory(@RequestBody OtaSyncRequest request) {
        otaChannelService.pushInventory(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/sync/price")
    @PreAuthorize("hasAuthority('ota:write')")
    public ApiResponse<Void> pushPrice(@RequestBody OtaSyncRequest request) {
        otaChannelService.pushPrice(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/sync/orders/pull")
    @PreAuthorize("hasAuthority('ota:write')")
    public ApiResponse<Void> pullOrders(@RequestBody OtaSyncRequest request) {
        otaChannelService.pullOrders(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/callback/{channelCode}")
    public ApiResponse<Void> callback(@PathVariable String channelCode, @RequestBody String payload) {
        otaChannelService.handleCallback(channelCode, payload);
        return ApiResponse.success(null);
    }

    @GetMapping("/sync/logs")
    @PreAuthorize("hasAuthority('ota:read')")
    public ApiResponse<List<ChannelSyncLog>> syncLogs() {
        return ApiResponse.success(otaChannelService.listSyncLogs());
    }

    @GetMapping("/callback/logs")
    @PreAuthorize("hasAuthority('ota:read')")
    public ApiResponse<List<ChannelCallbackLog>> callbackLogs() {
        return ApiResponse.success(otaChannelService.listCallbackLogs());
    }
}
