package com.edumark.file.service;

import com.edumark.file.vo.CornerDetectionResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * 四角定位服务接口
 *
 * @author EduMark
 */
public interface CornerDetectionService {

    /**
     * 检测图片中的四角定位点
     *
     * @param file 上传的图片文件
     * @return 四角定位结果
     */
    CornerDetectionResultVO detectCorners(MultipartFile file);

    /**
     * 检测已存储图片的四角定位点
     *
     * @param imagePath MinIO中的图片路径
     * @return 四角定位结果
     */
    CornerDetectionResultVO detectCorners(String imagePath);

    /**
     * 根据四角坐标矫正图片
     *
     * @param imagePath 原图路径
     * @param cornerConfig 四角配置
     * @return 矫正后的图片路径
     */
    String correctImage(String imagePath, Map<String, Object> cornerConfig);

    /**
     * 在BufferedImage上检测四角定位点
     *
     * @param image 图片
     * @return 四角定位结果
     */
    CornerDetectionResultVO detectCorners(BufferedImage image);
}
