package com.edumark.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 绑定学生参数
 *
 * @author EduMark
 */
@Schema(description = "绑定学生参数")
public class BindStudentDTO {

    @NotBlank(message = "学生姓名不能为空")
    @Schema(description = "学生姓名")
    private String studentName;

    @NotBlank(message = "学号不能为空")
    @Schema(description = "学号")
    private String studentNumber;

    @NotBlank(message = "绑定码不能为空")
    @Schema(description = "绑定码")
    private String bindCode;

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getBindCode() { return bindCode; }
    public void setBindCode(String bindCode) { this.bindCode = bindCode; }
}
