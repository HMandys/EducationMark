package com.edumark.file.recognition;

import com.google.zxing.ResultPoint;

import java.awt.image.BufferedImage;

/**
 * 图像对齐服务
 *
 * @author EduMark
 */
public interface ImageAlignmentService {

    /**
     * 按指定角度旋转图片
     *
     * @param source 源图
     * @param angleDegrees 角度
     * @return 旋转后的图片
     */
    BufferedImage rotate(BufferedImage source, double angleDegrees);

    /**
     * 基于条码定位点做四角对齐裁切
     *
     * @param source 源图
     * @param points 条码定位点
     * @return 对齐后的图片
     */
    BufferedImage alignByBarcode(BufferedImage source, ResultPoint[] points);
}
