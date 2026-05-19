package com.edumark.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edumark.ai.entity.AiMarkingProvider;
import com.edumark.ai.mapper.AiMarkingProviderMapper;
import com.edumark.app.service.AppAiAnalysisService;
import com.edumark.app.vo.AppAiReportVO;
import com.edumark.common.exception.BusinessException;
import com.edumark.score.mapper.ExamScoreMapper;
import com.edumark.score.mapper.SubjectScoreMapper;
import com.edumark.score.vo.ExamScoreVO;
import com.edumark.score.vo.SubjectScoreVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * App AI 分析服务实现
 *
 * @author EduMark
 */
@Service
public class AppAiAnalysisServiceImpl implements AppAiAnalysisService {

    private static final int PROVIDER_STATUS_ENABLED = 1;

    @Resource
    private HttpClient aiHttpClient;

    @Resource
    private AiMarkingProviderMapper providerMapper;

    @Resource
    private ExamScoreMapper examScoreMapper;

    @Resource
    private SubjectScoreMapper subjectScoreMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public AppAiReportVO generateStudentReport(Long studentId, Integer limit) {
        if (studentId == null) {
            throw new BusinessException("学生ID不能为空");
        }

        int safeLimit = limit == null || limit <= 0 ? 10 : Math.min(limit, 20);
        List<ExamScoreVO> scores = examScoreMapper.selectRecentByStudentId(studentId, safeLimit);
        if (scores == null || scores.isEmpty()) {
            throw new BusinessException("暂无成绩数据，无法生成AI分析");
        }

        for (ExamScoreVO score : scores) {
            score.setSubjectScores(subjectScoreMapper.selectListByExamAndStudent(score.getExamId(), studentId));
        }

        AiMarkingProvider provider = loadDefaultProvider();
        if (provider == null) {
            throw new BusinessException("请先在后台配置并启用默认 AI 提供商");
        }

        AppAiReportVO report = invokeAnalysis(provider, scores);
        ExamScoreVO latest = scores.get(0);

        report.setStudentId(latest.getStudentId());
        report.setStudentName(latest.getStudentName());
        report.setLatestTotalScore(latest.getTotalScore());
        report.setAverageTotalScore(calculateAverageScore(scores));
        report.setTrend(buildTrend(scores));
        report.setProviderName(provider.getProviderName());
        report.setProtocol(provider.getProtocol());
        report.setModel(provider.getModel());
        report.setGeneratedAt(LocalDateTime.now());
        return report;
    }

    private AiMarkingProvider loadDefaultProvider() {
        return providerMapper.selectOne(
                new LambdaQueryWrapper<AiMarkingProvider>()
                        .eq(AiMarkingProvider::getDeleted, 0)
                        .eq(AiMarkingProvider::getEnabled, PROVIDER_STATUS_ENABLED)
                        .eq(AiMarkingProvider::getIsDefault, 1)
                        .orderByAsc(AiMarkingProvider::getPriority)
                        .orderByAsc(AiMarkingProvider::getId)
                        .last("LIMIT 1")
        );
    }

    private AppAiReportVO invokeAnalysis(AiMarkingProvider provider, List<ExamScoreVO> scores) {
        try {
            String responseText = switch (provider.getProtocol()) {
                case "openai-compatible" -> invokeOpenAiCompatible(provider, scores);
                case "openai-responses" -> invokeOpenAiResponses(provider, scores);
                case "anthropic" -> invokeAnthropic(provider, scores);
                default -> throw new BusinessException("暂不支持该协议类型");
            };
            return parseReport(responseText);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("AI分析生成失败: " + ex.getMessage());
        }
    }

    private String invokeOpenAiCompatible(AiMarkingProvider provider, List<ExamScoreVO> scores) throws IOException, InterruptedException {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", provider.getModel());
        payload.put("temperature", 0.3);
        payload.put("max_tokens", provider.getMaxTokens() != null ? provider.getMaxTokens() : 2048);
        payload.put("response_format", Map.of("type", "json_object"));
        payload.put("messages", List.of(
                Map.of("role", "system", "content", buildSystemPrompt()),
                Map.of("role", "user", "content", buildUserPrompt(scores))
        ));

        String responseText = executeRequest(
                resolveEndpoint(provider.getBaseUrl(), "/chat/completions"),
                provider.getApiKey(),
                Map.of("Authorization", "Bearer " + provider.getApiKey()),
                payload,
                provider.getTimeoutMs()
        );

        JsonNode root = objectMapper.readTree(responseText);
        return root.path("choices").path(0).path("message").path("content").asText();
    }

    private String invokeOpenAiResponses(AiMarkingProvider provider, List<ExamScoreVO> scores) throws IOException, InterruptedException {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", provider.getModel());
        payload.put("instructions", buildSystemPrompt());
        payload.put("max_output_tokens", provider.getMaxTokens() != null ? provider.getMaxTokens() : 2048);
        payload.put("input", List.of(
                Map.of("role", "user", "content", List.of(
                        Map.of("type", "input_text", "text", buildUserPrompt(scores))
                ))
        ));

        String responseText = executeRequest(
                resolveEndpoint(provider.getBaseUrl(), "/responses"),
                provider.getApiKey(),
                Map.of("Authorization", "Bearer " + provider.getApiKey()),
                payload,
                provider.getTimeoutMs()
        );

        JsonNode root = objectMapper.readTree(responseText);
        return extractResponsesText(root);
    }

