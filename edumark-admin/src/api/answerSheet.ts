import { request } from '@/utils/request'
import type { Id, PageResult } from './types'

// 答题卡类型定义
export interface AnswerSheet {
  id: Id
  examId: Id
  examName?: string
  examSubjectId: Id
  subjectName?: string
  studentId?: Id
  studentName?: string
  studentNumber?: string
  className?: string
  seatNumber?: string
  imageCount: number
  status: number
  statusName?: string
  objectiveScore?: number
  subjectiveScore?: number
  totalScore?: number
  remark?: string
  images?: AnswerSheetImage[]
  createTime?: string
}

export interface AnswerSheetImage {
  id: Id
  answerSheetId: Id
  pageNum: number
  imagePath: string
  imageUrl: string
  originalName?: string
  fileSize?: number
  width?: number
  height?: number
  sort: number
}

export interface AnswerSheetQuestionDetail {
  id?: Id
  answerSheetId: Id
  questionId: Id
  questionNo?: string
  questionType?: number
  questionTypeName?: string
  isObjective?: number
  fullScore?: number
  correctAnswer?: string
  studentAnswer?: string
  score?: number
  status?: number
  statusName?: string
  regionRole?: string
  regionRoleName?: string
  cropMode?: string
  pageNo?: number
  optionCount?: number
  anomalyFlag?: boolean
  anomalyReason?: string
  previewAvailable?: boolean
}

export interface AnswerSheetObjectiveAnswerDTO {
  studentAnswer?: string
}

export interface AnswerSheetAiReviewResultDTO {
  studentAnswer?: string
  score?: number
}

export interface AnswerSheetQuery {
  pageNum: number
  pageSize: number
  examId?: Id
  examSubjectId?: Id
  studentId?: Id
  studentNumber?: string
  studentName?: string
  classId?: number
  status?: number
}

export interface AnswerSheetUploadDTO {
  examId: Id
  examSubjectId: Id
  studentId?: Id
  studentNumber?: string
  seatNumber?: string
  imageObjectNames: string[]
  imageOriginalNames?: string[]
}

export interface FileUploadResult {
  fileName: string
  originalName: string
  objectName: string
  fileSize: number
  contentType: string
  url: string
}

// ============ 答题卡 API ============
export function getAnswerSheetPage(params: AnswerSheetQuery) {
  return request.get<PageResult<AnswerSheet>>('/answer-sheet/page', { params })
}

export function getAnswerSheetDetail(id: Id) {
  return request.get<AnswerSheet>(`/answer-sheet/${id}`)
}

export function getAnswerSheetQuestionDetails(id: Id) {
  return request.get<AnswerSheetQuestionDetail[]>(`/answer-sheet/${id}/details`)
}

export function getAnswerSheetQuestionPreview(id: Id, questionId: Id) {
  return request.get<string>(`/answer-sheet/${id}/details/${questionId}/preview`)
}

export function recognizeObjectiveAnswers(id: Id) {
  return request.post<AnswerSheetQuestionDetail[]>(`/answer-sheet/${id}/objective-recognize`)
}

export function updateObjectiveAnswer(id: Id, questionId: Id, data: AnswerSheetObjectiveAnswerDTO) {
  return request.put<AnswerSheetQuestionDetail>(`/answer-sheet/${id}/details/${questionId}/objective-answer`, data)
}

export function updateSubjectiveReviewStatus(id: Id, questionId: Id, status: number) {
  return request.put<AnswerSheetQuestionDetail>(
    `/answer-sheet/${id}/details/${questionId}/subjective-review-status`,
    null,
    { params: { status } }
  )
}

export function updateAiReviewResult(id: Id, questionId: Id, data: AnswerSheetAiReviewResultDTO) {
  return request.put<AnswerSheetQuestionDetail>(
    `/answer-sheet/${id}/details/${questionId}/ai-review-result`,
    data
  )
}

export function rerunSubjectiveReview(id: Id) {
  return request.post<AnswerSheetQuestionDetail[]>(`/answer-sheet/${id}/subjective-review/rerun`)
}

export function rerunAiMarking(id: Id) {
  return request.post<void>(`/answer-sheet/${id}/ai-marking/rerun`)
}

export function createAnswerSheet(data: Partial<AnswerSheet>) {
  return request.post<Id>('/answer-sheet', data)
}

export function updateAnswerSheet(data: Partial<AnswerSheet>) {
  return request.put<void>('/answer-sheet', data)
}

export function deleteAnswerSheet(id: Id) {
  return request.delete<void>(`/answer-sheet/${id}`)
}

export function deleteAnswerSheetBatch(ids: Id[]) {
  return request.delete<void>('/answer-sheet/batch', { data: ids })
}

export function uploadAnswerSheetImages(id: Id, files: FormData) {
  return request.post<AnswerSheet>(`/answer-sheet/${id}/images`, files, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function uploadAnswerSheet(data: AnswerSheetUploadDTO) {
  return request.post<Id>('/answer-sheet/upload', data)
}

export function deleteAnswerSheetImage(imageId: Id) {
  return request.delete<void>(`/answer-sheet/image/${imageId}`)
}

export function getAnswerSheetListByExamSubject(examSubjectId: Id) {
  return request.get<AnswerSheet[]>(`/answer-sheet/list/${examSubjectId}`)
}

export function updateAnswerSheetStatus(id: Id, status: number) {
  return request.put<void>(`/answer-sheet/${id}/status`, null, { params: { status } })
}

export function rerunAnswerSheetRecognition(id: Id) {
  return request.post<void>(`/answer-sheet/${id}/recognize`)
}

// ============ 文件上传 API ============
export function uploadFile(file: File, directory: string = 'common') {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('directory', directory)
  return request.post<FileUploadResult>('/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function uploadFiles(files: File[], directory: string = 'common') {
  const formData = new FormData()
  files.forEach(file => formData.append('files', file))
  formData.append('directory', directory)
  return request.post<FileUploadResult[]>('/file/upload/batch', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function deleteFile(objectName: string) {
  return request.delete<void>('/file', { params: { objectName } })
}

export function getPresignedUrl(objectName: string, expiry: number = 3600) {
  return request.get<string>('/file/presigned-url', { params: { objectName, expiry } })
}
