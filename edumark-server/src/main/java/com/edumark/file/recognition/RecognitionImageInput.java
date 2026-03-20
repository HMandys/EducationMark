package com.edumark.file.recognition;

/**
 * 识别图片输入
 *
 * @author EduMark
 */
public class RecognitionImageInput {

    private Integer pageNum;

    private String objectName;

    private String originalName;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
}
