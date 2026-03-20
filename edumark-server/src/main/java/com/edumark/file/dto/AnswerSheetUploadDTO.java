package com.edumark.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 答题卡上传DTO
 *
 * @author EduMark
 */
@Schema(description = "答题卡上传DTO")
public class AnswerSheetUploadDTO {

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

    @Schema(description = "图片对象名称列表")
    private List<String> imageObjectNames;

    @Schema(description = "图片原始文件名列表")
    private List<String> imageOriginalNames;

    // Getters and Setters
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

    public List<String> getImageObjectNames() { return imageObjectNames; }
    public void setImageObjectNames(List<String> imageObjectNames) { this.imageObjectNames = imageObjectNames; }

    public List<String> getImageOriginalNames() { return imageOriginalNames; }
    public void setImageOriginalNames(List<String> imageOriginalNames) { this.imageOriginalNames = imageOriginalNames; }
}
