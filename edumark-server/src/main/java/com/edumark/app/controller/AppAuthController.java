package com.edumark.app.controller;

import com.edumark.app.dto.AppLoginDTO;
import com.edumark.app.dto.AppChangePasswordDTO;
import com.edumark.app.vo.AppLoginVO;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.Result;
import com.edumark.security.service.AuthService;
import com.edumark.security.service.LoginUserDetails;
import com.edumark.security.utils.SecurityUtils;
import com.edumark.system.dto.LoginDTO;
import com.edumark.system.entity.SysUser;
import com.edumark.system.service.SysUserService;
import com.edumark.system.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * App认证控制器
 *
 * @author EduMark
 */
@Tag(name = "App-认证接口")
@RestController
@RequestMapping("/app/auth")
public class AppAuthController {

    private final SysUserService sysUserService;
    private final AuthService authService;

    public AppAuthController(SysUserService sysUserService, AuthService authService) {
        this.sysUserService = sysUserService;
        this.authService = authService;
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<AppLoginVO> login(@Valid @RequestBody AppLoginDTO dto) {
        SysUser matchedUser = resolveAppUser(dto);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(matchedUser.getUsername());
        loginDTO.setPassword(dto.getPassword());

        LoginVO loginVO = authService.login(loginDTO);
        return Result.success(toAppLoginVO(loginVO, matchedUser));
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/user-info")
    public Result<AppLoginVO.UserInfo> getUserInfo() {
        LoginUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException("未登录");
        }

        SysUser user = currentUser.getUser();
        AppLoginVO.UserInfo userInfo = new AppLoginVO.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(resolveNickname(user));
        userInfo.setPhone(user.getPhone());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setRole(resolveRole(user));
        return Result.success(userInfo);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @Operation(summary = "修改密码")
    @PostMapping("/change-password")
    public Result<Void> changePassword(@Valid @RequestBody AppChangePasswordDTO dto) {
        LoginUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null || currentUser.getUser() == null) {
            throw new BusinessException("未登录");
        }

        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new BusinessException("新密码不能与原密码相同");
        }

        sysUserService.changePassword(currentUser.getUser().getId(), dto.getOldPassword(), dto.getNewPassword());
        return Result.success();
    }

    private SysUser resolveAppUser(AppLoginDTO dto) {
        List<SysUser> candidates = sysUserService.lambdaQuery()
                .eq(SysUser::getPhone, dto.getPhone())
                .eq(SysUser::getStatus, 1)
                .list();

        if (candidates == null || candidates.isEmpty()) {
            throw new BusinessException("账号不存在或未开通");
        }

        return candidates.stream()
                .map(candidate -> sysUserService.getByUsername(candidate.getUsername()))
                .filter(Objects::nonNull)
                .filter(user -> matchesAppRole(user, dto.getUserType()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("该手机号未开通对应的家长/学生端账号"));
    }

    private boolean matchesAppRole(SysUser user, String userType) {
        String roleCode = user.getRoleCode();
        if ("parent".equalsIgnoreCase(userType)) {
            return "PARENT".equalsIgnoreCase(roleCode) || ((roleCode == null || roleCode.isBlank()) && Objects.equals(user.getUserType(), 2));
        }
        if ("student".equalsIgnoreCase(userType)) {
            return "STUDENT".equalsIgnoreCase(roleCode) || ((roleCode == null || roleCode.isBlank()) && Objects.equals(user.getUserType(), 3));
        }
        return false;
    }

    private AppLoginVO toAppLoginVO(LoginVO loginVO, SysUser user) {
        AppLoginVO vo = new AppLoginVO();
        vo.setToken(loginVO.getToken());

        AppLoginVO.UserInfo userInfo = new AppLoginVO.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(resolveNickname(user));
        userInfo.setPhone(user.getPhone());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setRole(resolveRole(user));
        vo.setUserInfo(userInfo);
        return vo;
    }

    private String resolveNickname(SysUser user) {
        if (user == null) {
            return "";
        }
        if (user.getRealName() != null && !user.getRealName().isBlank()) {
            return user.getRealName();
        }
        return user.getUsername();
    }

    private String resolveRole(SysUser user) {
        if (user == null) {
            return "";
        }
        if ("STUDENT".equalsIgnoreCase(user.getRoleCode()) || Objects.equals(user.getUserType(), 3)) {
            return "student";
        }
        return "parent";
    }
}
