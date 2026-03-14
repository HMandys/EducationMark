package com.edumark.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 考试班级关联实体
 *
 * @author EduMark
 */
@TableName("exam_class")
@Schema(description = "考试班级关联")
public class ExamClass extends BaseEntity {

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "班级ID")
    private Long classId;

    @TableField(exist = false)
    @Schema(description = "班级名称")
    private String className;

    @TableField(exist = false)
    @Schema(description = "学生数量")
    private Integer studentCount;

    // Getters and Setters
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public Integer getStudentCount() { return studentCount; }
    public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }
}
