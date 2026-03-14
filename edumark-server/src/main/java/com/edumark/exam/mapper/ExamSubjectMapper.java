package com.edumark.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edumark.exam.entity.ExamSubject;
import com.edumark.exam.vo.ExamSubjectVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考试科目Mapper
 *
 * @author EduMark
 */
@Mapper
public interface ExamSubjectMapper extends BaseMapper<ExamSubject> {

    /**
     * 根据考试ID查询科目列表
     */
    List<ExamSubjectVO> selectListByExamId(@Param("examId") Long examId);

    /**
     * 根据ID查询科目详情
     */
    ExamSubjectVO selectVOById(@Param("id") Long id);
}
