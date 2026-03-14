package com.edumark.school.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 家长学生绑定关系实体
 *
 * @author EduMark
 */
@TableName("parent_student_bind")
@Schema(description = "家长学生绑定关系")
public class ParentStudentBind extends BaseEntity {

    @Schema(description = "家长ID")
    private Long parentId;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "关系: 1-父亲 2-母亲 3-爷爷 4-奶奶 5-外公 6-外婆 9-其他")
    private Integer relation;

    @Schema(description = "是否为主要联系人")
    private Integer isPrimary;

    @TableField(exist = false)
    @Schema(description = "家长姓名")
    private String parentName;

    @TableField(exist = false)
    @Schema(description = "家长手机")
    private String parentPhone;

    @TableField(exist = false)
    @Schema(description = "学生姓名")
    private String studentName;

    @TableField(exist = false)
    @Schema(description = "学生学号")
    private String studentNumber;

    @TableField(exist = false)
    @Schema(description = "班级名称")
    private String className;

    // Getters and Setters
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Integer getRelation() { return relation; }
    public void setRelation(Integer relation) { this.relation = relation; }

    public Integer getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Integer isPrimary) { this.isPrimary = isPrimary; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
