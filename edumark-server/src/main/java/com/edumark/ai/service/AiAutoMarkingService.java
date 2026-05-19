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

    /**
     * 自动批改答题卡中的 AI 填空题
     *
     * @param answerSheetId 答题卡ID
     * @param forceRerun    是否强制重跑已成功题目
     */
    void autoMarkFillBlankQuestions(Long answerSheetId, boolean forceRerun);

    /**
     * 自动批改科目下所有答题卡中的 AI 填空题
     *
     * @param examSubjectId 考试科目ID
     * @param forceRerun    是否强制重跑已成功题目
     * @return 加入批改的答题卡数量
     */
    int autoMarkExamSubjectFillBlankQuestions(Long examSubjectId, boolean forceRerun);

    /**
     * 判断答题卡是否存在启用 AI 批改的填空题
     *
     * @param answerSheetId 答题卡ID
     * @return 是否存在 AI 批改填空题
     */
    boolean hasAiFillBlankQuestions(Long answerSheetId);
}
