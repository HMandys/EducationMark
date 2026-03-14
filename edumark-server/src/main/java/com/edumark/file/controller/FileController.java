package com.edumark.file.controller;

import com.edumark.common.result.Result;
import com.edumark.file.dto.FileUploadResult;
import com.edumark.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传控制器
 *
 * @author EduMark
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Resource
    private FileService fileService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<FileUploadResult> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "directory", defaultValue = "common") String directory) {
        return Result.success(fileService.upload(file, directory));
    }

    @Operation(summary = "批量上传文件")
    @PostMapping("/upload/batch")
    public Result<List<FileUploadResult>> uploadBatch(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "directory", defaultValue = "common") String directory) {
        return Result.success(fileService.uploadBatch(files, directory));
    }

    @Operation(summary = "删除文件")
    @DeleteMapping
    public Result<Void> delete(@RequestParam String objectName) {
        fileService.delete(objectName);
        return Result.success();
    }

    @Operation(summary = "获取预签名URL")
    @GetMapping("/presigned-url")
    public Result<String> getPresignedUrl(
            @RequestParam String objectName,
            @RequestParam(defaultValue = "3600") int expiry) {
        return Result.success(fileService.getPresignedUrl(objectName, expiry));
    }
}
