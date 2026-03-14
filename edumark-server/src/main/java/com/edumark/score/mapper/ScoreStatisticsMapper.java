package com.edumark.score.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edumark.score.entity.ScoreStatistics;
import com.edumark.score.vo.ScoreStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 成绩统计Mapper
 *
 * @author EduMark
 */
@Mapper
public interface ScoreStatisticsMapper extends BaseMapper<ScoreStatistics> {

    /**
     * 根据考试ID查询统计数据
     */
    List<ScoreStatisticsVO> selectListByExamId(@Param("examId") Long examId);

    /**
     * 根据考试科目ID查询班级统计
     */
    List<ScoreStatisticsVO> selectClassStatBySubject(@Param("examSubjectId") Long examSubjectId);

    /**
     * 根据考试ID查询年级统计
     */
    ScoreStatisticsVO selectGradeStatByExam(@Param("examId") Long examId, @Param("examSubjectId") Long examSubjectId);

    /**
     * 删除考试统计
     */
    int deleteByExamId(@Param("examId") Long examId);
}
