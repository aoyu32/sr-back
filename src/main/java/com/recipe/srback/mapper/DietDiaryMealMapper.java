package com.recipe.srback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recipe.srback.entity.DietDiaryMeal;
import org.apache.ibatis.annotations.Mapper;

/**
 * 餐次打卡记录Mapper
 */
@Mapper
public interface DietDiaryMealMapper extends BaseMapper<DietDiaryMeal> {
}
