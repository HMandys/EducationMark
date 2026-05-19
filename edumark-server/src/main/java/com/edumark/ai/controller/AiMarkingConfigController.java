package com.edumark.ai.controller;

import com.edumark.ai.dto.AiMarkingPolicyDTO;
import com.edumark.ai.dto.AiMarkingProgressQueryDTO;
import com.edumark.ai.dto.AiMarkingRecordQueryDTO;
import com.edumark.ai.dto.AiMarkingProviderDTO;
import com.edumark.ai.dto.AiMarkingProviderQueryDTO;
import com.edumark.ai.service.AiAutoMarkingAsyncService;
import com.edumark.ai.service.AiMarkingConfigService;
import com.edumark.ai.vo.AiMarkingExamProgressDetailVO;
import com.edumark.ai.vo.AiMarkingExamProgressVO;
import com.edumark.ai.vo.AiMarkingPolicyVO;
import com.edumark.ai.vo.AiMarkingRecordVO;
import com.edumark.ai.vo.AiMarkingProviderVO;
import com.edumark.common.result.PageResult;
import com.edumark.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 批改配置控制器
 */
@Tag(name = "AI批改配置")
@RestController
@RequestMapping("/system/ai-marking")
public class AiMarkingConfigController {

    private final AiMarkingConfigService aiMarkingConfigService;
    private final AiAutoMarkingAsyncService aiAutoMarkingAsyncService;

    public AiMarkingConfigController(AiMarkingConfigService aiMarkingConfigService,
                                     AiAutoMarkingAsyncService aiAutoMarkingAsyncService) {
        this.aiMarkingConfigService = aiMarkingConfigService;
        this.aiAutoMarkingAsyncService = aiAutoMarkingAsyncService;
    }

    @Operation(summary = "分页查询 AI 批改提供商")
    @GetMapping("/provider/page")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<PageResult<AiMarkingProviderVO>> getProviderPage(AiMarkingProviderQueryDTO query) {
        return Result.success(aiMarkingConfigService.getProviderPage(query));
    }

    @Operation(summary = "创建 AI 批改提供商")
    @PostMapping("/provider")
    @PreAuthorize("hasAuthority('system:menu:add')")
    public Result<Long> createProvider(@Valid @RequestBody AiMarkingProviderDTO dto) {
        return Result.success(aiMarkingConfigService.createProvider(dto));
    }

    @Operation(summary = "更新 AI 批改提供商")
    @PutMapping("/provider")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public Result<Void> updateProvider(@Valid @RequestBody AiMarkingProviderDTO dto) {
        aiMarkingConfigService.updateProvider(dto);
        return Result.success();
    }

    @Operation(summary = "删除 AI 批改提供商")
    @DeleteMapping("/provider/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public Result<Void> deleteProvider(@PathVariable Long id) {
        aiMarkingConfigService.deleteProvider(id);
        return Result.success();
    }

    @Operation(summary = "分页查询 AI 批改审计记录")
    @GetMapping("/record/page")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<PageResult<AiMarkingRecordVO>> getRecordPage(AiMarkingRecordQueryDTO query) {
        return Result.success(aiMarkingConfigService.getRecordPage(query));
    }

    @Operation(summary = "分页查询 AI 考试进度")
    @GetMapping("/progress/page")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<PageResult<AiMarkingExamProgressVO>> getProgressPage(AiMarkingProgressQueryDTO query) {
        return Result.success(aiMarkingConfigService.getProgressPage(query));
    }

    @Operation(summary = "查询 AI 考试进度详情")
    @GetMapping("/progress/{examSubjectId}")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<AiMarkingExamProgressDetailVO> getProgressDetail(@PathVariable Long examSubjectId) {
        return Result.success(aiMarkingConfigService.getProgressDetail(examSubjectId));
    }

    @Operation(summary = "启动科目 AI 批改")
    @PostMapping("/progress/{examSubjectId}/run")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<Void> runExamSubjectAiMarking(@PathVariable Long examSubjectId) {
        aiAutoMarkingAsyncService.autoMarkExamSubjectFillBlankQuestionsAsync(examSubjectId, true);
        return Result.success();
    }

    @Operation(summary = "获取 AI 批改策略")
    @GetMapping("/policy")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<AiMarkingPolicyVO> getPolicy() {
        return Result.success(aiMarkingConfigService.getPolicy());
    }

    @Operation(summary = "保存 AI 批改策略")
    @PutMapping("/policy")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public Result<Void> savePolicy(@RequestBody AiMarkingPolicyDTO dto) {
        aiMarkingConfigService.savePolicy(dto);
        return Result.success();
    }
}
