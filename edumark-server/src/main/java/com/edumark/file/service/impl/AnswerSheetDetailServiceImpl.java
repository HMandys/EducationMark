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
import com.edumark.file.service.FileService;
import com.edumark.file.vo.AnswerSheetImageVO;
import com.edumark.file.vo.AnswerSheetQuestionDetailVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 答题卡题目明细服务实现
 *
 * @author EduMark
 */
@Service
public class AnswerSheetDetailServiceImpl implements AnswerSheetDetailService {

    private static final Map<Integer, String> QUESTION_TYPE_NAME_MAP = Map.of(
            1, "单选题",
            2, "多选题",
            3, "判断题",
            4, "填空题",
            5, "简答题",
            6, "计算题",
            7, "作文题",
            9, "其他"
    );

    private static final Map<String, String> REGION_ROLE_NAME_MAP = Map.of(
            "choice_block", "客观题涂卡区",
            "subjective_crop", "主观题裁题区",
            "essay_crop", "作文裁题区",
            "score_box", "评分框",
            "student_id", "学号识别区",
            "student_name", "姓名识别区",
            "class_name", "班级识别区",
            "barcode", "条码区"
    );

    private static final Set<String> PREVIEW_REGION_ROLES = Set.of("choice_block", "subjective_crop", "essay_crop");
    private static final double MIN_FILLED_SCORE = 0.16D;

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
    private FileService fileService;

    @Override
    public void initializeQuestionDetails(Long answerSheetId) {
        if (answerSheetId == null) {
            return;
        }

        AnswerSheet answerSheet = answerSheetMapper.selectById(answerSheetId);
        if (answerSheet == null || answerSheet.getExamSubjectId() == null) {
            return;
        }

        Paper paper = paperMapper.selectByExamSubjectId(answerSheet.getExamSubjectId());
        if (paper == null) {
            return;
        }

        List<PaperQuestion> questions = loadPaperQuestions(paper.getId());
        if (questions.isEmpty()) {
            return;
        }

        Map<Long, AnswerSheetDetail> detailMap = loadDetailMap(answerSheetId);
        for (PaperQuestion question : questions) {
            if (detailMap.containsKey(question.getId())) {
                continue;
            }
            AnswerSheetDetail detail = new AnswerSheetDetail();
            detail.setAnswerSheetId(answerSheetId);
            detail.setQuestionId(question.getId());
            detail.setStatus(0);
            detail.setScore(0);
            answerSheetDetailMapper.insert(detail);
        }
    }

    @Override
    public List<AnswerSheetQuestionDetailVO> recognizeObjectiveAnswers(Long answerSheetId) {
        AnswerSheetContext context = loadContext(answerSheetId, true);
        Map<String, BufferedImage> imageCache = new HashMap<>();

        for (AnswerSheetRegionVO region : context.template().getRegions()) {
            String regionRole = getConfigString(region.getConfig(), "regionRole");
            if (!"choice_block".equals(regionRole)) {
                continue;
            }

            List<BubbleDefinition> bubbleDefinitions = parseBubbleDefinitions(region.getConfig());
            if (bubbleDefinitions.isEmpty()) {
                continue;
            }

            AnswerSheetImageVO image = pickImageForRegion(context.images(), region.getPageNo());
            if (image == null || image.getImagePath() == null || image.getImagePath().isBlank()) {
                continue;
            }

            BufferedImage pageImage = readImage(image.getImagePath(), imageCache);
            if (pageImage == null) {
                continue;
            }

            Map<Integer, List<BubbleDefinition>> bubbleMapByQuestionNo = bubbleDefinitions.stream()
                    .collect(Collectors.groupingBy(BubbleDefinition::questionNo, LinkedHashMap::new, Collectors.toList()));

            for (PaperQuestion question : resolveQuestionsForRegion(region, context.questions())) {
                if (!isObjectiveQuestion(question)) {
                    continue;
                }

                Integer questionOrderNo = resolveQuestionOrderIndex(question);
                if (questionOrderNo == null) {
                    continue;
                }

                List<BubbleDefinition> questionBubbles = bubbleMapByQuestionNo.get(questionOrderNo);
                if (questionBubbles == null || questionBubbles.isEmpty()) {
                    continue;
                }

                String recognizedAnswer = resolveRecognizedAnswer(question, pageImage, questionBubbles);
                persistObjectiveAnswer(context.answerSheet().getId(), question, context.detailMap(), recognizedAnswer, false);
            }
        }

        recalculateAnswerSheetScores(answerSheetId);
        return listQuestionDetails(answerSheetId);
    }

