package com.edumark.score.service.impl;

import com.edumark.common.exception.BusinessException;
import com.edumark.exam.entity.Exam;
import com.edumark.exam.mapper.ExamMapper;
import com.edumark.score.vo.ScorePublishCheckVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoreServiceImplTest {

    @Mock
    private ExamMapper examMapper;

    private ScoreServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        service = spy(new ScoreServiceImpl());
        inject("examMapper", examMapper);
    }

    @Test
    void publish_publishCheckBlocked_shouldRejectBeforeAggregation() {
        Exam exam = new Exam();
        exam.setId(10L);
        exam.setStatus(4);

        ScorePublishCheckVO publishCheck = new ScorePublishCheckVO();
        publishCheck.setCanPublish(false);
        publishCheck.setBlockingItems(List.of("仍有 1 份答题卡未完成阅卷"));

        when(examMapper.selectById(10L)).thenReturn(exam);
        doReturn(publishCheck).when(service).getPublishCheck(10L);

        assertThrows(BusinessException.class, () -> service.publish(10L, 1L));

        verify(service, never()).aggregateScores(10L);
        verify(service, never()).calculateRanking(10L);
        verify(service, never()).calculateStatistics(10L);
    }

    private void inject(String fieldName, Object value) throws Exception {
        Field field = ScoreServiceImpl.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(service, value);
    }
}
