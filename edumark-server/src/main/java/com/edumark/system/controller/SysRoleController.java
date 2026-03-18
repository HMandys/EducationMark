package com.edumark.system.controller;

import com.edumark.common.result.Result;
import com.edumark.common.result.PageResult;
import com.edumark.system.dto.SysRoleDTO;
import com.edumark.system.dto.SysRoleQueryDTO;
import com.edumark.system.service.SysRoleService;
import com.edumark.system.vo.SysRoleVO;
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
 * 角色管理控制器
 *
 * @author EduMark
 */
@Tag(name = "角色管理", description = "角色查询")
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    private final SysRoleService roleService;

    public SysRoleController(SysRoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "分页查询角色列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<PageResult<SysRoleVO>> getRolePage(SysRoleQueryDTO query) {
        return Result.success(roleService.getRolePage(query));
    }

    @Operation(summary = "获取所有角色列表")
    @GetMapping("/list")
    public Result<List<SysRoleVO>> getAllRoles() {
        List<SysRoleVO> roles = roleService.getAllRoles();
        return Result.success(roles);
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<SysRoleVO> getRoleById(@PathVariable Long id) {
        return Result.success(roleService.getRoleById(id));
    }

    @Operation(summary = "创建角色")
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public Result<Long> createRole(@Valid @RequestBody SysRoleDTO dto) {
        return Result.success(roleService.createRole(dto));
    }

    @Operation(summary = "更新角色")
    @PutMapping
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> updateRole(@Valid @RequestBody SysRoleDTO dto) {
        roleService.updateRole(dto);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    @Operation(summary = "启用/禁用角色")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> updateStatus(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        roleService.updateStatus(id, status);
        return Result.success();
    }
}
