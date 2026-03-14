package com.edumark.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 文件上传结果
 *
 * @author EduMark
 */
@Schema(description = "文件上传结果")
public class FileUploadResult {

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "对象名称（存储路径）")
    private String objectName;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "文件类型")
    private String contentType;

    @Schema(description = "访问URL")
    private String url;

    public FileUploadResult() {
    }

    public FileUploadResult(String fileName, String originalName, String objectName,
                            Long fileSize, String contentType, String url) {
        this.fileName = fileName;
        this.originalName = originalName;
        this.objectName = objectName;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.url = url;
    }

    // Getters and Setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public String getObjectName() { return objectName; }
    public void setObjectName(String objectName) { this.objectName = objectName; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
