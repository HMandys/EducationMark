import { request } from '@/utils/request'
import type { Id, PageResult } from './types'

// 成绩查询参数
export interface ScoreQuery {
  pageNum: number
  pageSize: number
  examId?: Id
  examSubjectId?: Id
  classId?: number
  studentName?: string
  studentNumber?: string
}

// 考试成绩
export interface ExamScore {
  id: Id
  examId: Id
  examName?: string
  studentId: number
  studentName?: string
  studentNumber?: string
  classId: number
  className?: string
  totalScore: number
  subjectCount: number
  classRank?: number
  gradeRank?: number
  subjectScores?: SubjectScore[]
  createTime?: string
}

// 科目成绩
export interface SubjectScore {
  id: Id
  examSubjectId: Id
  subjectName?: string
  fullScore?: number
  studentId: number
  studentName?: string
  studentNumber?: string
  className?: string
  score: number
  objectiveScore?: number
  subjectiveScore?: number
  classRank?: number
  gradeRank?: number
}

// 分数段
export interface ScoreSegment {
  range: string
  count: number
}

// 成绩统计
export interface ScoreStatistics {
  id: Id
  examId: Id
  examName?: string
  examSubjectId?: Id
  subjectName?: string
  classId?: number
  className?: string
  statType: number // 1-班级科目 2-班级总分 3-年级科目 4-年级总分
  studentCount: number
  fullScore?: number
  maxScore: number
  minScore: number
  avgScore: number
  passCount?: number
  passRate?: number
  excellentCount?: number
  excellentRate?: number
  scoreSegments?: Record<string, number>
  segmentList?: ScoreSegment[]
}

export interface ScorePublishCheck {
  examId: Id
  examName?: string
  examStatus?: number
  canPublish: boolean
  subjectCount: number
  answerSheetCount: number
  completedAnswerSheetCount: number
  pendingRecognitionCount: number
  recognitionExceptionCount: number
  pendingMarkingAnswerSheetCount: number
  markingTaskCount: number
  unfinishedTaskCount: number
  pendingArbitrationCount: number
  examScoreCount: number
  subjectScoreCount: number
  statisticsCount: number
  blockingItems: string[]
  warningItems: string[]
}

// 分页查询考试成绩
export function getExamScorePage(params: ScoreQuery) {
  return request.get<PageResult<ExamScore>>('/score/exam/page', { params })
}

// 分页查询科目成绩
export function getSubjectScorePage(params: ScoreQuery) {
  return request.get<PageResult<SubjectScore>>('/score/subject/page', { params })
}

// 查询学生考试成绩详情
export function getStudentExamScore(examId: Id, studentId: Id) {
  return request.get<ExamScore>(`/score/student/${examId}/${studentId}`)
}

// 查询统计数据
export function getScoreStatistics(examId: Id, examSubjectId?: Id, classId?: number) {
  return request.get<ScoreStatistics[]>(`/score/statistics/${examId}`, {
    params: { examSubjectId, classId },
  })
}

export function getScorePublishCheck(examId: Id) {
  return request.get<ScorePublishCheck>(`/score/publish-check/${examId}`)
}

// 汇总成绩
export function aggregateScores(examId: Id) {
  return request.post<void>(`/score/aggregate/${examId}`)
}

// 计算排名
export function calculateRanking(examId: Id) {
  return request.post<void>(`/score/ranking/${examId}`)
}

// 计算统计
export function calculateStatistics(examId: Id) {
  return request.post<void>(`/score/statistics/${examId}`)
}

// 发布成绩
export function publishScore(examId: Id, userId: Id) {
  return request.post<void>(`/score/publish/${examId}`, null, { params: { userId } })
}

// 撤回成绩
export function unpublishScore(examId: Id, userId: Id) {
  return request.post<void>(`/score/unpublish/${examId}`, null, { params: { userId } })
}

// 导出成绩Excel
export function exportScoreExcel(examId: Id, classId?: number) {
  return request.getRaw<Blob>(`/score/export/${examId}`, {
    params: { classId },
    responseType: 'blob',
  })
}
