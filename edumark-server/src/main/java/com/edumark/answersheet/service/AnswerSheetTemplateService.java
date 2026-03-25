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
     * 根据考试科目ID查询模板
     * 优先通过 paperId 关联查找，如果找不到则通过 examId + subjectName 查找
     */
    AnswerSheetTemplateVO getByExamSubjectId(Long examSubjectId);

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

    /**
     * 上传模板图片
     *
     * @param id 模板ID
     * @param imagePath 图片路径
     * @return 图片访问URL
     */
    String uploadTemplateImage(Long id, String imagePath);

    /**
     * 保存四角定位配置
     *
     * @param id 模板ID
     * @param cornerConfig 四角配置
     */
    void saveCornerConfig(Long id, java.util.Map<String, Object> cornerConfig);

    /**
     * 保存区域正确答案
     *
     * @param templateId 模板ID
     * @param regionId 区域ID
     * @param correctAnswers 正确答案映射 {题号: 答案}
     */
    void saveRegionCorrectAnswers(Long templateId, Long regionId, java.util.Map<String, String> correctAnswers);

    /**
     * 获取模板图片URL
     *
     * @param id 模板ID
     * @return 图片URL
     */
    String getTemplateImageUrl(Long id);
}
