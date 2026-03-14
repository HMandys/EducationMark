package com.edumark.common.utils;

import com.edumark.common.exception.BusinessException;
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
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        // 假设principal包含userId信息
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            // 从UserDetails中提取userId（需要根据实际实现调整）
            String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            // 这里假设username就是userId的字符串形式
            try {
                return Long.parseLong(username);
            } catch (NumberFormatException e) {
                throw new BusinessException("无法获取当前用户ID");
            }
        }
        throw new BusinessException("无法获取当前用户ID");
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        return authentication.getName();
    }
}
