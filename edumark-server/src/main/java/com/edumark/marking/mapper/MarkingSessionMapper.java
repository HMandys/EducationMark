package com.edumark.marking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edumark.marking.entity.MarkingSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 阅卷会话Mapper
 *
 * @author EduMark
 */
@Mapper
public interface MarkingSessionMapper extends BaseMapper<MarkingSession> {

    /**
     * 根据会话令牌查询会话
     */
    MarkingSession selectBySessionToken(@Param("sessionToken") String sessionToken);

    /**
     * 删除过期的会话
     */
    int deleteExpiredSessions();
}
