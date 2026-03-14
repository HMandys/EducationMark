package com.edumark.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edumark.exam.entity.Paper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 试卷Mapper
 *
 * @author EduMark
 */
@Mapper
public interface PaperMapper extends BaseMapper<Paper> {

    /**
     * 根据考试科目ID查询试卷
     */
    Paper selectByExamSubjectId(@Param("examSubjectId") Long examSubjectId);
}
