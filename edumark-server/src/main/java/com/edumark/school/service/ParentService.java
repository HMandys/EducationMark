package com.edumark.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edumark.common.result.PageResult;
import com.edumark.school.dto.ParentBindDTO;
import com.edumark.school.dto.ParentDTO;
import com.edumark.school.dto.ParentQueryDTO;
import com.edumark.school.entity.Parent;
import com.edumark.school.vo.ParentVO;

import java.util.List;

/**
 * 家长服务接口
 *
 * @author EduMark
 */
public interface ParentService extends IService<Parent> {

    /**
     * 分页查询家长
     */
    PageResult<ParentVO> pageQuery(ParentQueryDTO query);

    /**
     * 根据ID查询家长详情
     */
    ParentVO getDetail(Long id);

    /**
     * 创建家长
     */
    Long create(ParentDTO dto);

    /**
     * 更新家长
     */
    void update(ParentDTO dto);

    /**
     * 删除家长
     */
    void delete(Long id);

    /**
     * 批量删除家长
     */
    void deleteBatch(List<Long> ids);

    /**
     * 更新家长状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 根据用户ID获取家长
     */
    Parent getByUserId(Long userId);

    /**
     * 家长绑定学生
     */
    void bindStudent(Long parentId, ParentBindDTO dto);

    /**
     * 家长解绑学生
     */
    void unbindStudent(Long parentId, Long studentId);

    /**
     * 获取家长绑定的学生列表
     */
    List<ParentVO.StudentBindVO> getBoundStudents(Long parentId);
}
