package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.SysUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SysUserMapper {
    @Select("""
            SELECT id, username, password_hash AS passwordHash, nickname, mobile, email, status,
                   created_by AS createdBy, created_at AS createdAt, updated_by AS updatedBy,
                   updated_at AS updatedAt, deleted
            FROM sys_user
            WHERE username = #{username} AND deleted = 0
            LIMIT 1
            """)
    SysUser findByUsername(String username);

    @Select("""
            SELECT id, username, password_hash AS passwordHash, nickname, mobile, email, status,
                   created_by AS createdBy, created_at AS createdAt, updated_by AS updatedBy,
                   updated_at AS updatedAt, deleted
            FROM sys_user
            WHERE id = #{id} AND deleted = 0
            LIMIT 1
            """)
    SysUser findById(Long id);

    @Select("""
            SELECT DISTINCT m.permission_code
            FROM sys_user_role ur
            INNER JOIN sys_role r ON r.id = ur.role_id AND r.deleted = 0 AND r.status = 1
            INNER JOIN sys_role_menu rm ON rm.role_id = r.id AND rm.deleted = 0
            INNER JOIN sys_menu m ON m.id = rm.menu_id AND m.deleted = 0 AND m.status = 1
            WHERE ur.user_id = #{userId}
              AND ur.deleted = 0
              AND m.permission_code IS NOT NULL
              AND m.permission_code != ''
            """)
    List<String> findPermissionCodesByUserId(Long userId);

    @Select("""
            SELECT DISTINCT property_id
            FROM sys_user_property_scope
            WHERE user_id = #{userId}
              AND deleted = 0
            """)
    List<Long> findPropertyScopesByUserId(Long userId);

    @Select("""
            SELECT id, username, password_hash AS passwordHash, nickname, mobile, email, status,
                   created_by AS createdBy, created_at AS createdAt, updated_by AS updatedBy,
                   updated_at AS updatedAt, deleted
            FROM sys_user
            WHERE deleted = 0
            ORDER BY id DESC
            """)
    List<SysUser> findAll();

    @Select("SELECT COUNT(1) FROM sys_user WHERE username = #{username} AND deleted = 0")
    int countByUsername(String username);

    @Insert("""
            INSERT INTO sys_user(username, password_hash, nickname, mobile, email, status, created_by, updated_by, deleted)
            VALUES(#{username}, #{passwordHash}, #{nickname}, #{mobile}, #{email}, #{status}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSimple(SysUser entity);

    @Update("""
            UPDATE sys_user
            SET nickname = #{nickname},
                mobile = #{mobile},
                email = #{email},
                status = #{status},
                updated_by = #{updatedBy}
            WHERE id = #{id} AND deleted = 0
            """)
    int updateSimple(SysUser entity);
}
