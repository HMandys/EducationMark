package com.edumark.school.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 年级DTO
 *
 * @author EduMark
 */
@Schema(description = "年级请求DTO")
public class GradeDTO {

    @Schema(description = "ID(更新时必填)")
    private Long id;

    @Schema(description = "所属学校ID", required = true)
    private Long schoolId;

    @Schema(description = "年级名称", required = true)
    private String name;

    @Schema(description = "年级编码")
    private String code;

    @Schema(description = "入学年份")
    private Integer enrollYear;

    @Schema(description = "年级序号")
    private Integer gradeNum;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
}
