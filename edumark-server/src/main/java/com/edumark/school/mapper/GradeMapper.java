package com.edumark.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.school.dto.GradeQueryDTO;
import com.edumark.school.entity.Grade;
import com.edumark.school.vo.GradeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 年级Mapper
 *
 * @author EduMark
 */
@Mapper
public interface GradeMapper extends BaseMapper<Grade> {

    /**
     * 分页查询年级
     */
    IPage<GradeVO> selectPageVO(Page<GradeVO> page, @Param("query") GradeQueryDTO query);

    /**
     * 根据ID查询年级详情
     */
    GradeVO selectVOById(@Param("id") Long id);

    /**
     * 根据学校ID查询年级列表(下拉选择用)
     */
    List<GradeVO> selectListBySchoolId(@Param("schoolId") Long schoolId, @Param("status") Integer status);
}
