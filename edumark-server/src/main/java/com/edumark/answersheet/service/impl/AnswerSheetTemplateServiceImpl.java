package com.edumark.answersheet.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.answersheet.dto.AnswerSheetRegionDTO;
import com.edumark.answersheet.dto.AnswerSheetTemplateDTO;
import com.edumark.answersheet.dto.AnswerSheetTemplateQueryDTO;
import com.edumark.answersheet.entity.AnswerSheetRegion;
import com.edumark.answersheet.entity.AnswerSheetTemplate;
import com.edumark.answersheet.mapper.AnswerSheetRegionMapper;
import com.edumark.answersheet.mapper.AnswerSheetTemplateMapper;
import com.edumark.answersheet.service.AnswerSheetTemplateService;
import com.edumark.answersheet.service.PdfGeneratorService;
import com.edumark.answersheet.vo.AnswerSheetRegionVO;
import com.edumark.answersheet.vo.AnswerSheetTemplateVO;
import com.edumark.answersheet.vo.AnswerSheetTemplateValidateVO;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.exam.entity.ExamSubject;
import com.edumark.exam.entity.Paper;
import com.edumark.exam.entity.PaperQuestion;
import com.edumark.exam.mapper.ExamSubjectMapper;
import com.edumark.exam.mapper.PaperMapper;
import com.edumark.exam.mapper.PaperQuestionMapper;
import com.edumark.file.service.FileService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 答题卡模板服务实现
 *
 * @author EduMark
 */
