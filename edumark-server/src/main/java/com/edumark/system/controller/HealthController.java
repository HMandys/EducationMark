package com.edumark.system.controller;

import com.edumark.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 *
 * @author EduMark
 */
@Tag(name = "健康检查", description = "服务健康状态检查")
@RestController
@RequestMapping("/health")
public class HealthController {

    @Operation(summary = "健康检查")
    @GetMapping
    public Result<Map<String, Object>> health() {
        Map<String, Object> info = new HashMap<>();
        info.put("status", "UP");
        info.put("application", "EduMark Server");
        info.put("version", "1.0.0");
        info.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        info.put("javaVersion", System.getProperty("java.version"));
        return Result.success(info);
    }

    @Operation(summary = "Ping测试")
    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.success("pong");
    }
}
