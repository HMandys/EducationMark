package com.edumark.file.recognition.impl;

import com.edumark.answersheet.service.AnswerSheetTemplateService;
import com.edumark.answersheet.vo.AnswerSheetRegionVO;
import com.edumark.answersheet.vo.AnswerSheetTemplateVO;
import com.edumark.file.recognition.BarcodeRecognitionResult;
import com.edumark.file.recognition.BarcodeRecognitionService;
import com.edumark.file.recognition.ImageAlignmentService;
import com.edumark.file.recognition.RecognitionImageInput;
import com.edumark.file.service.FileService;
import com.edumark.exam.entity.Paper;
import com.edumark.exam.mapper.PaperMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 条码识别服务实现
 *
 * @author EduMark
 */
@Service
public class BarcodeRecognitionServiceImpl implements BarcodeRecognitionService {

    private static final Logger log = LoggerFactory.getLogger(BarcodeRecognitionServiceImpl.class);
    private static final List<Integer> ROTATION_ANGLES = List.of(0, 90, 180, 270);
    private static final Map<DecodeHintType, Object> DECODE_HINTS = createDecodeHints();

    @Resource
    private PaperMapper paperMapper;

    @Resource
    private AnswerSheetTemplateService answerSheetTemplateService;

    @Resource
    private FileService fileService;

    @Resource
    private ImageAlignmentService imageAlignmentService;

    @Override
    public BarcodeRecognitionResult recognize(Long examSubjectId, List<RecognitionImageInput> images) {
        if (examSubjectId == null || images == null || images.isEmpty()) {
            return BarcodeRecognitionResult.failure("未提供可识别的答题卡图片");
        }

        AnswerSheetTemplateVO template = answerSheetTemplateService.getByExamSubjectId(examSubjectId);
        if (template == null || template.getRegions() == null || template.getRegions().isEmpty()) {
            return BarcodeRecognitionResult.failure("当前考试未配置答题卡模板");
        }

        List<AnswerSheetRegionVO> barcodeRegions = template.getRegions().stream()
                .filter(this::isBarcodeRegion)
                .sorted(Comparator.comparing(region -> region.getPageNo() == null ? Integer.MAX_VALUE : region.getPageNo()))
                .toList();

        if (barcodeRegions.isEmpty()) {
            return BarcodeRecognitionResult.failure("模板未配置条码区");
        }

        Map<String, BufferedImage> imageCache = new HashMap<>();
        List<String> failureMessages = new ArrayList<>();

        for (AnswerSheetRegionVO barcodeRegion : barcodeRegions) {
            RecognitionImageInput imageInput = pickImageForRegion(images, barcodeRegion.getPageNo());
            if (imageInput == null || imageInput.getObjectName() == null || imageInput.getObjectName().isBlank()) {
                failureMessages.add(buildRegionLabel(barcodeRegion) + "未找到对应页图片");
                continue;
            }

            BufferedImage pageImage = readImage(imageInput, imageCache);
            if (pageImage == null) {
                failureMessages.add(buildRegionLabel(barcodeRegion) + "图片无法读取");
                continue;
            }

            BufferedImage roi = cropRegion(pageImage, barcodeRegion);
            DetectionAttempt attempt = detectBarcode(roi);
            if (attempt != null && attempt.result() != null && attempt.result().getText() != null && !attempt.result().getText().isBlank()) {
                String message = String.format(
                        "Code128条码识别成功，第%s页%s%s",
                        imageInput.getPageNum() == null ? "?" : imageInput.getPageNum(),
                        barcodeRegion.getRegionName() == null ? "" : " / " + barcodeRegion.getRegionName(),
                        attempt.aligned() ? "，已完成四角对齐矫正" : ""
                );
                return BarcodeRecognitionResult.success(
                        attempt.result().getText().trim(),
                        attempt.aligned(),
                        imageInput.getPageNum(),
                        barcodeRegion.getRegionName(),
                        message
                );
            }

            failureMessages.add(buildRegionLabel(barcodeRegion) + "未识别到Code128条码");
        }

        return BarcodeRecognitionResult.failure(String.join("；", failureMessages));
    }

    private boolean isBarcodeRegion(AnswerSheetRegionVO region) {
        if (region == null || region.getConfig() == null) {
            return false;
        }
        Object roleValue = region.getConfig().get("regionRole");
        return "barcode".equals(roleValue)
                && getPercent(region.getConfig(), "boxX") != null
                && getPercent(region.getConfig(), "boxY") != null
                && getPercent(region.getConfig(), "boxWidth") != null
                && getPercent(region.getConfig(), "boxHeight") != null;
    }

