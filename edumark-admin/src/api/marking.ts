import { request } from '@/utils/request'
import type { PageResult } from './types'

// 阅卷任务相关接口
export interface MarkingTaskQuery {
  pageNum?: number
  pageSize?: number
  examId?: number
  examSubjectId?: number
  status?: number
}

export interface MarkingTaskVO {
  id: number
  examId: number
  examName: string
  examSubjectId: number
  subjectName: string
  questionId: number
  questionNo: string
  name: string
  taskType: number
  totalCount: number
  completedCount: number
  pendingCount: number
  enableDoubleMarking: number
  doubleMarkingThreshold: number
  status: number
  statusName: string
  startTime: string
  endTime: string
  remark: string
  createTime: string
  assigns?: MarkingTaskAssignVO[]
}

export interface MarkingTaskAssignVO {
  id: number
  taskId: number
  taskName: string
  teacherId: number
  teacherName: string
  assignCount: number
  completedCount: number
  markingRole: number
  markingRoleName: string
  status: number
  statusName: string
  examName: string
  subjectName: string
  questionNo: string
  taskStatus: number
}

export interface TeacherAssign {
  teacherId: number
  assignCount?: number
  markingRole: number
}

export interface MarkingTaskAssignDTO {
  taskId: number
  assigns: TeacherAssign[]
}

// 阅卷记录相关接口
export interface MarkingRecordVO {
  id: number
  taskId: number
  answerSheetId: number
  questionId: number
  questionNo: string
  studentId: number
  studentName: string
  studentNumber: string
  teacherId: number
  teacherName: string
  markingRole: number
  markingRoleName: string
  score: number
  fullScore: number
  comment: string
  markingTime: string
  status: number
  statusName: string
  answerImages: string[]
  answerImageUrl: string
  createTime: string
}

export interface MarkingSubmitDTO {
  recordId: number
  score: number
  comment?: string
}

// 仲裁记录相关接口
export interface MarkingArbitrationVO {
  id: number
  taskId: number
  answerSheetId: number
  questionId: number
  questionNo: string
  studentId: number
  studentName: string
  firstScore: number
  firstTeacherId: number
  firstTeacherName: string
  secondScore: number
  secondTeacherId: number
  secondTeacherName: string
  scoreDiff: number
  arbitrationTeacherId: number
  arbitrationTeacherName: string
  arbitrationScore: number
  arbitrationTime: string
  arbitrationComment: string
  status: number
  statusName: string
  answerImages: string[]
  answerImageUrl: string
  createTime: string
}

export interface ArbitrationSubmitDTO {
  arbitrationId: number
  score: number
  comment?: string
}

// 阅卷任务管理 API
export function pageMarkingTasks(params: MarkingTaskQuery) {
  return request.get<PageResult<MarkingTaskVO>>('/marking/task/page', { params })
}

export function getMarkingTaskDetail(id: number) {
  return request.get<MarkingTaskVO>(`/marking/task/${id}`)
}

export function generateMarkingTasks(examSubjectId: number) {
  return request.post<void>(`/marking/task/generate/${examSubjectId}`)
}

export function deleteMarkingTask(id: number) {
  return request.delete<void>(`/marking/task/${id}`)
}

export function assignMarkingTask(data: MarkingTaskAssignDTO) {
  return request.post<void>('/marking/task/assign', data)
}

export function startMarkingTask(id: number) {
  return request.post<void>(`/marking/task/start/${id}`)
}

export function completeMarkingTask(id: number) {
  return request.post<void>(`/marking/task/complete/${id}`)
}

export function listMarkingTasksByExamSubject(examSubjectId: number) {
  return request.get<MarkingTaskVO[]>(`/marking/task/list/${examSubjectId}`)
}

// 阅卷工作台 API
export function getMyAssigns() {
  return request.get<MarkingTaskAssignVO[]>('/marking/my-assigns')
}

export function pageMarkingRecords(params: { taskId: number; status?: number; pageNum?: number; pageSize?: number }) {
  return request.get<PageResult<MarkingRecordVO>>('/marking/records', { params })
}

export function getNextPendingRecord(taskId: number) {
  return request.get<MarkingRecordVO>('/marking/next-pending', { params: { taskId } })
}

export function getMarkingRecordDetail(recordId: number) {
  return request.get<MarkingRecordVO>(`/marking/record/${recordId}`)
}

export function submitMarkingScore(data: MarkingSubmitDTO) {
  return request.post<void>('/marking/submit', data)
}

// 仲裁 API
export function pageArbitrations(params: { taskId: number; status?: number; pageNum?: number; pageSize?: number }) {
  return request.get<PageResult<MarkingArbitrationVO>>('/marking/arbitrations', { params })
}

export function getNextArbitration(taskId: number) {
  return request.get<MarkingArbitrationVO>('/marking/next-arbitration', { params: { taskId } })
}

export function getArbitrationDetail(arbitrationId: number) {
  return request.get<MarkingArbitrationVO>(`/marking/arbitration/${arbitrationId}`)
}

export function submitArbitration(data: ArbitrationSubmitDTO) {
  return request.post<void>('/marking/arbitration/submit', data)
}

export function autoMarkObjective(examSubjectId: number) {
  return request.post<void>(`/marking/auto-mark/${examSubjectId}`)
}
