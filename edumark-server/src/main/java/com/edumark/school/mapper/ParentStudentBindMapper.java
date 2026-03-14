package com.edumark.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edumark.school.entity.ParentStudentBind;
import com.edumark.school.vo.ParentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 家长学生绑定关系Mapper
 *
 * @author EduMark
 */
@Mapper
public interface ParentStudentBindMapper extends BaseMapper<ParentStudentBind> {

    /**
     * 查询家长绑定的学生列表
     */
    List<ParentVO.StudentBindVO> selectStudentsByParentId(@Param("parentId") Long parentId);

    /**
     * 查询学生绑定的家长列表
     */
    List<ParentStudentBind> selectByStudentId(@Param("studentId") Long studentId);

    /**
     * 检查绑定是否存在
     */
    ParentStudentBind selectByParentAndStudent(@Param("parentId") Long parentId, @Param("studentId") Long studentId);
}