@Service
public class AnswerSheetTemplateServiceImpl extends ServiceImpl<AnswerSheetTemplateMapper, AnswerSheetTemplate>
        implements AnswerSheetTemplateService {

    @Resource
    private AnswerSheetRegionMapper regionMapper;

    @Resource
    private PaperMapper paperMapper;

    @Resource
    private PaperQuestionMapper paperQuestionMapper;

    @Resource
    private ExamSubjectMapper examSubjectMapper;

    @Resource
    private FileService fileService;

    @Resource
    private PdfGeneratorService pdfGeneratorService;

    private static final Map<Integer, String> REGION_TYPE_NAMES = Map.of(
            1, "选择题",
            2, "填空题",
            3, "主观题",
            4, "主观题"
    );

    @Override
    public PageResult<AnswerSheetTemplateVO> pageQuery(AnswerSheetTemplateQueryDTO query) {
        Page<AnswerSheetTemplateVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        baseMapper.selectPageVO(page, query);

        // 设置PDF URL
        for (AnswerSheetTemplateVO vo : page.getRecords()) {
            if (vo.getPdfObjectName() != null) {
                vo.setPdfUrl(fileService.getUrl(vo.getPdfObjectName()));
            }
        }

        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public AnswerSheetTemplateVO getDetail(Long id) {
        AnswerSheetTemplateVO vo = baseMapper.selectVOById(id);
        if (vo == null) {
            throw new BusinessException("模板不存在");
        }

        // 查询区域列表
        List<AnswerSheetRegionVO> regions = regionMapper.selectListByTemplateId(id);
        for (AnswerSheetRegionVO region : regions) {
            region.setRegionTypeName(REGION_TYPE_NAMES.getOrDefault(region.getRegionType(), "未知"));
        }
        vo.setRegions(regions);

        // 设置PDF URL
        if (vo.getPdfObjectName() != null) {
            vo.setPdfUrl(fileService.getUrl(vo.getPdfObjectName()));
        }

        // 设置模板图片URL
        if (vo.getTemplateImagePath() != null) {
            vo.setTemplateImageUrl(fileService.getUrl(vo.getTemplateImagePath()));
        }

        return vo;
    }

    @Override
    public AnswerSheetTemplateVO getByPaperId(Long paperId) {
        AnswerSheetTemplateVO vo = baseMapper.selectVOByPaperId(paperId);
        if (vo != null) {
            List<AnswerSheetRegionVO> regions = regionMapper.selectListByTemplateId(vo.getId());
            for (AnswerSheetRegionVO region : regions) {
                region.setRegionTypeName(REGION_TYPE_NAMES.getOrDefault(region.getRegionType(), "未知"));
            }
            vo.setRegions(regions);

            if (vo.getPdfObjectName() != null) {
                vo.setPdfUrl(fileService.getUrl(vo.getPdfObjectName()));
            }
            if (vo.getTemplateImagePath() != null) {
                vo.setTemplateImageUrl(fileService.getUrl(vo.getTemplateImagePath()));
            }
        }
        return vo;
    }

    @Override
    public AnswerSheetTemplateVO getByExamSubjectId(Long examSubjectId) {
        // 1. 首先尝试通过 paperId 查找（传统方式）
        Paper paper = paperMapper.selectByExamSubjectId(examSubjectId);
        if (paper != null) {
            AnswerSheetTemplateVO vo = getByPaperId(paper.getId());
            if (vo != null) {
                return vo;
            }
        }

        // 2. 如果通过 paperId 找不到，尝试通过 examId + subjectName 查找
        ExamSubject examSubject = examSubjectMapper.selectById(examSubjectId);
        if (examSubject == null) {
            return null;
        }

        AnswerSheetTemplateVO vo = baseMapper.selectVOByExamIdAndSubjectName(
                examSubject.getExamId(), examSubject.getSubjectName());
        if (vo != null) {
            List<AnswerSheetRegionVO> regions = regionMapper.selectListByTemplateId(vo.getId());
            for (AnswerSheetRegionVO region : regions) {
                region.setRegionTypeName(REGION_TYPE_NAMES.getOrDefault(region.getRegionType(), "未知"));
            }
            vo.setRegions(regions);

            if (vo.getPdfObjectName() != null) {
                vo.setPdfUrl(fileService.getUrl(vo.getPdfObjectName()));
            }
            if (vo.getTemplateImagePath() != null) {
                vo.setTemplateImageUrl(fileService.getUrl(vo.getTemplateImagePath()));
            }
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(AnswerSheetTemplateDTO dto) {
        // 如果关联了试卷，检查试卷是否已有模板
        if (dto.getPaperId() != null) {
            AnswerSheetTemplateVO existing = baseMapper.selectVOByPaperId(dto.getPaperId());
            if (existing != null) {
                throw new BusinessException("该试卷已存在答题卡模板");
            }

            // 检查试卷是否存在
            Paper paper = paperMapper.selectById(dto.getPaperId());
            if (paper == null) {
                throw new BusinessException("试卷不存在");
            }
        }

        // 创建模板
        AnswerSheetTemplate template = new AnswerSheetTemplate();
        BeanUtils.copyProperties(dto, template);
        template.setStatus(0); // 草稿状态
        save(template);

        // 保存区域配置
        saveRegions(template.getId(), dto.getRegions());

        return template.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AnswerSheetTemplateDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("模板ID不能为空");
        }
        AnswerSheetTemplate existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException("模板不存在");
        }
        if (existing.getStatus() == 1) {
            throw new BusinessException("已发布的模板不能修改，请先取消发布");
        }

        // 更新模板
        AnswerSheetTemplate template = new AnswerSheetTemplate();
        BeanUtils.copyProperties(dto, template);
        updateById(template);

        // 更新区域配置
        regionMapper.deleteByTemplateId(dto.getId());
        saveRegions(dto.getId(), dto.getRegions());
    }

    private void saveRegions(Long templateId, List<AnswerSheetRegionDTO> regionDTOs) {
        if (regionDTOs != null && !regionDTOs.isEmpty()) {
            for (int i = 0; i < regionDTOs.size(); i++) {
                AnswerSheetRegionDTO regionDTO = regionDTOs.get(i);
                AnswerSheetRegion region = new AnswerSheetRegion();
                BeanUtils.copyProperties(regionDTO, region);
                normalizeRegionQuestionScope(region);
                region.setId(null); // 确保是新增
                region.setTemplateId(templateId);
                region.setSortOrder(i);
                regionMapper.insert(region);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        AnswerSheetTemplate template = getById(id);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }

        // 删除PDF文件
        if (template.getPdfObjectName() != null) {
            fileService.delete(template.getPdfObjectName());
        }

        // 删除区域配置
        regionMapper.deleteByTemplateId(id);

        // 删除模板
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateFromPaper(Long paperId) {
        // 检查试卷是否已有模板
        AnswerSheetTemplateVO existing = baseMapper.selectVOByPaperId(paperId);
        if (existing != null) {
            throw new BusinessException("该试卷已存在答题卡模板");
        }

        // 获取试卷信息
        Paper paper = paperMapper.selectById(paperId);
        if (paper == null) {
            throw new BusinessException("试卷不存在");
        }

        // 获取题目列表
        List<PaperQuestion> questions = paperQuestionMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PaperQuestion>()
                        .eq(PaperQuestion::getPaperId, paperId)
                        .eq(PaperQuestion::getDeleted, 0)
                        .orderByAsc(PaperQuestion::getSort)
        );

        // 创建模板
        AnswerSheetTemplate template = new AnswerSheetTemplate();
        template.setPaperId(paperId);
        template.setName(paper.getName() + " - 答题卡");
        template.setPageSize("A4");
        template.setOrientation(1);
        template.setColumns(1);
        template.setMarginTop(20);
        template.setMarginBottom(20);
        template.setMarginLeft(15);
        template.setMarginRight(15);
        template.setStatus(0);

        // 设置默认页眉配置
        Map<String, Object> headerConfig = new HashMap<>();
        headerConfig.put("title", paper.getName());
        headerConfig.put("showTitle", true);
        template.setHeaderConfig(headerConfig);

        // 设置默认学生信息区配置
        Map<String, Object> studentInfoConfig = new HashMap<>();
        studentInfoConfig.put("showStudentId", true);
        studentInfoConfig.put("showName", true);
        studentInfoConfig.put("showClass", true);
        template.setStudentInfoConfig(studentInfoConfig);

        save(template);

        // 根据题目类型分组生成区域
        generateRegionsFromQuestions(template.getId(), questions);

        return template.getId();
    }

    @Override
    public AnswerSheetTemplateValidateVO validateTemplate(Long id) {
        AnswerSheetTemplate template = getById(id);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }

        List<AnswerSheetRegionVO> regions = regionMapper.selectListByTemplateId(id);
        AnswerSheetTemplateValidateVO result = new AnswerSheetTemplateValidateVO();
        result.setTotalRegionCount(regions.size());

        List<AnswerSheetTemplateValidateVO.ValidationIssue> issues = new ArrayList<>();
        int annotatedRegionCount = 0;

        if (regions.isEmpty()) {
            issues.add(new AnswerSheetTemplateValidateVO.ValidationIssue(null, "regions", "请至少配置一个答题区域"));
        }

        for (AnswerSheetRegionVO region : regions) {
            Map<String, Object> config = region.getConfig() != null ? region.getConfig() : Collections.emptyMap();
            String regionLabel = buildRegionLabel(region);

            Double boxX = getConfigNumber(config, "boxX", "x");
            Double boxY = getConfigNumber(config, "boxY", "y");
            Double boxWidth = getConfigNumber(config, "boxWidth", "width");
            Double boxHeight = getConfigNumber(config, "boxHeight");

            boolean hasAnyBounds = boxX != null || boxY != null || boxWidth != null || boxHeight != null;
            boolean hasAllBounds = boxX != null && boxY != null && boxWidth != null && boxHeight != null;
            if (hasAllBounds) {
                annotatedRegionCount++;
            } else {
                issues.add(new AnswerSheetTemplateValidateVO.ValidationIssue(
                        regionLabel,
                        "bounds",
                        regionLabel + " 未完成坐标标注，请补充 X/Y/宽/高"
                ));
            }

            if (hasAllBounds) {
                validateBounds(issues, regionLabel, boxX, boxY, boxWidth, boxHeight);
            } else if (hasAnyBounds) {
                issues.add(new AnswerSheetTemplateValidateVO.ValidationIssue(
                        regionLabel,
                        "bounds",
                        regionLabel + " 坐标信息不完整，必须同时提供 X/Y/宽/高"
                ));
            }

            String regionRole = getConfigString(config, "regionRole");
            if (regionRole == null || regionRole.isBlank()) {
                issues.add(new AnswerSheetTemplateValidateVO.ValidationIssue(
                        regionLabel,
                        "regionRole",
                        regionLabel + " 未设置区域用途"
                ));
            }

            if (requiresQuestionRange(regionRole, region.getRegionType())) {
                if (region.getQuestionStart() == null || region.getQuestionEnd() == null) {
                    issues.add(new AnswerSheetTemplateValidateVO.ValidationIssue(
                            regionLabel,
                            "questionRange",
                            regionLabel + " 缺少题号范围"
                    ));
                } else if (region.getQuestionStart() > region.getQuestionEnd()) {
                    issues.add(new AnswerSheetTemplateValidateVO.ValidationIssue(
                            regionLabel,
                            "questionRange",
                            regionLabel + " 的结束题号不能小于起始题号"
                    ));
                }
            }

            if ("choice_block".equals(regionRole)) {
                Integer optionCount = getConfigInteger(config, "optionCount");
                int questionCount = region.getQuestionStart() != null && region.getQuestionEnd() != null
                        ? region.getQuestionEnd() - region.getQuestionStart() + 1
                        : 0;
                int expectedBubbleCount = optionCount != null && questionCount > 0 ? optionCount * questionCount : 0;
                int actualBubbleCount = getConfigListSize(config, "bubbleMap");

                if (expectedBubbleCount <= 0) {
                    issues.add(new AnswerSheetTemplateValidateVO.ValidationIssue(
                            regionLabel,
                            "bubbleMap",
                            regionLabel + " 缺少客观题选项配置，无法自动生成选项坐标"
                    ));
                } else if (actualBubbleCount <= 0) {
                    issues.add(new AnswerSheetTemplateValidateVO.ValidationIssue(
                            regionLabel,
                            "bubbleMap",
                            regionLabel + " 尚未完成客观题选项自动识别"
                    ));
                } else if (actualBubbleCount < expectedBubbleCount) {
                    issues.add(new AnswerSheetTemplateValidateVO.ValidationIssue(
                            regionLabel,
                            "bubbleMap",
                            regionLabel + " 仅识别到 " + actualBubbleCount + "/" + expectedBubbleCount + " 个选项坐标"
                    ));
                }
            }

            if (requiresCropMode(regionRole)) {
                String cropMode = getConfigString(config, "cropMode");
                if (cropMode == null || cropMode.isBlank()) {
                    issues.add(new AnswerSheetTemplateValidateVO.ValidationIssue(
                            regionLabel,
                            "cropMode",
                            regionLabel + " 未设置裁题模式"
                    ));
                }
            }

            String anchorType = getConfigString(config, "anchorType");
            if (anchorType != null && !"none".equals(anchorType)) {
                String anchorKey = getConfigString(config, "anchorKey");
                if (anchorKey == null || anchorKey.isBlank()) {
                    issues.add(new AnswerSheetTemplateValidateVO.ValidationIssue(
                            regionLabel,
                            "anchorKey",
                            regionLabel + " 已设置锚点类型，但缺少锚点标识"
                    ));
                }
            }
        }

        result.setAnnotatedRegionCount(annotatedRegionCount);
        result.setIssues(issues);
        result.setIssueCount(issues.size());
        result.setPassed(issues.isEmpty());
        return result;
    }

    private void generateRegionsFromQuestions(Long templateId, List<PaperQuestion> questions) {
        // 按大题号分组
        Map<Integer, List<PaperQuestion>> sectionMap = questions.stream()
                .collect(Collectors.groupingBy(PaperQuestion::getSectionNo, LinkedHashMap::new, Collectors.toList()));

        int sortOrder = 0;
        for (Map.Entry<Integer, List<PaperQuestion>> entry : sectionMap.entrySet()) {
            List<PaperQuestion> sectionQuestions = entry.getValue();
            if (sectionQuestions.isEmpty()) continue;

            PaperQuestion firstQuestion = sectionQuestions.get(0);
            int regionType = determineRegionType(firstQuestion.getQuestionType());

            if (regionType != 1) {
                for (PaperQuestion question : sectionQuestions) {
                    createGeneratedRegion(templateId, List.of(question), determineRegionType(question.getQuestionType()), sortOrder++);
                }
                continue;
            }

            createGeneratedRegion(templateId, sectionQuestions, regionType, sortOrder++);
        }
    }

    private void createGeneratedRegion(Long templateId, List<PaperQuestion> questions, int regionType, int sortOrder) {
        if (questions == null || questions.isEmpty()) {
            return;
        }

        PaperQuestion firstQuestion = questions.get(0);
        AnswerSheetRegion region = new AnswerSheetRegion();
        region.setTemplateId(templateId);
        region.setRegionType(regionType);
        region.setRegionName(buildGeneratedRegionName(firstQuestion, regionType, questions.size() == 1));
        region.setPageNo(1);
        region.setSortOrder(sortOrder);

        int minNo = questions.stream()
                .mapToInt(q -> Integer.parseInt(q.getQuestionNo().replaceAll("[^0-9]", "")))
                .min().orElse(1);
        int maxNo = questions.stream()
                .mapToInt(q -> Integer.parseInt(q.getQuestionNo().replaceAll("[^0-9]", "")))
                .max().orElse(1);
        region.setQuestionStart(minNo);
        region.setQuestionEnd(maxNo);
        region.setQuestionIds(questions.stream().map(PaperQuestion::getId).collect(Collectors.toList()));
        region.setConfig(generateRegionConfig(regionType, questions));
        normalizeRegionQuestionScope(region);
        regionMapper.insert(region);
    }

    private String buildGeneratedRegionName(PaperQuestion question, int regionType, boolean singleQuestion) {
        if (singleQuestion) {
            return "第" + question.getQuestionNo() + "题";
        }
        return question.getSectionName() != null ? question.getSectionName() : REGION_TYPE_NAMES.get(regionType);
    }

    private int determineRegionType(Integer questionType) {
        if (questionType == null) return 3; // 默认解答题
        return switch (questionType) {
            case 1, 2, 3 -> 1; // 单选、多选、判断 -> 选择题
            case 4 -> 2; // 填空题
            case 7 -> 3; // 作文题暂按主观题处理
            default -> 3; // 其他 -> 主观题
        };
    }

    private Map<String, Object> generateRegionConfig(int regionType, List<PaperQuestion> questions) {
        Map<String, Object> config = new HashMap<>();

        switch (regionType) {
            case 1 -> { // 选择题
                config.put("optionCount", 4); // 默认4个选项
                config.put("questionsPerRow", 5); // 每行5题
                config.put("bubbleStyle", "circle"); // 涂卡样式: circle, square
                config.put("regionRole", "choice_block");
                config.put("anchorType", "none");
                config.put("cropMode", "range-question");
                boolean hasMultiple = questions.stream()
                        .anyMatch(q -> q.getQuestionType() != null && q.getQuestionType() == 2);
                config.put("hasMultipleChoice", hasMultiple);
            }
            case 2 -> { // 填空题
                int totalScore = questions.stream()
                        .mapToInt(q -> q.getScore() != null ? q.getScore() : 0)
                        .sum();
                config.put("height", 24); // 填空题区域高度
                config.put("showBorder", true); // 使用单框样式
                config.put("totalScore", Math.max(totalScore, 1)); // 单空分值
                config.put("regionRole", "subjective_crop");
                config.put("anchorType", "none");
                config.put("cropMode", "single-question");
            }
            case 3 -> { // 解答题
                int totalScore = questions.stream()
                        .mapToInt(q -> q.getScore() != null ? q.getScore() : 0)
                        .sum();
                config.put("height", Math.max(80, totalScore * 5)); // 区域高度
                config.put("showBorder", true); // 显示边框
                config.put("scoreBoxPosition", "top-right"); // 评分框位置
                config.put("regionRole", "subjective_crop");
                config.put("anchorType", "none");
                config.put("cropMode", "single-question");
            }
        }

        return config;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id) {
        AnswerSheetTemplate template = getById(id);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }

        AnswerSheetTemplateValidateVO validateResult = validateTemplate(id);
        if (!Boolean.TRUE.equals(validateResult.getPassed())) {
            String message = validateResult.getIssues().stream()
                    .limit(3)
                    .map(AnswerSheetTemplateValidateVO.ValidationIssue::getMessage)
                    .collect(Collectors.joining("；"));
            throw new BusinessException("模板校验未通过: " + message);
        }

        // 生成PDF
        AnswerSheetTemplateVO vo = getDetail(id);
        byte[] pdfBytes = pdfGeneratorService.generatePdf(vo);

        // 上传到MinIO
        String objectName = "answersheet/" + id + "/" + System.currentTimeMillis() + ".pdf";
        try {
            // 删除旧的PDF
            if (template.getPdfObjectName() != null) {
                fileService.delete(template.getPdfObjectName());
            }

            // 上传新PDF
            fileService.uploadBytes(pdfBytes, objectName, "application/pdf");

            // 更新模板状态
            template.setPdfObjectName(objectName);
            template.setStatus(1);
            updateById(template);
        } catch (Exception e) {
            throw new BusinessException("PDF生成失败: " + e.getMessage());
        }
    }

    @Override
    public String getPreviewUrl(Long id) {
        AnswerSheetTemplate template = getById(id);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }
        if (template.getPdfObjectName() == null) {
            throw new BusinessException("请先发布模板生成PDF");
        }
        return fileService.getPresignedUrl(template.getPdfObjectName(), 3600); // 1小时有效期
    }

    @Override
    public String getDownloadUrl(Long id) {
        return getPreviewUrl(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadTemplateImage(Long id, String imagePath) {
        AnswerSheetTemplate template = getById(id);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }

        // 删除旧的模板图片
        if (template.getTemplateImagePath() != null && !template.getTemplateImagePath().equals(imagePath)) {
            try {
                fileService.delete(template.getTemplateImagePath());
            } catch (Exception ignored) {
                // 忽略删除失败
            }
        }

        // 更新模板图片路径
        template.setTemplateImagePath(imagePath);
        updateById(template);

        return fileService.getUrl(imagePath);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCornerConfig(Long id, Map<String, Object> cornerConfig) {
        AnswerSheetTemplate template = getById(id);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }

        template.setCornerConfig(cornerConfig);
        updateById(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRegionCorrectAnswers(Long templateId, Long regionId, Map<String, String> correctAnswers) {
        AnswerSheetTemplate template = getById(templateId);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }

        AnswerSheetRegion region = regionMapper.selectById(regionId);
        if (region == null || !templateId.equals(region.getTemplateId())) {
            throw new BusinessException("区域不存在或不属于该模板");
        }

        // 更新区域配置中的correctAnswers
        Map<String, Object> config = region.getConfig();
        if (config == null) {
            config = new HashMap<>();
        }
        config.put("correctAnswers", correctAnswers);
        region.setConfig(config);
        regionMapper.updateById(region);
    }

    @Override
    public String getTemplateImageUrl(Long id) {
        AnswerSheetTemplate template = getById(id);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }
        if (template.getTemplateImagePath() == null) {
            return null;
        }
        return fileService.getUrl(template.getTemplateImagePath());
    }

    private void validateBounds(List<AnswerSheetTemplateValidateVO.ValidationIssue> issues,
                                String regionLabel,
                                Double boxX,
                                Double boxY,
                                Double boxWidth,
                                Double boxHeight) {
        List<Double> values = List.of(boxX, boxY, boxWidth, boxHeight);
        boolean outOfRange = values.stream().anyMatch(value -> value < 0 || value > 100);
        if (outOfRange) {
            issues.add(new AnswerSheetTemplateValidateVO.ValidationIssue(
                    regionLabel,
                    "bounds",
                    regionLabel + " 的坐标范围必须在 0 到 100 之间"
            ));
        }

        if (boxWidth <= 0 || boxHeight <= 0) {
            issues.add(new AnswerSheetTemplateValidateVO.ValidationIssue(
                    regionLabel,
                    "bounds",
                    regionLabel + " 的区域宽高必须大于 0"
            ));
        }

        if (boxX + boxWidth > 100 || boxY + boxHeight > 100) {
            issues.add(new AnswerSheetTemplateValidateVO.ValidationIssue(
                    regionLabel,
                    "bounds",
                    regionLabel + " 超出了页面范围"
            ));
        }
    }

    private boolean requiresQuestionRange(String regionRole, Integer regionType) {
        if (regionRole != null && !regionRole.isBlank()) {
            return Set.of("choice_block", "subjective_crop").contains(regionRole);
        }
        return regionType != null && regionType >= 1 && regionType <= 4;
    }

    private void normalizeRegionQuestionScope(AnswerSheetRegion region) {
        if (region == null) {
            return;
        }
        String regionRole = getConfigString(region.getConfig(), "regionRole");
        if ("subjective_crop".equals(regionRole)) {
            if (region.getQuestionStart() != null) {
                region.setQuestionEnd(region.getQuestionStart());
            }
            if (region.getQuestionIds() != null && region.getQuestionIds().size() > 1) {
                region.setQuestionIds(List.of(region.getQuestionIds().get(0)));
            }
        }
    }

    private boolean requiresCropMode(String regionRole) {
        if (regionRole == null || regionRole.isBlank()) {
            return false;
        }
        return Set.of("choice_block", "subjective_crop").contains(regionRole);
    }

    private String buildRegionLabel(AnswerSheetRegionVO region) {
        String pageText = region.getPageNo() != null ? "第" + region.getPageNo() + "页" : "未分页";
        return pageText + " " + (region.getRegionName() != null ? region.getRegionName() : "未命名区域");
    }

    private String getConfigString(Map<String, Object> config, String key) {
        Object value = config.get(key);
        return value != null ? String.valueOf(value) : null;
    }

    private Double getConfigNumber(Map<String, Object> config, String... keys) {
        for (String key : keys) {
            Object value = config.get(key);
            if (value instanceof Number number) {
                return number.doubleValue();
            }
            if (value instanceof String string && !string.isBlank()) {
                try {
                    return Double.parseDouble(string);
                } catch (NumberFormatException ignored) {
                    continue;
                }
            }
        }
        return null;
    }

    private Integer getConfigInteger(Map<String, Object> config, String key) {
        Double number = getConfigNumber(config, key);
        return number != null ? number.intValue() : null;
    }

    private int getConfigListSize(Map<String, Object> config, String key) {
        Object value = config.get(key);
        if (value instanceof Collection<?> collection) {
            return collection.size();
        }
        return 0;
    }
}
