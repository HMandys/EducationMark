package com.edumark.school.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 家长VO
 *
 * @author EduMark
 */
@Schema(description = "家长响应VO")
public class ParentVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "关联用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "绑定的学生列表")
    private List<StudentBindVO> students;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

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

    public List<StudentBindVO> getStudents() { return students; }
    public void setStudents(List<StudentBindVO> students) { this.students = students; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    /**
     * 绑定的学生简要信息
     */
    @Schema(description = "绑定的学生简要信息")
    public static class StudentBindVO {
        @Schema(description = "绑定ID")
        private Long bindId;

        @Schema(description = "学生ID")
        private Long studentId;

        @Schema(description = "学生姓名")
        private String studentName;

        @Schema(description = "学号")
        private String studentNumber;

        @Schema(description = "班级名称")
        private String className;

        @Schema(description = "关系")
        private Integer relation;

        @Schema(description = "关系名称")
        private String relationName;

        // Getters and Setters
        public Long getBindId() { return bindId; }
        public void setBindId(Long bindId) { this.bindId = bindId; }

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }

        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }

        public String getStudentNumber() { return studentNumber; }
        public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }

        public Integer getRelation() { return relation; }
        public void setRelation(Integer relation) { this.relation = relation; }

        public String getRelationName() { return relationName; }
        public void setRelationName(String relationName) { this.relationName = relationName; }
    }
}
