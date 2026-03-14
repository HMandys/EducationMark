import { request } from '@/utils/request'
import type { PageResult } from './types'

// 考试类型定义
export interface Exam {
  id: number
  schoolId: number
  schoolName?: string
  name: string
  code: string
  type: number
  typeName?: string
  academicYear: string
  semester: number
  gradeId?: number
  gradeName?: string
  startTime?: string
  endTime?: string
  status: number
  statusName?: string
  totalScore: number
  studentCount: number
  subjectCount?: number
  description?: string
  remark?: string
  classes?: ExamClass[]
  subjects?: ExamSubject[]
  createTime?: string
}

export interface ExamClass {
  classId: number
  className?: string
  studentCount?: number
}

export interface ExamQuery {
  pageNum: number
  pageSize: number
  schoolId?: number
  name?: string
  code?: string
  type?: number
  academicYear?: string
  semester?: number
  gradeId?: number
  status?: number
}

// 考试科目类型定义
export interface ExamSubject {
  id: number
  examId: number
  examName?: string
  subjectName: string
  subjectCode?: string
  fullScore: number
  passScore?: number
  excellentScore?: number
  duration?: number
  startTime?: string
  endTime?: string
  sort: number
  status: number
  remark?: string
  questionCount?: number
  paperId?: number
  createTime?: string
}

// 知识点类型定义
export interface KnowledgePoint {
  id: number
  schoolId: number
  schoolName?: string
  subjectName: string
  parentId: number
  parentName?: string
  name: string
  code?: string
  level: number
  path?: string
  sort: number
  status: number
  remark?: string
  children?: KnowledgePoint[]
  createTime?: string
}

export interface KnowledgePointQuery {
  pageNum: number
  pageSize: number
  schoolId?: number
  subjectName?: string
  name?: string
  code?: string
  parentId?: number
  status?: number
}

// ============ 考试 API ============
export function getExamPage(params: ExamQuery) {
  return request.get<PageResult<Exam>>('/exam/page', { params })
}

export function getExamDetail(id: number) {
  return request.get<Exam>(`/exam/${id}`)
}

export function createExam(data: Partial<Exam>) {
  return request.post<number>('/exam', data)
}

export function updateExam(data: Partial<Exam>) {
  return request.put<void>('/exam', data)
}

export function deleteExam(id: number) {
  return request.delete<void>(`/exam/${id}`)
}

export function deleteExamBatch(ids: number[]) {
  return request.delete<void>('/exam/batch', { data: ids })
}

export function updateExamStatus(id: number, status: number) {
  return request.put<void>(`/exam/${id}/status`, null, { params: { status } })
}

export function publishExam(id: number) {
  return request.post<void>(`/exam/${id}/publish`)
}

export function unpublishExam(id: number) {
  return request.post<void>(`/exam/${id}/unpublish`)
}

// ============ 考试科目 API ============
export function getExamSubjectList(examId: number) {
  return request.get<ExamSubject[]>(`/exam-subject/list/${examId}`)
}

export function getExamSubjectDetail(id: number) {
  return request.get<ExamSubject>(`/exam-subject/${id}`)
}

export function createExamSubject(data: Partial<ExamSubject>) {
  return request.post<number>('/exam-subject', data)
}

export function updateExamSubject(data: Partial<ExamSubject>) {
  return request.put<void>('/exam-subject', data)
}

export function deleteExamSubject(id: number) {
  return request.delete<void>(`/exam-subject/${id}`)
}

export function batchCreateExamSubject(examId: number, subjects: Partial<ExamSubject>[]) {
  return request.post<void>(`/exam-subject/batch/${examId}`, subjects)
}

// ============ 知识点 API ============
export function getKnowledgePointPage(params: KnowledgePointQuery) {
  return request.get<PageResult<KnowledgePoint>>('/knowledge-point/page', { params })
}

export function getKnowledgePointDetail(id: number) {
  return request.get<KnowledgePoint>(`/knowledge-point/${id}`)
}

export function createKnowledgePoint(data: Partial<KnowledgePoint>) {
  return request.post<number>('/knowledge-point', data)
}

export function updateKnowledgePoint(data: Partial<KnowledgePoint>) {
  return request.put<void>('/knowledge-point', data)
}

export function deleteKnowledgePoint(id: number) {
  return request.delete<void>(`/knowledge-point/${id}`)
}

export function deleteKnowledgePointBatch(ids: number[]) {
  return request.delete<void>('/knowledge-point/batch', { data: ids })
}

export function getKnowledgePointTree(schoolId: number, subjectName: string) {
  return request.get<KnowledgePoint[]>('/knowledge-point/tree', { params: { schoolId, subjectName } })
}

export function getKnowledgePointListBySubject(schoolId: number, subjectName: string) {
  return request.get<KnowledgePoint[]>('/knowledge-point/list', { params: { schoolId, subjectName } })
}
