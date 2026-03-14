package com.edumark.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edumark.common.result.PageResult;
import com.edumark.school.dto.SchoolDTO;
import com.edumark.school.dto.SchoolQueryDTO;
import com.edumark.school.entity.School;
import com.edumark.school.vo.SchoolVO;

import java.util.List;

/**
 * 学校服务接口
 *
 * @author EduMark
 */
public interface SchoolService extends IService<School> {

    /**
     * 分页查询学校
     */
    PageResult<SchoolVO> pageQuery(SchoolQueryDTO query);

    /**
     * 根据ID查询学校详情
     */
    SchoolVO getDetail(Long id);

    /**
     * 创建学校
     */
    Long create(SchoolDTO dto);

    /**
     * 更新学校
     */
    void update(SchoolDTO dto);

    /**
     * 删除学校
     */
    void delete(Long id);

    /**
     * 批量删除学校
     */
    void deleteBatch(List<Long> ids);

    /**
     * 更新学校状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 获取学校下拉列表
     */
    List<SchoolVO> listForSelect();
}
