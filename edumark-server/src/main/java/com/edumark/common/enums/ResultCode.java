package com.edumark.common.enums;

/**
 * 响应状态码枚举
 *
 * @author EduMark
 */
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有操作权限"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    CONFLICT(409, "数据冲突"),

    // 业务错误 5xx
    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    // 用户相关 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "用户名或密码错误"),
    USER_DISABLED(1003, "用户已被禁用"),
    USER_ALREADY_EXISTS(1004, "用户已存在"),
    OLD_PASSWORD_ERROR(1005, "原密码错误"),
    TOKEN_INVALID(1006, "Token无效或已过期"),
    TOKEN_EXPIRED(1007, "Token已过期"),

    // 权限相关 2xxx
    NO_PERMISSION(2001, "没有操作权限"),
    ROLE_NOT_FOUND(2002, "角色不存在"),
    PERMISSION_DENIED(2003, "权限不足"),

    // 学校组织相关 3xxx
    SCHOOL_NOT_FOUND(3001, "学校不存在"),
    GRADE_NOT_FOUND(3002, "年级不存在"),
    CLASS_NOT_FOUND(3003, "班级不存在"),
    STUDENT_NOT_FOUND(3004, "学生不存在"),
    TEACHER_NOT_FOUND(3005, "教师不存在"),
    PARENT_NOT_FOUND(3006, "家长不存在"),
    BIND_CODE_ERROR(3007, "绑定码错误"),
    ALREADY_BINDDED(3008, "已绑定该学生"),

    // 考试相关 4xxx
    EXAM_NOT_FOUND(4001, "考试不存在"),
    EXAM_STATUS_ERROR(4002, "考试状态错误"),
    SUBJECT_NOT_FOUND(4003, "科目不存在"),
    PAPER_NOT_FOUND(4004, "试卷不存在"),
    QUESTION_NOT_FOUND(4005, "题目不存在"),

    // 阅卷相关 5xxx
    MARKING_TASK_NOT_FOUND(5001, "阅卷任务不存在"),
    MARKING_TASK_ALREADY_CLAIMED(5002, "阅卷任务已被领取"),
    MARKING_SCORE_INVALID(5003, "评分无效"),
    MARKING_ALREADY_SUBMITTED(5004, "已提交评分"),
    ARBITRATION_REQUIRED(5005, "需要仲裁"),

    // 成绩相关 6xxx
    SCORE_NOT_FOUND(6001, "成绩不存在"),
    SCORE_NOT_PUBLISHED(6002, "成绩未发布"),
    SCORE_ALREADY_PUBLISHED(6003, "成绩已发布"),

    // 文件相关 7xxx
    FILE_UPLOAD_ERROR(7001, "文件上传失败"),
    FILE_NOT_FOUND(7002, "文件不存在"),
    FILE_TYPE_NOT_ALLOWED(7003, "文件类型不允许"),
    FILE_SIZE_EXCEEDED(7004, "文件大小超出限制"),

    // AI相关 8xxx
    AI_SERVICE_ERROR(8001, "AI服务异常"),
    AI_TASK_NOT_FOUND(8002, "AI分析任务不存在"),
    AI_REPORT_NOT_FOUND(8003, "AI报告不存在");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
