package com.edumark.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * App登录参数
 *
 * @author EduMark
 */
@Data
@Schema(description = "App登录参数")
public class AppLoginDTO {

    @NotBlank(message = "手机号不能为空")
    @Schema(description = "手机号")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;

    @NotBlank(message = "用户类型不能为空")
    @Schema(description = "用户类型: parent-家长, student-学生")
    private String userType;
}
