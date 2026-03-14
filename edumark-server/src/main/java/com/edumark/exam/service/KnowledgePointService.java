package com.edumark.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edumark.common.result.PageResult;
import com.edumark.exam.dto.KnowledgePointDTO;
import com.edumark.exam.dto.KnowledgePointQueryDTO;
import com.edumark.exam.entity.KnowledgePoint;
import com.edumark.exam.vo.KnowledgePointVO;

import java.util.List;

/**
 * 知识点服务接口
 *
 * @author EduMark
 */
public interface KnowledgePointService extends IService<KnowledgePoint> {

    /**
     * 分页查询知识点
     */
    PageResult<KnowledgePointVO> pageQuery(KnowledgePointQueryDTO query);

    /**
     * 根据ID查询知识点详情
     */
    KnowledgePointVO getDetail(Long id);

    /**
     * 创建知识点
     */
    Long create(KnowledgePointDTO dto);

    /**
     * 更新知识点
     */
    void update(KnowledgePointDTO dto);

    /**
     * 删除知识点
     */
    void delete(Long id);

    /**
     * 批量删除知识点
     */
    void deleteBatch(List<Long> ids);

    /**
     * 获取知识点树
     */
    List<KnowledgePointVO> getTree(Long schoolId, String subjectName);

    /**
     * 根据科目获取知识点列表
     */
    List<KnowledgePointVO> listBySubject(Long schoolId, String subjectName);
}
