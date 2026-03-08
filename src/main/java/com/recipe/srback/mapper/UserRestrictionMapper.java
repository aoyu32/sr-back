package com.recipe.srback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recipe.srback.entity.UserRestriction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户特殊禁忌Mapper
 */
@Mapper
public interface UserRestrictionMapper extends BaseMapper<UserRestriction> {
}
