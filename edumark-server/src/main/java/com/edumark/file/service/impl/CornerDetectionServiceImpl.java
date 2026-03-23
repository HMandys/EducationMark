package com.edumark.file.service.impl;

import com.edumark.common.exception.BusinessException;
import com.edumark.file.service.CornerDetectionService;
import com.edumark.file.service.FileService;
import com.edumark.file.vo.CornerDetectionResultVO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 四角定位服务实现
 * 使用连通域分析检测黑色实心方块定位点
 *
 * @author EduMark
 */
@Service
public class CornerDetectionServiceImpl implements CornerDetectionService {

    private static final Logger log = LoggerFactory.getLogger(CornerDetectionServiceImpl.class);

    // 四角搜索区域占图片的比例
    private static final double CORNER_SEARCH_RATIO = 0.15;

    // 定位点尺寸范围(像素)
    private static final int MIN_MARKER_SIZE = 15;
    private static final int MAX_MARKER_SIZE = 120;

    // 方形度阈值(宽高比)
    private static final double MIN_ASPECT_RATIO = 0.6;
    private static final double MAX_ASPECT_RATIO = 1.67;

    // 填充率阈值
    private static final double MIN_FILL_RATIO = 0.5;

    @Resource
    private FileService fileService;

    @Override
    public CornerDetectionResultVO detectCorners(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                return CornerDetectionResultVO.fail("无法读取图片文件");
            }
            return detectCorners(image);
        } catch (Exception e) {
            log.error("四角检测失败", e);
            return CornerDetectionResultVO.fail("四角检测失败: " + e.getMessage());
        }
    }

    @Override
    public CornerDetectionResultVO detectCorners(String imagePath) {
        try (InputStream inputStream = fileService.getFileStream(imagePath)) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                return CornerDetectionResultVO.fail("无法读取图片文件");
            }
            return detectCorners(image);
        } catch (Exception e) {
            log.error("四角检测失败: {}", imagePath, e);
            return CornerDetectionResultVO.fail("四角检测失败: " + e.getMessage());
        }
    }

    @Override
    public CornerDetectionResultVO detectCorners(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // 转灰度并计算OTSU阈值
        int[][] grayValues = toGrayScale(image);
        int threshold = computeOtsuThreshold(grayValues, width, height);

        // 在四个角落区域搜索定位点
        int searchWidth = (int) (width * CORNER_SEARCH_RATIO);
        int searchHeight = (int) (height * CORNER_SEARCH_RATIO);

        // 检测四个角落
        DetectedMarker topLeft = findMarkerInRegion(grayValues, 0, 0, searchWidth, searchHeight, threshold, width, height);
        DetectedMarker topRight = findMarkerInRegion(grayValues, width - searchWidth, 0, searchWidth, searchHeight, threshold, width, height);
        DetectedMarker bottomLeft = findMarkerInRegion(grayValues, 0, height - searchHeight, searchWidth, searchHeight, threshold, width, height);
        DetectedMarker bottomRight = findMarkerInRegion(grayValues, width - searchWidth, height - searchHeight, searchWidth, searchHeight, threshold, width, height);

        // 如果检测不到，使用默认角落位置
        if (topLeft == null) {
            topLeft = new DetectedMarker(searchWidth / 2.0, searchHeight / 2.0, 20, 20);
        }
        if (topRight == null) {
            topRight = new DetectedMarker(width - searchWidth / 2.0, searchHeight / 2.0, 20, 20);
        }
        if (bottomLeft == null) {
            bottomLeft = new DetectedMarker(searchWidth / 2.0, height - searchHeight / 2.0, 20, 20);
        }
        if (bottomRight == null) {
            bottomRight = new DetectedMarker(width - searchWidth / 2.0, height - searchHeight / 2.0, 20, 20);
        }

        // 计算倾斜角度
        double angle = calculateAngle(topLeft, topRight);

        // 转换为百分比坐标
        return CornerDetectionResultVO.success(
                topLeft.centerX / width * 100,
                topLeft.centerY / height * 100,
                topRight.centerX / width * 100,
                topRight.centerY / height * 100,
                bottomLeft.centerX / width * 100,
                bottomLeft.centerY / height * 100,
                bottomRight.centerX / width * 100,
                bottomRight.centerY / height * 100,
                angle,
                width,
                height
        );
    }

    @Override
    public String correctImage(String imagePath, Map<String, Object> cornerConfig) {
        try (InputStream inputStream = fileService.getFileStream(imagePath)) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                throw new BusinessException("无法读取图片文件");
            }

            // 获取角度
            Double angle = getDoubleValue(cornerConfig, "angle");
            if (angle == null || Math.abs(angle) < 0.1) {
                // 角度太小，不需要矫正
                return imagePath;
            }

            // 执行旋转矫正
            BufferedImage corrected = rotateImage(image, -angle);

            // 保存矫正后的图片
            String correctedPath = imagePath.replace(".", "_corrected.");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(corrected, "png", outputStream);
            fileService.uploadBytes(outputStream.toByteArray(), correctedPath, "image/png");

            return correctedPath;
        } catch (Exception e) {
            log.error("图片矫正失败: {}", imagePath, e);
            throw new BusinessException("图片矫正失败: " + e.getMessage());
        }
    }

    private int[][] toGrayScale(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] gray = new int[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                gray[y][x] = (int) (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
            }
        }
        return gray;
    }

    private int computeOtsuThreshold(int[][] grayValues, int width, int height) {
        int[] histogram = new int[256];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                histogram[grayValues[y][x]]++;
            }
        }

        int total = width * height;
        double sum = 0;
        for (int i = 0; i < 256; i++) {
            sum += i * histogram[i];
        }

        double sumB = 0;
        int weightB = 0;
        double maxVariance = 0;
        int threshold = 127;

        for (int i = 0; i < 256; i++) {
            weightB += histogram[i];
            if (weightB == 0) continue;

            int weightF = total - weightB;
            if (weightF == 0) break;

            sumB += i * histogram[i];
            double meanB = sumB / weightB;
            double meanF = (sum - sumB) / weightF;
            double betweenVariance = (double) weightB * weightF * Math.pow(meanB - meanF, 2);

            if (betweenVariance > maxVariance) {
                maxVariance = betweenVariance;
                threshold = i;
            }
        }

        return threshold;
    }

    private DetectedMarker findMarkerInRegion(int[][] grayValues, int startX, int startY,
                                               int regionWidth, int regionHeight,
                                               int threshold, int imageWidth, int imageHeight) {
        // 在区域内使用连通域分析找黑色方块
        boolean[][] visited = new boolean[regionHeight][regionWidth];
        List<DetectedMarker> candidates = new ArrayList<>();

        for (int y = 0; y < regionHeight; y++) {
            for (int x = 0; x < regionWidth; x++) {
                int globalY = startY + y;
                int globalX = startX + x;

                if (globalY >= imageHeight || globalX >= imageWidth) continue;
                if (visited[y][x]) continue;
                if (grayValues[globalY][globalX] > threshold) continue;

                // BFS找连通域
                List<int[]> component = new ArrayList<>();
                List<int[]> queue = new ArrayList<>();
                queue.add(new int[]{x, y});
                visited[y][x] = true;

                while (!queue.isEmpty()) {
                    int[] current = queue.remove(0);
                    component.add(current);

                    int[][] neighbors = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                    for (int[] neighbor : neighbors) {
                        int nx = current[0] + neighbor[0];
                        int ny = current[1] + neighbor[1];

                        if (nx < 0 || nx >= regionWidth || ny < 0 || ny >= regionHeight) continue;
                        if (visited[ny][nx]) continue;

                        int gx = startX + nx;
                        int gy = startY + ny;
                        if (gx >= imageWidth || gy >= imageHeight) continue;
                        if (grayValues[gy][gx] > threshold) continue;

                        visited[ny][nx] = true;
                        queue.add(new int[]{nx, ny});
                    }
                }

                // 分析连通域
                if (component.size() < MIN_MARKER_SIZE * MIN_MARKER_SIZE / 4) continue;

                int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
                int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
                for (int[] point : component) {
                    minX = Math.min(minX, point[0]);
                    maxX = Math.max(maxX, point[0]);
                    minY = Math.min(minY, point[1]);
                    maxY = Math.max(maxY, point[1]);
                }

                int compWidth = maxX - minX + 1;
                int compHeight = maxY - minY + 1;

                // 检查尺寸
                if (compWidth < MIN_MARKER_SIZE || compHeight < MIN_MARKER_SIZE) continue;
                if (compWidth > MAX_MARKER_SIZE || compHeight > MAX_MARKER_SIZE) continue;

                // 检查宽高比
                double aspectRatio = (double) compWidth / compHeight;
                if (aspectRatio < MIN_ASPECT_RATIO || aspectRatio > MAX_ASPECT_RATIO) continue;

                // 检查填充率
                double fillRatio = (double) component.size() / (compWidth * compHeight);
                if (fillRatio < MIN_FILL_RATIO) continue;

                // 计算中心点(全局坐标)
                double centerX = startX + (minX + maxX) / 2.0;
                double centerY = startY + (minY + maxY) / 2.0;

                candidates.add(new DetectedMarker(centerX, centerY, compWidth, compHeight));
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        // 选择最靠近角落且面积最大的
        candidates.sort(Comparator.comparingDouble((DetectedMarker m) -> m.width * m.height).reversed());
        return candidates.get(0);
    }

    private double calculateAngle(DetectedMarker left, DetectedMarker right) {
        double deltaY = right.centerY - left.centerY;
        double deltaX = right.centerX - left.centerX;
        return Math.toDegrees(Math.atan2(deltaY, deltaX));
    }

    private BufferedImage rotateImage(BufferedImage image, double angleDegrees) {
        double angleRadians = Math.toRadians(angleDegrees);
        int width = image.getWidth();
        int height = image.getHeight();

        double cos = Math.abs(Math.cos(angleRadians));
        double sin = Math.abs(Math.sin(angleRadians));
        int newWidth = (int) Math.floor(width * cos + height * sin);
        int newHeight = (int) Math.floor(height * cos + width * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = rotated.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, newWidth, newHeight);

        AffineTransform transform = new AffineTransform();
        transform.translate((newWidth - width) / 2.0, (newHeight - height) / 2.0);
        transform.rotate(angleRadians, width / 2.0, height / 2.0);

        graphics.setTransform(transform);
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        return rotated;
    }

    private Double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof String string && !string.isBlank()) {
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private static class DetectedMarker {
        final double centerX;
        final double centerY;
        final int width;
        final int height;

        DetectedMarker(double centerX, double centerY, int width, int height) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.width = width;
            this.height = height;
        }
    }
}
