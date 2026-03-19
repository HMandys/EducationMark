import { request } from '@/utils/request'
import type { PageResult } from './types'

// 答题卡模板类型定义
export interface AnswerSheetTemplate {
  id: number
  paperId: number
  paperName?: string
  examName?: string
  subjectName?: string
  name: string
  pageSize: string
  orientation: number
  columns: number
  marginTop: number
  marginBottom: number
  marginLeft: number
  marginRight: number
  headerConfig?: HeaderConfig
  studentInfoConfig?: StudentInfoConfig
  status: number
  pdfObjectName?: string
  pdfUrl?: string
  createTime?: string
  updateTime?: string
  regions?: AnswerSheetRegion[]
}

export interface HeaderConfig {
  title?: string
  showTitle?: boolean
}

export interface StudentInfoConfig {
  showStudentId?: boolean
  showName?: boolean
  showClass?: boolean
}

export interface AnswerSheetRegion {
  id?: number
  templateId?: number
  regionType: number
  regionTypeName?: string
  regionName: string
  pageNo: number
  sortOrder: number
  questionStart?: number
  questionEnd?: number
  questionIds?: number[]
  config?: RegionConfig
}

export type RegionRole =
  | 'student_id'
  | 'student_name'
  | 'class_name'
  | 'barcode'
  | 'choice_block'
  | 'subjective_crop'
  | 'essay_crop'
  | 'score_box'

export type AnchorType = 'corner' | 'marker' | 'barcode' | 'none'

export type CropMode = 'single-question' | 'range-question' | 'full-region'

export interface RegionConfig {
  boxX?: number
  boxY?: number
  boxWidth?: number
  boxHeight?: number
  regionRole?: RegionRole
  anchorType?: AnchorType
  anchorKey?: string
  cropMode?: CropMode
  // 选择题配置
  optionCount?: number
  questionsPerRow?: number
  bubbleStyle?: 'circle' | 'square'
  hasMultipleChoice?: boolean
  bubbleMap?: BubbleMapItem[]
  detectedBubbleCount?: number
  expectedBubbleCount?: number
  // 填空题配置
  lineHeight?: number
  linesPerQuestion?: number
  lineStyle?: 'underline' | 'box'
  // 解答题配置
  height?: number
  showBorder?: boolean
  scoreBoxPosition?: 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left'
  // 作文题配置
  gridType?: 'square' | 'line'
  gridSize?: number
  wordCount?: number
}

export interface BubbleMapItem {
  questionNo: number
  option: string
  x: number
  y: number
  width: number
  height: number
  confidence?: number
}

export interface TemplateValidationIssue {
  regionName?: string
  field?: string
  message: string
}

export interface TemplateValidationResult {
  passed: boolean
  totalRegionCount: number
  annotatedRegionCount: number
  issueCount: number
  issues: TemplateValidationIssue[]
}

export interface TemplateQuery {
  pageNum: number
  pageSize: number
  paperId?: number
  name?: string
  status?: number
  examId?: number
  subjectName?: string
}

// ============ API ============

export function getTemplatePage(params: TemplateQuery) {
  return request.get<PageResult<AnswerSheetTemplate>>('/answer-sheet-template/page', { params })
}

export function getTemplateDetail(id: number) {
  return request.get<AnswerSheetTemplate>(`/answer-sheet-template/${id}`)
}

export function getTemplateByPaperId(paperId: number) {
  return request.get<AnswerSheetTemplate>(`/answer-sheet-template/paper/${paperId}`)
}

export function createTemplate(data: Partial<AnswerSheetTemplate>) {
  return request.post<number>('/answer-sheet-template', data)
}

export function updateTemplate(data: Partial<AnswerSheetTemplate>) {
  return request.put<void>('/answer-sheet-template', data)
}

export function deleteTemplate(id: number) {
  return request.delete<void>(`/answer-sheet-template/${id}`)
}

export function generateTemplateFromPaper(paperId: number) {
  return request.post<number>(`/answer-sheet-template/generate/${paperId}`)
}

export function publishTemplate(id: number) {
  return request.post<void>(`/answer-sheet-template/${id}/publish`)
}

export function validateTemplate(id: number) {
  return request.get<TemplateValidationResult>(`/answer-sheet-template/${id}/validate`)
}

export function getTemplatePreviewUrl(id: number) {
  return request.get<string>(`/answer-sheet-template/${id}/preview`)
}

export function getTemplateDownloadUrl(id: number) {
  return request.get<string>(`/answer-sheet-template/${id}/download`)
}
