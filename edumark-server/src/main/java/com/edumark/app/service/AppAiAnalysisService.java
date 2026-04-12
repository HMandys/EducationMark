package com.edumark.app.service;

import com.edumark.app.vo.AppAiReportVO;

/**
 * App AI 分析服务
 *
 * @author EduMark
 */
public interface AppAiAnalysisService {

    /**
     * 生成学生 AI 学情分析报告
     *
     * @param studentId 学生ID
     * @param limit     最近考试数量
     * @return AI 学情分析结果
     */
    AppAiReportVO generateStudentReport(Long studentId, Integer limit);
}
