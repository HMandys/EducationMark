package com.edumark.security.service;

import com.edumark.common.enums.ResultCode;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.utils.JwtUtils;
import com.edumark.system.dto.LoginDTO;
import com.edumark.system.entity.SysUser;
import com.edumark.system.service.SysUserService;
import com.edumark.system.vo.LoginVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证服务
 *
 * @author EduMark
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final SysUserService userService;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    public AuthService(AuthenticationManager authenticationManager, JwtUtils jwtUtils, SysUserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    /**
     * 登录
     */
    public LoginVO login(LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );

            LoginUserDetails userDetails = (LoginUserDetails) authentication.getPrincipal();
            SysUser user = userDetails.getUser();

            String token = jwtUtils.generateToken(
                    user.getId(),
                    user.getUsername(),
                    user.getSchoolId(),
                    user.getRoleCode()
            );

            List<String> permissions = userService.getPermissionCodes(user.getId());

            LoginVO.UserInfoVO userInfo = new LoginVO.UserInfoVO();
            userInfo.setUserId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setRealName(user.getRealName());
            userInfo.setAvatar(user.getAvatar());
            userInfo.setRoleCode(user.getRoleCode());
            userInfo.setRoleName(user.getRoleName());
            userInfo.setSchoolId(user.getSchoolId());
            userInfo.setSchoolName(user.getSchoolName());
            userInfo.setPermissions(permissions);

            return new LoginVO(token, "Bearer", jwtExpiration / 1000, userInfo);

        } catch (DisabledException e) {
            log.warn("用户已被禁用: {}", loginDTO.getUsername());
            throw new BusinessException(ResultCode.USER_DISABLED);
        } catch (BadCredentialsException e) {
            log.warn("用户名或密码错误: {}", loginDTO.getUsername());
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "登录失败");
        }
    }

    /**
     * 获取当前登录用户信息
     */
    public LoginVO.UserInfoVO getCurrentUser(Long userId) {
        SysUser user = userService.getByUsername(
                userService.getById(userId).getUsername()
        );

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        List<String> permissions = userService.getPermissionCodes(userId);

        LoginVO.UserInfoVO userInfo = new LoginVO.UserInfoVO();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setRoleCode(user.getRoleCode());
        userInfo.setRoleName(user.getRoleName());
        userInfo.setSchoolId(user.getSchoolId());
        userInfo.setSchoolName(user.getSchoolName());
        userInfo.setPermissions(permissions);

        return userInfo;
    }
}
