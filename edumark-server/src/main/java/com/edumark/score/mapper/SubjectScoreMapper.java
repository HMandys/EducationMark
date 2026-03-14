package com.edumark.score.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.score.dto.ScoreQueryDTO;
import com.edumark.score.entity.SubjectScore;
import com.edumark.score.vo.SubjectScoreVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 科目成绩Mapper
 *
 * @author EduMark
 */
@Mapper
public interface SubjectScoreMapper extends BaseMapper<SubjectScore> {

    /**
     * 分页查询科目成绩
     */
    Page<SubjectScoreVO> selectPageVO(Page<SubjectScoreVO> page, @Param("query") ScoreQueryDTO query);

    /**
     * 根据考试ID和学生ID查询各科成绩
     */
    List<SubjectScoreVO> selectListByExamAndStudent(@Param("examId") Long examId, @Param("studentId") Long studentId);

    /**
     * 根据考试科目ID查询成绩
     */
    List<SubjectScore> selectListByExamSubjectId(@Param("examSubjectId") Long examSubjectId);

    /**
     * 删除考试科目成绩
     */
    int deleteByExamId(@Param("examId") Long examId);
}
