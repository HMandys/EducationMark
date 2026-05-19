package com.edumark.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

/**
 * AI 考试进度详情
 */
@Schema(description = "AI考试进度详情")
public class AiMarkingExamProgressDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "汇总信息")
    private AiMarkingExamProgressVO summary;

    @Schema(description = "AI题号列表")
    private List<Integer> aiQuestionNos;

    @Schema(description = "题目进度列表")
    private List<AiMarkingQuestionProgressVO> questionProgressList;

    @Schema(description = "答题卡进度列表")
    private List<AiMarkingSheetProgressVO> sheetProgressList;

    public AiMarkingExamProgressVO getSummary() {
        return summary;
    }

    public void setSummary(AiMarkingExamProgressVO summary) {
        this.summary = summary;
    }

    public List<Integer> getAiQuestionNos() {
        return aiQuestionNos;
    }

    public void setAiQuestionNos(List<Integer> aiQuestionNos) {
        this.aiQuestionNos = aiQuestionNos;
    }

    public List<AiMarkingQuestionProgressVO> getQuestionProgressList() {
        return questionProgressList;
    }

    public void setQuestionProgressList(List<AiMarkingQuestionProgressVO> questionProgressList) {
        this.questionProgressList = questionProgressList;
    }

    public List<AiMarkingSheetProgressVO> getSheetProgressList() {
        return sheetProgressList;
    }

    public void setSheetProgressList(List<AiMarkingSheetProgressVO> sheetProgressList) {
        this.sheetProgressList = sheetProgressList;
    }
}
