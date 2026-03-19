package com.kylan.hotel.service;

import com.kylan.hotel.domain.dto.*;
import com.kylan.hotel.domain.vo.CampaignVO;
import com.kylan.hotel.domain.vo.CouponVO;
import com.kylan.hotel.domain.vo.MemberPointLedgerVO;
import com.kylan.hotel.domain.vo.MemberPreferenceVO;
import com.kylan.hotel.domain.vo.MemberVO;

import java.util.List;

public interface MemberMarketingService {
    Long createMember(MemberCreateRequest request);

    List<MemberVO> listMembers();

    void adjustMemberPoint(MemberPointAdjustRequest request);

    List<MemberPointLedgerVO> listPointLedgers();

    Long createCoupon(CouponCreateRequest request);

    List<CouponVO> listCoupons();

    Long createPreference(MemberPreferenceCreateRequest request);

    List<MemberPreferenceVO> listPreferences(Long memberId);

    Long createCampaign(CampaignCreateRequest request);

    List<CampaignVO> listCampaigns();
}
