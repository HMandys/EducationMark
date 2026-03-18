package com.edumark.exam.dto;

import com.edumark.common.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 知识点查询DTO
 *
 * @author EduMark
 */
@Schema(description = "知识点查询DTO")
public class KnowledgePointQueryDTO extends PageQuery {

    @Schema(description = "所属学校ID")
    private Long schoolId;

    @Schema(description = "科目名称")
    private String subjectName;

    @Schema(description = "知识点名称(模糊)")
    private String name;

    @Schema(description = "知识点编码")
    private String code;

    @Schema(description = "父级ID")
    private Long parentId;

    @Schema(description = "状态")
    private Integer status;

    // Getters and Setters
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
