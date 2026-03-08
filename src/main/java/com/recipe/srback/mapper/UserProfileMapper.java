package com.recipe.srback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recipe.srback.entity.UserHealthProfile;
import com.recipe.srback.vo.UserProfileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户信息Mapper
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserHealthProfile> {
    
    /**
     * 查询用户信息（包含健康档案）
     */
    @Select("SELECT " +
            "u.id, " +
            "u.avatar, " +
            "u.nickname, " +
            "uhp.height, " +
            "uhp.weight, " +
            "uhp.bmi, " +
            "uhp.bmi_status AS bmiStatus, " +
            "u.birthday, " +
            "u.gender " +
            "FROM user u " +
            "LEFT JOIN user_health_profile uhp ON u.id = uhp.user_id " +
            "WHERE u.id = #{userId}")
    UserProfileVO getUserProfileById(Long userId);
}
