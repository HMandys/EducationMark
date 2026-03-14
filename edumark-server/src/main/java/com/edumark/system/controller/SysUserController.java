package com.edumark.system.controller;

import com.edumark.common.result.PageResult;
import com.edumark.common.result.Result;
import com.edumark.system.dto.SysUserDTO;
import com.edumark.system.dto.SysUserQueryDTO;
import com.edumark.system.service.SysUserService;
import com.edumark.system.vo.SysUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 *
 * @author EduMark
 */
@Tag(name = "用户管理", description = "用户CRUD、密码管理")
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    private final SysUserService userService;

    public SysUserController(SysUserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result<PageResult<SysUserVO>> getUserPage(SysUserQueryDTO query) {
        PageResult<SysUserVO> page = userService.getUserPage(query);
        return Result.success(page);
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<SysUserVO> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        SysUserVO user = userService.getUserById(id);
        return Result.success(user);
    }

    @Operation(summary = "创建用户")
    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result<Long> createUser(@Valid @RequestBody SysUserDTO dto) {
        Long userId = userService.createUser(dto);
        return Result.success(userId);
    }

    @Operation(summary = "更新用户")
    @PutMapping
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> updateUser(@Valid @RequestBody SysUserDTO dto) {
        userService.updateUser(dto);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @Operation(summary = "批量删除用户")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public Result<Void> deleteUsers(@RequestBody List<Long> ids) {
        userService.deleteUsers(ids);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PutMapping("/{id}/password/reset")
    @PreAuthorize("hasAuthority('system:user:resetPwd')")
    public Result<Void> resetPassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.success();
    }

    @Operation(summary = "启用/禁用用户")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> updateStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }
}
