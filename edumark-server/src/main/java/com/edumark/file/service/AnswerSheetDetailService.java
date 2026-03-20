package com.edumark.file.service;

import com.edumark.file.vo.AnswerSheetQuestionDetailVO;

import java.util.List;

/**
 * 答题卡题目明细服务
 *
 * @author EduMark
 */
public interface AnswerSheetDetailService {

    /**
     * 根据答题卡初始化题目明细
     *
     * @param answerSheetId 答题卡ID
     */
    void initializeQuestionDetails(Long answerSheetId);

    /**
     * 识别并写入客观题答案
     *
     * @param answerSheetId 答题卡ID
     * @return 最新题目明细
     */
    List<AnswerSheetQuestionDetailVO> recognizeObjectiveAnswers(Long answerSheetId);

    /**
     * 查询答题卡题目明细
     *
     * @param answerSheetId 答题卡ID
     * @return 题目明细
     */
    List<AnswerSheetQuestionDetailVO> listQuestionDetails(Long answerSheetId);

    /**
     * 生成并返回题目裁题预览地址
     *
     * @param answerSheetId 答题卡ID
     * @param questionId 题目ID
     * @return 预览地址
     */
    String getQuestionPreviewUrl(Long answerSheetId, Long questionId);

    /**
     * 更新客观题答案并重算得分
     *
     * @param answerSheetId 答题卡ID
     * @param questionId 题目ID
     * @param studentAnswer 学生答案
     * @return 更新后的题目明细
     */
    AnswerSheetQuestionDetailVO updateObjectiveAnswer(Long answerSheetId, Long questionId, String studentAnswer);

    /**
     * 更新题目得分并同步答题卡总分
     *
     * @param answerSheetId 答题卡ID
     * @param questionId 题目ID
     * @param score 题目得分
     * @param completed 是否标记为已完成
     */
    void updateQuestionScore(Long answerSheetId, Long questionId, Integer score, boolean completed);

    /**
     * 重算答题卡客观题、主观题与总分
     *
     * @param answerSheetId 答题卡ID
     */
    void recalculateAnswerSheetScores(Long answerSheetId);
}
