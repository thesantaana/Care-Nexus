package com.carenexus.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carenexus.training.entity.TrainingResourceTag;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TrainingResourceTagMapper extends BaseMapper<TrainingResourceTag> {

    @Delete("DELETE FROM training_resource_tag WHERE resource_id = #{resourceId}")
    int deleteByResourceId(@Param("resourceId") Long resourceId);

    @Select("SELECT tag_id FROM training_resource_tag WHERE resource_id = #{resourceId} ORDER BY tag_id")
    List<Long> selectTagIdsByResourceId(@Param("resourceId") Long resourceId);

    @Select("SELECT resource_id FROM training_resource_tag WHERE tag_id = #{tagId} ORDER BY resource_id")
    List<Long> selectResourceIdsByTagId(@Param("tagId") Long tagId);

    @Select({
            "<script>",
            "SELECT resource_id, tag_id FROM training_resource_tag",
            "WHERE resource_id IN",
            "<foreach collection='resourceIds' item='resourceId' open='(' separator=',' close=')'>",
            "#{resourceId}",
            "</foreach>",
            "ORDER BY resource_id, tag_id",
            "</script>"
    })
    List<TrainingResourceTag> selectByResourceIds(@Param("resourceIds") List<Long> resourceIds);
}
