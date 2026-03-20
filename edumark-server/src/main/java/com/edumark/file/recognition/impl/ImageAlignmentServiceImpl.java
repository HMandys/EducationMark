package com.edumark.file.recognition.impl;

import com.edumark.file.recognition.ImageAlignmentService;
import com.google.zxing.ResultPoint;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * 图像对齐服务实现
 *
 * @author EduMark
 */
@Service
public class ImageAlignmentServiceImpl implements ImageAlignmentService {

    private static final int MIN_BARCODE_HEIGHT = 36;

    @Override
    public BufferedImage rotate(BufferedImage source, double angleDegrees) {
        if (source == null) {
            return null;
        }
        if (Math.abs(angleDegrees) < 0.01D) {
            return source;
        }

        RotationContext context = buildRotationContext(source.getWidth(), source.getHeight(), angleDegrees);
        BufferedImage rotated = new BufferedImage(context.targetWidth(), context.targetHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = rotated.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, rotated.getWidth(), rotated.getHeight());
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.drawImage(source, context.transform(), null);
        graphics.dispose();
        return rotated;
    }

    @Override
    public BufferedImage alignByBarcode(BufferedImage source, ResultPoint[] points) {
        if (source == null || points == null || points.length < 2) {
            return source;
        }

        ResultPoint startPoint = points[0];
        ResultPoint endPoint = points[points.length - 1];
        double angleDegrees = Math.toDegrees(Math.atan2(endPoint.getY() - startPoint.getY(), endPoint.getX() - startPoint.getX()));

        RotationContext context = buildRotationContext(source.getWidth(), source.getHeight(), -angleDegrees);
        BufferedImage rotated = rotate(source, -angleDegrees);
        ResultPoint[] transformedPoints = transformPoints(points, context.transform());

        Rectangle roughBounds = buildBarcodeBounds(rotated, transformedPoints);
        Rectangle refinedBounds = refineBounds(rotated, roughBounds);

        if (refinedBounds.width <= 0 || refinedBounds.height <= 0) {
            return rotated;
        }
        return rotated.getSubimage(refinedBounds.x, refinedBounds.y, refinedBounds.width, refinedBounds.height);
    }

    private ResultPoint[] transformPoints(ResultPoint[] points, AffineTransform transform) {
        ResultPoint[] transformed = new ResultPoint[points.length];
        for (int i = 0; i < points.length; i++) {
            Point2D source = new Point2D.Double(points[i].getX(), points[i].getY());
            Point2D target = transform.transform(source, null);
            transformed[i] = new ResultPoint((float) target.getX(), (float) target.getY());
        }
        return transformed;
    }

    private Rectangle buildBarcodeBounds(BufferedImage image, ResultPoint[] points) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (ResultPoint point : points) {
            minX = Math.min(minX, point.getX());
            minY = Math.min(minY, point.getY());
            maxX = Math.max(maxX, point.getX());
            maxY = Math.max(maxY, point.getY());
        }

        double spanX = Math.max(1D, maxX - minX);
        double centerY = (minY + maxY) / 2D;
        double estimatedHeight = Math.max(Math.max(maxY - minY, MIN_BARCODE_HEIGHT), spanX * 0.24D);
        double paddingX = Math.max(10D, spanX * 0.1D);
        double paddingY = Math.max(10D, estimatedHeight * 0.2D);

        int left = clamp((int) Math.floor(minX - paddingX), 0, image.getWidth() - 1);
        int right = clamp((int) Math.ceil(maxX + paddingX), left + 1, image.getWidth());
        int top = clamp((int) Math.floor(centerY - estimatedHeight / 2D - paddingY), 0, image.getHeight() - 1);
        int bottom = clamp((int) Math.ceil(centerY + estimatedHeight / 2D + paddingY), top + 1, image.getHeight());

