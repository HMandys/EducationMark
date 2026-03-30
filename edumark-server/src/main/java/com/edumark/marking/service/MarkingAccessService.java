package com.edumark.marking.service;

import com.edumark.marking.vo.MarkingItemVO;
import com.edumark.marking.vo.MarkingSessionVO;
import com.edumark.marking.vo.MarkingTaskVO;

/**
 * 阅卷访问服务接口
 *
 * @author EduMark
 */
public interface MarkingAccessService {

    /**
     * 生成8位数字阅卷码
     *
     * @param taskId 任务ID
     * @return 任务详情
     */
    MarkingTaskVO generateAccessCode(Long taskId);

    /**
     * 验证阅卷码并创建会话
     *
     * @param accessCode 阅卷码
     * @return 会话信息
     */
    MarkingSessionVO validateAndLogin(String accessCode);

    /**
     * 验证会话Token
     *
     * @param sessionToken 会话令牌
     * @return 是否有效
     */
    boolean validateSession(String sessionToken);

    /**
     * 根据会话Token获取任务ID
     *
     * @param sessionToken 会话令牌
     * @return 任务ID
     */
    Long getTaskIdByToken(String sessionToken);

    /**
     * 获取任务信息
     *
     * @param sessionToken 会话令牌
     * @return 任务信息
     */
    MarkingSessionVO getTaskInfo(String sessionToken);

    /**
     * 获取下一份待阅记录
     *
     * @param sessionToken 会话令牌
     * @return 待阅项
     */
    MarkingItemVO getNextItem(String sessionToken);

    /**
     * 提交评分
     *
     * @param sessionToken 会话令牌
     * @param recordId     阅卷记录ID
     * @param score        分数
     * @param comment      评语
     * @return 是否成功
     */
    boolean submitScore(String sessionToken, Long recordId, Integer score, String comment, String annotations);

    /**
     * 刷新阅卷码过期时间
     *
     * @param taskId 任务ID
     * @param hours  有效时长（小时）
     */
    void refreshAccessCodeExpireTime(Long taskId, int hours);

    /**
     * 跳过当前记录
     *
     * @param sessionToken 会话令牌
     * @param recordId     阅卷记录ID
     * @return 是否成功
     */
    boolean skipRecord(String sessionToken, Long recordId);
}
