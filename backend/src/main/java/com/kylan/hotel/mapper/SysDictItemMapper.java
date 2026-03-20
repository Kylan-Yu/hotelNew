package com.kylan.hotel.mapper;

import com.kylan.hotel.domain.entity.SysDictItem;
import com.kylan.hotel.domain.vo.SystemDictItemVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SysDictItemMapper {

    @Select("""
            <script>
            SELECT id,
                   dict_code AS dictCode,
                   item_code AS itemCode,
                   item_name AS itemName,
                   item_value AS itemValue,
                   status,
                   sort_no AS sortNo,
                   remark
            FROM sys_dict_item
            WHERE deleted = 0
              AND dict_code = #{dictCode}
            <if test='enabledOnly'>
              AND status = 1
            </if>
            ORDER BY sort_no ASC, id ASC
            </script>
            """)
    List<SystemDictItemVO> findByDictCode(@Param("dictCode") String dictCode, @Param("enabledOnly") boolean enabledOnly);

    @Select("SELECT COUNT(1) FROM sys_dict_item WHERE dict_code = #{dictCode} AND item_code = #{itemCode} AND deleted = 0")
    int countByCode(@Param("dictCode") String dictCode, @Param("itemCode") String itemCode);

    @Select("""
            SELECT COUNT(1)
            FROM sys_dict_item
            WHERE dict_code = #{dictCode}
              AND status = 1
              AND deleted = 0
              AND (item_code = #{value} OR item_value = #{value})
            """)
    int countEnabledByValue(@Param("dictCode") String dictCode, @Param("value") String value);

    @Select("""
            SELECT id,
                   dict_code AS dictCode,
                   item_code AS itemCode,
                   item_name AS itemName,
                   item_value AS itemValue,
                   status,
                   sort_no AS sortNo,
                   remark,
                   created_by AS createdBy,
                   created_at AS createdAt,
                   updated_by AS updatedBy,
                   updated_at AS updatedAt,
                   deleted
            FROM sys_dict_item
            WHERE id = #{id}
              AND deleted = 0
            LIMIT 1
            """)
    SysDictItem findById(Long id);

    @Insert("""
            INSERT INTO sys_dict_item(dict_code, item_code, item_name, item_value, status, sort_no, remark, created_by, updated_by, deleted)
            VALUES(#{dictCode}, #{itemCode}, #{itemName}, #{itemValue}, #{status}, #{sortNo}, #{remark}, #{createdBy}, #{updatedBy}, #{deleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysDictItem entity);

    @Update("""
            UPDATE sys_dict_item
            SET item_name = #{itemName},
                item_value = #{itemValue},
                status = #{status},
                sort_no = #{sortNo},
                remark = #{remark},
                updated_by = #{updatedBy}
            WHERE id = #{id}
              AND deleted = 0
            """)
    int updateById(SysDictItem entity);
}