        return new Rectangle(left, top, right - left, bottom - top);
    }

    private Rectangle refineBounds(BufferedImage image, Rectangle roughBounds) {
        int left = roughBounds.x;
        int right = roughBounds.x + roughBounds.width - 1;
        int top = roughBounds.y;
        int bottom = roughBounds.y + roughBounds.height - 1;

        int horizontalThreshold = Math.max(6, roughBounds.height / 6);
        int verticalThreshold = Math.max(6, roughBounds.width / 5);

        int refinedLeft = findLeftEdge(image, left, right, top, bottom, horizontalThreshold);
        int refinedRight = findRightEdge(image, left, right, top, bottom, horizontalThreshold);
        int refinedTop = findTopEdge(image, refinedLeft, refinedRight, top, bottom, verticalThreshold);
        int refinedBottom = findBottomEdge(image, refinedLeft, refinedRight, top, bottom, verticalThreshold);

        int paddingX = Math.max(8, roughBounds.width / 20);
        int paddingY = Math.max(8, roughBounds.height / 20);

        int cropLeft = clamp(refinedLeft - paddingX, 0, image.getWidth() - 1);
        int cropRight = clamp(refinedRight + paddingX, cropLeft + 1, image.getWidth());
        int cropTop = clamp(refinedTop - paddingY, 0, image.getHeight() - 1);
        int cropBottom = clamp(refinedBottom + paddingY, cropTop + 1, image.getHeight());

        return new Rectangle(cropLeft, cropTop, cropRight - cropLeft, cropBottom - cropTop);
    }

    private int findLeftEdge(BufferedImage image, int left, int right, int top, int bottom, int threshold) {
        for (int x = left; x <= right; x++) {
            if (countDarkPixelsByColumn(image, x, top, bottom) >= threshold) {
                return x;
            }
        }
        return left;
    }

    private int findRightEdge(BufferedImage image, int left, int right, int top, int bottom, int threshold) {
        for (int x = right; x >= left; x--) {
            if (countDarkPixelsByColumn(image, x, top, bottom) >= threshold) {
                return x;
            }
        }
        return right;
    }

    private int findTopEdge(BufferedImage image, int left, int right, int top, int bottom, int threshold) {
        for (int y = top; y <= bottom; y++) {
            if (countDarkPixelsByRow(image, y, left, right) >= threshold) {
                return y;
            }
        }
        return top;
    }

    private int findBottomEdge(BufferedImage image, int left, int right, int top, int bottom, int threshold) {
        for (int y = bottom; y >= top; y--) {
            if (countDarkPixelsByRow(image, y, left, right) >= threshold) {
                return y;
            }
        }
        return bottom;
    }

    private int countDarkPixelsByColumn(BufferedImage image, int x, int top, int bottom) {
        int count = 0;
        for (int y = top; y <= bottom; y++) {
            if (isDark(image.getRGB(x, y))) {
                count++;
            }
        }
        return count;
    }

    private int countDarkPixelsByRow(BufferedImage image, int y, int left, int right) {
        int count = 0;
        for (int x = left; x <= right; x++) {
            if (isDark(image.getRGB(x, y))) {
                count++;
            }
        }
        return count;
    }

    private boolean isDark(int rgb) {
        Color color = new Color(rgb);
        int luminance = (int) (0.299D * color.getRed() + 0.587D * color.getGreen() + 0.114D * color.getBlue());
        return luminance < 180;
    }

    private RotationContext buildRotationContext(int width, int height, double angleDegrees) {
        double radians = Math.toRadians(angleDegrees);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int targetWidth = Math.max(1, (int) Math.ceil(width * cos + height * sin));
        int targetHeight = Math.max(1, (int) Math.ceil(height * cos + width * sin));

        AffineTransform transform = new AffineTransform();
        transform.translate((targetWidth - width) / 2.0D, (targetHeight - height) / 2.0D);
        transform.rotate(radians, width / 2.0D, height / 2.0D);

        return new RotationContext(transform, targetWidth, targetHeight);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private record RotationContext(AffineTransform transform, int targetWidth, int targetHeight) {
    }
}
