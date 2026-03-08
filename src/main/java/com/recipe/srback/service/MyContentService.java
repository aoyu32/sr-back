package com.recipe.srback.service;

import com.recipe.srback.vo.MyContentVO;

/**
 * 我的内容统计服务接口
 */
public interface MyContentService {
    /**
     * 获取我的内容统计
     */
    MyContentVO getMyContentStats(Long userId);
}
