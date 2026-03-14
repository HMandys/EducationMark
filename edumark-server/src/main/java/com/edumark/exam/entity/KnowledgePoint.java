package com.edumark.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 知识点实体
 *
 * @author EduMark
 */
@TableName("knowledge_point")
@Schema(description = "知识点")
public class KnowledgePoint extends BaseEntity {

    @Schema(description = "所属学校ID")
    private Long schoolId;

    @Schema(description = "科目名称")
    private String subjectName;

    @Schema(description = "父级ID")
    private Long parentId;

    @Schema(description = "知识点名称")
    private String name;

    @Schema(description = "知识点编码")
    private String code;

    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "路径")
    private String path;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "状态: 0-禁用 1-启用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @TableField(exist = false)
    @Schema(description = "学校名称")
    private String schoolName;

    @TableField(exist = false)
    @Schema(description = "父级名称")
    private String parentName;

    @TableField(exist = false)
    @Schema(description = "子知识点")
    private List<KnowledgePoint> children;

    // Getters and Setters
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

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public List<KnowledgePoint> getChildren() { return children; }
    public void setChildren(List<KnowledgePoint> children) { this.children = children; }
}
