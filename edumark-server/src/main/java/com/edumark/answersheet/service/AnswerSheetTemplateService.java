package com.edumark.answersheet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edumark.answersheet.dto.AnswerSheetTemplateDTO;
import com.edumark.answersheet.dto.AnswerSheetTemplateQueryDTO;
import com.edumark.answersheet.entity.AnswerSheetTemplate;
import com.edumark.answersheet.vo.AnswerSheetTemplateVO;
import com.edumark.answersheet.vo.AnswerSheetTemplateValidateVO;
import com.edumark.common.result.PageResult;

/**
 * 答题卡模板服务接口
 *
 * @author EduMark
 */
public interface AnswerSheetTemplateService extends IService<AnswerSheetTemplate> {

    /**
     * 分页查询模板列表
     */
    PageResult<AnswerSheetTemplateVO> pageQuery(AnswerSheetTemplateQueryDTO query);

    /**
     * 根据ID查询模板详情
     */
    AnswerSheetTemplateVO getDetail(Long id);

    /**
     * 根据试卷ID查询模板
     */
    AnswerSheetTemplateVO getByPaperId(Long paperId);

    /**
     * 创建模板
     */
    Long create(AnswerSheetTemplateDTO dto);

    /**
     * 更新模板
     */
    void update(AnswerSheetTemplateDTO dto);

    /**
     * 删除模板
     */
    void delete(Long id);

    /**
     * 根据试卷自动生成模板
     */
    Long generateFromPaper(Long paperId);

    /**
     * 校验模板完整性
     */
    AnswerSheetTemplateValidateVO validateTemplate(Long id);

    /**
     * 发布模板（生成PDF）
     */
    void publish(Long id);

    /**
     * 获取PDF预览URL
     */
    String getPreviewUrl(Long id);

    /**
     * 获取PDF下载URL
     */
    String getDownloadUrl(Long id);
}
