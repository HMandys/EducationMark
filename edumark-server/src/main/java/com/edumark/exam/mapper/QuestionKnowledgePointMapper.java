package com.edumark.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edumark.exam.entity.QuestionKnowledgePoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 题目知识点关联Mapper
 *
 * @author EduMark
 */
@Mapper
public interface QuestionKnowledgePointMapper extends BaseMapper<QuestionKnowledgePoint> {

    /**
     * 删除题目的知识点关联
     */
    int deleteByQuestionId(@Param("questionId") Long questionId);
}
