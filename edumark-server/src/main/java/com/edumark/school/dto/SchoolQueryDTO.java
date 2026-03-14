package com.edumark.school.dto;

import com.edumark.common.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 学校查询DTO
 *
 * @author EduMark
 */
@Schema(description = "学校查询DTO")
public class SchoolQueryDTO extends PageQuery {

    @Schema(description = "学校名称(模糊)")
    private String name;

    @Schema(description = "学校编码")
    private String code;

    @Schema(description = "学校类型")
    private Integer type;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "状态")
    private Integer status;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
