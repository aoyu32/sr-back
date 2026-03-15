package com.recipe.srback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recipe.srback.entity.DietDiary;
import org.apache.ibatis.annotations.Mapper;

/**
 * 饮食日记Mapper
 */
@Mapper
public interface DietDiaryMapper extends BaseMapper<DietDiary> {
}
