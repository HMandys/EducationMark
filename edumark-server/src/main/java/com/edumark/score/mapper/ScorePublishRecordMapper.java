package com.edumark.score.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edumark.score.entity.ScorePublishRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 成绩发布记录Mapper
 *
 * @author EduMark
 */
@Mapper
public interface ScorePublishRecordMapper extends BaseMapper<ScorePublishRecord> {

    /**
     * 根据考试ID查询发布记录
     */
    List<ScorePublishRecord> selectListByExamId(@Param("examId") Long examId);
}
