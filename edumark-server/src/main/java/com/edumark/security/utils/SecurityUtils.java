package com.edumark.security.utils;

import com.edumark.security.service.LoginUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 *
 * @author EduMark
 */
public class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * 获取当前登录用户
     */
    public static LoginUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUserDetails) {
            return (LoginUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        LoginUserDetails user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        LoginUserDetails user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 获取当前用户学校ID
     */
    public static Long getCurrentSchoolId() {
        LoginUserDetails user = getCurrentUser();
        return user != null ? user.getSchoolId() : null;
    }

    /**
     * 获取当前用户角色编码
     */
    public static String getCurrentRoleCode() {
        LoginUserDetails user = getCurrentUser();
        return user != null ? user.getRoleCode() : null;
    }

    /**
     * 判断当前用户是否为超级管理员
     */
    public static boolean isSuperAdmin() {
        String roleCode = getCurrentRoleCode();
        return "SUPER_ADMIN".equals(roleCode);
    }
}
