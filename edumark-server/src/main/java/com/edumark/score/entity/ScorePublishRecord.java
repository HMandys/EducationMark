package com.edumark.score.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 成绩发布记录实体
 *
 * @author EduMark
 */
@TableName("score_publish_record")
@Schema(description = "成绩发布记录")
public class ScorePublishRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "发布类型: 1-发布 2-撤回")
    private Integer publishType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发布/撤回时间")
    private LocalDateTime publishTime;

    @Schema(description = "操作人ID")
    private Long publishBy;

    @Schema(description = "备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(exist = false)
    @Schema(description = "考试名称")
    private String examName;

    @TableField(exist = false)
    @Schema(description = "操作人姓名")
    private String publishByName;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public Integer getPublishType() { return publishType; }
    public void setPublishType(Integer publishType) { this.publishType = publishType; }

    public LocalDateTime getPublishTime() { return publishTime; }
    public void setPublishTime(LocalDateTime publishTime) { this.publishTime = publishTime; }

    public Long getPublishBy() { return publishBy; }
    public void setPublishBy(Long publishBy) { this.publishBy = publishBy; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getPublishByName() { return publishByName; }
    public void setPublishByName(String publishByName) { this.publishByName = publishByName; }
}
