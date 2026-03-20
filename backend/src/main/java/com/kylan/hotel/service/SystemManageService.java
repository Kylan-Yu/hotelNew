package com.kylan.hotel.service;

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

import java.util.List;

public interface SystemManageService {
    List<SystemUserVO> users();

    List<SystemRoleVO> roles();

    List<SystemPermissionVO> permissions();

    List<SystemMenuVO> menus();

    List<SystemDictVO> dicts();

    List<SystemDictItemVO> dictItems(String dictCode, boolean enabledOnly);

    List<SystemParamVO> params();

    Long createUser(SystemUserCreateRequest request);

    void updateUser(Long id, SystemUserUpdateRequest request);

    Long createRole(SystemRoleCreateRequest request);

    void updateRole(Long id, SystemRoleUpdateRequest request);

    Long createDict(SystemDictCreateRequest request);

    Long createDictItem(String dictCode, SystemDictItemCreateRequest request);

    void updateDict(String dictCode, SystemDictUpdateRequest request);

    void updateDictItem(Long id, SystemDictItemUpdateRequest request);

    Long createParam(SystemParamCreateRequest request);

    void updateParam(String paramKey, SystemParamUpdateRequest request);
}
