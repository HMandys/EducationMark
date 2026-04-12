/**
 * AI 分析接口
 */
import { get } from '@/utils/request'

export interface AiReport {
  studentId: number
  studentName: string
  summary: string
  strengths: string[]
  weaknesses: string[]
  suggestions: string[]
  latestTotalScore: number
  averageTotalScore: number
  trend: string
  providerName: string
  protocol: string
  model: string
  generatedAt: string
}

/**
 * 获取学生 AI 学情分析
 */
export function getAiReport(studentId: number, limit: number = 10) {
  return get<AiReport>(`/app/ai/report/${studentId}`, { limit }, { loadingText: 'AI分析生成中...' })
}
