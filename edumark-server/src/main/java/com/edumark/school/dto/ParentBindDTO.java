package com.edumark.school.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 家长绑定学生DTO
 *
 * @author EduMark
 */
@Schema(description = "家长绑定学生请求DTO")
public class ParentBindDTO {

    @Schema(description = "学生姓名", required = true)
    private String studentName;

    @Schema(description = "学生学号", required = true)
    private String studentNumber;

    @Schema(description = "绑定码", required = true)
    private String bindCode;

    @Schema(description = "关系: 1-父亲 2-母亲 3-爷爷 4-奶奶 5-外公 6-外婆 9-其他")
    private Integer relation;

    // Getters and Setters
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getBindCode() { return bindCode; }
    public void setBindCode(String bindCode) { this.bindCode = bindCode; }

    public Integer getRelation() { return relation; }
    public void setRelation(Integer relation) { this.relation = relation; }
}
