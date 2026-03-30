package com.edumark.marking.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 阅卷提交DTO
 *
 * @author EduMark
 */
@Schema(description = "阅卷提交DTO")
public class MarkingSubmitDTO {

    @Schema(description = "阅卷记录ID")
    private Long recordId;

    @Schema(description = "得分")
    private Integer score;

    @Schema(description = "评语")
    private String comment;

    @Schema(description = "标注数据(JSON)")
    private String annotations;

    // Getters and Setters
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getAnnotations() { return annotations; }
    public void setAnnotations(String annotations) { this.annotations = annotations; }
}
