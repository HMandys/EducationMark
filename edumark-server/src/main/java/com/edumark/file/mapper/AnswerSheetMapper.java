package com.edumark.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.file.dto.AnswerSheetQueryDTO;
import com.edumark.file.entity.AnswerSheet;
import com.edumark.file.vo.AnswerSheetVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 答题卡Mapper
 *
 * @author EduMark
 */
@Mapper
public interface AnswerSheetMapper extends BaseMapper<AnswerSheet> {

    /**
     * 分页查询答题卡
     */
    IPage<AnswerSheetVO> selectPageVO(Page<AnswerSheetVO> page, @Param("query") AnswerSheetQueryDTO query);

    /**
     * 根据ID查询答题卡详情
     */
    AnswerSheetVO selectVOById(@Param("id") Long id);

    /**
     * 根据考试科目ID查询答题卡列表
     */
    List<AnswerSheetVO> selectListByExamSubjectId(@Param("examSubjectId") Long examSubjectId);

    /**
     * 根据学生ID和考试科目ID查询答题卡
     */
    AnswerSheet selectByStudentAndSubject(@Param("studentId") Long studentId, @Param("examSubjectId") Long examSubjectId);
}
