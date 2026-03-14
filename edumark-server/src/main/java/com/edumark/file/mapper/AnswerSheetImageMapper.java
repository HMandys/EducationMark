package com.edumark.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edumark.file.entity.AnswerSheetImage;
import com.edumark.file.vo.AnswerSheetImageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 答题卡图片Mapper
 *
 * @author EduMark
 */
@Mapper
public interface AnswerSheetImageMapper extends BaseMapper<AnswerSheetImage> {

    /**
     * 根据答题卡ID查询图片列表
     */
    List<AnswerSheetImageVO> selectListByAnswerSheetId(@Param("answerSheetId") Long answerSheetId);

    /**
     * 删除答题卡的所有图片
     */
    int deleteByAnswerSheetId(@Param("answerSheetId") Long answerSheetId);
}