    private RecognitionImageInput pickImageForRegion(List<RecognitionImageInput> images, Integer pageNo) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        if (pageNo != null) {
            for (RecognitionImageInput image : images) {
                if (pageNo.equals(image.getPageNum())) {
                    return image;
                }
            }
        }
        return images.get(0);
    }

    private BufferedImage readImage(RecognitionImageInput imageInput, Map<String, BufferedImage> imageCache) {
        if (imageCache.containsKey(imageInput.getObjectName())) {
            return imageCache.get(imageInput.getObjectName());
        }

        try (InputStream inputStream = fileService.getFileStream(imageInput.getObjectName())) {
            BufferedImage image = ImageIO.read(inputStream);
            imageCache.put(imageInput.getObjectName(), image);
            return image;
        } catch (Exception ex) {
            log.warn("读取答题卡图片失败: {}", imageInput.getObjectName(), ex);
            return null;
        }
    }

    private BufferedImage cropRegion(BufferedImage image, AnswerSheetRegionVO region) {
        double boxX = getPercent(region.getConfig(), "boxX");
        double boxY = getPercent(region.getConfig(), "boxY");
        double boxWidth = getPercent(region.getConfig(), "boxWidth");
        double boxHeight = getPercent(region.getConfig(), "boxHeight");

        int left = clamp((int) Math.floor(image.getWidth() * (boxX / 100D)), 0, image.getWidth() - 1);
        int top = clamp((int) Math.floor(image.getHeight() * (boxY / 100D)), 0, image.getHeight() - 1);
        int width = Math.max(1, (int) Math.ceil(image.getWidth() * (boxWidth / 100D)));
        int height = Math.max(1, (int) Math.ceil(image.getHeight() * (boxHeight / 100D)));

        int paddedLeft = clamp(left - Math.max(8, width / 25), 0, image.getWidth() - 1);
        int paddedTop = clamp(top - Math.max(8, height / 25), 0, image.getHeight() - 1);
        int paddedRight = clamp(left + width + Math.max(8, width / 25), paddedLeft + 1, image.getWidth());
        int paddedBottom = clamp(top + height + Math.max(8, height / 25), paddedTop + 1, image.getHeight());

        return image.getSubimage(paddedLeft, paddedTop, paddedRight - paddedLeft, paddedBottom - paddedTop);
    }

    private DetectionAttempt detectBarcode(BufferedImage roi) {
        for (int angle : ROTATION_ANGLES) {
            BufferedImage rotated = angle == 0 ? roi : imageAlignmentService.rotate(roi, angle);
            DetectionAttempt directAttempt = decodeWithVariants(rotated, false);
            if (directAttempt != null) {
                ResultPoint[] points = directAttempt.result().getResultPoints();
                if (points != null && points.length >= 2) {
                    BufferedImage aligned = imageAlignmentService.alignByBarcode(rotated, points);
                    DetectionAttempt alignedAttempt = decodeWithVariants(aligned, true);
                    if (alignedAttempt != null) {
                        return alignedAttempt;
                    }
                }
                return directAttempt;
            }
        }
        return null;
    }

    private DetectionAttempt decodeWithVariants(BufferedImage image, boolean aligned) {
        List<BufferedImage> variants = List.of(
                image,
                toGray(image),
                enhanceContrast(image),
                scale(image, 2.0D),
                scale(enhanceContrast(image), 2.0D)
        );

        for (BufferedImage variant : variants) {
            Result result = tryDecode(variant, true);
            if (result != null) {
                return new DetectionAttempt(result, aligned);
            }
            result = tryDecode(variant, false);
            if (result != null) {
                return new DetectionAttempt(result, aligned);
            }
        }
        return null;
    }

    private Result tryDecode(BufferedImage image, boolean useHybrid) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(useHybrid
                    ? new HybridBinarizer(source)
                    : new GlobalHistogramBinarizer(source));
            return new MultiFormatReader().decode(bitmap, DECODE_HINTS);
        } catch (NotFoundException ex) {
            return null;
        }
    }

    private BufferedImage toGray(BufferedImage source) {
        BufferedImage gray = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = gray.createGraphics();
        graphics.drawImage(source, 0, 0, null);
        graphics.dispose();
        return gray;
    }

    private BufferedImage enhanceContrast(BufferedImage source) {
        BufferedImage gray = toGray(source);
        RescaleOp op = new RescaleOp(1.35F, -18F, null);
        BufferedImage output = new BufferedImage(gray.getWidth(), gray.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        op.filter(gray, output);
        return output;
    }

    private BufferedImage scale(BufferedImage source, double scale) {
        int width = Math.max(1, (int) Math.round(source.getWidth() * scale));
        int height = Math.max(1, (int) Math.round(source.getHeight() * scale));
        BufferedImage scaled = new BufferedImage(width, height, source.getType() == 0 ? BufferedImage.TYPE_INT_RGB : source.getType());
        Graphics2D graphics = scaled.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.drawImage(source, 0, 0, width, height, null);
        graphics.dispose();
        return scaled;
    }

    private Double getPercent(Map<String, Object> config, String key) {
        if (config == null) {
            return null;
        }
        Object value = config.get(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof String str && !str.isBlank()) {
            try {
                return Double.parseDouble(str.trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private String buildRegionLabel(AnswerSheetRegionVO region) {
        String pageLabel = region.getPageNo() == null ? "未知页" : "第" + region.getPageNo() + "页";
        String nameLabel = region.getRegionName() == null || region.getRegionName().isBlank() ? "条码区" : region.getRegionName();
        return pageLabel + " / " + nameLabel;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private static Map<DecodeHintType, Object> createDecodeHints() {
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.of(BarcodeFormat.CODE_128));
        return hints;
    }

    private record DetectionAttempt(Result result, boolean aligned) {
    }
}
