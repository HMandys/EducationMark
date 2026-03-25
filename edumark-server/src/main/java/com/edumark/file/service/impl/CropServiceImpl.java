package com.edumark.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edumark.answersheet.service.AnswerSheetTemplateService;
import com.edumark.answersheet.vo.AnswerSheetRegionVO;
import com.edumark.answersheet.vo.AnswerSheetTemplateVO;
import com.edumark.common.exception.BusinessException;
import com.edumark.exam.entity.Paper;
import com.edumark.exam.entity.PaperQuestion;
import com.edumark.exam.mapper.PaperMapper;
import com.edumark.exam.mapper.PaperQuestionMapper;
import com.edumark.file.entity.AnswerSheet;
import com.edumark.file.entity.AnswerSheetDetail;
import com.edumark.file.mapper.AnswerSheetDetailMapper;
import com.edumark.file.mapper.AnswerSheetImageMapper;
import com.edumark.file.mapper.AnswerSheetMapper;
import com.edumark.file.service.AnswerSheetDetailService;
import com.edumark.file.service.CropService;
import com.edumark.file.service.FileService;
import com.edumark.file.vo.AnswerSheetImageVO;
import com.edumark.file.vo.CropItemVO;
import com.edumark.file.vo.CropProgressVO;
import com.edumark.file.vo.CropResultVO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 裁题服务实现
 *
 * @author EduMark
 */
@Service
public class CropServiceImpl implements CropService {

    private static final Logger log = LoggerFactory.getLogger(CropServiceImpl.class);
    private static final int DETAIL_STATUS_PENDING = 0;
    private static final int DETAIL_STATUS_SUBJECTIVE_VERIFIED = 2;
    private static final int DETAIL_STATUS_SUBJECTIVE_ANOMALY = 3;
    private static final Set<String> PREVIEW_REGION_ROLES = Set.of("choice_block", "subjective_crop", "essay_crop");

    @Resource
    private AnswerSheetMapper answerSheetMapper;

    @Resource
    private AnswerSheetDetailMapper answerSheetDetailMapper;

    @Resource
    private AnswerSheetImageMapper answerSheetImageMapper;

    @Resource
    private PaperMapper paperMapper;

    @Resource
    private PaperQuestionMapper paperQuestionMapper;

    @Resource
    private AnswerSheetTemplateService answerSheetTemplateService;

    @Resource
    private AnswerSheetDetailService answerSheetDetailService;

    @Resource
    private FileService fileService;

    @Override
    public CropProgressVO batchCropByExamSubject(Long examSubjectId) {
        if (examSubjectId == null) {
            throw new BusinessException("考试科目ID不能为空");
        }

        CropProgressVO progress = new CropProgressVO();
        progress.setExamSubjectId(examSubjectId);
        progress.setStartTime(LocalDateTime.now());
        progress.setStatus(1);
        progress.setStatusName("进行中");

        // 获取所有已识别的答题卡
        List<AnswerSheet> answerSheets = answerSheetMapper.selectList(
                new LambdaQueryWrapper<AnswerSheet>()
                        .eq(AnswerSheet::getExamSubjectId, examSubjectId)
                        .eq(AnswerSheet::getDeleted, 0)
                        .ge(AnswerSheet::getStatus, 1) // 已识别及以上状态
                        .isNotNull(AnswerSheet::getStudentId)
        );

        progress.setTotalAnswerSheets(answerSheets.size());

        AtomicInteger croppedSheets = new AtomicInteger(0);
        AtomicInteger totalQuestions = new AtomicInteger(0);
        AtomicInteger croppedQuestions = new AtomicInteger(0);
        AtomicInteger anomalyQuestions = new AtomicInteger(0);

        for (AnswerSheet answerSheet : answerSheets) {
            try {
                CropResultVO result = cropByAnswerSheet(answerSheet.getId());
                if (result.getSuccess()) {
                    croppedSheets.incrementAndGet();
                }
                totalQuestions.addAndGet(result.getSuccessCount() + result.getFailCount());
                croppedQuestions.addAndGet(result.getSuccessCount());
                anomalyQuestions.addAndGet(result.getFailCount());
            } catch (Exception e) {
                log.error("裁题失败，答题卡ID: {}", answerSheet.getId(), e);
            }
        }

        progress.setCroppedAnswerSheets(croppedSheets.get());
        progress.setTotalQuestions(totalQuestions.get());
        progress.setCroppedQuestions(croppedQuestions.get());
        progress.setAnomalyQuestions(anomalyQuestions.get());

        int percent = totalQuestions.get() > 0
                ? Math.round(croppedQuestions.get() * 100f / totalQuestions.get())
                : 0;
        progress.setProgressPercent((double) percent);

        progress.setEndTime(LocalDateTime.now());
        progress.setStatus(2);
        progress.setStatusName("已完成");

        return progress;
    }

