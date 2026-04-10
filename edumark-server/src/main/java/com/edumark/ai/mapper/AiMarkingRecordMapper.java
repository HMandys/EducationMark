package com.edumark.ai.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.ai.dto.AiMarkingRecordQueryDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edumark.ai.entity.AiMarkingRecord;
import com.edumark.ai.vo.AiMarkingRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * AI 批改审计记录 Mapper
 */
@Mapper
public interface AiMarkingRecordMapper extends BaseMapper<AiMarkingRecord> {

    IPage<AiMarkingRecordVO> selectPageVO(Page<AiMarkingRecordVO> page, @Param("query") AiMarkingRecordQueryDTO query);
}
