package com.edumark.exam.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 知识点DTO
 *
 * @author EduMark
 */
@Schema(description = "知识点请求DTO")
public class KnowledgePointDTO {

    @Schema(description = "ID(更新时必填)")
    private Long id;

    @Schema(description = "所属学校ID", required = true)
    private Long schoolId;

    @Schema(description = "科目名称", required = true)
    private String subjectName;

    @Schema(description = "父级ID")
    private Long parentId;

    @Schema(description = "知识点名称", required = true)
    private String name;

    @Schema(description = "知识点编码")
    private String code;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
