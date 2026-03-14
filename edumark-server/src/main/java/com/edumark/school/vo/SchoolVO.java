package com.edumark.school.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 学校VO
 *
 * @author EduMark
 */
@Schema(description = "学校响应VO")
public class SchoolVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "学校名称")
    private String name;

    @Schema(description = "学校编码")
    private String code;

    @Schema(description = "学校类型")
    private Integer type;

    @Schema(description = "学校类型名称")
    private String typeName;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区县")
    private String district;

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "负责人姓名")
    private String contactName;

    @Schema(description = "负责人手机")
    private String contactPhone;

    @Schema(description = "学校Logo")
    private String logo;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "年级数量")
    private Integer gradeCount;

    @Schema(description = "教师数量")
    private Integer teacherCount;

    @Schema(description = "学生数量")
    private Integer studentCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

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

    public Integer getGradeCount() { return gradeCount; }
    public void setGradeCount(Integer gradeCount) { this.gradeCount = gradeCount; }

    public Integer getTeacherCount() { return teacherCount; }
    public void setTeacherCount(Integer teacherCount) { this.teacherCount = teacherCount; }

    public Integer getStudentCount() { return studentCount; }
    public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
