package com.edumark.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * App登录结果
 *
 * @author EduMark
 */
@Schema(description = "App登录结果")
public class AppLoginVO {

    @Schema(description = "Token")
    private String token;

    @Schema(description = "用户信息")
    private UserInfo userInfo;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UserInfo getUserInfo() { return userInfo; }
    public void setUserInfo(UserInfo userInfo) { this.userInfo = userInfo; }

    @Schema(description = "用户信息")
    public static class UserInfo {
        @Schema(description = "用户ID")
        private Long id;

        @Schema(description = "用户名")
        private String username;

        @Schema(description = "昵称")
        private String nickname;

        @Schema(description = "手机号")
        private String phone;

        @Schema(description = "头像")
        private String avatar;

        @Schema(description = "角色: parent-家长, student-学生")
        private String role;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
