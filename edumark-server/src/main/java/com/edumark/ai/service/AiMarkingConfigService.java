package com.edumark.ai.service;

import com.edumark.ai.dto.AiMarkingPolicyDTO;
import com.edumark.ai.dto.AiMarkingRecordQueryDTO;
import com.edumark.ai.dto.AiMarkingProviderDTO;
import com.edumark.ai.dto.AiMarkingProviderQueryDTO;
import com.edumark.ai.vo.AiMarkingPolicyVO;
import com.edumark.ai.vo.AiMarkingRecordVO;
import com.edumark.ai.vo.AiMarkingProviderVO;
import com.edumark.common.result.PageResult;

/**
 * AI 批改配置服务
 */
public interface AiMarkingConfigService {

    PageResult<AiMarkingProviderVO> getProviderPage(AiMarkingProviderQueryDTO query);

    Long createProvider(AiMarkingProviderDTO dto);

    void updateProvider(AiMarkingProviderDTO dto);

    void deleteProvider(Long id);

    PageResult<AiMarkingRecordVO> getRecordPage(AiMarkingRecordQueryDTO query);

    AiMarkingPolicyVO getPolicy();

    void savePolicy(AiMarkingPolicyDTO dto);
}
