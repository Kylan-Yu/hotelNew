package com.kylan.hotel.controller;

import com.kylan.hotel.common.ApiResponse;
import com.kylan.hotel.domain.dto.*;
import com.kylan.hotel.domain.vo.CampaignVO;
import com.kylan.hotel.domain.vo.CouponVO;
import com.kylan.hotel.domain.vo.MemberPointLedgerVO;
import com.kylan.hotel.domain.vo.MemberPreferenceVO;
import com.kylan.hotel.domain.vo.MemberVO;
import com.kylan.hotel.service.MemberMarketingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member-marketing")
@RequiredArgsConstructor
public class MemberMarketingController {

    private final MemberMarketingService memberMarketingService;

    @PostMapping("/members")
    @PreAuthorize("hasAuthority('member:write')")
    public ApiResponse<Long> createMember(@Valid @RequestBody MemberCreateRequest request) {
        return ApiResponse.success(memberMarketingService.createMember(request));
    }

    @GetMapping("/members")
    @PreAuthorize("hasAuthority('member:read')")
    public ApiResponse<List<MemberVO>> listMembers() {
        return ApiResponse.success(memberMarketingService.listMembers());
    }

    @PatchMapping("/members/points")
    @PreAuthorize("hasAuthority('member:write')")
    public ApiResponse<Void> adjustPoints(@Valid @RequestBody MemberPointAdjustRequest request) {
        memberMarketingService.adjustMemberPoint(request);
        return ApiResponse.success(null);
    }

    @GetMapping("/points")
    @PreAuthorize("hasAuthority('member:read')")
    public ApiResponse<List<MemberPointLedgerVO>> listPointLedgers() {
        return ApiResponse.success(memberMarketingService.listPointLedgers());
    }

    @PostMapping("/coupons")
    @PreAuthorize("hasAuthority('member:write')")
    public ApiResponse<Long> createCoupon(@Valid @RequestBody CouponCreateRequest request) {
        return ApiResponse.success(memberMarketingService.createCoupon(request));
    }

    @GetMapping("/coupons")
    @PreAuthorize("hasAuthority('member:read')")
    public ApiResponse<List<CouponVO>> listCoupons() {
        return ApiResponse.success(memberMarketingService.listCoupons());
    }

    @PostMapping("/preferences")
    @PreAuthorize("hasAuthority('member:write')")
    public ApiResponse<Long> createPreference(@Valid @RequestBody MemberPreferenceCreateRequest request) {
        return ApiResponse.success(memberMarketingService.createPreference(request));
    }

    @GetMapping("/preferences")
    @PreAuthorize("hasAuthority('member:read')")
    public ApiResponse<List<MemberPreferenceVO>> listPreferences(
            @RequestParam(value = "memberId", required = false) Long memberId) {
        return ApiResponse.success(memberMarketingService.listPreferences(memberId));
    }

    @PostMapping("/campaigns")
    @PreAuthorize("hasAuthority('member:write')")
    public ApiResponse<Long> createCampaign(@Valid @RequestBody CampaignCreateRequest request) {
        return ApiResponse.success(memberMarketingService.createCampaign(request));
    }

    @GetMapping("/campaigns")
    @PreAuthorize("hasAuthority('member:read')")
    public ApiResponse<List<CampaignVO>> listCampaigns() {
        return ApiResponse.success(memberMarketingService.listCampaigns());
    }
}
