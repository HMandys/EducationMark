package com.edumark.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edumark.exam.entity.PaperQuestion;
import com.edumark.exam.vo.PaperQuestionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 试卷题目Mapper
 *
 * @author EduMark
 */
@Mapper
public interface PaperQuestionMapper extends BaseMapper<PaperQuestion> {

    /**
     * 根据试卷ID查询题目列表
     */
    List<PaperQuestionVO> selectListByPaperId(@Param("paperId") Long paperId);

    /**
     * 根据ID查询题目详情
     */
    PaperQuestionVO selectVOById(@Param("id") Long id);
}
