package com.edumark.ai.service;

/**
 * AI 自动批改服务
 */
public interface AiAutoMarkingService {

    /**
     * 自动批改答题卡中的 AI 填空题
     *
     * @param answerSheetId 答题卡ID
     */
    void autoMarkFillBlankQuestions(Long answerSheetId);
}
