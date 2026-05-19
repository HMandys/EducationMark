package com.edumark.file.service.impl;

import com.edumark.ai.service.AiAutoMarkingAsyncService;
import com.edumark.ai.service.AiAutoMarkingService;
import com.edumark.common.exception.BusinessException;
import com.edumark.exam.entity.Exam;
import com.edumark.exam.mapper.ExamMapper;
import com.edumark.file.entity.AnswerSheet;
import com.edumark.file.dto.AnswerSheetUploadDTO;
import com.edumark.file.mapper.AnswerSheetDetailMapper;
import com.edumark.file.service.AnswerSheetDetailService;
import com.edumark.marking.mapper.MarkingTaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnswerSheetServiceImplTest {

    @Mock
    private AnswerSheetDetailMapper answerSheetDetailMapper;

    @Mock
    private AnswerSheetDetailService answerSheetDetailService;

    @Mock
    private ExamMapper examMapper;

    @Mock
    private MarkingTaskMapper markingTaskMapper;

    @Mock
    private AiAutoMarkingService aiAutoMarkingService;

    @Mock
    private AiAutoMarkingAsyncService aiAutoMarkingAsyncService;

    private AnswerSheetServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        service = new AnswerSheetServiceImpl();
        inject("answerSheetDetailMapper", answerSheetDetailMapper);
        inject("answerSheetDetailService", answerSheetDetailService);
        inject("examMapper", examMapper);
        inject("markingTaskMapper", markingTaskMapper);
        inject("aiAutoMarkingService", aiAutoMarkingService);
        inject("aiAutoMarkingAsyncService", aiAutoMarkingAsyncService);
        lenient().when(aiAutoMarkingService.hasAiFillBlankQuestions(1L)).thenReturn(false);
    }

    @Test
    void initializeQuestionDetailsIfReady_existingDetails_shouldNotRecognizeWhenNotForced() throws Exception {
        AnswerSheet answerSheet = buildReadyAnswerSheet();
        when(answerSheetDetailMapper.selectCount(any())).thenReturn(1L);

        invokeInitializeQuestionDetailsIfReady(answerSheet, false);

        verify(answerSheetDetailService).initializeQuestionDetails(1L);
        verify(answerSheetDetailService, never()).recognizeObjectiveAnswers(1L);
    }

    @Test
    void initializeQuestionDetailsIfReady_existingDetails_shouldRecognizeWhenForced() throws Exception {
        AnswerSheet answerSheet = buildReadyAnswerSheet();
        when(answerSheetDetailMapper.selectCount(any())).thenReturn(1L);

        invokeInitializeQuestionDetailsIfReady(answerSheet, true);

        verify(answerSheetDetailService).initializeQuestionDetails(1L);
        verify(answerSheetDetailService).recognizeObjectiveAnswers(1L);
    }

    @Test
    void uploadAnswerSheet_startedMarkingTask_shouldRejectSupplementaryUpload() {
        AnswerSheetUploadDTO dto = new AnswerSheetUploadDTO();
        dto.setExamId(10L);
        dto.setExamSubjectId(20L);

        Exam exam = new Exam();
        exam.setId(10L);
        exam.setStatus(3);

        when(examMapper.selectById(10L)).thenReturn(exam);
        when(markingTaskMapper.selectCount(any())).thenReturn(1L);

        assertThrows(BusinessException.class, () -> service.uploadAnswerSheet(dto));
    }

    private AnswerSheet buildReadyAnswerSheet() {
        AnswerSheet answerSheet = new AnswerSheet();
        answerSheet.setId(1L);
        answerSheet.setStudentId(2L);
        answerSheet.setExamSubjectId(3L);
        answerSheet.setStatus(2);
        return answerSheet;
    }

    private void invokeInitializeQuestionDetailsIfReady(AnswerSheet answerSheet, boolean forceRecognize) throws Exception {
        Method method = AnswerSheetServiceImpl.class.getDeclaredMethod(
                "initializeQuestionDetailsIfReady",
                AnswerSheet.class,
                boolean.class
        );
        method.setAccessible(true);
        method.invoke(service, answerSheet, forceRecognize);
    }

    private void inject(String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = AnswerSheetServiceImpl.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(service, value);
    }
}
