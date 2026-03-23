package com.edumark.file.controller;

import com.edumark.common.result.Result;
import com.edumark.file.service.CornerDetectionService;
import com.edumark.file.service.FileService;
import com.edumark.file.vo.CornerDetectionResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 四角定位控制器
 *
 * @author EduMark
 */
@Tag(name = "四角定位")
@RestController
@RequestMapping("/corner-detection")
public class CornerDetectionController {

    @Resource
    private CornerDetectionService cornerDetectionService;

    @Resource
    private FileService fileService;

    @Operation(summary = "上传图片并检测四角定位点")
    @PostMapping("/detect")
    public Result<CornerDetectionResultVO> detect(@RequestParam("file") MultipartFile file) {
        return Result.success(cornerDetectionService.detectCorners(file));
    }

    @Operation(summary = "检测已上传图片的四角定位点")
    @GetMapping("/detect")
    public Result<CornerDetectionResultVO> detectByPath(@RequestParam String imagePath) {
        CornerDetectionResultVO result = cornerDetectionService.detectCorners(imagePath);
        if (Boolean.TRUE.equals(result.getSuccess()) && result.getCorrectedImagePath() != null) {
            result.setCorrectedImageUrl(fileService.getUrl(result.getCorrectedImagePath()));
        }
        return Result.success(result);
    }

    @Operation(summary = "应用四角矫正")
    @PostMapping("/correct")
    public Result<String> correct(
            @RequestParam String imagePath,
            @RequestBody Map<String, Object> cornerConfig) {
        String correctedPath = cornerDetectionService.correctImage(imagePath, cornerConfig);
        return Result.success(fileService.getUrl(correctedPath));
    }
}
