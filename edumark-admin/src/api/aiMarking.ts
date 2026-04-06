import { request } from '@/utils/request'
import type { PageResult } from './types'

export interface AiMarkingProvider {
  id?: number
  providerName: string
  protocol: string
  baseUrl: string
  apiKey?: string
  model: string
  enabled?: number
  isDefault?: number
  timeoutMs?: number
  maxTokens?: number
  priority?: number
  remark?: string
  apiKeyMasked?: string
  hasApiKey?: boolean
  updateTime?: string
}

export interface AiMarkingProviderQuery {
  pageNum: number
  pageSize: number
  providerName?: string
  protocol?: string
  enabled?: number
}

export interface AiMarkingPolicy {
  id?: number
  enabled?: number
  lowConfidenceThreshold?: number
  failureStrategy?: string
  promptTemplate?: string
}

export function getAiMarkingProviderPage(params: AiMarkingProviderQuery) {
  return request.get<PageResult<AiMarkingProvider>>('/system/ai-marking/provider/page', { params })
}

export function createAiMarkingProvider(data: AiMarkingProvider) {
  return request.post<number>('/system/ai-marking/provider', data)
}

export function updateAiMarkingProvider(data: AiMarkingProvider) {
  return request.put<void>('/system/ai-marking/provider', data)
}

export function deleteAiMarkingProvider(id: number) {
  return request.delete<void>(`/system/ai-marking/provider/${id}`)
}

export function getAiMarkingPolicy() {
  return request.get<AiMarkingPolicy>('/system/ai-marking/policy')
}

export function updateAiMarkingPolicy(data: AiMarkingPolicy) {
  return request.put<void>('/system/ai-marking/policy', data)
}
