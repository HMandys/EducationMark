/**
 * 答题卡相关接口
 */
import { get } from '@/utils/request'

// 答题卡图片
export interface AnswerSheetImage {
  id: number
  answerSheetId: number
  imageUrl: string
  pageNo: number
  imageType: number
  sort: number
}

// 答题卡信息
export interface AnswerSheet {
  id: number
  examId: number
  examSubjectId: number
  subjectName: string
  studentId: number
  totalScore?: number
  objectiveScore?: number
  subjectiveScore?: number
  status: number
  images: AnswerSheetImage[]
}

/**
 * 获取学生的答题卡列表
 */
export function getAnswerSheetList(examId: number, studentId: number) {
  return get<AnswerSheet[]>(`/app/answer-sheet/list/${examId}/${studentId}`)
}

/**
 * 获取答题卡详情
 */
export function getAnswerSheetDetail(answerSheetId: number) {
  return get<AnswerSheet>(`/app/answer-sheet/${answerSheetId}`)
}

/**
 * 获取答题卡图片列表
 */
export function getAnswerSheetImages(answerSheetId: number) {
  return get<AnswerSheetImage[]>(`/app/answer-sheet/images/${answerSheetId}`)
}
