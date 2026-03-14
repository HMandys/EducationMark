package com.edumark.school.dto;

import com.edumark.common.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 年级查询DTO
 *
 * @author EduMark
 */
@Schema(description = "年级查询DTO")
public class GradeQueryDTO extends PageQuery {

    @Schema(description = "所属学校ID")
    private Long schoolId;

    @Schema(description = "年级名称(模糊)")
    private String name;

    @Schema(description = "入学年份")
    private Integer enrollYear;

    @Schema(description = "状态")
    private Integer status;

    // Getters and Setters
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getEnrollYear() { return enrollYear; }
    public void setEnrollYear(Integer enrollYear) { this.enrollYear = enrollYear; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
