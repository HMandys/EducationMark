package com.edumark.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.school.dto.ClassInfoQueryDTO;
import com.edumark.school.entity.ClassInfo;
import com.edumark.school.vo.ClassInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 班级Mapper
 *
 * @author EduMark
 */
@Mapper
public interface ClassInfoMapper extends BaseMapper<ClassInfo> {

    /**
     * 分页查询班级
     */
    IPage<ClassInfoVO> selectPageVO(Page<ClassInfoVO> page, @Param("query") ClassInfoQueryDTO query);

    /**
     * 根据ID查询班级详情
     */
    ClassInfoVO selectVOById(@Param("id") Long id);

    /**
     * 根据年级ID查询班级列表(下拉选择用)
     */
    List<ClassInfoVO> selectListByGradeId(@Param("gradeId") Long gradeId, @Param("status") Integer status);

    /**
     * 根据学校ID查询班级列表
     */
    List<ClassInfoVO> selectListBySchoolId(@Param("schoolId") Long schoolId, @Param("status") Integer status);
}
