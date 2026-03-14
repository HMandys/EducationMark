package com.edumark.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 绑定学生参数
 *
 * @author EduMark
 */
@Data
@Schema(description = "绑定学生参数")
public class BindStudentDTO {

    @NotBlank(message = "学生姓名不能为空")
    @Schema(description = "学生姓名")
    private String studentName;

    @NotBlank(message = "学号不能为空")
    @Schema(description = "学号")
    private String studentNumber;

    @NotBlank(message = "绑定码不能为空")
    @Schema(description = "绑定码")
    private String bindCode;
}
