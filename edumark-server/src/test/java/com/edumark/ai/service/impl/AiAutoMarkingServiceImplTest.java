package com.edumark.ai.service.impl;

import com.edumark.ai.entity.AiMarkingPolicy;
import com.edumark.ai.mapper.AiMarkingPolicyMapper;
import com.edumark.answersheet.vo.AnswerSheetRegionVO;
import com.edumark.file.entity.AnswerSheetDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AiAutoMarkingServiceImplTest {

    @Mock
    private AiMarkingPolicyMapper policyMapper;

    private AiAutoMarkingServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        service = new AiAutoMarkingServiceImpl();
        inject("policyMapper", policyMapper);
    }

    @Test
    void isAiManagedFillBlankRegion_shouldAllowCorrectAnswerFallback() throws Exception {
        AnswerSheetRegionVO region = new AnswerSheetRegionVO();
        region.setRegionType(2);
        region.setConfig(Map.of("enableAiMarking", true));

        boolean result = (boolean) invoke("isAiManagedFillBlankRegion",
                new Class[]{AnswerSheetRegionVO.class}, region);

        assertTrue(result);
    }

    @Test
    void buildSystemPrompt_shouldRenderPromptTemplatePlaceholders() throws Exception {
        AiMarkingPolicy policy = new AiMarkingPolicy();
        policy.setLowConfidenceThreshold(0.82D);
        policy.setPromptTemplate("答案={{referenceAnswer}}; 分值={{fullScore}}; 阈值={{lowConfidenceThreshold}}");

        String prompt = (String) invoke("buildSystemPrompt",
                new Class[]{AiMarkingPolicy.class, String.class, Integer.class},
                policy, "春风又绿江南岸", 5);

        assertEquals("答案=春风又绿江南岸; 分值=5; 阈值=0.82", prompt);
    }

    @Test
    void applyFailureStrategy_shouldClearStaleResultForManualReview() throws Exception {
        AiMarkingPolicy policy = new AiMarkingPolicy();
        policy.setFailureStrategy("manual-review");

        AnswerSheetDetail detail = new AnswerSheetDetail();
        detail.setStudentAnswer("旧答案");
        detail.setScore(5);
        detail.setStatus(1);

        invoke("applyFailureStrategy",
                new Class[]{AiMarkingPolicy.class, AnswerSheetDetail.class, Class.forName("com.edumark.ai.service.impl.AiAutoMarkingServiceImpl$AiJudgeResult")},
                policy, detail, null);

        assertNull(detail.getStudentAnswer());
        assertEquals(0, detail.getScore());
        assertEquals(0, detail.getStatus());
    }

    @Test
    void autoMarkFillBlankQuestions_whenAlreadyRunning_shouldSkipDuplicateExecution() throws Exception {
        @SuppressWarnings("unchecked")
        Set<Long> runningAnswerSheetIds = (Set<Long>) getField("runningAnswerSheetIds");
        runningAnswerSheetIds.add(10L);

        service.autoMarkFillBlankQuestions(10L, true);

        verify(policyMapper, never()).selectOne(any());
    }

    private Object invoke(String methodName, Class<?>[] parameterTypes, Object... args) throws Exception {
        Method method = AiAutoMarkingServiceImpl.class.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(service, args);
    }

    private Object getField(String fieldName) throws Exception {
        Field field = AiAutoMarkingServiceImpl.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(service);
    }

    private void inject(String fieldName, Object value) throws Exception {
        Field field = AiAutoMarkingServiceImpl.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(service, value);
    }
}
