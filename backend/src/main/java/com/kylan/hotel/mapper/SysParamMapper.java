package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.SysParam;
import com.kylan.hotel.domain.vo.SystemParamVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SysParamMapper {

    @Select("""
            SELECT param_key AS paramKey,
                   param_value AS paramValue,
                   remark
            FROM sys_param
            WHERE deleted = 0
            ORDER BY id DESC
            """)
    List<SystemParamVO> findAll();

    @Select("SELECT COUNT(1) FROM sys_param WHERE param_key = #{paramKey} AND deleted = 0")
    int countByKey(String paramKey);

    @Select("""
            SELECT id,
                   param_key AS paramKey,
                   param_value AS paramValue,
                   remark,
                   created_by AS createdBy,
                   created_at AS createdAt,
                   updated_by AS updatedBy,
                   updated_at AS updatedAt,
                   deleted
            FROM sys_param
            WHERE param_key = #{paramKey}
              AND deleted = 0
            LIMIT 1
            """)
    SysParam findByKey(String paramKey);

    @Insert("""
            INSERT INTO sys_param(param_key, param_value, remark, created_by, updated_by, deleted)
            VALUES(#{paramKey}, #{paramValue}, #{remark}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysParam entity);

    @Update("""
            UPDATE sys_param
            SET param_value = #{paramValue},
                remark = #{remark},
                updated_by = #{updatedBy}
            WHERE param_key = #{paramKey}
              AND deleted = 0
            """)
    int updateByKey(SysParam entity);
}
