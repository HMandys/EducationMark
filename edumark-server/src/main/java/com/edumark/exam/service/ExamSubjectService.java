package com.edumark.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edumark.exam.dto.ExamSubjectDTO;
import com.edumark.exam.entity.ExamSubject;
import com.edumark.exam.vo.ExamSubjectVO;

import java.util.List;

/**
 * 考试科目服务接口
 *
 * @author EduMark
 */
public interface ExamSubjectService extends IService<ExamSubject> {

    /**
     * 根据考试ID查询科目列表
     */
    List<ExamSubjectVO> listByExamId(Long examId);

    /**
     * 根据ID查询科目详情
     */
    ExamSubjectVO getDetail(Long id);

    /**
     * 创建科目
     */
    Long create(ExamSubjectDTO dto);

    /**
     * 更新科目
     */
    void update(ExamSubjectDTO dto);

    /**
     * 删除科目
     */
    void delete(Long id);

    /**
     * 批量添加科目
     */
    void batchCreate(Long examId, List<ExamSubjectDTO> subjects);
}
