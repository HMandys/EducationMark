import { request } from '@/utils/request'
import type { Id, PageResult } from './types'

// 阅卷任务相关接口
export interface MarkingTaskQuery {
  pageNum?: number
  pageSize?: number
  examId?: Id
  examSubjectId?: Id
  status?: number
}

export interface MarkingTaskVO {
  id: Id
  examId: Id
  examName: string
  examSubjectId: Id
  subjectName: string
  questionId: Id
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
  accessCode?: string
  secondAccessCode?: string
  accessCodeExpireTime?: string
  startTime: string
  endTime: string
  remark: string
  createTime: string
  assigns?: MarkingTaskAssignVO[]
}

export interface MarkingTaskAssignVO {
  id: Id
  taskId: Id
  taskName: string
  teacherId: Id
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
  teacherId: Id
  assignCount?: number
  markingRole: number
}

export interface MarkingTaskAssignDTO {
  taskId: Id
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
  originalImageUrl?: string
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
  originalImageUrl?: string
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

export function getMarkingTaskDetail(id: Id) {
  return request.get<MarkingTaskVO>(`/marking/task/${id}`)
}

export function generateMarkingTasks(examSubjectId: Id) {
  return request.post<void>(`/marking/task/generate/${examSubjectId}`)
}

export function deleteMarkingTask(id: Id) {
  return request.delete<void>(`/marking/task/${id}`)
}

export function assignMarkingTask(data: MarkingTaskAssignDTO) {
  return request.post<void>('/marking/task/assign', data)
}

export function startMarkingTask(id: Id) {
  return request.post<void>(`/marking/task/start/${id}`)
}

export function completeMarkingTask(id: Id) {
  return request.post<void>(`/marking/task/complete/${id}`)
}

export function listMarkingTasksByExamSubject(examSubjectId: Id) {
  return request.get<MarkingTaskVO[]>(`/marking/task/list/${examSubjectId}`)
}

// 阅卷工作台 API
export function getMyAssigns() {
  return request.get<MarkingTaskAssignVO[]>('/marking/my-assigns')
}

export function pageMarkingRecords(params: { taskId: number; status?: number; pageNum?: number; pageSize?: number }) {
  return request.get<PageResult<MarkingRecordVO>>('/marking/records', { params })
}

export function getNextPendingRecord(taskId: Id) {
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

export function getNextArbitration(taskId: Id) {
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

// ===================== 阅卷码访问 API =====================

export interface MarkingSessionVO {
  sessionToken: string
  taskId: Id
  examName: string
  subjectName: string
  questionNo: string
  fullScore: number
  markingRole: number
  markingRoleName: string
  totalCount: number
  completedCount: number
  pendingCount: number
}

export interface MarkingItemVO {
  recordId: number
  answerSheetId: number
  questionId: number
  questionImage: string
  fullScore: number
  questionNo: string
  currentIndex: number
  totalCount: number
  annotations?: string
}

export interface ScoreSubmitDTO {
  recordId: number
  score: number
  comment?: string
  annotations?: string
}

// 阅卷码登录（免登录）
export function accessCodeLogin(accessCode: string) {
  return request.post<MarkingSessionVO>('/marking/access/login', { accessCode })
}

// 获取任务信息
export function getAccessTaskInfo(sessionToken: string) {
  return request.get<MarkingSessionVO>('/marking/access/task', {
    headers: { 'X-Session-Token': sessionToken }
  })
}

// 获取下一份待阅记录
export function getNextAccessItem(sessionToken: string) {
  return request.get<MarkingItemVO>('/marking/access/next', {
    headers: { 'X-Session-Token': sessionToken }
  })
}

// 提交评分
export function submitAccessScore(sessionToken: string, data: ScoreSubmitDTO) {
  return request.post<boolean>('/marking/access/submit', data, {
    headers: { 'X-Session-Token': sessionToken }
  })
}

// 跳过当前记录
export function skipAccessRecord(sessionToken: string, recordId: number) {
  return request.post<boolean>('/marking/access/skip', { recordId }, {
    headers: { 'X-Session-Token': sessionToken }
  })
}

// 生成阅卷码（需要管理员权限）
export function generateAccessCode(taskId: Id) {
  return request.post<MarkingTaskVO>(`/marking/access/generate/${taskId}`)
}

// 刷新阅卷码有效期
export function refreshAccessCode(taskId: Id, hours: number = 24) {
  return request.post<void>(`/marking/access/refresh/${taskId}?hours=${hours}`)
}

// 获取任务详情（含阅卷码）
export function getTaskWithAccessCode(taskId: Id) {
  return request.get<MarkingTaskVO>(`/marking/access/task/${taskId}`)
}
