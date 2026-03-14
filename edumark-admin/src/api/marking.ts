import request from '@/utils/request'

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
  return request({
    url: '/api/marking/task/page',
    method: 'get',
    params
  })
}

export function getMarkingTaskDetail(id: number) {
  return request({
    url: `/api/marking/task/${id}`,
    method: 'get'
  })
}

export function generateMarkingTasks(examSubjectId: number) {
  return request({
    url: `/api/marking/task/generate/${examSubjectId}`,
    method: 'post'
  })
}

export function deleteMarkingTask(id: number) {
  return request({
    url: `/api/marking/task/${id}`,
    method: 'delete'
  })
}

export function assignMarkingTask(data: MarkingTaskAssignDTO) {
  return request({
    url: '/api/marking/task/assign',
    method: 'post',
    data
  })
}

export function startMarkingTask(id: number) {
  return request({
    url: `/api/marking/task/start/${id}`,
    method: 'post'
  })
}

export function completeMarkingTask(id: number) {
  return request({
    url: `/api/marking/task/complete/${id}`,
    method: 'post'
  })
}

export function listMarkingTasksByExamSubject(examSubjectId: number) {
  return request({
    url: `/api/marking/task/list/${examSubjectId}`,
    method: 'get'
  })
}

// 阅卷工作台 API
export function getMyAssigns() {
  return request({
    url: '/api/marking/my-assigns',
    method: 'get'
  })
}

export function pageMarkingRecords(params: { taskId: number; status?: number; pageNum?: number; pageSize?: number }) {
  return request({
    url: '/api/marking/records',
    method: 'get',
    params
  })
}

export function getNextPendingRecord(taskId: number) {
  return request({
    url: '/api/marking/next-pending',
    method: 'get',
    params: { taskId }
  })
}

export function getMarkingRecordDetail(recordId: number) {
  return request({
    url: `/api/marking/record/${recordId}`,
    method: 'get'
  })
}

export function submitMarkingScore(data: MarkingSubmitDTO) {
  return request({
    url: '/api/marking/submit',
    method: 'post',
    data
  })
}

// 仲裁 API
export function pageArbitrations(params: { taskId: number; status?: number; pageNum?: number; pageSize?: number }) {
  return request({
    url: '/api/marking/arbitrations',
    method: 'get',
    params
  })
}

export function getNextArbitration(taskId: number) {
  return request({
    url: '/api/marking/next-arbitration',
    method: 'get',
    params: { taskId }
  })
}

export function getArbitrationDetail(arbitrationId: number) {
  return request({
    url: `/api/marking/arbitration/${arbitrationId}`,
    method: 'get'
  })
}

export function submitArbitration(data: ArbitrationSubmitDTO) {
  return request({
    url: '/api/marking/arbitration/submit',
    method: 'post',
    data
  })
}

export function autoMarkObjective(examSubjectId: number) {
  return request({
    url: `/api/marking/auto-mark/${examSubjectId}`,
    method: 'post'
  })
}
