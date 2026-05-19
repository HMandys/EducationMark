package com.edumark.ai.mapper;

import com.edumark.ai.dto.AiMarkingProgressQueryDTO;
import com.edumark.ai.vo.AiMarkingExamProgressVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI 批改进度 Mapper
 */
@Mapper
public interface AiMarkingProgressMapper {

    /**
     * 查询启用了 AI 批改的考试科目列表
     */
    List<AiMarkingExamProgressVO> selectExamProgressBaseList(@Param("query") AiMarkingProgressQueryDTO query);

    /**
     * 查询单个启用了 AI 批改的考试科目
     */
    AiMarkingExamProgressVO selectExamProgressBaseBySubjectId(@Param("examSubjectId") Long examSubjectId);
}
