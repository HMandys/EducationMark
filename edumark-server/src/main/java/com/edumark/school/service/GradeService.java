package com.edumark.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edumark.common.result.PageResult;
import com.edumark.school.dto.GradeDTO;
import com.edumark.school.dto.GradeQueryDTO;
import com.edumark.school.entity.Grade;
import com.edumark.school.vo.GradeVO;

import java.util.List;

/**
 * 年级服务接口
 *
 * @author EduMark
 */
public interface GradeService extends IService<Grade> {

    /**
     * 分页查询年级
     */
    PageResult<GradeVO> pageQuery(GradeQueryDTO query);

    /**
     * 根据ID查询年级详情
     */
    GradeVO getDetail(Long id);

    /**
     * 创建年级
     */
    Long create(GradeDTO dto);

    /**
     * 更新年级
     */
    void update(GradeDTO dto);

    /**
     * 删除年级
     */
    void delete(Long id);

    /**
     * 批量删除年级
     */
    void deleteBatch(List<Long> ids);

    /**
     * 更新年级状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 根据学校ID获取年级列表
     */
    List<GradeVO> listBySchoolId(Long schoolId);
}