    @Override
    public List<AnswerSheetQuestionDetailVO> listQuestionDetails(Long answerSheetId) {
        AnswerSheetContext context = loadContext(answerSheetId, true);
        List<AnswerSheetQuestionDetailVO> result = new ArrayList<>();

        for (PaperQuestion question : context.questions()) {
            result.add(buildQuestionDetailVO(context, question));
        }

        return result;
    }

    @Override
    public String getQuestionPreviewUrl(Long answerSheetId, Long questionId) {
        AnswerSheetContext context = loadContext(answerSheetId, false);
        PaperQuestion question = context.questionMap().get(questionId);
        if (question == null) {
            throw new BusinessException("题目不存在");
        }

        QuestionRegionBinding binding = findQuestionBinding(context.template(), context.questions(), question);
        if (binding.region() == null) {
            throw new BusinessException("当前题目未配置裁题区域");
        }
        if (!supportsPreview(binding.regionRole())) {
            throw new BusinessException("当前题型暂不支持裁题预览");
        }

        AnswerSheetImageVO image = pickImageForRegion(context.images(), binding.region().getPageNo());
        if (image == null || image.getImagePath() == null || image.getImagePath().isBlank()) {
            throw new BusinessException("未找到题目对应页的扫描图片");
        }

        BufferedImage pageImage = readImage(image.getImagePath(), new HashMap<>());
        if (pageImage == null) {
            throw new BusinessException("扫描图片读取失败");
        }

        BufferedImage previewImage = cropQuestionPreview(pageImage, binding.region(), binding.orderedQuestions(), question.getId(), binding.cropMode());
        byte[] bytes = toPngBytes(previewImage);
        String objectName = "answer-sheet-preview/" + answerSheetId + "/" + questionId + ".png";
        fileService.uploadBytes(bytes, objectName, "image/png");
        return fileService.getUrl(objectName);
    }

    @Override
    public AnswerSheetQuestionDetailVO updateObjectiveAnswer(Long answerSheetId, Long questionId, String studentAnswer) {
        AnswerSheetContext context = loadContext(answerSheetId, true);
        PaperQuestion question = context.questionMap().get(questionId);
        if (question == null) {
            throw new BusinessException("题目不存在");
        }
        if (!isObjectiveQuestion(question)) {
            throw new BusinessException("当前题目不是客观题");
        }

        persistObjectiveAnswer(answerSheetId, question, context.detailMap(), normalizeAnswer(studentAnswer), true);
        recalculateAnswerSheetScores(answerSheetId);
        AnswerSheetContext refreshedContext = loadContext(answerSheetId, false);
        return buildQuestionDetailVO(refreshedContext, refreshedContext.questionMap().get(questionId));
    }

    @Override
    public void updateQuestionScore(Long answerSheetId, Long questionId, Integer score, boolean completed) {
        if (answerSheetId == null || questionId == null) {
            return;
        }

        AnswerSheetDetail detail = answerSheetDetailMapper.selectOne(
                new LambdaQueryWrapper<AnswerSheetDetail>()
                        .eq(AnswerSheetDetail::getAnswerSheetId, answerSheetId)
                        .eq(AnswerSheetDetail::getQuestionId, questionId)
                        .last("LIMIT 1")
        );

        if (detail == null) {
            detail = new AnswerSheetDetail();
            detail.setAnswerSheetId(answerSheetId);
            detail.setQuestionId(questionId);
            detail.setScore(score != null ? score : 0);
            detail.setStatus(completed ? 1 : 0);
            answerSheetDetailMapper.insert(detail);
        } else {
            detail.setScore(score != null ? score : 0);
            detail.setStatus(completed ? 1 : 0);
            answerSheetDetailMapper.updateById(detail);
        }

        recalculateAnswerSheetScores(answerSheetId);
    }

