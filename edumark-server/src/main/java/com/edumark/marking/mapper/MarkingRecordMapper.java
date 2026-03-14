package com.edumark.marking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.marking.entity.MarkingRecord;
import com.edumark.marking.vo.MarkingRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 阅卷记录Mapper
 *
 * @author EduMark
 */
@Mapper
public interface MarkingRecordMapper extends BaseMapper<MarkingRecord> {

    /**
     * 分页查询阅卷记录
     */
    IPage<MarkingRecordVO> selectPageVO(Page<MarkingRecordVO> page, @Param("taskId") Long taskId, @Param("teacherId") Long teacherId, @Param("status") Integer status);

    /**
     * 根据ID查询阅卷记录详情
     */
    MarkingRecordVO selectVOById(@Param("id") Long id);

    /**
     * 获取教师待阅卷的下一条记录
     */
    MarkingRecordVO selectNextPending(@Param("taskId") Long taskId, @Param("teacherId") Long teacherId);

    /**
     * 根据答题卡和题目查询阅卷记录
     */
    List<MarkingRecord> selectByAnswerSheetAndQuestion(@Param("answerSheetId") Long answerSheetId, @Param("questionId") Long questionId);
}
