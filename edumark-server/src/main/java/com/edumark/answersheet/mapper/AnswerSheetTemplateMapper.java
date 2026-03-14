package com.edumark.answersheet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.answersheet.dto.AnswerSheetTemplateQueryDTO;
import com.edumark.answersheet.entity.AnswerSheetTemplate;
import com.edumark.answersheet.vo.AnswerSheetTemplateVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 答题卡模板Mapper
 *
 * @author EduMark
 */
@Mapper
public interface AnswerSheetTemplateMapper extends BaseMapper<AnswerSheetTemplate> {

    /**
     * 分页查询模板列表
     */
    Page<AnswerSheetTemplateVO> selectPageVO(Page<AnswerSheetTemplateVO> page, @Param("query") AnswerSheetTemplateQueryDTO query);

    /**
     * 根据ID查询模板详情
     */
    AnswerSheetTemplateVO selectVOById(@Param("id") Long id);

    /**
     * 根据试卷ID查询模板
     */
    AnswerSheetTemplateVO selectVOByPaperId(@Param("paperId") Long paperId);
}
