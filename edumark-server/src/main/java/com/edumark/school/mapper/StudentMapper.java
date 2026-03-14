package com.edumark.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.school.dto.StudentQueryDTO;
import com.edumark.school.entity.Student;
import com.edumark.school.vo.StudentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生Mapper
 *
 * @author EduMark
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 分页查询学生
     */
    IPage<StudentVO> selectPageVO(Page<StudentVO> page, @Param("query") StudentQueryDTO query);

    /**
     * 根据ID查询学生详情
     */
    StudentVO selectVOById(@Param("id") Long id);

    /**
     * 根据班级ID查询学生列表
     */
    List<StudentVO> selectListByClassId(@Param("classId") Long classId, @Param("status") Integer status);

    /**
     * 根据学号和姓名查询学生(用于家长绑定验证)
     */
    Student selectByNumberAndName(@Param("studentNumber") String studentNumber, @Param("name") String name);

    /**
     * 根据用户ID查询学生
     */
    Student selectByUserId(@Param("userId") Long userId);
}
