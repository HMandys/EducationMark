package com.edumark.file.service.impl;

import com.edumark.answersheet.service.AnswerSheetTemplateService;
import com.edumark.answersheet.vo.AnswerSheetRegionVO;
import com.edumark.answersheet.vo.AnswerSheetTemplateVO;
import com.edumark.common.exception.BusinessException;
import com.edumark.exam.entity.Exam;
import com.edumark.exam.mapper.ExamMapper;
import com.edumark.file.entity.AnswerSheet;
import com.edumark.file.entity.AnswerSheetDetail;
import com.edumark.file.mapper.AnswerSheetDetailMapper;
import com.edumark.file.mapper.AnswerSheetImageMapper;
import com.edumark.file.mapper.AnswerSheetMapper;
import com.edumark.exam.mapper.PaperMapper;
import com.edumark.exam.mapper.PaperQuestionMapper;
import com.edumark.marking.mapper.MarkingTaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnswerSheetDetailServiceImplTest {

    @Mock
    private AnswerSheetMapper answerSheetMapper;

    @Mock
    private AnswerSheetDetailMapper answerSheetDetailMapper;

    @Mock
    private AnswerSheetImageMapper answerSheetImageMapper;

    @Mock
    private ExamMapper examMapper;

    @Mock
    private PaperMapper paperMapper;

    @Mock
    private PaperQuestionMapper paperQuestionMapper;

    @Mock
    private MarkingTaskMapper markingTaskMapper;

    @Mock
    private AnswerSheetTemplateService answerSheetTemplateService;

    private AnswerSheetDetailServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        service = new AnswerSheetDetailServiceImpl();
        inject("answerSheetMapper", answerSheetMapper);
        inject("answerSheetDetailMapper", answerSheetDetailMapper);
        inject("answerSheetImageMapper", answerSheetImageMapper);
        inject("examMapper", examMapper);
        inject("paperMapper", paperMapper);
        inject("paperQuestionMapper", paperQuestionMapper);
        inject("markingTaskMapper", markingTaskMapper);
        inject("answerSheetTemplateService", answerSheetTemplateService);
    }

    @Test
    void recalculateAnswerSheetScores_independentMode_shouldUseDetailObjectiveFlag() {
        AnswerSheet answerSheet = new AnswerSheet();
        answerSheet.setId(10L);
        answerSheet.setExamSubjectId(20L);

        AnswerSheetDetail objectiveDetail = new AnswerSheetDetail();
        objectiveDetail.setId(1L);
        objectiveDetail.setQuestionNo(1);
        objectiveDetail.setIsObjective(1);
        objectiveDetail.setScore(6);

        AnswerSheetDetail subjectiveDetail = new AnswerSheetDetail();
        subjectiveDetail.setId(2L);
        subjectiveDetail.setQuestionNo(2);
        subjectiveDetail.setIsObjective(0);
        subjectiveDetail.setScore(9);

        when(answerSheetMapper.selectById(10L)).thenReturn(answerSheet);
        when(answerSheetDetailMapper.selectList(any())).thenReturn(List.of(objectiveDetail, subjectiveDetail));
        when(answerSheetImageMapper.selectListByAnswerSheetId(10L)).thenReturn(List.of());
        when(paperMapper.selectByExamSubjectId(20L)).thenReturn(null);
        AnswerSheetTemplateVO template = new AnswerSheetTemplateVO();
        template.setRegions(List.of());
        when(answerSheetTemplateService.getByExamSubjectId(20L)).thenReturn(template);

        service.recalculateAnswerSheetScores(10L);

        ArgumentCaptor<AnswerSheet> captor = ArgumentCaptor.forClass(AnswerSheet.class);
        verify(answerSheetMapper).updateById(captor.capture());
        assertEquals(6, captor.getValue().getObjectiveScore());
        assertEquals(9, captor.getValue().getSubjectiveScore());
        assertEquals(15, captor.getValue().getTotalScore());
    }

    @Test
    void recognizeObjectiveAnswersIndependent_shouldMarkRecognizedAnswerAsCompleted() throws Exception {
        AnswerSheetRegionVO region = new AnswerSheetRegionVO();
        region.setQuestionStart(1);
        region.setQuestionEnd(1);
        region.setConfig(Map.of("hasMultipleChoice", false));

        AnswerSheetDetail detail = new AnswerSheetDetail();
        detail.setQuestionNo(1);
        detail.setFullScore(2);
        detail.setIsObjective(1);
        Map<Long, AnswerSheetDetail> detailMap = new LinkedHashMap<>();
        detailMap.put(-1L, detail);

        BufferedImage pageImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                pageImage.setRGB(x, y, Color.WHITE.getRGB());
            }
        }
        for (int y = 10; y < 30; y++) {
            for (int x = 10; x < 30; x++) {
                pageImage.setRGB(x, y, Color.BLACK.getRGB());
            }
        }

        List<Object> bubbles = List.of(
                newBubbleDefinition(1, "A", 10D, 10D, 20D, 20D),
                newBubbleDefinition(1, "B", 40D, 10D, 20D, 20D)
        );
        Map<Integer, List<Object>> bubbleMap = new LinkedHashMap<>();
        bubbleMap.put(1, bubbles);

        Method method = AnswerSheetDetailServiceImpl.class.getDeclaredMethod(
                "recognizeObjectiveAnswersIndependent",
                Long.class,
                AnswerSheetRegionVO.class,
                BufferedImage.class,
                Map.class,
                Map.class,
                int.class,
                Map.class
        );
        method.setAccessible(true);
        method.invoke(service, 10L, region, pageImage, bubbleMap, Map.of("1", "A"), 2, detailMap);

        verify(answerSheetDetailMapper).updateById(detail);
        assertEquals("A", detail.getStudentAnswer());
        assertEquals(2, detail.getScore());
        assertEquals(1, detail.getStatus());
    }

    @Test
    void refreshAnswerSheetStatus_completedSheetShouldRollbackWhenAiDetailPending() {
        AnswerSheet answerSheet = new AnswerSheet();
        answerSheet.setId(10L);
        answerSheet.setExamSubjectId(20L);
        answerSheet.setStatus(4);

        AnswerSheetDetail detail = new AnswerSheetDetail();
        detail.setId(1L);
        detail.setQuestionNo(1);
        detail.setRegionId(11L);
        detail.setIsObjective(0);
        detail.setStatus(0);
        detail.setScore(0);

        AnswerSheetRegionVO region = new AnswerSheetRegionVO();
        region.setId(11L);
        region.setRegionType(2);
        region.setQuestionStart(1);
        region.setQuestionEnd(1);
        region.setConfig(Map.of("enableAiMarking", true));

        AnswerSheetTemplateVO template = new AnswerSheetTemplateVO();
        template.setRegions(List.of(region));

        when(answerSheetMapper.selectById(10L)).thenReturn(answerSheet);
        when(answerSheetDetailMapper.selectList(any())).thenReturn(List.of(detail));
        when(answerSheetImageMapper.selectListByAnswerSheetId(10L)).thenReturn(List.of());
        when(paperMapper.selectByExamSubjectId(20L)).thenReturn(null);
        when(answerSheetTemplateService.getByExamSubjectId(20L)).thenReturn(template);

        service.refreshAnswerSheetStatus(10L);

        ArgumentCaptor<AnswerSheet> captor = ArgumentCaptor.forClass(AnswerSheet.class);
        verify(answerSheetMapper).updateById(captor.capture());
        assertEquals(2, captor.getValue().getStatus());
    }

    @Test
    void updateQuestionScore_publishedExam_shouldRejectMutation() {
        AnswerSheet answerSheet = new AnswerSheet();
        answerSheet.setId(10L);
        answerSheet.setExamId(99L);

        Exam exam = new Exam();
        exam.setId(99L);
        exam.setStatus(5);

        when(answerSheetMapper.selectById(10L)).thenReturn(answerSheet);
        when(examMapper.selectById(99L)).thenReturn(exam);

        assertThrows(BusinessException.class, () -> service.updateQuestionScore(10L, 20L, 6, true));
    }

    private Object newBubbleDefinition(int questionNo, String option, double x, double y, double width, double height) throws Exception {
        Class<?> bubbleDefinitionClass = Class.forName("com.edumark.file.service.impl.AnswerSheetDetailServiceImpl$BubbleDefinition");
        Constructor<?> constructor = bubbleDefinitionClass.getDeclaredConstructor(
                Integer.class,
                String.class,
                Double.class,
                Double.class,
                Double.class,
                Double.class
        );
        constructor.setAccessible(true);
        return constructor.newInstance(questionNo, option, x, y, width, height);
    }

    private void inject(String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = AnswerSheetDetailServiceImpl.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(service, value);
    }
}
