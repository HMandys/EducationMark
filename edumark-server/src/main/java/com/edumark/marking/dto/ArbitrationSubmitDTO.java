package com.edumark.marking.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 仲裁提交DTO
 *
 * @author EduMark
 */
@Schema(description = "仲裁提交DTO")
public class ArbitrationSubmitDTO {

    @Schema(description = "仲裁记录ID")
    private Long arbitrationId;

    @Schema(description = "仲裁分数")
    private Integer score;

    @Schema(description = "仲裁说明")
    private String comment;

    // Getters and Setters
    public Long getArbitrationId() { return arbitrationId; }
    public void setArbitrationId(Long arbitrationId) { this.arbitrationId = arbitrationId; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
