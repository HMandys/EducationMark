package com.edumark.school.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 年级实体
 *
 * @author EduMark
 */
@TableName("grade")
@Schema(description = "年级")
public class Grade extends BaseEntity {

    @Schema(description = "所属学校ID")
    private Long schoolId;

    @Schema(description = "年级名称")
    private String name;

    @Schema(description = "年级编码")
    private String code;

    @Schema(description = "入学年份")
    private Integer enrollYear;

    @Schema(description = "年级序号: 1-9表示一年级到九年级, 10-12表示高一到高三")
    private Integer gradeNum;

    @Schema(description = "状态: 0-禁用 1-启用")
    private Integer status;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    @TableField(exist = false)
    @Schema(description = "学校名称")
    private String schoolName;

    @TableField(exist = false)
    @Schema(description = "班级数量")
    private Integer classCount;

    // Getters and Setters
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getEnrollYear() { return enrollYear; }
    public void setEnrollYear(Integer enrollYear) { this.enrollYear = enrollYear; }

    public Integer getGradeNum() { return gradeNum; }
    public void setGradeNum(Integer gradeNum) { this.gradeNum = gradeNum; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public Integer getClassCount() { return classCount; }
    public void setClassCount(Integer classCount) { this.classCount = classCount; }
}
