package com.edumark.common.constant;

/**
 * 通用常量
 *
 * @author EduMark
 */
public class CommonConstant {

    private CommonConstant() {
    }

    /**
     * 超级管理员角色编码
     */
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";

    /**
     * 学校管理员角色编码
     */
    public static final String ROLE_SCHOOL_ADMIN = "SCHOOL_ADMIN";

    /**
     * 教务主任角色编码
     */
    public static final String ROLE_DIRECTOR = "DIRECTOR";

    /**
     * 阅卷组长角色编码
     */
    public static final String ROLE_MARKING_LEADER = "MARKING_LEADER";

    /**
     * 任课教师角色编码
     */
    public static final String ROLE_TEACHER = "TEACHER";

    /**
     * 班主任角色编码
     */
    public static final String ROLE_HEAD_TEACHER = "HEAD_TEACHER";

    /**
     * 家长角色编码
     */
    public static final String ROLE_PARENT = "PARENT";

    /**
     * 学生角色编码
     */
    public static final String ROLE_STUDENT = "STUDENT";

    /**
     * 正常状态
     */
    public static final Integer STATUS_NORMAL = 1;

    /**
     * 禁用状态
     */
    public static final Integer STATUS_DISABLED = 0;

    /**
     * 删除标记：未删除
     */
    public static final Integer NOT_DELETED = 0;

    /**
     * 删除标记：已删除
     */
    public static final Integer DELETED = 1;

    /**
     * Redis 缓存前缀
     */
    public static final String REDIS_PREFIX = "edumark:";

    /**
     * 登录用户缓存前缀
     */
    public static final String LOGIN_USER_KEY = REDIS_PREFIX + "login:user:";

    /**
     * Token 黑名单前缀
     */
    public static final String TOKEN_BLACKLIST_KEY = REDIS_PREFIX + "token:blacklist:";

    /**
     * 验证码缓存前缀
     */
    public static final String CAPTCHA_KEY = REDIS_PREFIX + "captcha:";

    /**
     * 家长绑定码前缀
     */
    public static final String BIND_CODE_KEY = REDIS_PREFIX + "bind:code:";
}
