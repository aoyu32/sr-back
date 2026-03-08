package com.recipe.srback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recipe.srback.entity.RecipeLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 食谱点赞Mapper
 */
@Mapper
public interface RecipeLikeMapper extends BaseMapper<RecipeLike> {
}
