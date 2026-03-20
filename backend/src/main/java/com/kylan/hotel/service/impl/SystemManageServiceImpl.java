package com.kylan.hotel.service.impl;

import com.kylan.hotel.common.BusinessException;
import com.kylan.hotel.common.SecurityUtils;
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
import com.kylan.hotel.domain.entity.SysDict;
import com.kylan.hotel.domain.entity.SysDictItem;
import com.kylan.hotel.domain.entity.SysParam;
import com.kylan.hotel.domain.entity.SysUser;
import com.kylan.hotel.domain.vo.SystemDictItemVO;
import com.kylan.hotel.domain.vo.SystemDictVO;
import com.kylan.hotel.domain.vo.SystemMenuVO;
import com.kylan.hotel.domain.vo.SystemParamVO;
import com.kylan.hotel.domain.vo.SystemPermissionVO;
import com.kylan.hotel.domain.vo.SystemRoleVO;
import com.kylan.hotel.domain.vo.SystemUserVO;
import com.kylan.hotel.mapper.SysDictItemMapper;
import com.kylan.hotel.mapper.SysDictMapper;
import com.kylan.hotel.mapper.SysMenuSimpleMapper;
import com.kylan.hotel.mapper.SysParamMapper;
import com.kylan.hotel.mapper.SysRoleSimpleMapper;
import com.kylan.hotel.mapper.SysUserMapper;
import com.kylan.hotel.service.SystemManageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemManageServiceImpl implements SystemManageService {

    private static final String DEFAULT_PASSWORD_HASH = "$2b$12$Shy81wwJpP1bv22tA6r2Ruxketd61fSbggJjfkzix1ssC946nQmPq";

    private final SysUserMapper sysUserMapper;
    private final SysRoleSimpleMapper sysRoleSimpleMapper;
    private final SysMenuSimpleMapper sysMenuSimpleMapper;
    private final SysDictMapper sysDictMapper;
    private final SysDictItemMapper sysDictItemMapper;
    private final SysParamMapper sysParamMapper;
    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initBuiltInDataOnStartup() {
        ensureBuiltInDictData();
        ensureBuiltInSystemParams();
    }

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
        return sysDictMapper.findAll();
    }

    @Override
    public List<SystemDictItemVO> dictItems(String dictCode, boolean enabledOnly) {
        return sysDictItemMapper.findByDictCode(dictCode, enabledOnly);
    }

    @Override
    public List<SystemParamVO> params() {
        ensureSysParamTable();
        return sysParamMapper.findAll();
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
        ensureDictTables();
        if (sysDictMapper.countByCode(request.getDictCode()) > 0) {
            throw new BusinessException("dictCode already exists");
        }
        SysDict entity = new SysDict();
        entity.setDictCode(request.getDictCode());
        entity.setDictName(request.getDictName());
        entity.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        entity.setSortNo(request.getSortNo() == null ? 100 : request.getSortNo());
        entity.setRemark(request.getRemark());
        entity.setCreatedBy(SecurityUtils.currentUsername());
        entity.setUpdatedBy(SecurityUtils.currentUsername());
        entity.setDeleted(0);
        sysDictMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public Long createDictItem(String dictCode, SystemDictItemCreateRequest request) {
        ensureDictTables();
        SysDict dict = sysDictMapper.findByCode(dictCode);
        if (dict == null) {
            throw new BusinessException("dict not found");
        }
        if (sysDictItemMapper.countByCode(dictCode, request.getItemCode()) > 0) {
            throw new BusinessException("dict item already exists");
        }
        SysDictItem entity = new SysDictItem();
        entity.setDictCode(dictCode);
        entity.setItemCode(request.getItemCode());
        entity.setItemName(request.getItemName());
        entity.setItemValue(request.getItemValue() == null ? request.getItemCode() : request.getItemValue());
        entity.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        entity.setSortNo(request.getSortNo() == null ? 100 : request.getSortNo());
        entity.setRemark(request.getRemark());
        entity.setCreatedBy(SecurityUtils.currentUsername());
        entity.setUpdatedBy(SecurityUtils.currentUsername());
        entity.setDeleted(0);
        sysDictItemMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public void updateDict(String dictCode, SystemDictUpdateRequest request) {
        ensureDictTables();
        SysDict target = sysDictMapper.findByCode(dictCode);
        if (target == null) {
            throw new BusinessException("dict not found");
        }
        target.setDictName(request.getDictName());
        target.setStatus(request.getStatus() == null ? target.getStatus() : request.getStatus());
        target.setSortNo(request.getSortNo() == null ? target.getSortNo() : request.getSortNo());
        target.setRemark(request.getRemark());
        target.setUpdatedBy(SecurityUtils.currentUsername());
        sysDictMapper.updateByCode(target);
    }

    @Override
    public void updateDictItem(Long id, SystemDictItemUpdateRequest request) {
        ensureDictTables();
        SysDictItem target = sysDictItemMapper.findById(id);
        if (target == null) {
            throw new BusinessException("dict item not found");
        }
        target.setItemName(request.getItemName());
        target.setItemValue(request.getItemValue() == null ? target.getItemValue() : request.getItemValue());
        target.setStatus(request.getStatus() == null ? target.getStatus() : request.getStatus());
        target.setSortNo(request.getSortNo() == null ? target.getSortNo() : request.getSortNo());
        target.setRemark(request.getRemark());
        target.setUpdatedBy(SecurityUtils.currentUsername());
        sysDictItemMapper.updateById(target);
    }

    @Override
    public Long createParam(SystemParamCreateRequest request) {
        ensureSysParamTable();
        if (sysParamMapper.countByKey(request.getParamKey()) > 0) {
            throw new BusinessException("paramKey already exists");
        }
        SysParam entity = new SysParam();
        entity.setParamKey(request.getParamKey());
        entity.setParamValue(request.getParamValue());
        entity.setRemark(request.getRemark());
        entity.setCreatedBy(SecurityUtils.currentUsername());
        entity.setUpdatedBy(SecurityUtils.currentUsername());
        entity.setDeleted(0);
        sysParamMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public void updateParam(String paramKey, SystemParamUpdateRequest request) {
        ensureSysParamTable();
        SysParam target = sysParamMapper.findByKey(paramKey);
        if (target == null) {
            throw new BusinessException("param not found");
        }
        target.setParamValue(request.getParamValue());
        target.setRemark(request.getRemark());
        target.setUpdatedBy(SecurityUtils.currentUsername());
        sysParamMapper.updateByKey(target);
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

    private void ensureBuiltInDictData() {
        ensureDictTables();
        ensureDict(
                "GENDER",
                "性别 / Gender",
                "入住人及会员性别字典 / guest and member gender dictionary",
                10
        );
        ensureDictItem("GENDER", "MALE", "男 / Male", "MALE", "男性 / Male", 10);
        ensureDictItem("GENDER", "FEMALE", "女 / Female", "FEMALE", "女性 / Female", 20);
        ensureDictItem("GENDER", "UNKNOWN", "未知 / Unknown", "UNKNOWN", "未知性别 / Unknown", 30);

        ensureDict(
                "BUSINESS_MODE",
                "经营模式 / Business Mode",
                "民宿与酒店经营模式 / homestay and hotel mode",
                20
        );
        ensureDictItem("BUSINESS_MODE", "HOMESTAY", "民宿 / Homestay", "HOMESTAY", "民宿经营模式 / homestay mode", 10);
        ensureDictItem("BUSINESS_MODE", "HOTEL", "酒店 / Hotel", "HOTEL", "酒店经营模式 / hotel mode", 20);

        ensureDict(
                "ROOM_STATUS",
                "房间状态 / Room Status",
                "房态字典 / room status dictionary",
                30
        );
        ensureDictItem("ROOM_STATUS", "VACANT_CLEAN", "空净 / Vacant Clean", "VACANT_CLEAN", "可直接入住 / ready to check in", 10);
        ensureDictItem("ROOM_STATUS", "OCCUPIED", "在住 / Occupied", "OCCUPIED", "住客在住 / currently occupied", 20);
        ensureDictItem("ROOM_STATUS", "VACANT_DIRTY", "待清扫 / Vacant Dirty", "VACANT_DIRTY", "待保洁 / waiting for housekeeping", 30);
        ensureDictItem("ROOM_STATUS", "MAINTENANCE", "维修 / Maintenance", "MAINTENANCE", "维修中 / under maintenance", 40);
        ensureDictItem("ROOM_STATUS", "LOCKED", "锁房 / Locked", "LOCKED", "不可售 / not for sale", 50);
    }

    private void ensureBuiltInSystemParams() {
        ensureSysParamTable();
        ensureParam("hms.currentProperty.prefer", "true", "日志筛选默认按当前门店收敛 / log filters prefer current property by default");
        ensureParam("hms.ota.callback.maxRetry", "5", "OTA回调最大重试次数 / max retry count for OTA callbacks");
        ensureParam("hms.ota.callback.firstRetrySeconds", "30", "OTA回调首次重试秒数 / first retry delay seconds for OTA callbacks");
    }

    private void ensureDict(String dictCode, String dictName, String remark, int sortNo) {
        jdbcTemplate.update("""
                INSERT INTO sys_dict(dict_code, dict_name, status, sort_no, remark, created_by, updated_by, deleted)
                VALUES(?, ?, 1, ?, ?, 'system', 'system', 0)
                ON DUPLICATE KEY UPDATE
                  dict_name = VALUES(dict_name),
                  status = VALUES(status),
                  sort_no = VALUES(sort_no),
                  remark = VALUES(remark),
                  updated_by = 'system',
                  updated_at = CURRENT_TIMESTAMP
                """, dictCode, dictName, sortNo, remark);
    }

    private void ensureDictItem(String dictCode, String itemCode, String itemName, String itemValue, String remark, int sortNo) {
        jdbcTemplate.update("""
                INSERT INTO sys_dict_item(dict_code, item_code, item_name, item_value, status, sort_no, remark, created_by, updated_by, deleted)
                VALUES(?, ?, ?, ?, 1, ?, ?, 'system', 'system', 0)
                ON DUPLICATE KEY UPDATE
                  item_name = VALUES(item_name),
                  item_value = VALUES(item_value),
                  status = VALUES(status),
                  sort_no = VALUES(sort_no),
                  remark = VALUES(remark),
                  updated_by = 'system',
                  updated_at = CURRENT_TIMESTAMP
                """, dictCode, itemCode, itemName, itemValue, sortNo, remark);
    }

    private void ensureParam(String paramKey, String paramValue, String remark) {
        if (sysParamMapper.countByKey(paramKey) > 0) {
            return;
        }
        SysParam entity = new SysParam();
        entity.setParamKey(paramKey);
        entity.setParamValue(paramValue);
        entity.setRemark(remark);
        entity.setCreatedBy("system");
        entity.setUpdatedBy("system");
        entity.setDeleted(0);
        sysParamMapper.insert(entity);
    }

    private void ensureDictTables() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS sys_dict (
                  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典ID / Dictionary ID',
                  dict_code VARCHAR(64) NOT NULL COMMENT '字典编码 / Dictionary Code',
                  dict_name VARCHAR(128) NOT NULL COMMENT '字典名称 / Dictionary Name',
                  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1启用0停用) / Status (1 enabled, 0 disabled)',
                  sort_no INT NOT NULL DEFAULT 100 COMMENT '排序号 / Sort Number',
                  remark VARCHAR(255) DEFAULT NULL COMMENT '备注 / Remark',
                  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
                  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
                  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
                  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
                  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
                  UNIQUE KEY uk_sys_dict_code (dict_code),
                  KEY idx_sys_dict_status_sort (status, sort_no)
                ) COMMENT='系统字典表 / System Dictionary Table'
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS sys_dict_item (
                  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典项ID / Dictionary Item ID',
                  dict_code VARCHAR(64) NOT NULL COMMENT '字典编码 / Dictionary Code',
                  item_code VARCHAR(64) NOT NULL COMMENT '字典项编码 / Dictionary Item Code',
                  item_name VARCHAR(128) NOT NULL COMMENT '字典项名称 / Dictionary Item Name',
                  item_value VARCHAR(128) DEFAULT NULL COMMENT '字典项值 / Dictionary Item Value',
                  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1启用0停用) / Status (1 enabled, 0 disabled)',
                  sort_no INT NOT NULL DEFAULT 100 COMMENT '排序号 / Sort Number',
                  remark VARCHAR(255) DEFAULT NULL COMMENT '备注 / Remark',
                  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
                  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
                  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
                  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
                  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
                  UNIQUE KEY uk_sys_dict_item_code (dict_code, item_code),
                  KEY idx_sys_dict_item_dict_status_sort (dict_code, status, sort_no)
                ) COMMENT='系统字典项表 / System Dictionary Item Table'
                """);
    }

    private void ensureSysParamTable() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS sys_param (
                  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '参数ID / Parameter ID',
                  param_key VARCHAR(128) NOT NULL COMMENT '参数键 / Parameter Key',
                  param_value VARCHAR(512) NOT NULL COMMENT '参数值 / Parameter Value',
                  remark VARCHAR(255) DEFAULT NULL COMMENT '备注 / Remark',
                  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
                  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
                  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
                  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
                  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
                  UNIQUE KEY uk_sys_param_key (param_key)
                ) COMMENT='系统参数表 / System Parameter Table'
                """);
    }
}
