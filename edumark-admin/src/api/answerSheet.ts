import { request } from '@/utils/request'
import type { PageResult } from './types'

// 答题卡类型定义
export interface AnswerSheet {
  id: number
  examId: number
  examName?: string
  examSubjectId: number
  subjectName?: string
  studentId?: number
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
  id: number
  answerSheetId: number
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
  id?: number
  answerSheetId: number
  questionId: number
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
  previewAvailable?: boolean
}

export interface AnswerSheetQuery {
  pageNum: number
  pageSize: number
  examId?: number
  examSubjectId?: number
  studentId?: number
  studentNumber?: string
  studentName?: string
  classId?: number
  status?: number
}

export interface AnswerSheetUploadDTO {
  examId: number
  examSubjectId: number
  studentId?: number
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

export function getAnswerSheetDetail(id: number) {
  return request.get<AnswerSheet>(`/answer-sheet/${id}`)
}

export function getAnswerSheetQuestionDetails(id: number) {
  return request.get<AnswerSheetQuestionDetail[]>(`/answer-sheet/${id}/details`)
}

export function getAnswerSheetQuestionPreview(id: number, questionId: number) {
  return request.get<string>(`/answer-sheet/${id}/details/${questionId}/preview`)
}

export function createAnswerSheet(data: Partial<AnswerSheet>) {
  return request.post<number>('/answer-sheet', data)
}

export function updateAnswerSheet(data: Partial<AnswerSheet>) {
  return request.put<void>('/answer-sheet', data)
}

export function deleteAnswerSheet(id: number) {
  return request.delete<void>(`/answer-sheet/${id}`)
}

export function deleteAnswerSheetBatch(ids: number[]) {
  return request.delete<void>('/answer-sheet/batch', { data: ids })
}

export function uploadAnswerSheetImages(id: number, files: FormData) {
  return request.post<AnswerSheet>(`/answer-sheet/${id}/images`, files, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function uploadAnswerSheet(data: AnswerSheetUploadDTO) {
  return request.post<number>('/answer-sheet/upload', data)
}

export function deleteAnswerSheetImage(imageId: number) {
  return request.delete<void>(`/answer-sheet/image/${imageId}`)
}

export function getAnswerSheetListByExamSubject(examSubjectId: number) {
  return request.get<AnswerSheet[]>(`/answer-sheet/list/${examSubjectId}`)
}

export function updateAnswerSheetStatus(id: number, status: number) {
  return request.put<void>(`/answer-sheet/${id}/status`, null, { params: { status } })
}

export function rerunAnswerSheetRecognition(id: number) {
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