    private String invokeAnthropic(AiMarkingProvider provider, List<ExamScoreVO> scores) throws IOException, InterruptedException {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", provider.getModel());
        payload.put("temperature", 0.3);
        payload.put("max_tokens", provider.getMaxTokens() != null ? provider.getMaxTokens() : 2048);
        payload.put("system", buildSystemPrompt());
        payload.put("messages", List.of(
                Map.of("role", "user", "content", List.of(
                        Map.of("type", "text", "text", buildUserPrompt(scores))
                ))
        ));

        String responseText = executeRequest(
                resolveEndpoint(provider.getBaseUrl(), "/messages"),
                provider.getApiKey(),
                Map.of(
                        "x-api-key", provider.getApiKey(),
                        "anthropic-version", "2023-06-01"
                ),
                payload,
                provider.getTimeoutMs()
        );

        JsonNode root = objectMapper.readTree(responseText);
        JsonNode content = root.path("content");
        if (content.isArray()) {
            for (JsonNode item : content) {
                if ("text".equals(item.path("type").asText())) {
                    return item.path("text").asText("");
                }
            }
        }
        throw new BusinessException("Anthropic 未返回可解析文本");
    }

    private String executeRequest(String endpoint,
                                  String apiKey,
                                  Map<String, String> extraHeaders,
                                  Map<String, Object> payload,
                                  Integer timeoutMs) throws IOException, InterruptedException {
        if (!StringUtils.hasText(apiKey)) {
            throw new BusinessException("默认提供商未配置 API Key");
        }

        String body = objectMapper.writeValueAsString(payload);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofMillis(timeoutMs != null ? timeoutMs : 30000))
                .header("Content-Type", "application/json");
        extraHeaders.forEach(builder::header);

        HttpResponse<String> response = aiHttpClient.send(
                builder.POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new BusinessException("模型请求失败: HTTP " + response.statusCode());
        }
        return response.body();
    }

    private AppAiReportVO parseReport(String text) throws IOException {
        String jsonText = extractJson(text);
        JsonNode root = objectMapper.readTree(jsonText);

        AppAiReportVO vo = new AppAiReportVO();
        vo.setSummary(root.path("summary").asText(""));
        vo.setStrengths(readStringList(root.path("strengths")));
        vo.setWeaknesses(readStringList(root.path("weaknesses")));
        vo.setSuggestions(readStringList(root.path("suggestions")));
        return vo;
    }

    private List<String> readStringList(JsonNode node) {
        List<String> result = new ArrayList<>();
        if (node != null && node.isArray()) {
            for (JsonNode item : node) {
                String text = item.asText("");
                if (StringUtils.hasText(text)) {
                    result.add(text.trim());
                }
            }
        }
        return result;
    }

    private String extractResponsesText(JsonNode root) {
        JsonNode output = root.path("output");
        if (output.isArray()) {
            for (JsonNode item : output) {
                JsonNode content = item.path("content");
                if (!content.isArray()) {
                    continue;
                }
                for (JsonNode contentItem : content) {
                    String type = contentItem.path("type").asText("");
                    if ("output_text".equals(type) || "text".equals(type)) {
                        String text = contentItem.path("text").asText("");
                        if (StringUtils.hasText(text)) {
                            return text;
                        }
                    }
                }
            }
        }
        String fallback = root.path("output_text").asText("");
        if (StringUtils.hasText(fallback)) {
            return fallback;
        }
        throw new BusinessException("Responses API 未返回可解析文本");
    }

    private String extractJson(String text) {
        if (!StringUtils.hasText(text)) {
            throw new BusinessException("模型未返回内容");
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start < 0 || end <= start) {
            throw new BusinessException("模型未返回有效 JSON");
        }
        return text.substring(start, end + 1);
    }

    private String resolveEndpoint(String baseUrl, String path) {
        String trimmed = baseUrl.trim();
        if (trimmed.endsWith(path)) {
            return trimmed;
        }
        String base = trimmed.replaceAll("/+$", "");
        return base + path;
    }

    private String buildSystemPrompt() {
        return """
                你是学生学情分析助手。
                请根据输入的考试成绩数据，输出简明、客观、可执行的学习分析。
                不要夸张，不要编造不存在的数据，不要输出 JSON 之外的任何文字。
                """;
    }

    private String buildUserPrompt(List<ExamScoreVO> scores) throws IOException {
        return """
                请根据以下学生最近考试成绩数据，生成家长/学生端可直接展示的学情分析 JSON。

                要求：
                1. summary 用 2-4 句话总结整体表现和趋势。
                2. strengths 输出 2-4 条优势项。
                3. weaknesses 输出 2-4 条待提升项。
                4. suggestions 输出 3-5 条具体可执行建议。
                5. 只能基于提供的数据分析，不要编造老师评价、学校背景或不存在的科目。
                6. 只返回 JSON。

                输出格式：
                {
                  "summary": "",
                  "strengths": ["", ""],
                  "weaknesses": ["", ""],
                  "suggestions": ["", "", ""]
                }

                成绩数据：
                %s
                """.formatted(objectMapper.writeValueAsString(scores));
    }

    private BigDecimal calculateAverageScore(List<ExamScoreVO> scores) {
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (ExamScoreVO score : scores) {
            if (score.getTotalScore() != null) {
                total = total.add(score.getTotalScore());
                count++;
            }
        }
        if (count == 0) {
            return BigDecimal.ZERO;
        }
        return total.divide(BigDecimal.valueOf(count), 1, RoundingMode.HALF_UP);
    }

    private String buildTrend(List<ExamScoreVO> scores) {
        if (scores.size() < 2 || scores.get(0).getTotalScore() == null || scores.get(1).getTotalScore() == null) {
            return "样本不足";
        }
        int compare = scores.get(0).getTotalScore().compareTo(scores.get(1).getTotalScore());
        if (compare > 0) {
            return "较上次有所提升";
        }
        if (compare < 0) {
            return "较上次有所回落";
        }
        return "与上次基本持平";
    }
}
