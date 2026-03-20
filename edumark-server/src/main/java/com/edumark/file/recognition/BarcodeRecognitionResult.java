package com.edumark.file.recognition;

/**
 * 条码识别结果
 *
 * @author EduMark
 */
public class BarcodeRecognitionResult {

    private boolean success;

    private String text;

    private boolean aligned;

    private Integer pageNum;

    private String regionName;

    private String message;

    public static BarcodeRecognitionResult success(String text, boolean aligned, Integer pageNum, String regionName, String message) {
        BarcodeRecognitionResult result = new BarcodeRecognitionResult();
        result.setSuccess(true);
        result.setText(text);
        result.setAligned(aligned);
        result.setPageNum(pageNum);
        result.setRegionName(regionName);
        result.setMessage(message);
        return result;
    }

    public static BarcodeRecognitionResult failure(String message) {
        BarcodeRecognitionResult result = new BarcodeRecognitionResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isAligned() {
        return aligned;
    }

    public void setAligned(boolean aligned) {
        this.aligned = aligned;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
