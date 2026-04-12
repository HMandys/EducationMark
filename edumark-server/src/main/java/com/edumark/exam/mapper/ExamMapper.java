package com.edumark.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.exam.dto.ExamQueryDTO;
import com.edumark.exam.entity.Exam;
import com.edumark.exam.vo.ExamVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考试Mapper
 *
 * @author EduMark
 */
@Mapper
public interface ExamMapper extends BaseMapper<Exam> {

    /**
     * 分页查询考试
     */
    IPage<ExamVO> selectPageVO(Page<ExamVO> page, @Param("query") ExamQueryDTO query);

    /**
     * 根据ID查询考试详情
     */
    ExamVO selectVOById(@Param("id") Long id);

    /**
     * 查询学生已发布考试列表
     */
    List<ExamVO> selectPublishedListByStudentId(@Param("studentId") Long studentId);
}
