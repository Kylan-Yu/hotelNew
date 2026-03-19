package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.vo.SystemRoleVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SysRoleSimpleMapper {

    @Select("""
            SELECT id, role_code AS roleCode, role_name AS roleName, status
            FROM sys_role
            WHERE deleted = 0
            ORDER BY id DESC
            """)
    List<SystemRoleVO> findAll();

    @Select("SELECT COUNT(1) FROM sys_role WHERE role_code = #{roleCode} AND deleted = 0")
    int countByRoleCode(String roleCode);

    @Select("""
            SELECT id, role_code AS roleCode, role_name AS roleName, status
            FROM sys_role
            WHERE id = #{id} AND deleted = 0
            LIMIT 1
            """)
    SystemRoleVO findById(Long id);

    @Select("""
            SELECT id, role_code AS roleCode, role_name AS roleName, status
            FROM sys_role
            WHERE role_code = #{roleCode} AND deleted = 0
            LIMIT 1
            """)
    SystemRoleVO findByRoleCode(String roleCode);

    @Insert("""
            INSERT INTO sys_role(role_code, role_name, status, created_by, updated_by, deleted)
            VALUES(#{roleCode}, #{roleName}, #{status}, #{operator}, #{operator}, 0)
            """)
    int insertSimple(@Param("roleCode") String roleCode,
                     @Param("roleName") String roleName,
                     @Param("status") Integer status,
                     @Param("operator") String operator);

    @Update("""
            UPDATE sys_role
            SET role_name = #{roleName},
                status = #{status},
                updated_by = #{operator}
            WHERE id = #{id} AND deleted = 0
            """)
    int updateSimple(@Param("id") Long id,
                     @Param("roleName") String roleName,
                     @Param("status") Integer status,
                     @Param("operator") String operator);
}
