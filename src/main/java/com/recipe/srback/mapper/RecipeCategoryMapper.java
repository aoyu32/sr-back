package com.recipe.srback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recipe.srback.entity.RecipeCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 食谱分类Mapper
 */
@Mapper
public interface RecipeCategoryMapper extends BaseMapper<RecipeCategory> {
}
