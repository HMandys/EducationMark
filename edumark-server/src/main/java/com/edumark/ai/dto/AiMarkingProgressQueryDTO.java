package com.edumark.ai.dto;

import com.edumark.common.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * AI 批改进度查询参数
 */
@Schema(description = "AI批改进度查询参数")
public class AiMarkingProgressQueryDTO extends PageQuery {

    @Schema(description = "考试名称")
    private String examName;

    @Schema(description = "科目名称")
    private String subjectName;

    @Schema(description = "进度状态: 0-未开始 1-进行中 2-已完成 3-异常待处理")
    private Integer status;

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
