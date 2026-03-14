package com.edumark.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edumark.common.result.PageResult;
import com.edumark.school.dto.ClassInfoDTO;
import com.edumark.school.dto.ClassInfoQueryDTO;
import com.edumark.school.entity.ClassInfo;
import com.edumark.school.vo.ClassInfoVO;

import java.util.List;

/**
 * 班级服务接口
 *
 * @author EduMark
 */
public interface ClassInfoService extends IService<ClassInfo> {

    /**
     * 分页查询班级
     */
    PageResult<ClassInfoVO> pageQuery(ClassInfoQueryDTO query);

    /**
     * 根据ID查询班级详情
     */
    ClassInfoVO getDetail(Long id);

    /**
     * 创建班级
     */
    Long create(ClassInfoDTO dto);

    /**
     * 更新班级
     */
    void update(ClassInfoDTO dto);

    /**
     * 删除班级
     */
    void delete(Long id);

    /**
     * 批量删除班级
     */
    void deleteBatch(List<Long> ids);

    /**
     * 更新班级状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 根据年级ID获取班级列表
     */
    List<ClassInfoVO> listByGradeId(Long gradeId);

    /**
     * 根据学校ID获取班级列表
     */
    List<ClassInfoVO> listBySchoolId(Long schoolId);
}
