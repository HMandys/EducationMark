package com.edumark.file.service;

import com.edumark.file.vo.CropProgressVO;
import com.edumark.file.vo.CropResultVO;

import java.util.List;

/**
 * 裁题服务接口
 *
 * @author EduMark
 */
public interface CropService {

    /**
     * 批量裁题 - 按考试科目
     *
     * @param examSubjectId 考试科目ID
     * @return 裁题进度信息
     */
    CropProgressVO batchCropByExamSubject(Long examSubjectId);

    /**
     * 批量裁题 - 按答题卡
     *
     * @param answerSheetId 答题卡ID
     * @return 裁题结果
     */
    CropResultVO cropByAnswerSheet(Long answerSheetId);

    /**
     * 获取裁题进度
     *
     * @param examSubjectId 考试科目ID
     * @return 裁题进度信息
     */
    CropProgressVO getCropProgress(Long examSubjectId);

    /**
     * 重新裁题 - 按答题卡题目
     *
     * @param answerSheetId 答题卡ID
     * @param questionId    题目ID
     * @return 裁题图片URL
     */
    String recropQuestion(Long answerSheetId, Long questionId);

    /**
     * 获取裁题统计
     *
     * @param examSubjectId 考试科目ID
     * @return 统计信息
     */
    CropProgressVO getCropStatistics(Long examSubjectId);

    /**
     * 检查裁题是否完成
     *
     * @param examSubjectId 考试科目ID
     * @return 是否完成
     */
    boolean isCropCompleted(Long examSubjectId);
}
