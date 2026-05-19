package com.edumark.exam.service.impl;

import com.edumark.common.exception.BusinessException;
import com.edumark.exam.entity.Exam;
import com.edumark.score.service.ScoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {

    @Mock
    private ScoreService scoreService;

    private ExamServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        service = spy(new ExamServiceImpl());
        inject("scoreService", scoreService);
    }

    @Test
    void updateStatus_manualCompleted_shouldReject() {
        Exam exam = new Exam();
        exam.setId(10L);
        exam.setStatus(3);

        doReturn(exam).when(service).getById(10L);

        assertThrows(BusinessException.class, () -> service.updateStatus(10L, 4));
        verifyNoInteractions(scoreService);
    }

    private void inject(String fieldName, Object value) throws Exception {
        Field field = ExamServiceImpl.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(service, value);
    }
}
