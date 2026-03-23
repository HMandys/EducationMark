package com.edumark.file.service.impl;

import com.edumark.file.service.BubbleDetectionService;
import com.edumark.file.service.FileService;
import com.edumark.file.vo.BubbleDetectionResultVO;
import com.edumark.file.vo.BubbleDetectionResultVO.BubbleItem;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 气泡检测服务实现
 * 使用连通域分析检测选项气泡，聚类成行后按X坐标排序分配选项
 *
 * @author EduMark
 */
@Service
public class BubbleDetectionServiceImpl implements BubbleDetectionService {

    private static final Logger log = LoggerFactory.getLogger(BubbleDetectionServiceImpl.class);

    // 气泡尺寸约束(相对于裁切区域)
    private static final double MIN_BUBBLE_RATIO = 0.008;
    private static final double MAX_BUBBLE_RATIO = 0.12;

    // 宽高比约束
    private static final double MIN_ASPECT_RATIO = 0.45;
    private static final double MAX_ASPECT_RATIO = 1.9;

    // 填充率约束
    private static final double MIN_FILL_RATIO = 0.12;
    private static final double MAX_FILL_RATIO = 0.85;

    @Resource
    private FileService fileService;

    @Override
    public BubbleDetectionResultVO detectBubbles(
            MultipartFile file,
            double boxX, double boxY, double boxWidth, double boxHeight,
            int questionStart, int questionEnd,
            int optionCount, int questionsPerRow) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                return BubbleDetectionResultVO.fail("无法读取图片文件");
            }
            return detectBubbles(image, boxX, boxY, boxWidth, boxHeight,
                    questionStart, questionEnd, optionCount, questionsPerRow);
        } catch (Exception e) {
            log.error("气泡检测失败", e);
            return BubbleDetectionResultVO.fail("气泡检测失败: " + e.getMessage());
        }
    }

    @Override
    public BubbleDetectionResultVO detectBubbles(
            String imagePath,
            double boxX, double boxY, double boxWidth, double boxHeight,
            int questionStart, int questionEnd,
            int optionCount, int questionsPerRow) {
        try (InputStream inputStream = fileService.getFileStream(imagePath)) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                return BubbleDetectionResultVO.fail("无法读取图片文件");
            }
            return detectBubbles(image, boxX, boxY, boxWidth, boxHeight,
                    questionStart, questionEnd, optionCount, questionsPerRow);
        } catch (Exception e) {
            log.error("气泡检测失败: {}", imagePath, e);
            return BubbleDetectionResultVO.fail("气泡检测失败: " + e.getMessage());
        }
    }

    @Override
    public BubbleDetectionResultVO detectBubbles(
            BufferedImage image,
            double boxX, double boxY, double boxWidth, double boxHeight,
            int questionStart, int questionEnd,
            int optionCount, int questionsPerRow) {

        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();

        // 裁切区域
        int cropX = Math.max((int) (imgWidth * boxX / 100), 0);
        int cropY = Math.max((int) (imgHeight * boxY / 100), 0);
        int cropWidth = Math.max((int) (imgWidth * boxWidth / 100), 1);
        int cropHeight = Math.max((int) (imgHeight * boxHeight / 100), 1);

        // 边界检查
        cropWidth = Math.min(cropWidth, imgWidth - cropX);
        cropHeight = Math.min(cropHeight, imgHeight - cropY);

        // 转灰度
        int[][] grayValues = toGrayScale(image, cropX, cropY, cropWidth, cropHeight);

        // 计算OTSU阈值
        int threshold = computeOtsuThreshold(grayValues, cropWidth, cropHeight);
        threshold = Math.min(220, (int) (threshold * 0.92));

        // 连通域分析
        List<DetectedComponent> components = detectConnectedComponents(grayValues, cropWidth, cropHeight, threshold);

        // 计算预期数量
        int questionCount = Math.max(questionEnd - questionStart + 1, 0);
        int expectedCount = questionCount * optionCount;

        if (components.isEmpty()) {
            return BubbleDetectionResultVO.fail("没有检测到可用的选项气泡");
        }

        // 过滤候选气泡
        double minSize = Math.min(cropWidth, cropHeight) * MIN_BUBBLE_RATIO;
        double maxSize = Math.max(cropWidth, cropHeight) * MAX_BUBBLE_RATIO;

        List<DetectedComponent> candidates = new ArrayList<>();
        for (DetectedComponent comp : components) {
            if (comp.width < minSize || comp.height < minSize) continue;
            if (comp.width > maxSize || comp.height > maxSize) continue;

            double aspectRatio = (double) comp.width / comp.height;
            if (aspectRatio < MIN_ASPECT_RATIO || aspectRatio > MAX_ASPECT_RATIO) continue;

            double fillRatio = (double) comp.area / (comp.width * comp.height);
            if (fillRatio < MIN_FILL_RATIO || fillRatio > MAX_FILL_RATIO) continue;

            candidates.add(comp);
        }

        if (candidates.isEmpty()) {
            return BubbleDetectionResultVO.fail("过滤后没有有效的气泡候选");
        }

        // 获取典型尺寸
        candidates.sort(Comparator.comparingInt((DetectedComponent c) -> c.area).reversed());
        List<DetectedComponent> sampleCandidates = candidates.subList(0, Math.min(candidates.size(), Math.max(expectedCount * 2, 20)));
        double medianWidth = median(sampleCandidates.stream().mapToDouble(c -> c.width).toArray());
        double medianHeight = median(sampleCandidates.stream().mapToDouble(c -> c.height).toArray());

        // 再次过滤，只保留尺寸相近的
        List<DetectedComponent> finalCandidates = new ArrayList<>();
        for (DetectedComponent comp : candidates) {
            if (comp.width >= medianWidth * 0.55 && comp.width <= medianWidth * 1.8 &&
                comp.height >= medianHeight * 0.55 && comp.height <= medianHeight * 1.8) {
                finalCandidates.add(comp);
            }
        }

        // 按Y坐标聚类成行
        int expectedRowCount = (int) Math.ceil((double) questionCount / questionsPerRow);
        List<List<DetectedComponent>> rows = clusterRows(finalCandidates, medianHeight * 1.2);

        // 合并行数过多的情况
        while (rows.size() > expectedRowCount && rows.size() > 1) {
            int mergeIndex = 0;
            double minDistance = Double.MAX_VALUE;
            for (int i = 0; i < rows.size() - 1; i++) {
                double currentY = rows.get(i).stream().mapToDouble(c -> c.centerY).average().orElse(0);
                double nextY = rows.get(i + 1).stream().mapToDouble(c -> c.centerY).average().orElse(0);
                double distance = Math.abs(nextY - currentY);
                if (distance < minDistance) {
                    minDistance = distance;
                    mergeIndex = i;
                }
            }
            List<DetectedComponent> merged = new ArrayList<>(rows.get(mergeIndex));
            merged.addAll(rows.get(mergeIndex + 1));
            merged.sort(Comparator.comparingDouble(c -> c.centerX));
            rows.remove(mergeIndex + 1);
            rows.remove(mergeIndex);
            rows.add(mergeIndex, merged);
        }

        // 生成bubbleMap
        List<BubbleItem> bubbleMap = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            int questionsInRow = Math.min(questionsPerRow, questionCount - rowIndex * questionsPerRow);
            if (questionsInRow <= 0) break;

            int expectedRowBubbleCount = questionsInRow * optionCount;
            List<DetectedComponent> rowComponents = pickBestWindow(rows.get(rowIndex), expectedRowBubbleCount);

            if (rowComponents.size() < expectedRowBubbleCount) {
                continue;
            }

            for (int questionOffset = 0; questionOffset < questionsInRow; questionOffset++) {
                int questionNo = questionStart + rowIndex * questionsPerRow + questionOffset;
                List<DetectedComponent> optionComponents = rowComponents.subList(
                        questionOffset * optionCount,
                        Math.min((questionOffset + 1) * optionCount, rowComponents.size())
                );

                for (int optionIndex = 0; optionIndex < optionComponents.size(); optionIndex++) {
                    DetectedComponent comp = optionComponents.get(optionIndex);
                    String option = String.valueOf((char) ('A' + optionIndex));

                    // 转换为相对于原图的百分比坐标
                    double x = boxX + (comp.x * 1.0 / cropWidth) * boxWidth;
                    double y = boxY + (comp.y * 1.0 / cropHeight) * boxHeight;
                    double w = (comp.width * 1.0 / cropWidth) * boxWidth;
                    double h = (comp.height * 1.0 / cropHeight) * boxHeight;
                    double confidence = comp.area / (medianWidth * medianHeight);

                    bubbleMap.add(new BubbleItem(questionNo, option, x, y, w, h, Math.min(confidence, 2.0)));
                }
            }
        }

        BubbleDetectionResultVO result = BubbleDetectionResultVO.success(bubbleMap, expectedCount);
        result.setRowCount(rows.size());
        result.setBubblesPerRow(questionsPerRow * optionCount);
        return result;
    }

    private int[][] toGrayScale(BufferedImage image, int startX, int startY, int width, int height) {
        int[][] gray = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(startX + x, startY + y));
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

    private List<DetectedComponent> detectConnectedComponents(int[][] grayValues, int width, int height, int threshold) {
        boolean[][] visited = new boolean[height][width];
        List<DetectedComponent> components = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (visited[y][x] || grayValues[y][x] > threshold) {
                    continue;
                }

                // BFS
                List<int[]> queue = new ArrayList<>();
                List<int[]> points = new ArrayList<>();
                queue.add(new int[]{x, y});
                visited[y][x] = true;

                int minX = x, maxX = x, minY = y, maxY = y;

                while (!queue.isEmpty()) {
                    int[] current = queue.remove(0);
                    points.add(current);
                    minX = Math.min(minX, current[0]);
                    maxX = Math.max(maxX, current[0]);
                    minY = Math.min(minY, current[1]);
                    maxY = Math.max(maxY, current[1]);

                    int[][] neighbors = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                    for (int[] neighbor : neighbors) {
                        int nx = current[0] + neighbor[0];
                        int ny = current[1] + neighbor[1];

                        if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;
                        if (visited[ny][nx] || grayValues[ny][nx] > threshold) continue;

                        visited[ny][nx] = true;
                        queue.add(new int[]{nx, ny});
                    }
                }

                int compWidth = maxX - minX + 1;
                int compHeight = maxY - minY + 1;
                components.add(new DetectedComponent(
                        minX, minY, compWidth, compHeight, points.size(),
                        minX + compWidth / 2.0, minY + compHeight / 2.0
                ));
            }
        }

        return components;
    }

    private List<List<DetectedComponent>> clusterRows(List<DetectedComponent> components, double rowTolerance) {
        List<List<DetectedComponent>> rows = new ArrayList<>();
        List<DetectedComponent> sorted = new ArrayList<>(components);
        sorted.sort(Comparator.comparingDouble(c -> c.centerY));

        for (DetectedComponent comp : sorted) {
            if (rows.isEmpty()) {
                List<DetectedComponent> newRow = new ArrayList<>();
                newRow.add(comp);
                rows.add(newRow);
                continue;
            }

            List<DetectedComponent> lastRow = rows.get(rows.size() - 1);
            double avgY = lastRow.stream().mapToDouble(c -> c.centerY).average().orElse(0);
            if (Math.abs(comp.centerY - avgY) <= rowTolerance) {
                lastRow.add(comp);
            } else {
                List<DetectedComponent> newRow = new ArrayList<>();
                newRow.add(comp);
                rows.add(newRow);
            }
        }

        // 每行按X坐标排序
        for (List<DetectedComponent> row : rows) {
            row.sort(Comparator.comparingDouble(c -> c.centerX));
        }

        return rows;
    }

    private List<DetectedComponent> pickBestWindow(List<DetectedComponent> row, int expectedCount) {
        if (row.size() <= expectedCount) {
            return new ArrayList<>(row);
        }

        List<DetectedComponent> bestWindow = row.subList(0, expectedCount);
        double bestScore = Double.NEGATIVE_INFINITY;

        for (int start = 0; start <= row.size() - expectedCount; start++) {
            List<DetectedComponent> window = row.subList(start, start + expectedCount);
            double[] gaps = new double[window.size() - 1];
            for (int i = 0; i < gaps.length; i++) {
                gaps[i] = window.get(i + 1).centerX - window.get(i).centerX;
            }
            double gapMedian = median(gaps);
            double gapVariance = 0;
            for (double gap : gaps) {
                gapVariance += Math.abs(gap - gapMedian);
            }
            double areaScore = window.stream().mapToInt(c -> c.area).sum();
            double score = areaScore - gapVariance * 4;

            if (score > bestScore) {
                bestScore = score;
                bestWindow = new ArrayList<>(window);
            }
        }

        return bestWindow;
    }

    private double median(double[] values) {
        if (values.length == 0) return 0;
        double[] sorted = values.clone();
        java.util.Arrays.sort(sorted);
        int mid = sorted.length / 2;
        return sorted.length % 2 == 0 ? (sorted[mid - 1] + sorted[mid]) / 2 : sorted[mid];
    }

    private static class DetectedComponent {
        final int x;
        final int y;
        final int width;
        final int height;
        final int area;
        final double centerX;
        final double centerY;

        DetectedComponent(int x, int y, int width, int height, int area, double centerX, double centerY) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.area = area;
            this.centerX = centerX;
            this.centerY = centerY;
        }
    }
}
