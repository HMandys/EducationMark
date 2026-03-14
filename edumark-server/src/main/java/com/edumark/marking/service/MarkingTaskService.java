package com.edumark.marking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edumark.common.result.PageResult;
import com.edumark.marking.dto.MarkingTaskAssignDTO;
import com.edumark.marking.dto.MarkingTaskQueryDTO;
import com.edumark.marking.entity.MarkingTask;
import com.edumark.marking.vo.MarkingTaskVO;

import java.util.List;

/**
 * 阅卷任务服务接口
 *
 * @author EduMark
 */
public interface MarkingTaskService extends IService<MarkingTask> {

    /**
     * 分页查询阅卷任务
     */
    PageResult<MarkingTaskVO> pageQuery(MarkingTaskQueryDTO query);

    /**
     * 根据ID查询阅卷任务详情
     */
    MarkingTaskVO getDetail(Long id);

    /**
     * 生成阅卷任务
     */
    void generateTasks(Long examSubjectId);

    /**
     * 删除阅卷任务
     */
    void delete(Long id);

    /**
     * 分配阅卷任务
     */
    void assignTask(MarkingTaskAssignDTO dto);

    /**
     * 开始阅卷任务
     */
    void startTask(Long id);

    /**
     * 完成阅卷任务
     */
    void completeTask(Long id);

    /**
     * 根据考试科目ID查询阅卷任务列表
     */
    List<MarkingTaskVO> listByExamSubjectId(Long examSubjectId);

    /**
     * 更新任务进度
     */
    void updateProgress(Long taskId);
}
