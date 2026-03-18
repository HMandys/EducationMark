package com.edumark.marking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.marking.dto.MarkingTaskQueryDTO;
import com.edumark.marking.entity.MarkingTask;
import com.edumark.marking.vo.MarkingTaskVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 阅卷任务Mapper
 *
 * @author EduMark
 */
@Mapper
public interface MarkingTaskMapper extends BaseMapper<MarkingTask> {

    /**
     * 分页查询阅卷任务
     */
    IPage<MarkingTaskVO> selectPageVO(Page<MarkingTaskVO> page, @Param("examId") Long examId, @Param("examSubjectId") Long examSubjectId, @Param("status") Integer status);

    /**
     * 根据ID查询阅卷任务详情
     */
    MarkingTaskVO selectVOById(@Param("id") Long id);

    /**
     * 根据考试科目ID查询阅卷任务列表
     */
    List<MarkingTaskVO> selectListByExamSubjectId(@Param("examSubjectId") Long examSubjectId);
}
