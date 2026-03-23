package com.edumark.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 气泡检测结果VO
 *
 * @author EduMark
 */
@Schema(description = "气泡检测结果")
public class BubbleDetectionResultVO {

    @Schema(description = "是否检测成功")
    private Boolean success;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "检测到的气泡数量")
    private Integer detectedCount;

    @Schema(description = "预期气泡数量")
    private Integer expectedCount;

    @Schema(description = "气泡映射列表")
    private List<BubbleItem> bubbleMap;

    @Schema(description = "检测到的行数")
    private Integer rowCount;

    @Schema(description = "每行气泡数")
    private Integer bubblesPerRow;

    public static BubbleDetectionResultVO success(List<BubbleItem> bubbleMap, int expectedCount) {
        BubbleDetectionResultVO vo = new BubbleDetectionResultVO();
        vo.setSuccess(true);
        vo.setBubbleMap(bubbleMap);
        vo.setDetectedCount(bubbleMap != null ? bubbleMap.size() : 0);
        vo.setExpectedCount(expectedCount);
        return vo;
    }

    public static BubbleDetectionResultVO fail(String errorMessage) {
        BubbleDetectionResultVO vo = new BubbleDetectionResultVO();
        vo.setSuccess(false);
        vo.setErrorMessage(errorMessage);
        return vo;
    }

    /**
     * 单个气泡定义
     */
    @Schema(description = "气泡定义")
    public static class BubbleItem {

        @Schema(description = "题号")
        private Integer questionNo;

        @Schema(description = "选项(A/B/C/D)")
        private String option;

        @Schema(description = "X坐标(百分比)")
        private Double x;

        @Schema(description = "Y坐标(百分比)")
        private Double y;

        @Schema(description = "宽度(百分比)")
        private Double width;

        @Schema(description = "高度(百分比)")
        private Double height;

        @Schema(description = "置信度")
        private Double confidence;

        public BubbleItem() {}

        public BubbleItem(int questionNo, String option, double x, double y, double width, double height, double confidence) {
            this.questionNo = questionNo;
            this.option = option;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.confidence = confidence;
        }

        // Getters and Setters
        public Integer getQuestionNo() { return questionNo; }
        public void setQuestionNo(Integer questionNo) { this.questionNo = questionNo; }

        public String getOption() { return option; }
        public void setOption(String option) { this.option = option; }

        public Double getX() { return x; }
        public void setX(Double x) { this.x = x; }

        public Double getY() { return y; }
        public void setY(Double y) { this.y = y; }

        public Double getWidth() { return width; }
        public void setWidth(Double width) { this.width = width; }

        public Double getHeight() { return height; }
        public void setHeight(Double height) { this.height = height; }

        public Double getConfidence() { return confidence; }
        public void setConfidence(Double confidence) { this.confidence = confidence; }
    }

    // Getters and Setters
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Integer getDetectedCount() { return detectedCount; }
    public void setDetectedCount(Integer detectedCount) { this.detectedCount = detectedCount; }

    public Integer getExpectedCount() { return expectedCount; }
    public void setExpectedCount(Integer expectedCount) { this.expectedCount = expectedCount; }

    public List<BubbleItem> getBubbleMap() { return bubbleMap; }
    public void setBubbleMap(List<BubbleItem> bubbleMap) { this.bubbleMap = bubbleMap; }

    public Integer getRowCount() { return rowCount; }
    public void setRowCount(Integer rowCount) { this.rowCount = rowCount; }

    public Integer getBubblesPerRow() { return bubblesPerRow; }
    public void setBubblesPerRow(Integer bubblesPerRow) { this.bubblesPerRow = bubblesPerRow; }
}
