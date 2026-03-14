package com.edumark.system.controller;

import com.edumark.common.result.Result;
import com.edumark.system.service.SysRoleService;
import com.edumark.system.vo.SysRoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @Operation(summary = "获取所有角色列表")
    @GetMapping("/list")
    public Result<List<SysRoleVO>> getAllRoles() {
        List<SysRoleVO> roles = roleService.getAllRoles();
        return Result.success(roles);
    }
}