    @Override
    public void recalculateAnswerSheetScores(Long answerSheetId) {
        if (answerSheetId == null) {
            return;
        }

        AnswerSheetContext context = loadContext(answerSheetId, false);
        int objectiveScore = 0;
        int subjectiveScore = 0;

        for (AnswerSheetDetail detail : context.detailMap().values()) {
            if (detail.getScore() == null) {
                continue;
            }
            PaperQuestion question = context.questionMap().get(detail.getQuestionId());
            if (question != null && isObjectiveQuestion(question)) {
                objectiveScore += detail.getScore();
            } else {
                subjectiveScore += detail.getScore();
            }
        }

        AnswerSheet answerSheet = context.answerSheet();
        answerSheet.setObjectiveScore(objectiveScore);
        answerSheet.setSubjectiveScore(subjectiveScore);
        answerSheet.setTotalScore(objectiveScore + subjectiveScore);
        answerSheetMapper.updateById(answerSheet);
    }

    private AnswerSheetQuestionDetailVO buildQuestionDetailVO(AnswerSheetContext context, PaperQuestion question) {
        AnswerSheetDetail detail = context.detailMap().get(question.getId());
        QuestionRegionBinding binding = findQuestionBinding(context.template(), context.questions(), question);

        AnswerSheetQuestionDetailVO vo = new AnswerSheetQuestionDetailVO();
        vo.setId(detail != null ? detail.getId() : null);
        vo.setAnswerSheetId(context.answerSheet().getId());
        vo.setQuestionId(question.getId());
        vo.setQuestionNo(question.getQuestionNo());
        vo.setQuestionType(question.getQuestionType());
        vo.setQuestionTypeName(QUESTION_TYPE_NAME_MAP.getOrDefault(question.getQuestionType(), "未知题型"));
        vo.setIsObjective(question.getIsObjective());
        vo.setFullScore(question.getScore());
        vo.setCorrectAnswer(question.getCorrectAnswer());
        vo.setStudentAnswer(detail != null ? detail.getStudentAnswer() : null);
        vo.setScore(detail != null ? detail.getScore() : null);
        vo.setStatus(detail != null ? detail.getStatus() : 0);
        vo.setStatusName(resolveDetailStatusName(detail, question));
        vo.setRegionRole(binding.regionRole());
        vo.setRegionRoleName(REGION_ROLE_NAME_MAP.getOrDefault(binding.regionRole(), "未配置区域"));
        vo.setCropMode(binding.cropMode());
        vo.setPageNo(binding.region() != null ? binding.region().getPageNo() : null);
        vo.setOptionCount(binding.region() != null ? getConfigInteger(binding.region().getConfig(), "optionCount") : null);
        vo.setPreviewAvailable(binding.region() != null && supportsPreview(binding.regionRole()));
        return vo;
    }

    private AnswerSheetContext loadContext(Long answerSheetId, boolean initializeIfNeeded) {
        AnswerSheet answerSheet = answerSheetMapper.selectById(answerSheetId);
        if (answerSheet == null) {
            throw new BusinessException("答题卡不存在");
        }
        if (answerSheet.getExamSubjectId() == null) {
            throw new BusinessException("答题卡未关联考试科目");
        }

        if (initializeIfNeeded) {
            initializeQuestionDetails(answerSheetId);
        }

        Paper paper = paperMapper.selectByExamSubjectId(answerSheet.getExamSubjectId());
        if (paper == null) {
            throw new BusinessException("当前科目未关联试卷");
        }

        List<PaperQuestion> questions = loadPaperQuestions(paper.getId());
        Map<Long, PaperQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(PaperQuestion::getId, question -> question, (left, right) -> left, LinkedHashMap::new));

