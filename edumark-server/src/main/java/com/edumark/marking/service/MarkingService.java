package com.edumark.marking.service;

import com.edumark.common.result.PageResult;
import com.edumark.marking.dto.ArbitrationSubmitDTO;
import com.edumark.marking.dto.MarkingSubmitDTO;
import com.edumark.marking.vo.MarkingArbitrationVO;
import com.edumark.marking.vo.MarkingRecordVO;
import com.edumark.marking.vo.MarkingTaskAssignVO;
import com.edumark.marking.vo.ProblemRecordStatistics;

import java.util.List;

/**
 * 阅卷服务接口
 *
 * @author EduMark
 */
public interface MarkingService {

    /**
     * 获取教师的阅卷任务分配
     */
    List<MarkingTaskAssignVO> getMyAssigns(Long teacherId);

    /**
     * 分页查询阅卷记录
     */
    PageResult<MarkingRecordVO> pageRecords(Long taskId, Long teacherId, Integer status, int pageNum, int pageSize);

    /**
     * 获取待阅卷的下一条记录
     */
    MarkingRecordVO getNextPending(Long taskId, Long teacherId);

    /**
     * 获取阅卷记录详情
     */
    MarkingRecordVO getRecordDetail(Long recordId);

    /**
     * 提交评分
     */
    void submitScore(MarkingSubmitDTO dto, Long teacherId);

    /**
     * 分页查询仲裁记录
     */
    PageResult<MarkingArbitrationVO> pageArbitrations(Long taskId, Integer status, int pageNum, int pageSize);

    /**
     * 获取待仲裁的下一条记录
     */
    MarkingArbitrationVO getNextArbitration(Long taskId, Long teacherId);

    /**
     * 获取仲裁记录详情
     */
    MarkingArbitrationVO getArbitrationDetail(Long arbitrationId);

    /**
     * 提交仲裁
     */
    void submitArbitration(ArbitrationSubmitDTO dto, Long teacherId);

    /**
     * 客观题自动判分
     */
    void autoMarkObjective(Long examSubjectId);

    /**
     * 标记为问题卷
     *
     * @param recordId      阅卷记录ID
     * @param problemReason 问题原因
     * @param teacherId     教师ID
     */
    void markAsProblem(Long recordId, String problemReason, Long teacherId);

    /**
     * 取消问题卷标记
     *
     * @param recordId  阅卷记录ID
     * @param teacherId 教师ID
     */
    void unmarkProblem(Long recordId, Long teacherId);

    /**
     * 获取问题卷列表
     *
     * @param taskId 阅卷任务ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 问题卷列表
     */
    PageResult<MarkingRecordVO> pageProblemRecords(Long taskId, int pageNum, int pageSize);

    /**
     * 获取问题卷统计
     *
     * @param taskId 阅卷任务ID
     * @return 统计信息
     */
    ProblemRecordStatistics getProblemStatistics(Long taskId);
}
