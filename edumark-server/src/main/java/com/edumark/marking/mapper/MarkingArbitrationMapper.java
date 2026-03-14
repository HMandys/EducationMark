package com.edumark.marking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.marking.entity.MarkingArbitration;
import com.edumark.marking.vo.MarkingArbitrationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 阅卷仲裁Mapper
 *
 * @author EduMark
 */
@Mapper
public interface MarkingArbitrationMapper extends BaseMapper<MarkingArbitration> {

    /**
     * 分页查询仲裁记录
     */
    IPage<MarkingArbitrationVO> selectPageVO(Page<MarkingArbitrationVO> page, @Param("taskId") Long taskId, @Param("status") Integer status);

    /**
     * 根据ID查询仲裁记录详情
     */
    MarkingArbitrationVO selectVOById(@Param("id") Long id);

    /**
     * 获取待仲裁的下一条记录
     */
    MarkingArbitrationVO selectNextPending(@Param("taskId") Long taskId, @Param("teacherId") Long teacherId);
}
