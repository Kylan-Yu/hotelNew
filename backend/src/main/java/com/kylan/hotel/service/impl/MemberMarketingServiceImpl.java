package com.kylan.hotel.service.impl;

import com.kylan.hotel.common.BusinessException;
import com.kylan.hotel.common.SecurityUtils;
import com.kylan.hotel.domain.dto.*;
import com.kylan.hotel.domain.entity.*;
import com.kylan.hotel.domain.vo.CampaignVO;
import com.kylan.hotel.domain.vo.CouponVO;
import com.kylan.hotel.domain.vo.MemberPointLedgerVO;
import com.kylan.hotel.domain.vo.MemberPreferenceVO;
import com.kylan.hotel.domain.vo.MemberVO;
import com.kylan.hotel.mapper.*;
import com.kylan.hotel.service.MemberMarketingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberMarketingServiceImpl implements MemberMarketingService {

    private final MemberProfileMapper memberProfileMapper;
    private final MemberPointLedgerMapper memberPointLedgerMapper;
    private final CouponTemplateMapper couponTemplateMapper;
    private final MemberPreferenceMapper memberPreferenceMapper;
    private final MarketingCampaignMapper marketingCampaignMapper;

    @Override
    public Long createMember(MemberCreateRequest request) {
        SecurityUtils.assertPropertyAccessible(request.getPropertyId());
        MemberProfile member = new MemberProfile();
        member.setPropertyId(request.getPropertyId());
        member.setMemberNo("M" + System.currentTimeMillis());
        member.setMemberName(request.getMemberName());
        member.setMobile(request.getMobile());
        member.setLevelCode(request.getLevelCode() == null ? 1 : request.getLevelCode());
        member.setPointBalance(0);
        member.setStatus("ACTIVE");
        member.setCreatedBy(SecurityUtils.currentUsername());
        member.setUpdatedBy(SecurityUtils.currentUsername());
        member.setDeleted(0);
        memberProfileMapper.insert(member);
        return member.getId();
    }

    @Override
    public List<MemberVO> listMembers() {
        return memberProfileMapper.findAll().stream()
                .filter(item -> canAccessProperty(item.getPropertyId()))
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustMemberPoint(MemberPointAdjustRequest request) {
        MemberProfile member = memberProfileMapper.findById(request.getMemberId());
        if (member == null) {
            throw new BusinessException("member not found");
        }
        SecurityUtils.assertPropertyAccessible(member.getPropertyId());
        int nextPoint = member.getPointBalance() + request.getPointDelta();
        if (nextPoint < 0) {
            throw new BusinessException("insufficient member points");
        }
        memberProfileMapper.updatePoint(member.getId(), nextPoint, SecurityUtils.currentUsername());

        MemberPointLedger ledger = new MemberPointLedger();
        ledger.setMemberId(member.getId());
        ledger.setPointDelta(request.getPointDelta());
        ledger.setBizType(request.getBizType());
        ledger.setBizNo(request.getBizNo());
        ledger.setRemark(request.getRemark());
        ledger.setCreatedBy(SecurityUtils.currentUsername());
        ledger.setUpdatedBy(SecurityUtils.currentUsername());
        ledger.setDeleted(0);
        memberPointLedgerMapper.insert(ledger);
    }

    @Override
    public List<MemberPointLedgerVO> listPointLedgers() {
        return memberPointLedgerMapper.findAll().stream()
                .filter(item -> canAccessProperty(item.getPropertyId()))
                .toList();
    }

    @Override
    public Long createCoupon(CouponCreateRequest request) {
        SecurityUtils.assertPropertyAccessible(request.getPropertyId());
        CouponTemplate coupon = new CouponTemplate();
        coupon.setPropertyId(request.getPropertyId());
        coupon.setCouponCode(request.getCouponCode());
        coupon.setCouponName(request.getCouponName());
        coupon.setAmount(request.getAmount());
        coupon.setThreshold(request.getThreshold());
        coupon.setStatus("ACTIVE");
        coupon.setCreatedBy(SecurityUtils.currentUsername());
        coupon.setUpdatedBy(SecurityUtils.currentUsername());
        coupon.setDeleted(0);
        couponTemplateMapper.insert(coupon);
        return coupon.getId();
    }

    @Override
    public List<CouponVO> listCoupons() {
        return couponTemplateMapper.findAll().stream()
                .filter(item -> canAccessProperty(item.getPropertyId()))
                .toList();
    }

    @Override
    public Long createPreference(MemberPreferenceCreateRequest request) {
        MemberPreference preference = new MemberPreference();
        preference.setMemberId(request.getMemberId());
        preference.setPreferenceType(request.getPreferenceType());
        preference.setPreferenceValue(request.getPreferenceValue());
        preference.setCreatedBy(SecurityUtils.currentUsername());
        preference.setUpdatedBy(SecurityUtils.currentUsername());
        preference.setDeleted(0);
        memberPreferenceMapper.insert(preference);
        return preference.getId();
    }

    @Override
    public List<MemberPreferenceVO> listPreferences(Long memberId) {
        return memberPreferenceMapper.findAll(memberId).stream()
                .filter(item -> canAccessProperty(item.getPropertyId()))
                .toList();
    }

    @Override
    public Long createCampaign(CampaignCreateRequest request) {
        SecurityUtils.assertPropertyAccessible(request.getPropertyId());
        MarketingCampaign campaign = new MarketingCampaign();
        campaign.setPropertyId(request.getPropertyId());
        campaign.setCampaignCode(request.getCampaignCode());
        campaign.setCampaignName(request.getCampaignName());
        campaign.setCampaignType(request.getCampaignType());
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());
        campaign.setStatus("ACTIVE");
        campaign.setCreatedBy(SecurityUtils.currentUsername());
        campaign.setUpdatedBy(SecurityUtils.currentUsername());
        campaign.setDeleted(0);
        marketingCampaignMapper.insert(campaign);
        return campaign.getId();
    }

    @Override
    public List<CampaignVO> listCampaigns() {
        return marketingCampaignMapper.findAll().stream()
                .filter(item -> canAccessProperty(item.getPropertyId()))
                .toList();
    }

    private boolean canAccessProperty(Long propertyId) {
        if (SecurityUtils.hasPermission("scope:all")) {
            return true;
        }
        return SecurityUtils.propertyScopes().contains(propertyId);
    }
}
