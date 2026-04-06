package com.edumark.answersheet.service.impl;

import com.edumark.answersheet.service.PdfGeneratorService;
import com.edumark.answersheet.vo.AnswerSheetRegionVO;
import com.edumark.answersheet.vo.AnswerSheetTemplateVO;
import com.edumark.common.exception.BusinessException;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

/**
 * PDF生成服务实现
 *
 * @author EduMark
 */
@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(PdfGeneratorServiceImpl.class);

    // 点数转换 (1mm = 2.834645669291339 pt)
    private static final float MM_TO_PT = 2.834645669291339f;

    @Override
    public byte[] generatePdf(AnswerSheetTemplateVO template) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 创建PDF文档
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);

            // 设置页面大小和方向
            PageSize pageSize = getPageSize(template.getPageSize(), template.getOrientation());
            pdfDoc.setDefaultPageSize(pageSize);

            // 设置边距
            float marginTop = template.getMarginTop() * MM_TO_PT;
            float marginBottom = template.getMarginBottom() * MM_TO_PT;
            float marginLeft = template.getMarginLeft() * MM_TO_PT;
            float marginRight = template.getMarginRight() * MM_TO_PT;

            Document document = new Document(pdfDoc, pageSize);
            document.setMargins(marginTop, marginRight, marginBottom, marginLeft);

            // 加载中文字体
            PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");

            // 渲染页眉
            renderHeader(document, template, font);

            // 渲染学生信息区
            renderStudentInfoArea(document, template, font);

            // 添加分隔线
            document.add(new Paragraph().setMarginBottom(10));

            // 渲染答题区域
            if (template.getRegions() != null) {
                for (AnswerSheetRegionVO region : template.getRegions()) {
                    renderRegion(document, region, font);
                }
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("PDF生成失败", e);
            throw new BusinessException("PDF生成失败: " + e.getMessage());
        }
    }

    private PageSize getPageSize(String pageSizeStr, Integer orientation) {
        PageSize baseSize = switch (pageSizeStr != null ? pageSizeStr.toUpperCase() : "A4") {
            case "A3" -> PageSize.A3;
            case "B5" -> PageSize.B5;
            default -> PageSize.A4;
        };

        // 横向
        if (orientation != null && orientation == 2) {
            return baseSize.rotate();
        }
        return baseSize;
    }

    private void renderHeader(Document document, AnswerSheetTemplateVO template, PdfFont font) {
        Map<String, Object> headerConfig = template.getHeaderConfig();
        if (headerConfig == null) return;

        boolean showTitle = Boolean.TRUE.equals(headerConfig.get("showTitle"));
        if (!showTitle) return;

        String title = (String) headerConfig.getOrDefault("title", template.getName());

        // 标题
        Paragraph titlePara = new Paragraph(title)
                .setFont(font)
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(titlePara);

        // 副标题（考试名称 + 科目）
        String subtitle = "";
        if (template.getExamName() != null) {
            subtitle += template.getExamName();
        }
        if (template.getSubjectName() != null) {
            subtitle += " - " + template.getSubjectName();
        }
        if (!subtitle.isEmpty()) {
            Paragraph subtitlePara = new Paragraph(subtitle)
                    .setFont(font)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(subtitlePara);
        }
    }

    private void renderStudentInfoArea(Document document, AnswerSheetTemplateVO template, PdfFont font) {
        Map<String, Object> studentInfoConfig = template.getStudentInfoConfig();
        if (studentInfoConfig == null) return;

        boolean showStudentId = Boolean.TRUE.equals(studentInfoConfig.get("showStudentId"));
        boolean showName = Boolean.TRUE.equals(studentInfoConfig.get("showName"));
        boolean showClass = Boolean.TRUE.equals(studentInfoConfig.get("showClass"));

        if (!showStudentId && !showName && !showClass) return;

        // 创建学生信息表格
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                .useAllAvailableWidth()
                .setBorder(new SolidBorder(1));

        if (showName) {
            Cell nameCell = new Cell()
                    .add(new Paragraph("姓名：__________________").setFont(font).setFontSize(12))
                    .setBorder(Border.NO_BORDER)
                    .setPadding(10);
            table.addCell(nameCell);
        }

        if (showStudentId) {
            Cell idCell = new Cell()
                    .add(new Paragraph("学号：__________________").setFont(font).setFontSize(12))
                    .setBorder(Border.NO_BORDER)
                    .setPadding(10);
            table.addCell(idCell);
        }

        if (showClass) {
            Cell classCell = new Cell()
                    .add(new Paragraph("班级：__________________").setFont(font).setFontSize(12))
                    .setBorder(Border.NO_BORDER)
                    .setPadding(10);
            table.addCell(classCell);
        }

        // 填充空单元格
        int cellCount = (showName ? 1 : 0) + (showStudentId ? 1 : 0) + (showClass ? 1 : 0);
        for (int i = cellCount; i < 3; i++) {
            table.addCell(new Cell().setBorder(Border.NO_BORDER));
        }

        document.add(table);
    }

    private void renderRegion(Document document, AnswerSheetRegionVO region, PdfFont font) {
        // 区域标题
        String titleText = region.getRegionName();
        if (region.getQuestionStart() != null && region.getQuestionEnd() != null) {
            if (region.getQuestionStart().equals(region.getQuestionEnd())) {
                titleText += String.format("（第%d题）", region.getQuestionStart());
            } else {
                titleText += String.format("（第%d-%d题）", region.getQuestionStart(), region.getQuestionEnd());
            }
        }

        Paragraph title = new Paragraph(titleText)
                .setFont(font)
                .setFontSize(12)
                .setBold()
                .setMarginTop(15)
                .setMarginBottom(5);
        document.add(title);

        // 根据区域类型渲染
        switch (region.getRegionType()) {
            case 1 -> renderChoiceRegion(document, region, font);
            case 2 -> renderFillBlankRegion(document, region, font);
            case 3 -> renderAnswerRegion(document, region, font);
            case 4 -> renderEssayRegion(document, region, font);
        }
    }

    private void renderChoiceRegion(Document document, AnswerSheetRegionVO region, PdfFont font) {
        Map<String, Object> config = region.getConfig();
        int optionCount = config != null ? (int) config.getOrDefault("optionCount", 4) : 4;
        int questionsPerRow = config != null ? (int) config.getOrDefault("questionsPerRow", 5) : 5;
        boolean hasMultipleChoice = config != null && Boolean.TRUE.equals(config.get("hasMultipleChoice"));

        int start = region.getQuestionStart() != null ? region.getQuestionStart() : 1;
        int end = region.getQuestionEnd() != null ? region.getQuestionEnd() : start;
        int totalQuestions = end - start + 1;

        // 计算列数
        int colCount = questionsPerRow;
        float[] colWidths = new float[colCount];
        for (int i = 0; i < colCount; i++) {
            colWidths[i] = 1;
        }

        Table table = new Table(UnitValue.createPercentArray(colWidths))
                .useAllAvailableWidth()
                .setBorder(new SolidBorder(0.5f));

        for (int q = start; q <= end; q++) {
            Cell cell = new Cell()
                    .setPadding(5)
                    .setBorder(new SolidBorder(0.5f));

            // 题号
            Paragraph questionNo = new Paragraph(String.valueOf(q) + ".")
                    .setFont(font)
                    .setFontSize(10);
            cell.add(questionNo);

            // 选项气泡
            StringBuilder options = new StringBuilder();
            for (int i = 0; i < optionCount; i++) {
                char option = (char) ('A' + i);
                options.append("[ ").append(option).append(" ] ");
            }
            Paragraph optionPara = new Paragraph(options.toString())
                    .setFont(font)
                    .setFontSize(9);
            cell.add(optionPara);

            table.addCell(cell);
        }

        // 填充空单元格
        int remainder = totalQuestions % colCount;
        if (remainder > 0) {
            for (int i = 0; i < (colCount - remainder); i++) {
                table.addCell(new Cell().setBorder(new SolidBorder(0.5f)));
            }
        }

        document.add(table);

        if (hasMultipleChoice) {
            document.add(new Paragraph("注：多选题答案之间无需空格")
                    .setFont(font)
                    .setFontSize(8)
                    .setFontColor(new DeviceGray(0.5f)));
        }
    }

    private void renderFillBlankRegion(Document document, AnswerSheetRegionVO region, PdfFont font) {
        Map<String, Object> config = region.getConfig();
        int height = getConfigInt(config, "height", 24);
        boolean showBorder = getConfigBoolean(config, "showBorder", true);
        double totalScore = getConfigDouble(config, "totalScore", 0D);

        int start = region.getQuestionStart() != null ? region.getQuestionStart() : 1;
        int end = region.getQuestionEnd() != null ? region.getQuestionEnd() : start;
        int questionCount = Math.max(end - start + 1, 1);
        double scorePerQuestion = questionCount > 0 ? totalScore / questionCount : totalScore;

        for (int q = start; q <= end; q++) {
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{0.82f, 0.18f}))
                    .useAllAvailableWidth();

            Cell numCell = new Cell()
                    .add(new Paragraph(String.valueOf(q) + ".").setFont(font).setFontSize(11).setBold())
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);
            headerTable.addCell(numCell);

            String scoreText = scorePerQuestion > 0 ? formatScore(scorePerQuestion) + "分" : "填空";
            Cell scoreCell = new Cell()
                    .add(new Paragraph(scoreText)
                            .setFont(font)
                            .setFontSize(8)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(new SolidBorder(1))
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setPadding(4);
            headerTable.addCell(scoreCell);
            document.add(headerTable);

            Div answerArea = new Div()
                    .setHeight(Math.max(height, 18) * MM_TO_PT / 3)
                    .setMarginBottom(10)
                    .setBackgroundColor(ColorConstants.WHITE);

            if (showBorder) {
                answerArea.setBorder(new SolidBorder(0.6f));
            } else {
                answerArea.setBorderBottom(new SolidBorder(0.6f));
            }

            document.add(answerArea);
        }
    }

    private void renderAnswerRegion(Document document, AnswerSheetRegionVO region, PdfFont font) {
        Map<String, Object> config = region.getConfig();
        int height = config != null ? (int) config.getOrDefault("height", 100) : 100;
        boolean showBorder = config == null || Boolean.TRUE.equals(config.get("showBorder"));

        int start = region.getQuestionStart() != null ? region.getQuestionStart() : 1;
        int end = region.getQuestionEnd() != null ? region.getQuestionEnd() : start;
        int questionCount = end - start + 1;
        int heightPerQuestion = height / questionCount;

        for (int q = start; q <= end; q++) {
            // 题号和评分框
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{0.9f, 0.1f}))
                    .useAllAvailableWidth();

            Cell numCell = new Cell()
                    .add(new Paragraph(String.valueOf(q) + ".").setFont(font).setFontSize(11).setBold())
                    .setBorder(Border.NO_BORDER);
            headerTable.addCell(numCell);

            // 评分框
            Cell scoreCell = new Cell()
                    .add(new Paragraph("得分").setFont(font).setFontSize(8).setTextAlignment(TextAlignment.CENTER))
                    .setBorder(new SolidBorder(1))
                    .setWidth(40)
                    .setHeight(25);
            headerTable.addCell(scoreCell);

            document.add(headerTable);

            // 答题区域
            Div answerArea = new Div()
                    .setHeight(heightPerQuestion * MM_TO_PT / 2)
                    .setMarginBottom(10);

            if (showBorder) {
                answerArea.setBorder(new SolidBorder(0.5f));
            }

            document.add(answerArea);
        }
    }

    private void renderEssayRegion(Document document, AnswerSheetRegionVO region, PdfFont font) {
        Map<String, Object> config = region.getConfig();
        String gridType = config != null ? (String) config.getOrDefault("gridType", "square") : "square";
        int gridSize = config != null ? (int) config.getOrDefault("gridSize", 10) : 10;
        int wordCount = config != null ? (int) config.getOrDefault("wordCount", 800) : 800;

        // 计算网格
        float gridSizePt = gridSize * MM_TO_PT / 3;
        int colsPerRow = (int) (document.getPdfDocument().getDefaultPageSize().getWidth() * 0.85 / gridSizePt);
        int rows = (int) Math.ceil((double) wordCount / colsPerRow);

        // 创建作文格子
        float[] colWidths = new float[colsPerRow];
        for (int i = 0; i < colsPerRow; i++) {
            colWidths[i] = 1;
        }

        Table table = new Table(UnitValue.createPercentArray(colWidths))
                .useAllAvailableWidth();

        int cellCount = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < colsPerRow; c++) {
                if (cellCount >= wordCount) break;

                Cell cell = new Cell()
                        .setHeight(gridSizePt)
                        .setBorder(new SolidBorder(0.3f));

                // 每100字标记
                if ((cellCount + 1) % 100 == 0) {
                    cell.add(new Paragraph(String.valueOf(cellCount + 1))
                            .setFont(font)
                            .setFontSize(6)
                            .setFontColor(new DeviceGray(0.7f))
                            .setTextAlignment(TextAlignment.CENTER));
                }

                table.addCell(cell);
                cellCount++;
            }
        }

        // 填充空单元格到完整行
        int remainder = wordCount % colsPerRow;
        if (remainder > 0) {
            for (int i = 0; i < (colsPerRow - remainder); i++) {
                table.addCell(new Cell()
                        .setHeight(gridSizePt)
                        .setBorder(new SolidBorder(0.3f)));
            }
        }

        document.add(table);

        // 字数统计
        document.add(new Paragraph("（本题共" + wordCount + "格）")
                .setFont(font)
                .setFontSize(8)
                .setFontColor(new DeviceGray(0.5f))
                .setTextAlignment(TextAlignment.RIGHT));
    }

    private int getConfigInt(Map<String, Object> config, String key, int defaultValue) {
        if (config == null) {
            return defaultValue;
        }
        Object value = config.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        return defaultValue;
    }

    private double getConfigDouble(Map<String, Object> config, String key, double defaultValue) {
        if (config == null) {
            return defaultValue;
        }
        Object value = config.get(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return defaultValue;
    }

    private boolean getConfigBoolean(Map<String, Object> config, String key, boolean defaultValue) {
        if (config == null) {
            return defaultValue;
        }
        Object value = config.get(key);
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        return defaultValue;
    }

    private String formatScore(double score) {
        if (Math.abs(score - Math.rint(score)) < 0.001D) {
            return String.valueOf((int) Math.rint(score));
        }
        return String.format("%.1f", score);
    }
}
