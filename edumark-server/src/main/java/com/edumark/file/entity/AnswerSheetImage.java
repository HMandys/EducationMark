package com.edumark.file.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 答题卡图片实体
 *
 * @author EduMark
 */
@TableName("answer_sheet_image")
@Schema(description = "答题卡图片")
public class AnswerSheetImage extends BaseEntity {

    @Schema(description = "答题卡ID")
    private Long answerSheetId;

    @Schema(description = "页码")
    private Integer pageNum;

    @Schema(description = "图片路径")
    private String imagePath;

    @Schema(description = "图片URL")
    private String imageUrl;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "图片宽度")
    private Integer width;

    @Schema(description = "图片高度")
    private Integer height;

    @Schema(description = "排序号")
    private Integer sort;

    // Getters and Setters
    public Long getAnswerSheetId() { return answerSheetId; }
    public void setAnswerSheetId(Long answerSheetId) { this.answerSheetId = answerSheetId; }

    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }

    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
}
