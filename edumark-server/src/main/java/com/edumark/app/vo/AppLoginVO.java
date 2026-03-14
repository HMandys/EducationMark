package com.edumark.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * App登录结果
 *
 * @author EduMark
 */
@Data
@Schema(description = "App登录结果")
public class AppLoginVO {

    @Schema(description = "Token")
    private String token;

    @Schema(description = "用户信息")
    private UserInfo userInfo;

    @Data
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
    }
}
