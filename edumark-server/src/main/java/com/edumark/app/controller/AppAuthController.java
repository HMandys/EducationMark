package com.edumark.app.controller;

import com.edumark.app.dto.AppLoginDTO;
import com.edumark.app.vo.AppLoginVO;
import com.edumark.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * App认证控制器
 *
 * @author EduMark
 */
@Tag(name = "App-认证接口")
@RestController
@RequestMapping("/app/auth")
public class AppAuthController {

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<AppLoginVO> login(@Valid @RequestBody AppLoginDTO dto) {
        // TODO: 实现家长/学生登录逻辑
        // 1. 根据 userType 判断是家长还是学生
        // 2. 查询 parent 或 student 表验证手机号密码
        // 3. 生成JWT Token
        // 4. 返回登录信息

        AppLoginVO vo = new AppLoginVO();
        vo.setToken("mock-token");

        AppLoginVO.UserInfo userInfo = new AppLoginVO.UserInfo();
        userInfo.setId(1L);
        userInfo.setUsername(dto.getPhone());
        userInfo.setNickname("测试用户");
        userInfo.setPhone(dto.getPhone());
        userInfo.setRole(dto.getUserType());
        vo.setUserInfo(userInfo);

        return Result.success(vo);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/user-info")
    public Result<AppLoginVO.UserInfo> getUserInfo() {
        // TODO: 从SecurityContext获取当前用户信息
        AppLoginVO.UserInfo userInfo = new AppLoginVO.UserInfo();
        userInfo.setId(1L);
        userInfo.setUsername("13800138000");
        userInfo.setNickname("测试用户");
        userInfo.setPhone("13800138000");
        userInfo.setRole("parent");
        return Result.success(userInfo);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // TODO: 清除Token等
        return Result.success();
    }
}
