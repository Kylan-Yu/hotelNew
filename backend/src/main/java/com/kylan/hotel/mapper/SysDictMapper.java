package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.SysDict;
import com.kylan.hotel.domain.vo.SystemDictVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SysDictMapper {

    @Select("""
            SELECT d.dict_code AS dictCode,
                   d.dict_name AS dictName,
                   d.status,
                   d.sort_no AS sortNo,
                   d.remark,
                   (SELECT COUNT(1) FROM sys_dict_item i WHERE i.dict_code = d.dict_code AND i.deleted = 0) AS itemCount
            FROM sys_dict d
            WHERE d.deleted = 0
            ORDER BY d.sort_no ASC, d.id ASC
            """)
    List<SystemDictVO> findAll();

    @Select("SELECT COUNT(1) FROM sys_dict WHERE dict_code = #{dictCode} AND deleted = 0")
    int countByCode(String dictCode);

    @Select("""
            SELECT id,
                   dict_code AS dictCode,
                   dict_name AS dictName,
                   status,
                   sort_no AS sortNo,
                   remark,
                   created_by AS createdBy,
                   created_at AS createdAt,
                   updated_by AS updatedBy,
                   updated_at AS updatedAt,
                   deleted
            FROM sys_dict
            WHERE dict_code = #{dictCode}
              AND deleted = 0
            LIMIT 1
            """)
    SysDict findByCode(String dictCode);

    @Insert("""
            INSERT INTO sys_dict(dict_code, dict_name, status, sort_no, remark, created_by, updated_by, deleted)
            VALUES(#{dictCode}, #{dictName}, #{status}, #{sortNo}, #{remark}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysDict entity);

    @Update("""
            UPDATE sys_dict
            SET dict_name = #{dictName},
                status = #{status},
                sort_no = #{sortNo},
                remark = #{remark},
                updated_by = #{updatedBy}
            WHERE dict_code = #{dictCode}
              AND deleted = 0
            """)
    int updateByCode(SysDict entity);

}
