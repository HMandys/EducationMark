package com.edumark.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * 登录请求DTO
 *
 * @author EduMark
 */
@Schema(description = "登录请求参数")
public class LoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", required = true, example = "admin")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", required = true, example = "123456")
    private String password;

    @Schema(description = "验证码")
    private String captcha;

    @Schema(description = "验证码key")
    private String captchaKey;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getCaptchaKey() {
        return captchaKey;
    }

    public void setCaptchaKey(String captchaKey) {
        this.captchaKey = captchaKey;
    }
}
