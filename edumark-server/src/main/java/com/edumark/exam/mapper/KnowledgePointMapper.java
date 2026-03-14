package com.edumark.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.exam.dto.KnowledgePointQueryDTO;
import com.edumark.exam.entity.KnowledgePoint;
import com.edumark.exam.vo.KnowledgePointVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 知识点Mapper
 *
 * @author EduMark
 */
@Mapper
public interface KnowledgePointMapper extends BaseMapper<KnowledgePoint> {

    /**
     * 分页查询知识点
     */
    IPage<KnowledgePointVO> selectPageVO(Page<KnowledgePointVO> page, @Param("query") KnowledgePointQueryDTO query);

    /**
     * 根据ID查询知识点详情
     */
    KnowledgePointVO selectVOById(@Param("id") Long id);

    /**
     * 查询知识点树
     */
    List<KnowledgePointVO> selectTree(@Param("schoolId") Long schoolId, @Param("subjectName") String subjectName);

    /**
     * 根据题目ID查询关联的知识点
     */
    List<KnowledgePointVO> selectByQuestionId(@Param("questionId") Long questionId);
}