        Map<Long, AnswerSheetDetail> detailMap = loadDetailMap(answerSheetId);
        List<AnswerSheetImageVO> images = answerSheetImageMapper.selectListByAnswerSheetId(answerSheetId);
        AnswerSheetTemplateVO template = answerSheetTemplateService.getByPaperId(paper.getId());
        if (template == null) {
            throw new BusinessException("当前试卷未配置答题卡模板");
        }

        return new AnswerSheetContext(answerSheet, questions, questionMap, detailMap, images, template);
    }

    private Map<Long, AnswerSheetDetail> loadDetailMap(Long answerSheetId) {
        return answerSheetDetailMapper.selectList(
                        new LambdaQueryWrapper<AnswerSheetDetail>()
                                .eq(AnswerSheetDetail::getAnswerSheetId, answerSheetId))
                .stream()
                .collect(Collectors.toMap(AnswerSheetDetail::getQuestionId, detail -> detail, (left, right) -> left, LinkedHashMap::new));
    }

    private List<PaperQuestion> loadPaperQuestions(Long paperId) {
        return paperQuestionMapper.selectList(
                new LambdaQueryWrapper<PaperQuestion>()
                        .eq(PaperQuestion::getPaperId, paperId)
                        .eq(PaperQuestion::getDeleted, 0)
                        .orderByAsc(PaperQuestion::getSort)
                        .orderByAsc(PaperQuestion::getId)
        );
    }

    private void persistObjectiveAnswer(Long answerSheetId,
                                        PaperQuestion question,
                                        Map<Long, AnswerSheetDetail> detailMap,
                                        String studentAnswer,
                                        boolean reviewed) {
        AnswerSheetDetail detail = detailMap.get(question.getId());
        if (detail == null) {
            detail = new AnswerSheetDetail();
            detail.setAnswerSheetId(answerSheetId);
            detail.setQuestionId(question.getId());
            detail.setStudentAnswer(studentAnswer);
            detail.setScore(scoreObjectiveQuestion(question, studentAnswer));
            detail.setStatus(reviewed && studentAnswer != null ? 1 : 0);
            answerSheetDetailMapper.insert(detail);
            detailMap.put(question.getId(), detail);
            return;
        }

        detail.setStudentAnswer(studentAnswer);
        detail.setScore(scoreObjectiveQuestion(question, studentAnswer));
        detail.setStatus(reviewed && studentAnswer != null ? 1 : 0);
        answerSheetDetailMapper.updateById(detail);
    }

    private int scoreObjectiveQuestion(PaperQuestion question, String studentAnswer) {
        if (question == null || question.getScore() == null) {
            return 0;
        }
        String normalizedStudentAnswer = normalizeAnswer(studentAnswer);
        String normalizedCorrectAnswer = normalizeAnswer(question.getCorrectAnswer());
        if (normalizedStudentAnswer == null || normalizedCorrectAnswer == null) {
            return 0;
        }
        return normalizedCorrectAnswer.equals(normalizedStudentAnswer) ? question.getScore() : 0;
    }

    private String resolveRecognizedAnswer(PaperQuestion question, BufferedImage pageImage, List<BubbleDefinition> questionBubbles) {
        List<BubbleMetric> metrics = questionBubbles.stream()
                .map(bubble -> analyzeBubble(pageImage, bubble))
                .sorted(Comparator.comparing(BubbleMetric::score).reversed())
                .toList();
        if (metrics.isEmpty()) {
            return null;
        }

        BubbleMetric topMetric = metrics.get(0);
        BubbleMetric secondMetric = metrics.size() > 1 ? metrics.get(1) : null;
        if (topMetric.score() < MIN_FILLED_SCORE) {
            return null;
        }

        if (isMultipleChoice(question)) {
            double averageScore = metrics.stream().mapToDouble(BubbleMetric::score).average().orElse(0D);
            double threshold = Math.max(MIN_FILLED_SCORE, Math.max(topMetric.score() * 0.72D, averageScore + 0.04D));
            String answer = normalizeAnswer(metrics.stream()
                    .filter(metric -> metric.score() >= threshold)
                    .map(BubbleMetric::option)
                    .collect(Collectors.joining()));
            return answer != null ? answer : topMetric.option();
        }

        if (secondMetric != null
                && secondMetric.score() >= MIN_FILLED_SCORE
                && Math.abs(topMetric.score() - secondMetric.score()) <= 0.025D
                && secondMetric.score() >= topMetric.score() * 0.9D) {
            return normalizeAnswer(topMetric.option() + secondMetric.option());
        }

        return normalizeAnswer(topMetric.option());
    }

    private BubbleMetric analyzeBubble(BufferedImage pageImage, BubbleDefinition bubble) {
        BufferedImage bubbleImage = cropPercentRegion(pageImage, bubble.x(), bubble.y(), bubble.width(), bubble.height());
        if (bubbleImage == null) {
            return new BubbleMetric(bubble.option(), 0D);
        }

        int paddingX = Math.max(1, bubbleImage.getWidth() / 5);
        int paddingY = Math.max(1, bubbleImage.getHeight() / 5);
        int left = Math.min(paddingX, bubbleImage.getWidth() - 1);
        int top = Math.min(paddingY, bubbleImage.getHeight() - 1);
        int right = Math.max(left + 1, bubbleImage.getWidth() - paddingX);
        int bottom = Math.max(top + 1, bubbleImage.getHeight() - paddingY);

        int darkPixelCount = 0;
        int inkPixelCount = 0;
        double darknessSum = 0D;
        int totalPixelCount = 0;

        for (int y = top; y < bottom; y++) {
            for (int x = left; x < right; x++) {
                Color color = new Color(bubbleImage.getRGB(x, y));
                int luminance = (int) (0.299D * color.getRed() + 0.587D * color.getGreen() + 0.114D * color.getBlue());
                darknessSum += (255 - luminance) / 255D;
                if (luminance <= 170) {
                    darkPixelCount++;
                }
                if (luminance <= 130) {
                    inkPixelCount++;
                }
                totalPixelCount++;
            }
        }

        if (totalPixelCount <= 0) {
            return new BubbleMetric(bubble.option(), 0D);
        }

        double averageDarkness = darknessSum / totalPixelCount;
        double darkRatio = darkPixelCount * 1D / totalPixelCount;
        double inkRatio = inkPixelCount * 1D / totalPixelCount;
        double score = averageDarkness * 0.45D + darkRatio * 0.35D + inkRatio * 0.20D;
        return new BubbleMetric(bubble.option(), score);
    }

    private boolean isMultipleChoice(PaperQuestion question) {
        if (question == null) {
            return false;
        }
        if (question.getQuestionType() != null && question.getQuestionType() == 2) {
            return true;
        }
        String correctAnswer = normalizeAnswer(question.getCorrectAnswer());
        return correctAnswer != null && correctAnswer.length() > 1;
    }

    private String normalizeAnswer(String answer) {
        if (answer == null || answer.isBlank()) {
            return null;
        }

        TreeSet<String> tokens = new TreeSet<>();
        for (char ch : answer.toUpperCase().toCharArray()) {
            if (Character.isLetterOrDigit(ch)) {
                tokens.add(String.valueOf(ch));
            }
        }
        if (tokens.isEmpty()) {
            return null;
        }
        return String.join("", tokens);
    }

    private boolean isObjectiveQuestion(PaperQuestion question) {
        return question != null && question.getIsObjective() != null && question.getIsObjective() == 1;
    }

    private String resolveDetailStatusName(AnswerSheetDetail detail, PaperQuestion question) {
        if (detail == null) {
            return isObjectiveQuestion(question) ? "待识别" : "待裁题";
        }
        if (detail.getStatus() != null && detail.getStatus() == 1) {
            return "已完成";
        }
        if (isObjectiveQuestion(question)) {
            return detail.getStudentAnswer() == null || detail.getStudentAnswer().isBlank() ? "待识别" : "待复核";
        }
        return "待阅卷";
    }

    private boolean supportsPreview(String regionRole) {
        return PREVIEW_REGION_ROLES.contains(regionRole);
    }

    private QuestionRegionBinding findQuestionBinding(AnswerSheetTemplateVO template, List<PaperQuestion> allQuestions, PaperQuestion question) {
        if (template == null || template.getRegions() == null || template.getRegions().isEmpty()) {
            return new QuestionRegionBinding(null, null, null, List.of());
        }

        for (AnswerSheetRegionVO region : template.getRegions()) {
            String regionRole = getConfigString(region.getConfig(), "regionRole");
            if (!supportsPreview(regionRole)) {
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
                    .filter(question -> regionQuestionIds.contains(question.getId()))
                    .sorted(questionComparator())
                    .toList();
        }

        Integer questionStart = region.getQuestionStart();
        Integer questionEnd = region.getQuestionEnd();
        if (questionStart == null || questionEnd == null) {
            return List.of();
        }

        return allQuestions.stream()
                .filter(question -> {
                    Integer index = resolveQuestionOrderIndex(question);
                    return index != null && index >= questionStart && index <= questionEnd;
                })
                .sorted(questionComparator())
                .toList();
    }

    private Comparator<PaperQuestion> questionComparator() {
        return Comparator
                .comparing((PaperQuestion question) -> question.getSort() == null ? Integer.MAX_VALUE : question.getSort())
                .thenComparing(question -> question.getId() == null ? Long.MAX_VALUE : question.getId());
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

    private List<BubbleDefinition> parseBubbleDefinitions(Map<String, Object> config) {
        if (config == null) {
            return List.of();
        }
        Object bubbleMapValue = config.get("bubbleMap");
        if (!(bubbleMapValue instanceof Collection<?> collection) || collection.isEmpty()) {
            return List.of();
        }

        List<BubbleDefinition> result = new ArrayList<>();
        for (Object item : collection) {
            if (!(item instanceof Map<?, ?> bubbleMap)) {
                continue;
            }

            Integer questionNo = getInteger(bubbleMap, "questionNo");
            String option = getString(bubbleMap, "option");
            Double x = getDouble(bubbleMap, "x");
            Double y = getDouble(bubbleMap, "y");
            Double width = getDouble(bubbleMap, "width");
            Double height = getDouble(bubbleMap, "height");
            if (questionNo == null || option == null || x == null || y == null || width == null || height == null) {
                continue;
            }
            result.add(new BubbleDefinition(questionNo, option.trim().toUpperCase(), x, y, width, height));
        }
        return result;
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

    private BufferedImage readImage(String objectName, Map<String, BufferedImage> imageCache) {
        if (imageCache.containsKey(objectName)) {
            return imageCache.get(objectName);
        }

        try (InputStream inputStream = fileService.getFileStream(objectName)) {
            BufferedImage image = ImageIO.read(inputStream);
            imageCache.put(objectName, image);
            return image;
        } catch (Exception ex) {
            return null;
        }
    }

    private BufferedImage cropQuestionPreview(BufferedImage pageImage,
                                             AnswerSheetRegionVO region,
                                             List<PaperQuestion> orderedQuestions,
                                             Long questionId,
                                             String cropMode) {
        BufferedImage regionImage = cropRegion(pageImage, region);
        if (regionImage == null) {
            throw new BusinessException("裁题区域无效");
        }

        if ("single-question".equals(cropMode) && orderedQuestions != null && orderedQuestions.size() > 1) {
            int index = findQuestionIndex(orderedQuestions, questionId);
            if (index >= 0) {
                int perHeight = Math.max(1, regionImage.getHeight() / orderedQuestions.size());
                int top = Math.min(index * perHeight, regionImage.getHeight() - 1);
                int height = index == orderedQuestions.size() - 1
                        ? regionImage.getHeight() - top
                        : Math.min(perHeight, regionImage.getHeight() - top);
                return safeSubImage(regionImage, 0, top, regionImage.getWidth(), height);
            }
        }

        return regionImage;
    }

    private int findQuestionIndex(List<PaperQuestion> orderedQuestions, Long questionId) {
        for (int i = 0; i < orderedQuestions.size(); i++) {
            if (orderedQuestions.get(i).getId().equals(questionId)) {
                return i;
            }
        }
        return -1;
    }

    private BufferedImage cropRegion(BufferedImage image, AnswerSheetRegionVO region) {
        if (image == null || region == null || region.getConfig() == null) {
            return null;
        }

        Double boxX = getPercent(region.getConfig(), "boxX");
        Double boxY = getPercent(region.getConfig(), "boxY");
        Double boxWidth = getPercent(region.getConfig(), "boxWidth");
        Double boxHeight = getPercent(region.getConfig(), "boxHeight");
        if (boxX == null || boxY == null || boxWidth == null || boxHeight == null) {
            return null;
        }

        return cropPercentRegion(image, boxX, boxY, boxWidth, boxHeight);
    }

    private BufferedImage cropPercentRegion(BufferedImage image, Double boxX, Double boxY, Double boxWidth, Double boxHeight) {
        if (image == null || boxX == null || boxY == null || boxWidth == null || boxHeight == null) {
            return null;
        }

        int left = clamp((int) Math.floor(image.getWidth() * (boxX / 100D)), 0, image.getWidth() - 1);
        int top = clamp((int) Math.floor(image.getHeight() * (boxY / 100D)), 0, image.getHeight() - 1);
        int width = Math.max(1, (int) Math.ceil(image.getWidth() * (boxWidth / 100D)));
        int height = Math.max(1, (int) Math.ceil(image.getHeight() * (boxHeight / 100D)));

        int right = clamp(left + width, left + 1, image.getWidth());
        int bottom = clamp(top + height, top + 1, image.getHeight());
        BufferedImage cropped = image.getSubimage(left, top, right - left, bottom - top);

        BufferedImage output = new BufferedImage(cropped.getWidth(), cropped.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = output.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, output.getWidth(), output.getHeight());
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(cropped, 0, 0, null);
        graphics.dispose();
        return output;
    }

    private BufferedImage safeSubImage(BufferedImage source, int x, int y, int width, int height) {
        int cropX = clamp(x, 0, source.getWidth() - 1);
        int cropY = clamp(y, 0, source.getHeight() - 1);
        int cropWidth = Math.max(1, Math.min(width, source.getWidth() - cropX));
        int cropHeight = Math.max(1, Math.min(height, source.getHeight() - cropY));
        return source.getSubimage(cropX, cropY, cropWidth, cropHeight);
    }

    private byte[] toPngBytes(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new BusinessException("生成题图预览失败");
        }
    }

    private String getConfigString(Map<String, Object> config, String key) {
        if (config == null) {
            return null;
        }
        Object value = config.get(key);
        return value == null ? null : String.valueOf(value).trim();
    }

    private Integer getConfigInteger(Map<String, Object> config, String key) {
        Double number = getPercent(config, key);
        return number == null ? null : number.intValue();
    }

    private Double getPercent(Map<String, Object> config, String key) {
        if (config == null) {
            return null;
        }
        Object value = config.get(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof String text && !text.isBlank()) {
            try {
                return Double.parseDouble(text.trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private String getString(Map<?, ?> source, String key) {
        Object value = source.get(key);
        return value == null ? null : String.valueOf(value);
    }

    private Integer getInteger(Map<?, ?> source, String key) {
        Object value = source.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String text && !text.isBlank()) {
            try {
                return Integer.parseInt(text.trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private Double getDouble(Map<?, ?> source, String key) {
        Object value = source.get(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof String text && !text.isBlank()) {
            try {
                return Double.parseDouble(text.trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private record AnswerSheetContext(
            AnswerSheet answerSheet,
            List<PaperQuestion> questions,
            Map<Long, PaperQuestion> questionMap,
            Map<Long, AnswerSheetDetail> detailMap,
            List<AnswerSheetImageVO> images,
            AnswerSheetTemplateVO template
    ) {
    }

    private record QuestionRegionBinding(
            AnswerSheetRegionVO region,
            String regionRole,
            String cropMode,
            List<PaperQuestion> orderedQuestions
    ) {
    }

    private record BubbleDefinition(
            Integer questionNo,
            String option,
            Double x,
            Double y,
            Double width,
            Double height
    ) {
    }

    private record BubbleMetric(String option, double score) {
    }
}
