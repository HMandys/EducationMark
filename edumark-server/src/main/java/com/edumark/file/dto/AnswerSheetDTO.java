package com.edumark.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 答题卡DTO
 *
 * @author EduMark
 */
@Schema(description = "答题卡请求DTO")
public class AnswerSheetDTO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "考试科目ID")
    private Long examSubjectId;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学号")
    private String studentNumber;

    @Schema(description = "座位号")
    private String seatNumber;

    @Schema(description = "备注")
    private String remark;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public Long getExamSubjectId() { return examSubjectId; }
    public void setExamSubjectId(Long examSubjectId) { this.examSubjectId = examSubjectId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
