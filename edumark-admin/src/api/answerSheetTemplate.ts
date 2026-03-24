import { request } from '@/utils/request'
import type { Id, PageResult } from './types'

// 答题卡模板类型定义
export interface AnswerSheetTemplate {
  id: Id
  paperId: Id
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
  templateImagePath?: string
  templateImageUrl?: string
  cornerConfig?: CornerConfig
  createTime?: string
  updateTime?: string
  regions?: AnswerSheetRegion[]
}

export interface CornerConfig {
  topLeft?: { x: number; y: number }
  topRight?: { x: number; y: number }
  bottomLeft?: { x: number; y: number }
  bottomRight?: { x: number; y: number }
  corrected?: boolean
  angle?: number
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
  id?: Id
  templateId?: Id
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
  layoutDirection?: 'row' | 'column' // 布局方向：row=横向排列，column=纵向排列
  bubbleStyle?: 'circle' | 'square'
  hasMultipleChoice?: boolean
  bubbleMap?: BubbleMapItem[]
  detectedBubbleCount?: number
  expectedBubbleCount?: number
  // 正确答案配置 {题号: 答案}
  correctAnswers?: Record<string, string>
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
  paperId?: Id
  name?: string
  status?: number
  examId?: Id
  subjectName?: string
}

// ============ API ============

export function getTemplatePage(params: TemplateQuery) {
  return request.get<PageResult<AnswerSheetTemplate>>('/answer-sheet-template/page', { params })
}

export function getTemplateDetail(id: Id) {
  return request.get<AnswerSheetTemplate>(`/answer-sheet-template/${id}`)
}

export function getTemplateByPaperId(paperId: Id) {
  return request.get<AnswerSheetTemplate>(`/answer-sheet-template/paper/${paperId}`)
}

export function createTemplate(data: Partial<AnswerSheetTemplate>) {
  return request.post<Id>('/answer-sheet-template', data)
}

export function updateTemplate(data: Partial<AnswerSheetTemplate>) {
  return request.put<void>('/answer-sheet-template', data)
}

export function deleteTemplate(id: Id) {
  return request.delete<void>(`/answer-sheet-template/${id}`)
}

export function generateTemplateFromPaper(paperId: Id) {
  return request.post<Id>(`/answer-sheet-template/generate/${paperId}`)
}

export function publishTemplate(id: Id) {
  return request.post<void>(`/answer-sheet-template/${id}/publish`)
}

export function validateTemplate(id: Id) {
  return request.get<TemplateValidationResult>(`/answer-sheet-template/${id}/validate`)
}

export function getTemplatePreviewUrl(id: Id) {
  return request.get<string>(`/answer-sheet-template/${id}/preview`)
}

export function getTemplateDownloadUrl(id: Id) {
  return request.get<string>(`/answer-sheet-template/${id}/download`)
}

// ============ 新增API - 答题卡设计重做 ============

export interface CornerDetectionResult {
  success: boolean
  errorMessage?: string
  topLeftX?: number
  topLeftY?: number
  topRightX?: number
  topRightY?: number
  bottomLeftX?: number
  bottomLeftY?: number
  bottomRightX?: number
  bottomRightY?: number
  angle?: number
  imageWidth?: number
  imageHeight?: number
  correctedImagePath?: string
  correctedImageUrl?: string
}

export interface BubbleDetectionResult {
  success: boolean
  errorMessage?: string
  detectedCount?: number
  expectedCount?: number
  bubbleMap?: BubbleMapItem[]
  rowCount?: number
  bubblesPerRow?: number
}

// 上传模板图片
export function uploadTemplateImage(id: Id, imagePath: string) {
  return request.post<string>(`/answer-sheet-template/${id}/upload-image`, null, {
    params: { imagePath },
  })
}

// 保存四角定位配置
export function saveCornerConfig(id: Id, cornerConfig: CornerConfig) {
  return request.put<void>(`/answer-sheet-template/${id}/corner-config`, cornerConfig)
}

// 保存区域正确答案
export function saveRegionAnswers(id: Id, regionId: Id, correctAnswers: Record<string, string>) {
  return request.put<void>(`/answer-sheet-template/${id}/region/${regionId}/answers`, correctAnswers)
}

// 获取模板图片URL
export function getTemplateImageUrl(id: Id) {
  return request.get<string>(`/answer-sheet-template/${id}/image`)
}

// 检测四角定位点(上传文件)
export function detectCorners(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<CornerDetectionResult>('/corner-detection/detect', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

// 检测四角定位点(已上传图片)
export function detectCornersByPath(imagePath: string) {
  return request.get<CornerDetectionResult>('/corner-detection/detect', {
    params: { imagePath },
  })
}

// 应用四角矫正
export function correctImage(imagePath: string, cornerConfig: CornerConfig) {
  return request.post<string>('/corner-detection/correct', cornerConfig, {
    params: { imagePath },
  })
}

// 检测气泡(上传文件)
export function detectBubbles(
  file: File,
  params: {
    boxX: number
    boxY: number
    boxWidth: number
    boxHeight: number
    questionStart: number
    questionEnd: number
    optionCount?: number
    questionsPerRow?: number
    layoutDirection?: 'row' | 'column'
  },
) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<BubbleDetectionResult>('/bubble-detection/detect', formData, {
    params,
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

// 检测气泡(已上传图片)
export function detectBubblesByPath(
  imagePath: string,
  params: {
    boxX: number
    boxY: number
    boxWidth: number
    boxHeight: number
    questionStart: number
    questionEnd: number
    optionCount?: number
    questionsPerRow?: number
    layoutDirection?: 'row' | 'column'
  },
) {
  return request.get<BubbleDetectionResult>('/bubble-detection/detect', {
    params: { imagePath, ...params },
  })
}
