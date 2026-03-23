package com.edumark.file.service;

import com.edumark.file.vo.BubbleDetectionResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

/**
 * 气泡检测服务接口
 *
 * @author EduMark
 */
public interface BubbleDetectionService {

    /**
     * 检测上传图片中的选项气泡
     *
     * @param file 上传的图片文件
     * @param boxX 区域X坐标(百分比)
     * @param boxY 区域Y坐标(百分比)
     * @param boxWidth 区域宽度(百分比)
     * @param boxHeight 区域高度(百分比)
     * @param questionStart 起始题号
     * @param questionEnd 结束题号
     * @param optionCount 每题选项数
     * @param questionsPerRow 每行题数
     * @return 气泡检测结果
     */
    BubbleDetectionResultVO detectBubbles(
            MultipartFile file,
            double boxX, double boxY, double boxWidth, double boxHeight,
            int questionStart, int questionEnd,
            int optionCount, int questionsPerRow);

    /**
     * 检测已存储图片中的选项气泡
     *
     * @param imagePath MinIO中的图片路径
     * @param boxX 区域X坐标(百分比)
     * @param boxY 区域Y坐标(百分比)
     * @param boxWidth 区域宽度(百分比)
     * @param boxHeight 区域高度(百分比)
     * @param questionStart 起始题号
     * @param questionEnd 结束题号
     * @param optionCount 每题选项数
     * @param questionsPerRow 每行题数
     * @return 气泡检测结果
     */
    BubbleDetectionResultVO detectBubbles(
            String imagePath,
            double boxX, double boxY, double boxWidth, double boxHeight,
            int questionStart, int questionEnd,
            int optionCount, int questionsPerRow);

    /**
     * 检测BufferedImage中的选项气泡
     *
     * @param image 图片
     * @param boxX 区域X坐标(百分比)
     * @param boxY 区域Y坐标(百分比)
     * @param boxWidth 区域宽度(百分比)
     * @param boxHeight 区域高度(百分比)
     * @param questionStart 起始题号
     * @param questionEnd 结束题号
     * @param optionCount 每题选项数
     * @param questionsPerRow 每行题数
     * @return 气泡检测结果
     */
    BubbleDetectionResultVO detectBubbles(
            BufferedImage image,
            double boxX, double boxY, double boxWidth, double boxHeight,
            int questionStart, int questionEnd,
            int optionCount, int questionsPerRow);
}
