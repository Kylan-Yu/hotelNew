package com.kylan.hotel.service.impl;

import com.kylan.hotel.common.BusinessException;
import com.kylan.hotel.common.SecurityUtils;
import com.kylan.hotel.domain.dto.SystemDictCreateRequest;
import com.kylan.hotel.domain.dto.SystemDictUpdateRequest;
import com.kylan.hotel.domain.dto.SystemParamCreateRequest;
import com.kylan.hotel.domain.dto.SystemParamUpdateRequest;
import com.kylan.hotel.domain.dto.SystemRoleCreateRequest;
import com.kylan.hotel.domain.dto.SystemRoleUpdateRequest;
import com.kylan.hotel.domain.dto.SystemUserCreateRequest;
import com.kylan.hotel.domain.dto.SystemUserUpdateRequest;
import com.kylan.hotel.domain.entity.SysUser;
import com.kylan.hotel.domain.vo.SystemDictVO;
import com.kylan.hotel.domain.vo.SystemMenuVO;
import com.kylan.hotel.domain.vo.SystemParamVO;
import com.kylan.hotel.domain.vo.SystemPermissionVO;
import com.kylan.hotel.domain.vo.SystemRoleVO;
import com.kylan.hotel.domain.vo.SystemUserVO;
import com.kylan.hotel.mapper.SysMenuSimpleMapper;
import com.kylan.hotel.mapper.SysRoleSimpleMapper;
import com.kylan.hotel.mapper.SysUserMapper;
import com.kylan.hotel.service.SystemManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class SystemManageServiceImpl implements SystemManageService {

    private static final String DEFAULT_PASSWORD_HASH = "$2b$12$Shy81wwJpP1bv22tA6r2Ruxketd61fSbggJjfkzix1ssC946nQmPq";

    private final SysUserMapper sysUserMapper;
    private final SysRoleSimpleMapper sysRoleSimpleMapper;
    private final SysMenuSimpleMapper sysMenuSimpleMapper;

    private final CopyOnWriteArrayList<SystemDictVO> dictStore = new CopyOnWriteArrayList<>(List.of(
            SystemDictVO.builder()
                    .dictCode("ROOM_TIMELINE_NODE")
                    .dictName("房态时间线节点 / Room Timeline Node")
                    .remark("订单详情时间线节点国际化字典 / i18n dictionary for room timeline nodes")
                    .build(),
            SystemDictVO.builder()
                    .dictCode("ROOM_TIMELINE_ACTION")
                    .dictName("房态时间线动作 / Room Timeline Action")
                    .remark("订单详情时间线动作国际化字典 / i18n dictionary for room timeline actions")
                    .build(),
            SystemDictVO.builder()
                    .dictCode("ROOM_TIMELINE_REMARK")
                    .dictName("房态时间线备注 / Room Timeline Remark")
                    .remark("订单详情时间线备注类型字典 / remark type dictionary for room timeline")
                    .build(),
            SystemDictVO.builder()
                    .dictCode("BIZ_MODE")
                    .dictName("经营模式 / Business Mode")
                    .remark("酒店/民宿模式字典 / hotel and homestay mode dictionary")
                    .build()
    ));

    private final CopyOnWriteArrayList<SystemParamVO> paramStore = new CopyOnWriteArrayList<>(List.of(
            SystemParamVO.builder()
                    .paramKey("hms.currentProperty.prefer")
                    .paramValue("true")
                    .remark("日志筛选默认按当前门店收敛 / log filters prefer current property by default")
                    .build(),
            SystemParamVO.builder()
                    .paramKey("hms.ota.callback.maxRetry")
                    .paramValue("5")
                    .remark("OTA回调最大重试次数 / max retry count for OTA callbacks")
                    .build(),
            SystemParamVO.builder()
                    .paramKey("hms.ota.callback.firstRetrySeconds")
                    .paramValue("30")
                    .remark("OTA回调首次重试秒数 / first retry delay seconds for OTA callbacks")
                    .build()
    ));

    @Override
    public List<SystemUserVO> users() {
        return sysUserMapper.findAll().stream()
                .map(this::toUserVO)
                .toList();
    }

    @Override
    public List<SystemRoleVO> roles() {
        return sysRoleSimpleMapper.findAll();
    }

    @Override
    public List<SystemPermissionVO> permissions() {
        return sysMenuSimpleMapper.findPermissions();
    }

    @Override
    public List<SystemMenuVO> menus() {
        return sysMenuSimpleMapper.findAll();
    }

    @Override
    public List<SystemDictVO> dicts() {
        return List.copyOf(dictStore);
    }

    @Override
    public List<SystemParamVO> params() {
        return List.copyOf(paramStore);
    }

    @Override
    public Long createUser(SystemUserCreateRequest request) {
        if (sysUserMapper.countByUsername(request.getUsername()) > 0) {
            throw new BusinessException("username already exists");
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(DEFAULT_PASSWORD_HASH);
        user.setNickname(request.getNickname());
        user.setMobile(request.getMobile());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        user.setCreatedBy(SecurityUtils.currentUsername());
        user.setUpdatedBy(SecurityUtils.currentUsername());
        user.setDeleted(0);
        sysUserMapper.insertSimple(user);
        return user.getId();
    }

    @Override
    public void updateUser(Long id, SystemUserUpdateRequest request) {
        SysUser existing = sysUserMapper.findById(id);
        if (existing == null) {
            throw new BusinessException("user not found");
        }
        existing.setNickname(request.getNickname());
        existing.setMobile(request.getMobile());
        existing.setEmail(request.getEmail());
        existing.setStatus(request.getStatus() == null ? existing.getStatus() : request.getStatus());
        existing.setUpdatedBy(SecurityUtils.currentUsername());
        sysUserMapper.updateSimple(existing);
    }

    @Override
    public Long createRole(SystemRoleCreateRequest request) {
        if (sysRoleSimpleMapper.countByRoleCode(request.getRoleCode()) > 0) {
            throw new BusinessException("roleCode already exists");
        }
        sysRoleSimpleMapper.insertSimple(
                request.getRoleCode(),
                request.getRoleName(),
                request.getStatus() == null ? 1 : request.getStatus(),
                SecurityUtils.currentUsername()
        );
        SystemRoleVO role = sysRoleSimpleMapper.findByRoleCode(request.getRoleCode());
        return role == null ? null : role.getId();
    }

    @Override
    public void updateRole(Long id, SystemRoleUpdateRequest request) {
        SystemRoleVO existing = sysRoleSimpleMapper.findById(id);
        if (existing == null) {
            throw new BusinessException("role not found");
        }
        sysRoleSimpleMapper.updateSimple(
                id,
                request.getRoleName(),
                request.getStatus() == null ? existing.getStatus() : request.getStatus(),
                SecurityUtils.currentUsername()
        );
    }

    @Override
    public Long createDict(SystemDictCreateRequest request) {
        boolean exists = dictStore.stream().anyMatch(item -> item.getDictCode().equalsIgnoreCase(request.getDictCode()));
        if (exists) {
            throw new BusinessException("dictCode already exists");
        }
        dictStore.add(0, SystemDictVO.builder()
                .dictCode(request.getDictCode())
                .dictName(request.getDictName())
                .remark(request.getRemark())
                .build());
        return (long) dictStore.size();
    }

    @Override
    public void updateDict(String dictCode, SystemDictUpdateRequest request) {
        SystemDictVO target = dictStore.stream().filter(item -> item.getDictCode().equalsIgnoreCase(dictCode)).findFirst().orElse(null);
        if (target == null) {
            throw new BusinessException("dict not found");
        }
        target.setDictName(request.getDictName());
        target.setRemark(request.getRemark());
    }

    @Override
    public Long createParam(SystemParamCreateRequest request) {
        boolean exists = paramStore.stream().anyMatch(item -> item.getParamKey().equalsIgnoreCase(request.getParamKey()));
        if (exists) {
            throw new BusinessException("paramKey already exists");
        }
        paramStore.add(0, SystemParamVO.builder()
                .paramKey(request.getParamKey())
                .paramValue(request.getParamValue())
                .remark(request.getRemark())
                .build());
        return (long) paramStore.size();
    }

    @Override
    public void updateParam(String paramKey, SystemParamUpdateRequest request) {
        SystemParamVO target = paramStore.stream().filter(item -> item.getParamKey().equalsIgnoreCase(paramKey)).findFirst().orElse(null);
        if (target == null) {
            throw new BusinessException("param not found");
        }
        target.setParamValue(request.getParamValue());
        target.setRemark(request.getRemark());
    }

    private SystemUserVO toUserVO(SysUser user) {
        SystemUserVO vo = new SystemUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setMobile(user.getMobile());
        vo.setEmail(user.getEmail());
        vo.setStatus(user.getStatus());
        return vo;
    }
}
