package com.edumark.score.service;

import com.edumark.common.result.PageResult;
import com.edumark.score.dto.ScoreQueryDTO;
import com.edumark.score.vo.ExamScoreVO;
import com.edumark.score.vo.ScoreStatisticsVO;
import com.edumark.score.vo.SubjectScoreVO;

import java.util.List;

/**
 * 成绩服务接口
 *
 * @author EduMark
 */
public interface ScoreService {

    /**
     * 汇总考试成绩
     */
    void aggregateScores(Long examId);

    /**
     * 计算排名
     */
    void calculateRanking(Long examId);

    /**
     * 计算统计数据
     */
    void calculateStatistics(Long examId);

    /**
     * 分页查询考试成绩
     */
    PageResult<ExamScoreVO> pageExamScores(ScoreQueryDTO query);

    /**
     * 分页查询科目成绩
     */
    PageResult<SubjectScoreVO> pageSubjectScores(ScoreQueryDTO query);

    /**
     * 查询学生考试成绩详情
     */
    ExamScoreVO getStudentExamScore(Long examId, Long studentId);

    /**
     * 查询统计数据
     */
    List<ScoreStatisticsVO> getStatistics(Long examId, Long examSubjectId, Long classId);

    /**
     * 发布成绩
     */
    void publish(Long examId, Long userId);

    /**
     * 撤回成绩
     */
    void unpublish(Long examId, Long userId);

    /**
     * 导出成绩Excel
     */
    byte[] exportExcel(Long examId, Long classId);
}
