package com.edumark.file.dto;

import com.edumark.common.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 答题卡查询DTO
 *
 * @author EduMark
 */
@Schema(description = "答题卡查询DTO")
public class AnswerSheetQueryDTO extends PageQuery {

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "考试科目ID")
    private Long examSubjectId;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学号")
    private String studentNumber;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "状态")
    private Integer status;

    // Getters and Setters
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public Long getExamSubjectId() { return examSubjectId; }
    public void setExamSubjectId(Long examSubjectId) { this.examSubjectId = examSubjectId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
