package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.vo.SystemMenuVO;
import com.kylan.hotel.domain.vo.SystemPermissionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysMenuSimpleMapper {

    @Select("""
            SELECT id, parent_id AS parentId, menu_name AS menuName, permission_code AS permissionCode,
                   menu_type AS menuType, path, status
            FROM sys_menu
            WHERE deleted = 0
            ORDER BY sort_no ASC, id ASC
            """)
    List<SystemMenuVO> findAll();

    @Select("""
            SELECT permission_code AS permissionCode, menu_name AS menuName, menu_type AS menuType
            FROM sys_menu
            WHERE deleted = 0
              AND permission_code IS NOT NULL
              AND permission_code != ''
            ORDER BY permission_code ASC
            """)
    List<SystemPermissionVO> findPermissions();
}
