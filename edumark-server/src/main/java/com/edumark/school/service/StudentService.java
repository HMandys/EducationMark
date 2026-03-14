package com.edumark.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edumark.common.result.PageResult;
import com.edumark.school.dto.StudentDTO;
import com.edumark.school.dto.StudentQueryDTO;
import com.edumark.school.entity.Student;
import com.edumark.school.vo.StudentVO;

import java.util.List;

/**
 * 学生服务接口
 *
 * @author EduMark
 */
public interface StudentService extends IService<Student> {

    /**
     * 分页查询学生
     */
    PageResult<StudentVO> pageQuery(StudentQueryDTO query);

    /**
     * 根据ID查询学生详情
     */
    StudentVO getDetail(Long id);

    /**
     * 创建学生
     */
    Long create(StudentDTO dto);

    /**
     * 更新学生
     */
    void update(StudentDTO dto);

    /**
     * 删除学生
     */
    void delete(Long id);

    /**
     * 批量删除学生
     */
    void deleteBatch(List<Long> ids);

    /**
     * 更新学生状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 根据班级ID获取学生列表
     */
    List<StudentVO> listByClassId(Long classId);

    /**
     * 根据用户ID获取学生
     */
    Student getByUserId(Long userId);

    /**
     * 刷新学生绑定码
     */
    String refreshBindCode(Long id);
}
