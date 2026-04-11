package com.edumark.school.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 班级实体
 *
 * @author EduMark
 */
@TableName("class_info")
@Schema(description = "班级")
public class ClassInfo extends BaseEntity {

    @Schema(description = "所属学校ID")
    private Long schoolId;

    @Schema(description = "所属年级ID")
    private Long gradeId;

    @Schema(description = "班级名称")
    @TableField("class_name")
    private String name;

    @Schema(description = "班级编码")
    @TableField("class_code")
    private String code;

    @Schema(description = "班级序号")
    @TableField(exist = false)
    private Integer classNum;

    @Schema(description = "班主任教师ID")
    private Long headTeacherId;

    @Schema(description = "状态: 0-禁用 1-启用")
    private Integer status;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "备注")
    @TableField(exist = false)
    private String remark;

    @TableField(exist = false)
    @Schema(description = "学校名称")
    private String schoolName;

    @TableField(exist = false)
    @Schema(description = "年级名称")
    private String gradeName;

    @TableField(exist = false)
    @Schema(description = "班主任姓名")
    private String headTeacherName;

    @TableField(exist = false)
    @Schema(description = "学生人数")
    private Integer studentCount;

    // Getters and Setters
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public Long getGradeId() { return gradeId; }
    public void setGradeId(Long gradeId) { this.gradeId = gradeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getClassNum() { return classNum; }
    public void setClassNum(Integer classNum) { this.classNum = classNum; }

    public Long getHeadTeacherId() { return headTeacherId; }
    public void setHeadTeacherId(Long headTeacherId) { this.headTeacherId = headTeacherId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }

    public String getHeadTeacherName() { return headTeacherName; }
    public void setHeadTeacherName(String headTeacherName) { this.headTeacherName = headTeacherName; }

    public Integer getStudentCount() { return studentCount; }
    public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }
}
