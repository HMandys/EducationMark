package com.edumark.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.school.dto.SchoolQueryDTO;
import com.edumark.school.entity.School;
import com.edumark.school.vo.SchoolVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学校Mapper
 *
 * @author EduMark
 */
@Mapper
public interface SchoolMapper extends BaseMapper<School> {

    /**
     * 分页查询学校
     */
    IPage<SchoolVO> selectPageVO(Page<SchoolVO> page, @Param("query") SchoolQueryDTO query);

    /**
     * 根据ID查询学校详情
     */
    SchoolVO selectVOById(@Param("id") Long id);

    /**
     * 查询学校列表(下拉选择用)
     */
    List<SchoolVO> selectListForSelect(@Param("status") Integer status);
}
