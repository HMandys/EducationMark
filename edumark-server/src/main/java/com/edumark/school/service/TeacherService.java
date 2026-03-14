package com.edumark.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edumark.common.result.PageResult;
import com.edumark.school.dto.TeacherDTO;
import com.edumark.school.dto.TeacherQueryDTO;
import com.edumark.school.entity.Teacher;
import com.edumark.school.vo.TeacherVO;

import java.util.List;

/**
 * 教师服务接口
 *
 * @author EduMark
 */
public interface TeacherService extends IService<Teacher> {

    /**
     * 分页查询教师
     */
    PageResult<TeacherVO> pageQuery(TeacherQueryDTO query);

    /**
     * 根据ID查询教师详情
     */
    TeacherVO getDetail(Long id);

    /**
     * 创建教师
     */
    Long create(TeacherDTO dto);

    /**
     * 更新教师
     */
    void update(TeacherDTO dto);

    /**
     * 删除教师
     */
    void delete(Long id);

    /**
     * 批量删除教师
     */
    void deleteBatch(List<Long> ids);

    /**
     * 更新教师状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 根据学校ID获取教师列表
     */
    List<TeacherVO> listBySchoolId(Long schoolId);

    /**
     * 根据用户ID获取教师
     */
    Teacher getByUserId(Long userId);
}
