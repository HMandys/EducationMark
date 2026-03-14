package com.edumark.school.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 家长DTO
 *
 * @author EduMark
 */
@Schema(description = "家长请求DTO")
public class ParentDTO {

    @Schema(description = "ID(更新时必填)")
    private Long id;

    @Schema(description = "姓名", required = true)
    private String name;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "手机号", required = true)
    private String phone;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
