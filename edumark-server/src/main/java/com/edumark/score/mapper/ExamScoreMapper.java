package com.edumark.score.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.score.dto.ScoreQueryDTO;
import com.edumark.score.entity.ExamScore;
import com.edumark.score.vo.ExamScoreVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考试成绩Mapper
 *
 * @author EduMark
 */
@Mapper
public interface ExamScoreMapper extends BaseMapper<ExamScore> {

    /**
     * 分页查询考试成绩
     */
    Page<ExamScoreVO> selectPageVO(Page<ExamScoreVO> page, @Param("query") ScoreQueryDTO query);

    /**
     * 根据考试ID和学生ID查询成绩
     */
    ExamScoreVO selectVOByExamAndStudent(@Param("examId") Long examId, @Param("studentId") Long studentId);

    /**
     * 根据考试ID查询所有成绩
     */
    List<ExamScore> selectListByExamId(@Param("examId") Long examId);

    /**
     * 删除考试成绩
     */
    int deleteByExamId(@Param("examId") Long examId);
}
