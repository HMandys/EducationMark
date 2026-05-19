package com.edumark.ai.service;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * AI 自动批改异步服务
 */
@Service
public class AiAutoMarkingAsyncService {

    private static final Logger log = LoggerFactory.getLogger(AiAutoMarkingAsyncService.class);

    @Resource
    private AiAutoMarkingService aiAutoMarkingService;

    @Async("aiMarkingExecutor")
    public void autoMarkFillBlankQuestionsAsync(Long answerSheetId) {
        try {
            aiAutoMarkingService.autoMarkFillBlankQuestions(answerSheetId, false);
        } catch (Exception ex) {
            log.warn("异步 AI 自动批改失败，answerSheetId={}, forceRerun=false, error={}",
                    answerSheetId, ex.getMessage(), ex);
        }
    }

    @Async("aiMarkingExecutor")
    public void autoMarkFillBlankQuestionsAsync(Long answerSheetId, boolean forceRerun) {
        try {
            aiAutoMarkingService.autoMarkFillBlankQuestions(answerSheetId, forceRerun);
        } catch (Exception ex) {
            log.warn("异步 AI 自动批改失败，answerSheetId={}, forceRerun={}, error={}",
                    answerSheetId, forceRerun, ex.getMessage(), ex);
        }
    }

    @Async("aiMarkingExecutor")
    public void autoMarkExamSubjectFillBlankQuestionsAsync(Long examSubjectId, boolean forceRerun) {
        try {
            aiAutoMarkingService.autoMarkExamSubjectFillBlankQuestions(examSubjectId, forceRerun);
        } catch (Exception ex) {
            log.warn("异步 AI 科目批改失败，examSubjectId={}, forceRerun={}, error={}",
                    examSubjectId, forceRerun, ex.getMessage(), ex);
        }
    }
}
