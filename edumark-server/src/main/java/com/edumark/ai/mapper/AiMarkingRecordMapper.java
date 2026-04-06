package com.edumark.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edumark.ai.entity.AiMarkingRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 批改审计记录 Mapper
 */
@Mapper
public interface AiMarkingRecordMapper extends BaseMapper<AiMarkingRecord> {
}
