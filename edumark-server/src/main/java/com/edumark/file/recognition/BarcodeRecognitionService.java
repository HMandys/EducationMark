package com.edumark.file.recognition;

import java.util.List;

/**
 * 条码识别服务
 *
 * @author EduMark
 */
public interface BarcodeRecognitionService {

    /**
     * 按模板条码区识别 Code128 条码
     *
     * @param examSubjectId 考试科目ID
     * @param images 图片列表
     * @return 识别结果
     */
    BarcodeRecognitionResult recognize(Long examSubjectId, List<RecognitionImageInput> images);
}
