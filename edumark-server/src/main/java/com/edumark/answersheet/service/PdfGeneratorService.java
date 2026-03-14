package com.edumark.answersheet.service;

import com.edumark.answersheet.vo.AnswerSheetTemplateVO;

/**
 * PDF生成服务接口
 *
 * @author EduMark
 */
public interface PdfGeneratorService {

    /**
     * 根据模板配置生成PDF
     *
     * @param template 模板配置
     * @return PDF字节数组
     */
    byte[] generatePdf(AnswerSheetTemplateVO template);
}
