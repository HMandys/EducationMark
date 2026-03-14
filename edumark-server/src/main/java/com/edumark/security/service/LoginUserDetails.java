package com.edumark.security.service;

import com.edumark.system.entity.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录用户详情
 *
 * @author EduMark
 */
public class LoginUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final Long userId;
    private final String username;
    private final String password;
    private final String realName;
    private final Long schoolId;
    private final String roleCode;
    private final Integer status;
    private final List<String> permissions;
    private final SysUser user;

    public LoginUserDetails(SysUser user, List<String> permissions) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.realName = user.getRealName();
        this.schoolId = user.getSchoolId();
        this.roleCode = user.getRoleCode();
        this.status = user.getStatus();
        this.permissions = permissions;
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (permissions == null || permissions.isEmpty()) {
            return List.of();
        }
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status != null && this.status == 1;
    }

    public Long getUserId() { return userId; }
    public String getRealName() { return realName; }
    public Long getSchoolId() { return schoolId; }
    public String getRoleCode() { return roleCode; }
    public Integer getStatus() { return status; }
    public List<String> getPermissions() { return permissions; }
    public SysUser getUser() { return user; }
}
