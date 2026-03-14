package com.edumark.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.school.dto.ParentQueryDTO;
import com.edumark.school.entity.Parent;
import com.edumark.school.vo.ParentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 家长Mapper
 *
 * @author EduMark
 */
@Mapper
public interface ParentMapper extends BaseMapper<Parent> {

    /**
     * 分页查询家长
     */
    IPage<ParentVO> selectPageVO(Page<ParentVO> page, @Param("query") ParentQueryDTO query);

    /**
     * 根据ID查询家长详情(包含绑定的学生)
     */
    ParentVO selectVOById(@Param("id") Long id);

    /**
     * 根据用户ID查询家长
     */
    Parent selectByUserId(@Param("userId") Long userId);

    /**
     * 根据手机号查询家长
     */
    Parent selectByPhone(@Param("phone") String phone);
}
