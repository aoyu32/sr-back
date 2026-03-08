package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recipe.srback.dto.AddRestrictionDTO;
import com.recipe.srback.entity.UserRestriction;
import com.recipe.srback.enums.RestrictionTypeEnum;
import com.recipe.srback.enums.SeverityLevelEnum;
import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.mapper.UserRestrictionMapper;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.RestrictionService;
import com.recipe.srback.vo.RestrictionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 特殊禁忌服务实现类
 */
@Slf4j
@Service
public class RestrictionServiceImpl implements RestrictionService {
    
    @Autowired
    private UserRestrictionMapper restrictionMapper;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRestriction(Long userId, AddRestrictionDTO dto) {
        UserRestriction restriction = new UserRestriction();
        restriction.setUserId(userId);
        restriction.setType(dto.getType());
        restriction.setName(dto.getName());
        restriction.setDescription(dto.getDescription());
        restriction.setSeverity(dto.getSeverity());
        restriction.setAddedDate(LocalDate.now());
        
        restrictionMapper.insert(restriction);
    }
    
    @Override
    public List<RestrictionVO> getUserRestrictions(Long userId) {
        LambdaQueryWrapper<UserRestriction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRestriction::getUserId, userId);
        wrapper.orderByDesc(UserRestriction::getCreatedAt);
        
        List<UserRestriction> restrictions = restrictionMapper.selectList(wrapper);
        
        return restrictions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRestriction(Long userId, Long restrictionId) {
        UserRestriction restriction = restrictionMapper.selectById(restrictionId);
        
        if (restriction == null || !restriction.getUserId().equals(userId)) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        
        restrictionMapper.deleteById(restrictionId);
    }
    
    /**
     * 转换为VO
     */
    private RestrictionVO convertToVO(UserRestriction restriction) {
        RestrictionVO vo = new RestrictionVO();
        vo.setId(restriction.getId());
        vo.setType(restriction.getType());
        vo.setTypeName(RestrictionTypeEnum.getNameByCode(restriction.getType()));
        vo.setName(restriction.getName());
        vo.setDescription(restriction.getDescription());
        vo.setSeverity(restriction.getSeverity());
        vo.setSeverityName(SeverityLevelEnum.getNameByCode(restriction.getSeverity()));
        vo.setAddedDate(restriction.getAddedDate() != null ? restriction.getAddedDate().format(DATE_FORMATTER) : null);
        
        return vo;
    }
}
