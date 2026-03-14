package com.edumark.system.controller;

import com.edumark.common.result.Result;
import com.edumark.security.service.AuthService;
import com.edumark.security.utils.SecurityUtils;
import com.edumark.system.dto.LoginDTO;
import com.edumark.system.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author EduMark
 */
@Tag(name = "认证管理", description = "登录、登出、获取用户信息")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = authService.login(loginDTO);
        return Result.success(loginVO);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<LoginVO.UserInfoVO> getCurrentUserInfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        LoginVO.UserInfoVO userInfo = authService.getCurrentUser(userId);
        return Result.success(userInfo);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT 无状态，前端删除 token 即可
        // 如需后端处理，可将 token 加入黑名单
        return Result.success();
    }
}