    @Override
    public CropResultVO cropByAnswerSheet(Long answerSheetId) {
        CropResultVO result = new CropResultVO();
        result.setAnswerSheetId(answerSheetId);
        result.setItems(new ArrayList<>());

        AnswerSheet answerSheet = answerSheetMapper.selectById(answerSheetId);
        if (answerSheet == null) {
            result.setSuccess(false);
            result.setErrorMessage("答题卡不存在");
            return result;
        }

        if (answerSheet.getExamSubjectId() == null) {
            result.setSuccess(false);
            result.setErrorMessage("答题卡未关联考试科目");
            return result;
        }

        // 先尝试获取答题卡模板（支持通过paperId或examId+subjectName）
        AnswerSheetTemplateVO template = answerSheetTemplateService.getByExamSubjectId(answerSheet.getExamSubjectId());
        if (template == null) {
            result.setSuccess(false);
            result.setErrorMessage("当前考试未配置答题卡模板");
            return result;
        }

        // 尝试获取试卷和题目（如果有关联试卷的话）
        List<PaperQuestion> questions = new ArrayList<>();
        Paper paper = paperMapper.selectByExamSubjectId(answerSheet.getExamSubjectId());
        if (paper != null) {
            questions = paperQuestionMapper.selectList(
                    new LambdaQueryWrapper<PaperQuestion>()
                            .eq(PaperQuestion::getPaperId, paper.getId())
                            .eq(PaperQuestion::getDeleted, 0)
                            .orderByAsc(PaperQuestion::getSort)
                            .orderByAsc(PaperQuestion::getId)
            );
        }

        Map<Long, AnswerSheetDetail> detailMap = loadDetailMap(answerSheetId);
        List<AnswerSheetImageVO> images = answerSheetImageMapper.selectListByAnswerSheetId(answerSheetId);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (PaperQuestion question : questions) {
            CropItemVO item = new CropItemVO();
            item.setQuestionId(question.getId());
            item.setQuestionNo(question.getQuestionNo());
            item.setIsObjective(question.getIsObjective() != null && question.getIsObjective() == 1);

            try {
                String imageUrl = cropQuestion(answerSheet, question, template, questions, images, detailMap);
                item.setSuccess(true);
                item.setImageUrl(imageUrl);
                successCount.incrementAndGet();
            } catch (Exception e) {
                item.setSuccess(false);
                item.setErrorMessage(e.getMessage());
                failCount.incrementAndGet();

                // 主观题标记为异常
                if (!item.getIsObjective()) {
                    markSubjectiveAsAnomaly(answerSheetId, question.getId(), detailMap);
                }
            }

            result.getItems().add(item);
        }

        result.setSuccess(failCount.get() == 0);
        result.setSuccessCount(successCount.get());
        result.setFailCount(failCount.get());

        return result;
    }

    @Override
    public CropProgressVO getCropProgress(Long examSubjectId) {
        return getCropStatistics(examSubjectId);
    }

    @Override
    public String recropQuestion(Long answerSheetId, Long questionId) {
        return answerSheetDetailService.getQuestionPreviewUrl(answerSheetId, questionId);
    }

