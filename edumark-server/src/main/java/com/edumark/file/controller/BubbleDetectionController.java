package com.edumark.file.controller;

import com.edumark.common.result.Result;
import com.edumark.file.service.BubbleDetectionService;
import com.edumark.file.vo.BubbleDetectionResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 气泡检测控制器
 *
 * @author EduMark
 */
@Tag(name = "气泡检测")
@RestController
@RequestMapping("/bubble-detection")
public class BubbleDetectionController {

    @Resource
    private BubbleDetectionService bubbleDetectionService;

    @Operation(summary = "上传图片并检测客观题气泡")
    @PostMapping("/detect")
    public Result<BubbleDetectionResultVO> detect(
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "区域X坐标(百分比)") @RequestParam double boxX,
            @Parameter(description = "区域Y坐标(百分比)") @RequestParam double boxY,
            @Parameter(description = "区域宽度(百分比)") @RequestParam double boxWidth,
            @Parameter(description = "区域高度(百分比)") @RequestParam double boxHeight,
            @Parameter(description = "起始题号") @RequestParam int questionStart,
            @Parameter(description = "结束题号") @RequestParam int questionEnd,
            @Parameter(description = "每题选项数") @RequestParam(defaultValue = "4") int optionCount,
            @Parameter(description = "每组题数") @RequestParam(defaultValue = "5") int questionsPerRow,
            @Parameter(description = "布局方向: row=横向, column=纵向") @RequestParam(defaultValue = "column") String layoutDirection) {

        return Result.success(bubbleDetectionService.detectBubbles(
                file, boxX, boxY, boxWidth, boxHeight,
                questionStart, questionEnd, optionCount, questionsPerRow, layoutDirection));
    }

    @Operation(summary = "检测已上传图片中的客观题气泡")
    @GetMapping("/detect")
    public Result<BubbleDetectionResultVO> detectByPath(
            @Parameter(description = "图片路径") @RequestParam String imagePath,
            @Parameter(description = "区域X坐标(百分比)") @RequestParam double boxX,
            @Parameter(description = "区域Y坐标(百分比)") @RequestParam double boxY,
            @Parameter(description = "区域宽度(百分比)") @RequestParam double boxWidth,
            @Parameter(description = "区域高度(百分比)") @RequestParam double boxHeight,
            @Parameter(description = "起始题号") @RequestParam int questionStart,
            @Parameter(description = "结束题号") @RequestParam int questionEnd,
            @Parameter(description = "每题选项数") @RequestParam(defaultValue = "4") int optionCount,
            @Parameter(description = "每组题数") @RequestParam(defaultValue = "5") int questionsPerRow,
            @Parameter(description = "布局方向: row=横向, column=纵向") @RequestParam(defaultValue = "column") String layoutDirection) {

        return Result.success(bubbleDetectionService.detectBubbles(
                imagePath, boxX, boxY, boxWidth, boxHeight,
                questionStart, questionEnd, optionCount, questionsPerRow, layoutDirection));
    }
}
