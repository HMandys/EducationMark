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

export interface AiMarkingRecord {
  id: number
  answerSheetId: number
  questionNo?: number
  providerName?: string
  protocol?: string
  model?: string
  referenceAnswer?: string
  recognizedText?: string
  suggestedScore?: number
  confidence?: number
  judgeReason?: string
  status?: number
  errorMessage?: string
  rawResponse?: string
  createTime?: string
}

export interface AiMarkingRecordQuery {
  pageNum: number
  pageSize: number
  answerSheetId?: number
  questionNo?: number
  status?: number
  providerName?: string
  protocol?: string
}

export interface AiMarkingExamProgress {
  templateId: number
  examId: number
  examName: string
  examSubjectId: number
  subjectName: string
  templateName?: string
  aiQuestionCount: number
  answerSheetCount: number
  totalTaskCount: number
  completedCount: number
  pendingCount: number
  anomalyCount: number
  progress: number
  status: number
  statusName: string
  updateTime?: string
}

export interface AiMarkingExamProgressQuery {
  pageNum: number
  pageSize: number
  examName?: string
  subjectName?: string
  status?: number
}

export interface AiMarkingQuestionProgress {
  questionNo: number
  totalCount: number
  completedCount: number
  pendingCount: number
  anomalyCount: number
  progress: number
}

export interface AiMarkingSheetProgress {
  answerSheetId: number
  studentId?: number
  studentName?: string
  studentNumber?: string
  className?: string
  answerSheetStatus?: number
  answerSheetStatusName?: string
  totalQuestionCount: number
  completedCount: number
  pendingCount: number
  anomalyCount: number
  progress: number
}

export interface AiMarkingExamProgressDetail {
  summary: AiMarkingExamProgress
  aiQuestionNos: number[]
  questionProgressList: AiMarkingQuestionProgress[]
  sheetProgressList: AiMarkingSheetProgress[]
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

export function getAiMarkingRecordPage(params: AiMarkingRecordQuery) {
  return request.get<PageResult<AiMarkingRecord>>('/system/ai-marking/record/page', { params })
}

export function getAiMarkingProgressPage(params: AiMarkingExamProgressQuery) {
  return request.get<PageResult<AiMarkingExamProgress>>('/system/ai-marking/progress/page', { params })
}

export function getAiMarkingProgressDetail(examSubjectId: number) {
  return request.get<AiMarkingExamProgressDetail>(`/system/ai-marking/progress/${examSubjectId}`)
}

export function runAiMarkingProgress(examSubjectId: number) {
  return request.post<void>(`/system/ai-marking/progress/${examSubjectId}/run`)
}

export function getAiMarkingPolicy() {
  return request.get<AiMarkingPolicy>('/system/ai-marking/policy')
}

export function updateAiMarkingPolicy(data: AiMarkingPolicy) {
  return request.put<void>('/system/ai-marking/policy', data)
}
