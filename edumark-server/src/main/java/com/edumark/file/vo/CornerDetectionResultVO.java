package com.edumark.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 四角定位检测结果VO
 *
 * @author EduMark
 */
@Schema(description = "四角定位检测结果")
public class CornerDetectionResultVO {

    @Schema(description = "是否检测成功")
    private Boolean success;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "左上角X坐标(百分比)")
    private Double topLeftX;

    @Schema(description = "左上角Y坐标(百分比)")
    private Double topLeftY;

    @Schema(description = "右上角X坐标(百分比)")
    private Double topRightX;

    @Schema(description = "右上角Y坐标(百分比)")
    private Double topRightY;

    @Schema(description = "左下角X坐标(百分比)")
    private Double bottomLeftX;

    @Schema(description = "左下角Y坐标(百分比)")
    private Double bottomLeftY;

    @Schema(description = "右下角X坐标(百分比)")
    private Double bottomRightX;

    @Schema(description = "右下角Y坐标(百分比)")
    private Double bottomRightY;

    @Schema(description = "检测到的倾斜角度(度)")
    private Double angle;

    @Schema(description = "图片宽度")
    private Integer imageWidth;

    @Schema(description = "图片高度")
    private Integer imageHeight;

    @Schema(description = "矫正后的图片路径")
    private String correctedImagePath;

    @Schema(description = "矫正后的图片URL")
    private String correctedImageUrl;

    public static CornerDetectionResultVO success(
            double topLeftX, double topLeftY,
            double topRightX, double topRightY,
            double bottomLeftX, double bottomLeftY,
            double bottomRightX, double bottomRightY,
            double angle, int imageWidth, int imageHeight) {
        CornerDetectionResultVO vo = new CornerDetectionResultVO();
        vo.setSuccess(true);
        vo.setTopLeftX(topLeftX);
        vo.setTopLeftY(topLeftY);
        vo.setTopRightX(topRightX);
        vo.setTopRightY(topRightY);
        vo.setBottomLeftX(bottomLeftX);
        vo.setBottomLeftY(bottomLeftY);
        vo.setBottomRightX(bottomRightX);
        vo.setBottomRightY(bottomRightY);
        vo.setAngle(angle);
        vo.setImageWidth(imageWidth);
        vo.setImageHeight(imageHeight);
        return vo;
    }

    public static CornerDetectionResultVO fail(String errorMessage) {
        CornerDetectionResultVO vo = new CornerDetectionResultVO();
        vo.setSuccess(false);
        vo.setErrorMessage(errorMessage);
        return vo;
    }

    // Getters and Setters
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Double getTopLeftX() { return topLeftX; }
    public void setTopLeftX(Double topLeftX) { this.topLeftX = topLeftX; }

    public Double getTopLeftY() { return topLeftY; }
    public void setTopLeftY(Double topLeftY) { this.topLeftY = topLeftY; }

    public Double getTopRightX() { return topRightX; }
    public void setTopRightX(Double topRightX) { this.topRightX = topRightX; }

    public Double getTopRightY() { return topRightY; }
    public void setTopRightY(Double topRightY) { this.topRightY = topRightY; }

    public Double getBottomLeftX() { return bottomLeftX; }
    public void setBottomLeftX(Double bottomLeftX) { this.bottomLeftX = bottomLeftX; }

    public Double getBottomLeftY() { return bottomLeftY; }
    public void setBottomLeftY(Double bottomLeftY) { this.bottomLeftY = bottomLeftY; }

    public Double getBottomRightX() { return bottomRightX; }
    public void setBottomRightX(Double bottomRightX) { this.bottomRightX = bottomRightX; }

    public Double getBottomRightY() { return bottomRightY; }
    public void setBottomRightY(Double bottomRightY) { this.bottomRightY = bottomRightY; }

    public Double getAngle() { return angle; }
    public void setAngle(Double angle) { this.angle = angle; }

    public Integer getImageWidth() { return imageWidth; }
    public void setImageWidth(Integer imageWidth) { this.imageWidth = imageWidth; }

    public Integer getImageHeight() { return imageHeight; }
    public void setImageHeight(Integer imageHeight) { this.imageHeight = imageHeight; }

    public String getCorrectedImagePath() { return correctedImagePath; }
    public void setCorrectedImagePath(String correctedImagePath) { this.correctedImagePath = correctedImagePath; }

    public String getCorrectedImageUrl() { return correctedImageUrl; }
    public void setCorrectedImageUrl(String correctedImageUrl) { this.correctedImageUrl = correctedImageUrl; }
}
