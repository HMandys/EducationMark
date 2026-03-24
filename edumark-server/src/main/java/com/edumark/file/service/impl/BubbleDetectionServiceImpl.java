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
 * 支持两种样式：实心气泡（○●）和空心方框（[A][B][C][D]）
 *
 * @author EduMark
 */
@Service
public class BubbleDetectionServiceImpl implements BubbleDetectionService {

    private static final Logger log = LoggerFactory.getLogger(BubbleDetectionServiceImpl.class);

    // 气泡尺寸约束(相对于裁切区域)
    private static final double MIN_BUBBLE_RATIO = 0.005;  // 放宽最小尺寸
    private static final double MAX_BUBBLE_RATIO = 0.15;   // 放宽最大尺寸

    // 宽高比约束（放宽以支持方框样式）
    private static final double MIN_ASPECT_RATIO = 0.35;
    private static final double MAX_ASPECT_RATIO = 2.8;

    // 填充率约束（大幅降低以支持空心方框）
    private static final double MIN_FILL_RATIO = 0.03;     // 空心方框填充率很低
    private static final double MAX_FILL_RATIO = 0.95;

    @Resource
    private FileService fileService;

    @Override
    public BubbleDetectionResultVO detectBubbles(
            MultipartFile file,
            double boxX, double boxY, double boxWidth, double boxHeight,
            int questionStart, int questionEnd,
            int optionCount, int questionsPerRow, String layoutDirection) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                return BubbleDetectionResultVO.fail("无法读取图片文件");
            }
            return detectBubbles(image, boxX, boxY, boxWidth, boxHeight,
                    questionStart, questionEnd, optionCount, questionsPerRow, layoutDirection);
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
            int optionCount, int questionsPerRow, String layoutDirection) {
        try (InputStream inputStream = fileService.getFileStream(imagePath)) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                return BubbleDetectionResultVO.fail("无法读取图片文件");
            }
            return detectBubbles(image, boxX, boxY, boxWidth, boxHeight,
                    questionStart, questionEnd, optionCount, questionsPerRow, layoutDirection);
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
            int optionCount, int questionsPerRow, String layoutDirection) {

        int questionCount = Math.max(questionEnd - questionStart + 1, 0);
        int expectedCount = questionCount * optionCount;
        boolean isColumnLayout = "column".equalsIgnoreCase(layoutDirection);

        log.info("气泡检测: 题目 {}-{}, 共 {} 题, 每题 {} 选项, 每组 {} 题, 布局: {}",
                questionStart, questionEnd, questionCount, optionCount, questionsPerRow,
                isColumnLayout ? "纵向" : "横向");

        // 使用基于网格的检测方法 - 更可靠
        // 用户已经拉框指定了区域，根据题目数量和布局均匀划分
        List<BubbleItem> bubbleMap = generateGridBasedBubbleMap(
                boxX, boxY, boxWidth, boxHeight,
                questionStart, questionEnd,
                optionCount, questionsPerRow, isColumnLayout
        );

        log.info("网格检测生成 {} 个选项位置", bubbleMap.size());

        BubbleDetectionResultVO result = BubbleDetectionResultVO.success(bubbleMap, expectedCount);
        int rowCount = isColumnLayout ? questionsPerRow : (int) Math.ceil((double) questionCount / questionsPerRow);
        result.setRowCount(rowCount);
        result.setBubblesPerRow(isColumnLayout ? optionCount : questionsPerRow * optionCount);
        return result;
    }

    /**
     * 基于网格的气泡位置生成
     * 根据用户指定的区域和题目信息，均匀划分网格生成选项位置
     * 直接将区域均匀划分，每个选项占据一个小格子
     *
     * @param isColumnLayout true=纵向布局(1,2,3竖着排), false=横向布局(1,2,3横着排)
     */
    private List<BubbleItem> generateGridBasedBubbleMap(
            double boxX, double boxY, double boxWidth, double boxHeight,
            int questionStart, int questionEnd,
            int optionCount, int questionsPerGroup, boolean isColumnLayout) {

        List<BubbleItem> bubbleMap = new ArrayList<>();
        int questionCount = questionEnd - questionStart + 1;

        if (isColumnLayout) {
            // 纵向布局：题目从上到下排列，然后换列
            // questionsPerGroup 表示每列有多少题
            int columnCount = (int) Math.ceil((double) questionCount / questionsPerGroup);
            int rowCount = Math.min(questionsPerGroup, questionCount);

            // 总共有多少列格子（每题有optionCount个选项）
            int totalGridCols = columnCount * optionCount;
            int totalGridRows = rowCount;

            // 每个格子的尺寸
            double cellWidth = boxWidth / totalGridCols;
            double cellHeight = boxHeight / totalGridRows;

            // 选项框尺寸（格子内留边距）
            double optionWidth = cellWidth * 0.85;
            double optionHeight = cellHeight * 0.7;
            double horizontalPadding = cellWidth * 0.075;
            double verticalPadding = cellHeight * 0.15;

            for (int questionIndex = 0; questionIndex < questionCount; questionIndex++) {
                int questionNo = questionStart + questionIndex;
                // 计算该题在哪一列组、哪一行
                int colGroupIndex = questionIndex / questionsPerGroup;
                int rowIndex = questionIndex % questionsPerGroup;

                for (int optionIndex = 0; optionIndex < optionCount; optionIndex++) {
                    String option = String.valueOf((char) ('A' + optionIndex));

                    // 计算格子位置
                    int gridCol = colGroupIndex * optionCount + optionIndex;

                    double x = boxX + gridCol * cellWidth + horizontalPadding;
                    double y = boxY + rowIndex * cellHeight + verticalPadding;

                    bubbleMap.add(new BubbleItem(questionNo, option, x, y, optionWidth, optionHeight, 1.0));
                }
            }
        } else {
            // 横向布局：题目从左到右排列，然后换行
            // questionsPerGroup 表示每行有多少题
            int rowCount = (int) Math.ceil((double) questionCount / questionsPerGroup);

            // 总共有多少列格子
            int totalGridCols = questionsPerGroup * optionCount;
            int totalGridRows = rowCount;

            // 每个格子的尺寸
            double cellWidth = boxWidth / totalGridCols;
            double cellHeight = boxHeight / totalGridRows;

            // 选项框尺寸（格子内留边距）
            double optionWidth = cellWidth * 0.85;
            double optionHeight = cellHeight * 0.7;
            double horizontalPadding = cellWidth * 0.075;
            double verticalPadding = cellHeight * 0.15;

            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                int questionsInThisRow = Math.min(questionsPerGroup, questionCount - rowIndex * questionsPerGroup);

                for (int questionOffset = 0; questionOffset < questionsInThisRow; questionOffset++) {
                    int questionNo = questionStart + rowIndex * questionsPerGroup + questionOffset;

                    for (int optionIndex = 0; optionIndex < optionCount; optionIndex++) {
                        String option = String.valueOf((char) ('A' + optionIndex));

                        // 计算格子位置
                        int gridCol = questionOffset * optionCount + optionIndex;

                        double x = boxX + gridCol * cellWidth + horizontalPadding;
                        double y = boxY + rowIndex * cellHeight + verticalPadding;

                        bubbleMap.add(new BubbleItem(questionNo, option, x, y, optionWidth, optionHeight, 1.0));
                    }
                }
            }
        }

        return bubbleMap;
    }

    /**
     * 原始的连通域检测方法（保留作为备用）
     */
    @SuppressWarnings("unused")
    private BubbleDetectionResultVO detectBubblesWithConnectedComponents(
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

        log.info("检测到 {} 个连通域，预期 {} 个气泡", components.size(), expectedCount);

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

        log.info("初步过滤后剩余 {} 个候选", candidates.size());

        // 如果候选太少，尝试组合相邻的小连通域（支持空心方框样式）
        if (candidates.size() < expectedCount * 0.5) {
            log.info("候选不足，尝试组合检测模式");
            List<DetectedComponent> combinedCandidates = detectCombinedRegions(components, cropWidth, cropHeight, expectedCount);
            if (combinedCandidates.size() > candidates.size()) {
                candidates = combinedCandidates;
                log.info("组合检测后得到 {} 个候选", candidates.size());
            }
        }

        if (candidates.isEmpty()) {
            return BubbleDetectionResultVO.fail("过滤后没有有效的气泡候选");
        }

        // 获取典型尺寸
        candidates.sort(Comparator.comparingInt((DetectedComponent c) -> c.area).reversed());
        List<DetectedComponent> sampleCandidates = candidates.subList(0, Math.min(candidates.size(), Math.max(expectedCount * 2, 20)));
        double medianWidth = median(sampleCandidates.stream().mapToDouble(c -> c.width).toArray());
        double medianHeight = median(sampleCandidates.stream().mapToDouble(c -> c.height).toArray());

        log.info("中位尺寸: {}x{}", medianWidth, medianHeight);

        // 再次过滤，只保留尺寸相近的（放宽约束以支持方框样式）
        List<DetectedComponent> finalCandidates = new ArrayList<>();
        for (DetectedComponent comp : candidates) {
            if (comp.width >= medianWidth * 0.4 && comp.width <= medianWidth * 2.5 &&
                comp.height >= medianHeight * 0.4 && comp.height <= medianHeight * 2.5) {
                finalCandidates.add(comp);
            }
        }

        log.info("尺寸过滤后剩余 {} 个最终候选", finalCandidates.size());

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

    /**
     * 组合检测模式：将相邻的小连通域合并成选项区域
     * 用于检测 [A] [B] [C] [D] 这种空心方框样式
     */
    private List<DetectedComponent> detectCombinedRegions(
            List<DetectedComponent> allComponents, int cropWidth, int cropHeight, int expectedCount) {

        // 估算单个选项的大致尺寸
        double estimatedOptionWidth = cropWidth * 0.06;  // 假设每个选项占区域的6%
        double estimatedOptionHeight = cropHeight * 0.08;

        // 筛选出有意义的小连通域
        List<DetectedComponent> smallComponents = new ArrayList<>();
        for (DetectedComponent comp : allComponents) {
            // 过滤噪点（太小）和大块（非选项元素）
            if (comp.area < 8) continue;
            if (comp.width > cropWidth * 0.2 || comp.height > cropHeight * 0.3) continue;
            smallComponents.add(comp);
        }

        if (smallComponents.isEmpty()) {
            return new ArrayList<>();
        }

        // 按位置聚类，合并相邻的连通域
        List<DetectedComponent> mergedRegions = new ArrayList<>();
        boolean[] used = new boolean[smallComponents.size()];

        // 计算合并距离阈值
        double mergeDistance = Math.max(estimatedOptionWidth, estimatedOptionHeight) * 0.8;

        for (int i = 0; i < smallComponents.size(); i++) {
            if (used[i]) continue;

            DetectedComponent seed = smallComponents.get(i);
            List<DetectedComponent> group = new ArrayList<>();
            group.add(seed);
            used[i] = true;

            // 寻找相邻的连通域并合并
            boolean found;
            do {
                found = false;
                for (int j = 0; j < smallComponents.size(); j++) {
                    if (used[j]) continue;

                    DetectedComponent candidate = smallComponents.get(j);

                    // 检查是否与组内任一元素相邻
                    for (DetectedComponent member : group) {
                        double dx = Math.abs(candidate.centerX - member.centerX);
                        double dy = Math.abs(candidate.centerY - member.centerY);

                        // 允许一定的重叠或紧邻
                        if (dx < mergeDistance && dy < mergeDistance * 1.5) {
                            group.add(candidate);
                            used[j] = true;
                            found = true;
                            break;
                        }
                    }
                }
            } while (found);

            // 如果组内元素足够多（至少2个小连通域），认为是一个有效选项
            if (group.size() >= 2) {
                // 计算合并后的边界
                int minX = Integer.MAX_VALUE, maxX = 0, minY = Integer.MAX_VALUE, maxY = 0;
                int totalArea = 0;
                for (DetectedComponent comp : group) {
                    minX = Math.min(minX, comp.x);
                    maxX = Math.max(maxX, comp.x + comp.width);
                    minY = Math.min(minY, comp.y);
                    maxY = Math.max(maxY, comp.y + comp.height);
                    totalArea += comp.area;
                }

                int width = maxX - minX;
                int height = maxY - minY;

                // 验证合并后的尺寸是否合理
                if (width > 5 && height > 5 &&
                    width < cropWidth * 0.15 && height < cropHeight * 0.2) {
                    mergedRegions.add(new DetectedComponent(
                            minX, minY, width, height, totalArea,
                            minX + width / 2.0, minY + height / 2.0
                    ));
                }
            } else if (group.size() == 1 && seed.area > 50) {
                // 单独的较大连通域也可能是一个选项
                double fillRatio = (double) seed.area / (seed.width * seed.height);
                if (fillRatio > 0.02 && fillRatio < 0.95) {
                    mergedRegions.add(seed);
                }
            }
        }

        log.info("组合检测合并后得到 {} 个区域", mergedRegions.size());
        return mergedRegions;
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
