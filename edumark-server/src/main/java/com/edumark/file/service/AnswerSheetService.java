package com.edumark.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edumark.common.result.PageResult;
import com.edumark.file.dto.AnswerSheetDTO;
import com.edumark.file.dto.AnswerSheetQueryDTO;
import com.edumark.file.dto.AnswerSheetUploadDTO;
import com.edumark.file.entity.AnswerSheet;
import com.edumark.file.vo.AnswerSheetVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 答题卡服务接口
 *
 * @author EduMark
 */
public interface AnswerSheetService extends IService<AnswerSheet> {

    /**
     * 分页查询答题卡
     */
    PageResult<AnswerSheetVO> pageQuery(AnswerSheetQueryDTO query);

    /**
     * 根据ID查询答题卡详情
     */
    AnswerSheetVO getDetail(Long id);

    /**
     * 创建答题卡
     */
    Long create(AnswerSheetDTO dto);

    /**
     * 更新答题卡
     */
    void update(AnswerSheetDTO dto);

    /**
     * 删除答题卡
     */
    void delete(Long id);

    /**
     * 批量删除答题卡
     */
    void deleteBatch(List<Long> ids);

    /**
     * 上传答题卡图片
     */
    AnswerSheetVO uploadImages(Long answerSheetId, List<MultipartFile> files);

    /**
     * 批量上传答题卡（含图片）
     */
    Long uploadAnswerSheet(AnswerSheetUploadDTO dto);

    /**
     * 删除答题卡图片
     */
    void deleteImage(Long imageId);

    /**
     * 根据考试科目ID查询答题卡列表
     */
    List<AnswerSheetVO> listByExamSubjectId(Long examSubjectId);

    /**
     * 统计考试科目的答题卡数量
     */
    int countByExamSubjectId(Long examSubjectId);

    /**
     * 更新答题卡状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 重新执行识别与匹配
     */
    void reRecognize(Long id);
}
