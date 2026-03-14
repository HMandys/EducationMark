package com.edumark.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edumark.exam.entity.ExamClass;
import com.edumark.exam.vo.ExamVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考试班级关联Mapper
 *
 * @author EduMark
 */
@Mapper
public interface ExamClassMapper extends BaseMapper<ExamClass> {

    /**
     * 根据考试ID查询班级列表
     */
    List<ExamVO.ExamClassVO> selectListByExamId(@Param("examId") Long examId);

    /**
     * 删除考试的班级关联
     */
    int deleteByExamId(@Param("examId") Long examId);
}
