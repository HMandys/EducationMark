package com.edumark.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edumark.common.result.PageResult;
import com.edumark.exam.dto.ExamDTO;
import com.edumark.exam.dto.ExamQueryDTO;
import com.edumark.exam.entity.Exam;
import com.edumark.exam.vo.ExamVO;

import java.util.List;

/**
 * 考试服务接口
 *
 * @author EduMark
 */
public interface ExamService extends IService<Exam> {

    /**
     * 分页查询考试
     */
    PageResult<ExamVO> pageQuery(ExamQueryDTO query);

    /**
     * 根据ID查询考试详情
     */
    ExamVO getDetail(Long id);

    /**
     * 创建考试
     */
    Long create(ExamDTO dto);

    /**
     * 更新考试
     */
    void update(ExamDTO dto);

    /**
     * 删除考试
     */
    void delete(Long id);

    /**
     * 批量删除考试
     */
    void deleteBatch(List<Long> ids);

    /**
     * 更新考试状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 发布考试
     */
    void publish(Long id);

    /**
     * 撤回发布
     */
    void unpublish(Long id);
}
