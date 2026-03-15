package com.recipe.srback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recipe.srback.entity.DietDiaryFood;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打卡食物记录Mapper
 */
@Mapper
public interface DietDiaryFoodMapper extends BaseMapper<DietDiaryFood> {
}
