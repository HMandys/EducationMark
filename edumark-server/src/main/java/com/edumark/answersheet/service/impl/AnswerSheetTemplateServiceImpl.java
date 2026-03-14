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
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.exam.entity.Paper;
import com.edumark.exam.entity.PaperQuestion;
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
    private FileService fileService;

    @Resource
    private PdfGeneratorService pdfGeneratorService;

    private static final Map<Integer, String> REGION_TYPE_NAMES = Map.of(
            1, "选择题",
            2, "填空题",
            3, "解答题",
            4, "作文题"
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
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(AnswerSheetTemplateDTO dto) {
        // 检查试卷是否已有模板
        AnswerSheetTemplateVO existing = baseMapper.selectVOByPaperId(dto.getPaperId());
        if (existing != null) {
            throw new BusinessException("该试卷已存在答题卡模板");
        }

        // 检查试卷是否存在
        Paper paper = paperMapper.selectById(dto.getPaperId());
        if (paper == null) {
            throw new BusinessException("试卷不存在");
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

            AnswerSheetRegion region = new AnswerSheetRegion();
            region.setTemplateId(templateId);
            region.setRegionType(regionType);
            region.setRegionName(firstQuestion.getSectionName() != null ?
                    firstQuestion.getSectionName() : REGION_TYPE_NAMES.get(regionType));
            region.setPageNo(1);
            region.setSortOrder(sortOrder++);

            // 设置题号范围
            int minNo = sectionQuestions.stream()
                    .mapToInt(q -> Integer.parseInt(q.getQuestionNo().replaceAll("[^0-9]", "")))
                    .min().orElse(1);
            int maxNo = sectionQuestions.stream()
                    .mapToInt(q -> Integer.parseInt(q.getQuestionNo().replaceAll("[^0-9]", "")))
                    .max().orElse(1);
            region.setQuestionStart(minNo);
            region.setQuestionEnd(maxNo);

            // 设置关联题目ID
            List<Long> questionIds = sectionQuestions.stream()
                    .map(PaperQuestion::getId)
                    .collect(Collectors.toList());
            region.setQuestionIds(questionIds);

            // 设置区域配置
            Map<String, Object> config = generateRegionConfig(regionType, sectionQuestions);
            region.setConfig(config);

            regionMapper.insert(region);
        }
    }

    private int determineRegionType(Integer questionType) {
        if (questionType == null) return 3; // 默认解答题
        return switch (questionType) {
            case 1, 2, 3 -> 1; // 单选、多选、判断 -> 选择题
            case 4 -> 2; // 填空题
            case 7 -> 4; // 作文题
            default -> 3; // 其他 -> 解答题
        };
    }

    private Map<String, Object> generateRegionConfig(int regionType, List<PaperQuestion> questions) {
        Map<String, Object> config = new HashMap<>();

        switch (regionType) {
            case 1 -> { // 选择题
                config.put("optionCount", 4); // 默认4个选项
                config.put("questionsPerRow", 5); // 每行5题
                config.put("bubbleStyle", "circle"); // 涂卡样式: circle, square
                boolean hasMultiple = questions.stream()
                        .anyMatch(q -> q.getQuestionType() != null && q.getQuestionType() == 2);
                config.put("hasMultipleChoice", hasMultiple);
            }
            case 2 -> { // 填空题
                config.put("lineHeight", 30); // 行高(mm)
                config.put("linesPerQuestion", 1); // 每题行数
                config.put("lineStyle", "underline"); // 下划线样式
            }
            case 3 -> { // 解答题
                int totalScore = questions.stream()
                        .mapToInt(q -> q.getScore() != null ? q.getScore() : 0)
                        .sum();
                config.put("height", Math.max(80, totalScore * 5)); // 区域高度
                config.put("showBorder", true); // 显示边框
                config.put("scoreBoxPosition", "top-right"); // 评分框位置
            }
            case 4 -> { // 作文题
                int totalScore = questions.stream()
                        .mapToInt(q -> q.getScore() != null ? q.getScore() : 0)
                        .sum();
                int wordCount = totalScore >= 50 ? 800 : (totalScore >= 30 ? 600 : 400);
                config.put("gridType", "square"); // 格子类型: square, line
                config.put("gridSize", 10); // 格子大小(mm)
                config.put("wordCount", wordCount); // 总字数
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

        // 查询区域配置
        List<AnswerSheetRegionVO> regions = regionMapper.selectListByTemplateId(id);
        if (regions.isEmpty()) {
            throw new BusinessException("请先配置答题区域");
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
}
