package com.edumark.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * App学生信息
 *
 * @author EduMark
 */
@Data
@Schema(description = "App学生信息")
public class AppStudentVO {

    @Schema(description = "学生ID")
    private Long id;

    @Schema(description = "学生姓名")
    private String name;

    @Schema(description = "学号")
    private String studentNumber;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "年级ID")
    private Long gradeId;

    @Schema(description = "年级名称")
    private String gradeName;

    @Schema(description = "学校ID")
    private Long schoolId;

    @Schema(description = "学校名称")
    private String schoolName;

    @Schema(description = "绑定时间")
    private LocalDateTime bindTime;
}
