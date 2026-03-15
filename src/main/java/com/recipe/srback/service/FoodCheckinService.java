package com.recipe.srback.service;

import com.recipe.srback.vo.FoodCheckinVO;

/**
 * 食物打卡服务接口
 */
public interface FoodCheckinService {
    
    /**
     * 分析食物图片并返回营养数据
     * 
     * @param imageUrl 食物图片URL
     * @param mealType 餐次类型：breakfast/lunch/dinner
     * @return 食物营养数据
     */
    FoodCheckinVO analyzeFoodImage(String imageUrl, String mealType);
}
