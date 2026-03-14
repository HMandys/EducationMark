/**
 * 考试相关接口
 */
import { get } from '@/utils/request'

// 考试信息
export interface Exam {
  id: number
  name: string
  code: string
  typeName: string
  academicYear: string
  semester: number
  gradeName: string
  startTime?: string
  endTime?: string
  status: number
  statusName: string
  totalScore: number
  subjectCount: number
}

// 科目成绩
export interface SubjectScore {
  id: number
  examSubjectId: number
  subjectName: string
  fullScore: number
  score: number
  objectiveScore?: number
  subjectiveScore?: number
  classRank?: number
  gradeRank?: number
}

// 考试成绩
export interface ExamScore {
  id: number
  examId: number
  examName: string
  studentId: number
  studentName: string
  studentNumber: string
  className: string
  totalScore: number
  subjectCount: number
  classRank?: number
  gradeRank?: number
  subjectScores?: SubjectScore[]
  createTime: string
}

// 成绩统计
export interface ScoreStatistics {
  id: number
  examId: number
  examName?: string
  examSubjectId?: number
  subjectName?: string
  classId?: number
  className?: string
  statType: number
  studentCount: number
  fullScore?: number
  maxScore: number
  minScore: number
  avgScore: number
  passCount?: number
  passRate?: number
  excellentCount?: number
  excellentRate?: number
}

/**
 * 获取学生的考试列表
 */
export function getExamList(studentId: number) {
  return get<Exam[]>(`/app/exam/list/${studentId}`)
}

/**
 * 获取考试详情
 */
export function getExamDetail(examId: number) {
  return get<Exam>(`/app/exam/${examId}`)
}

/**
 * 获取学生的考试成绩
 */
export function getStudentExamScore(examId: number, studentId: number) {
  return get<ExamScore>(`/app/score/${examId}/${studentId}`)
}

/**
 * 获取学生的最新成绩列表
 */
export function getRecentScores(studentId: number, limit: number = 5) {
  return get<ExamScore[]>(`/app/score/recent/${studentId}`, { limit })
}

/**
 * 获取考试统计信息（班级）
 */
export function getExamStatistics(examId: number, classId: number) {
  return get<ScoreStatistics[]>(`/app/score/statistics/${examId}`, { classId })
}
