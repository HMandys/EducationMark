package com.edumark.answersheet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edumark.answersheet.entity.AnswerSheetRegion;
import com.edumark.answersheet.vo.AnswerSheetRegionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 答题区域配置Mapper
 *
 * @author EduMark
 */
@Mapper
public interface AnswerSheetRegionMapper extends BaseMapper<AnswerSheetRegion> {

    /**
     * 根据模板ID查询区域列表
     */
    List<AnswerSheetRegionVO> selectListByTemplateId(@Param("templateId") Long templateId);

    /**
     * 根据模板ID删除区域
     */
    int deleteByTemplateId(@Param("templateId") Long templateId);
}
