package com.recipe.srback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recipe.srback.entity.UserHealthProfile;
import com.recipe.srback.vo.HealthProfileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户健康档案Mapper
 */
@Mapper
public interface UserHealthProfileMapper extends BaseMapper<UserHealthProfile> {
    
    /**
     * 多表查询用户健康档案（关联user表获取性别）
     */
    @Select("SELECT " +
            "u.id AS userId, " +
            "u.gender AS gender, " +
            "p.height AS height, " +
            "p.weight AS weight, " +
            "p.age AS age, " +
            "p.bmi AS bmi, " +
            "p.bmi_status AS bmiStatus, " +
            "p.activity_level AS activityLevel, " +
            "p.blood_pressure AS bloodPressure, " +
            "p.blood_sugar AS bloodSugar " +
            "FROM user u " +
            "LEFT JOIN user_health_profile p ON u.id = p.user_id " +
            "WHERE u.id = #{userId}")
    HealthProfileVO selectHealthProfileByUserId(Long userId);
}
