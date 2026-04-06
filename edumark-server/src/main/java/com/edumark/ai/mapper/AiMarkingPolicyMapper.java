package com.edumark.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edumark.ai.entity.AiMarkingPolicy;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 批改策略 Mapper
 */
@Mapper
public interface AiMarkingPolicyMapper extends BaseMapper<AiMarkingPolicy> {
}