    @Override
    public CropProgressVO getCropStatistics(Long examSubjectId) {
        CropProgressVO stats = new CropProgressVO();
        stats.setExamSubjectId(examSubjectId);

        // 统计答题卡数量
        Long totalSheets = answerSheetMapper.selectCount(
                new LambdaQueryWrapper<AnswerSheet>()
                        .eq(AnswerSheet::getExamSubjectId, examSubjectId)
                        .eq(AnswerSheet::getDeleted, 0)
                        .ge(AnswerSheet::getStatus, 1)
                        .isNotNull(AnswerSheet::getStudentId)
        );
        stats.setTotalAnswerSheets(totalSheets != null ? totalSheets.intValue() : 0);

        // 统计题目数量
        Paper paper = paperMapper.selectByExamSubjectId(examSubjectId);
        int questionCount = 0;
        int subjectiveQuestionCount = 0;
        if (paper != null) {
            List<PaperQuestion> questions = paperQuestionMapper.selectList(
                    new LambdaQueryWrapper<PaperQuestion>()
                            .eq(PaperQuestion::getPaperId, paper.getId())
                            .eq(PaperQuestion::getDeleted, 0)
            );
            questionCount = questions.size();
            subjectiveQuestionCount = (int) questions.stream()
                    .filter(q -> q.getIsObjective() == null || q.getIsObjective() != 1)
                    .count();
        }
        // 总题目数 = 答题卡数 * 每卷题目数
        stats.setTotalQuestions(questionCount * stats.getTotalAnswerSheets());

        // 从 answer_sheet_detail 统计实际已裁题的主观题数量
        // 主观题裁题后状态为 2(已验证) 或 3(异常)
        List<AnswerSheet> sheets = answerSheetMapper.selectList(
                new LambdaQueryWrapper<AnswerSheet>()
                        .eq(AnswerSheet::getExamSubjectId, examSubjectId)
                        .eq(AnswerSheet::getDeleted, 0)
                        .ge(AnswerSheet::getStatus, 1)
                        .isNotNull(AnswerSheet::getStudentId)
                        .select(AnswerSheet::getId)
        );

        if (sheets.isEmpty()) {
            stats.setCroppedAnswerSheets(0);
            stats.setCroppedQuestions(0);
            stats.setAnomalyQuestions(0);
            stats.setProgressPercent(0.0);
            stats.setStatus(0);
            stats.setStatusName("未开始");
            return stats;
        }

        List<Long> sheetIds = sheets.stream().map(AnswerSheet::getId).toList();

        // 统计已裁题的记录（主观题状态>=2表示已裁题）
        Long croppedCount = answerSheetDetailMapper.selectCount(
                new LambdaQueryWrapper<AnswerSheetDetail>()
                        .in(AnswerSheetDetail::getAnswerSheetId, sheetIds)
                        .ge(AnswerSheetDetail::getStatus, DETAIL_STATUS_SUBJECTIVE_VERIFIED)
        );

        // 统计异常的记录
        Long anomalyCount = answerSheetDetailMapper.selectCount(
                new LambdaQueryWrapper<AnswerSheetDetail>()
                        .in(AnswerSheetDetail::getAnswerSheetId, sheetIds)
                        .eq(AnswerSheetDetail::getStatus, DETAIL_STATUS_SUBJECTIVE_ANOMALY)
        );

        int cropped = croppedCount != null ? croppedCount.intValue() : 0;
        int anomaly = anomalyCount != null ? anomalyCount.intValue() : 0;

        // 主观题总数
        int totalSubjective = subjectiveQuestionCount * stats.getTotalAnswerSheets();

        stats.setCroppedQuestions(cropped);
        stats.setAnomalyQuestions(anomaly);

        // 计算已完成裁题的答题卡数（所有主观题都已裁题的答题卡）
        int completedSheets = 0;
        if (subjectiveQuestionCount > 0) {
            for (AnswerSheet sheet : sheets) {
                Long sheetCroppedCount = answerSheetDetailMapper.selectCount(
                        new LambdaQueryWrapper<AnswerSheetDetail>()
                                .eq(AnswerSheetDetail::getAnswerSheetId, sheet.getId())
                                .ge(AnswerSheetDetail::getStatus, DETAIL_STATUS_SUBJECTIVE_VERIFIED)
                );
                if (sheetCroppedCount != null && sheetCroppedCount >= subjectiveQuestionCount) {
                    completedSheets++;
                }
            }
        } else {
            // 没有主观题，所有答题卡视为已完成
            completedSheets = stats.getTotalAnswerSheets();
        }
        stats.setCroppedAnswerSheets(completedSheets);

        // 计算进度百分比
        double percent = totalSubjective > 0
                ? Math.round(cropped * 1000.0 / totalSubjective) / 10.0
                : 100.0;
        stats.setProgressPercent(percent);

        // 设置状态
        if (cropped == 0) {
            stats.setStatus(0);
            stats.setStatusName("未开始");
        } else if (percent >= 100.0) {
            stats.setStatus(2);
            stats.setStatusName("已完成");
        } else {
            stats.setStatus(1);
            stats.setStatusName("进行中");
        }

        return stats;
    }

    @Override
    public boolean isCropCompleted(Long examSubjectId) {
        CropProgressVO stats = getCropStatistics(examSubjectId);
        return stats.getTotalAnswerSheets() > 0
                && stats.getCroppedAnswerSheets().equals(stats.getTotalAnswerSheets());
    }

    /**
     * 裁切单个题目
     */
    private String cropQuestion(AnswerSheet answerSheet,
                                PaperQuestion question,
                                AnswerSheetTemplateVO template,
                                List<PaperQuestion> allQuestions,
                                List<AnswerSheetImageVO> images,
                                Map<Long, AnswerSheetDetail> detailMap) {
        QuestionRegionBinding binding = findQuestionBinding(template, allQuestions, question);
        if (binding.region() == null) {
            throw new BusinessException("未配置裁题区域");
        }

        if (!PREVIEW_REGION_ROLES.contains(binding.regionRole())) {
            throw new BusinessException("当前区域不支持裁题");
        }

        AnswerSheetImageVO image = pickImageForRegion(images, binding.region().getPageNo());
        if (image == null || image.getImagePath() == null || image.getImagePath().isBlank()) {
            throw new BusinessException("缺少题目对应页扫描图片");
        }

        // 使用 AnswerSheetDetailService 的预览功能生成裁题图片
        return answerSheetDetailService.getQuestionPreviewUrl(answerSheet.getId(), question.getId());
    }

