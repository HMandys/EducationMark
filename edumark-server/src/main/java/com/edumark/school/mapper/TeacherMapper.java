package com.edumark.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.school.dto.TeacherQueryDTO;
import com.edumark.school.entity.Teacher;
import com.edumark.school.vo.TeacherVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教师Mapper
 *
 * @author EduMark
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

    /**
     * 分页查询教师
     */
    IPage<TeacherVO> selectPageVO(Page<TeacherVO> page, @Param("query") TeacherQueryDTO query);

    /**
     * 根据ID查询教师详情
     */
    TeacherVO selectVOById(@Param("id") Long id);

    /**
     * 根据学校ID查询教师列表(下拉选择用)
     */
    List<TeacherVO> selectListBySchoolId(@Param("schoolId") Long schoolId, @Param("status") Integer status);

    /**
     * 根据用户ID查询教师
     */
    Teacher selectByUserId(@Param("userId") Long userId);
}
