package com.edumark.school.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 学校实体
 *
 * @author EduMark
 */
@TableName("school")
@Schema(description = "学校")
public class School extends BaseEntity {

    @Schema(description = "学校名称")
    @TableField("school_name")
    private String name;

    @Schema(description = "学校编码")
    @TableField("school_code")
    private String code;

    @Schema(description = "学校类型: 1-小学 2-初中 3-高中 4-完全中学 5-九年一贯制")
    @TableField(exist = false)
    private Integer type;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区县")
    private String district;

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "联系电话")
    @TableField(exist = false)
    private String phone;

    @Schema(description = "负责人姓名")
    @TableField("contact_person")
    private String contactName;

    @Schema(description = "负责人手机")
    @TableField("contact_phone")
    private String contactPhone;

    @Schema(description = "学校Logo")
    private String logo;

    @Schema(description = "状态: 0-禁用 1-启用")
    private Integer status;

    @Schema(description = "排序号")
    @TableField(exist = false)
    private Integer sort;

    @Schema(description = "备注")
    @TableField(exist = false)
    private String remark;

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

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
