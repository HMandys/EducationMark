package com.edumark.system.controller;

import com.edumark.common.result.PageResult;
import com.edumark.common.result.Result;
import com.edumark.system.dto.SysPermissionDTO;
import com.edumark.system.dto.SysPermissionQueryDTO;
import com.edumark.system.entity.SysPermission;
import com.edumark.system.service.SysPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 权限管理控制器
 *
 * @author EduMark
 */
@Tag(name = "权限管理", description = "权限树查询")
@RestController
@RequestMapping("/system/permission")
public class SysPermissionController {

    private final SysPermissionService permissionService;

    public SysPermissionController(SysPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Operation(summary = "分页查询权限列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<PageResult<SysPermission>> getPermissionPage(SysPermissionQueryDTO query) {
        return Result.success(permissionService.getPermissionPage(query));
    }

    @Operation(summary = "获取权限详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public Result<SysPermission> getPermissionById(@PathVariable Long id) {
        return Result.success(permissionService.getPermissionById(id));
    }

    @Operation(summary = "获取权限树")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<List<SysPermission>> getPermissionTree() {
        return Result.success(permissionService.getPermissionTree());
    }

    @Operation(summary = "创建权限")
    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:add')")
    public Result<Long> createPermission(@Valid @RequestBody SysPermissionDTO dto) {
        return Result.success(permissionService.createPermission(dto));
    }

    @Operation(summary = "更新权限")
    @PutMapping
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public Result<Void> updatePermission(@Valid @RequestBody SysPermissionDTO dto) {
        permissionService.updatePermission(dto);
        return Result.success();
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public Result<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success();
    }

    @Operation(summary = "更新权限状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public Result<Void> updateStatus(
            @Parameter(description = "权限ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        permissionService.updateStatus(id, status);
        return Result.success();
    }
}
