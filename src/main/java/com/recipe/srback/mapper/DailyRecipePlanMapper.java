package com.recipe.srback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recipe.srback.entity.DailyRecipePlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 每日食谱计划Mapper
 */
@Mapper
public interface DailyRecipePlanMapper extends BaseMapper<DailyRecipePlan> {
}