    private void markSubjectiveAsAnomaly(Long answerSheetId, Long questionId, Map<Long, AnswerSheetDetail> detailMap) {
        AnswerSheetDetail detail = detailMap.get(questionId);
        if (detail == null) {
            detail = new AnswerSheetDetail();
            detail.setAnswerSheetId(answerSheetId);
            detail.setQuestionId(questionId);
            detail.setScore(0);
            detail.setStatus(DETAIL_STATUS_SUBJECTIVE_ANOMALY);
            answerSheetDetailMapper.insert(detail);
            detailMap.put(questionId, detail);
        } else if (detail.getStatus() == null || detail.getStatus() == DETAIL_STATUS_PENDING) {
            detail.setStatus(DETAIL_STATUS_SUBJECTIVE_ANOMALY);
            answerSheetDetailMapper.updateById(detail);
        }
    }

    private Map<Long, AnswerSheetDetail> loadDetailMap(Long answerSheetId) {
        return answerSheetDetailMapper.selectList(
                        new LambdaQueryWrapper<AnswerSheetDetail>()
                                .eq(AnswerSheetDetail::getAnswerSheetId, answerSheetId))
                .stream()
                .collect(Collectors.toMap(AnswerSheetDetail::getQuestionId, detail -> detail, (left, right) -> left, LinkedHashMap::new));
    }

    private QuestionRegionBinding findQuestionBinding(AnswerSheetTemplateVO template, List<PaperQuestion> allQuestions, PaperQuestion question) {
        if (template == null || template.getRegions() == null || template.getRegions().isEmpty()) {
            return new QuestionRegionBinding(null, null, null, List.of());
        }

        for (AnswerSheetRegionVO region : template.getRegions()) {
            String regionRole = getConfigString(region.getConfig(), "regionRole");
            if (!PREVIEW_REGION_ROLES.contains(regionRole)) {
                continue;
            }
            List<PaperQuestion> matchedQuestions = resolveQuestionsForRegion(region, allQuestions);
            boolean matched = matchedQuestions.stream().anyMatch(item -> item.getId().equals(question.getId()));
            if (!matched) {
                continue;
            }
            String cropMode = getConfigString(region.getConfig(), "cropMode");
            return new QuestionRegionBinding(region, regionRole, cropMode, matchedQuestions);
        }

        return new QuestionRegionBinding(null, null, null, List.of());
    }

    private List<PaperQuestion> resolveQuestionsForRegion(AnswerSheetRegionVO region, List<PaperQuestion> allQuestions) {
        if (region == null) {
            return List.of();
        }

        if (region.getQuestionIds() != null && !region.getQuestionIds().isEmpty()) {
            Set<Long> regionQuestionIds = Set.copyOf(region.getQuestionIds());
            return allQuestions.stream()
                    .filter(q -> regionQuestionIds.contains(q.getId()))
                    .toList();
        }

        Integer questionStart = region.getQuestionStart();
        Integer questionEnd = region.getQuestionEnd();
        if (questionStart == null || questionEnd == null) {
            return List.of();
        }

        return allQuestions.stream()
                .filter(q -> {
                    Integer index = resolveQuestionOrderIndex(q);
                    return index != null && index >= questionStart && index <= questionEnd;
                })
                .toList();
    }

    private Integer resolveQuestionOrderIndex(PaperQuestion question) {
        if (question.getItemNo() != null && question.getItemNo() > 0) {
            return question.getItemNo();
        }
        if (question.getQuestionNo() == null || question.getQuestionNo().isBlank()) {
            return null;
        }
        StringBuilder digits = new StringBuilder();
        for (char ch : question.getQuestionNo().toCharArray()) {
            if (Character.isDigit(ch)) {
                digits.append(ch);
            } else if (!digits.isEmpty()) {
                break;
            }
        }
        if (digits.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(digits.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private AnswerSheetImageVO pickImageForRegion(List<AnswerSheetImageVO> images, Integer pageNo) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        if (pageNo != null) {
            for (AnswerSheetImageVO image : images) {
                if (pageNo.equals(image.getPageNum())) {
                    return image;
                }
            }
        }
        return images.get(0);
    }

    private String getConfigString(Map<String, Object> config, String key) {
        if (config == null) {
            return null;
        }
        Object value = config.get(key);
        return value == null ? null : String.valueOf(value).trim();
    }

    private record QuestionRegionBinding(
            AnswerSheetRegionVO region,
            String regionRole,
            String cropMode,
            List<PaperQuestion> orderedQuestions
    ) {
    }
}
