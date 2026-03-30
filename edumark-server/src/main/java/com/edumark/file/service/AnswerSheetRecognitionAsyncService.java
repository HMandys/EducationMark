package com.edumark.file.service;

import com.edumark.file.entity.AnswerSheet;
import com.edumark.file.mapper.AnswerSheetMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 答题卡异步识别服务
 *
 * @author EduMark
 */
@Service
public class AnswerSheetRecognitionAsyncService {

    private static final int STATUS_RECOGNITION_EXCEPTION = 5;

    @Resource
    private AnswerSheetMapper answerSheetMapper;

    @Lazy
    @Resource
    private AnswerSheetService answerSheetService;

    /**
     * 异步执行答题卡识别
     */
    @Async("answerSheetRecognitionExecutor")
    public void recognizeAsync(Long answerSheetId) {
        try {
            answerSheetService.reRecognize(answerSheetId);
        } catch (Exception e) {
            // 识别失败，更新为异常状态
            AnswerSheet answerSheet = answerSheetMapper.selectById(answerSheetId);
            if (answerSheet != null) {
                answerSheet.setStatus(STATUS_RECOGNITION_EXCEPTION);
                answerSheet.setRemark("识别失败: " + e.getMessage());
                answerSheet.setUpdateTime(LocalDateTime.now());
                answerSheetMapper.updateById(answerSheet);
            }
        }
    }
}
