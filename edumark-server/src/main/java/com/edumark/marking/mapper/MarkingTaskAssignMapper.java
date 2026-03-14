package com.edumark.marking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edumark.marking.entity.MarkingTaskAssign;
import com.edumark.marking.vo.MarkingTaskAssignVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 阅卷任务分配Mapper
 *
 * @author EduMark
 */
@Mapper
public interface MarkingTaskAssignMapper extends BaseMapper<MarkingTaskAssign> {

    /**
     * 根据任务ID查询分配列表
     */
    List<MarkingTaskAssignVO> selectListByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据教师ID查询分配列表
     */
    List<MarkingTaskAssignVO> selectListByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 删除任务的所有分配
     */
    int deleteByTaskId(@Param("taskId") Long taskId);
}
