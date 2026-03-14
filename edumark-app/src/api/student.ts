/**
 * 学生相关接口
 */
import { post, get, del } from '@/utils/request'

// 学生信息
export interface Student {
  id: number
  name: string
  studentNumber: string
  className: string
  classId: number
  gradeName: string
  gradeId: number
  schoolName: string
  schoolId: number
  bindTime?: string
}

// 绑定参数
export interface BindParams {
  studentName: string
  studentNumber: string
  bindCode: string
}

/**
 * 获取已绑定的学生列表（家长）
 */
export function getBindStudents() {
  return get<Student[]>('/app/student/bind-list')
}

/**
 * 绑定学生（家长）
 */
export function bindStudent(data: BindParams) {
  return post<Student>('/app/student/bind', data)
}

/**
 * 解绑学生（家长）
 */
export function unbindStudent(studentId: number) {
  return del<void>(`/app/student/unbind/${studentId}`)
}

/**
 * 获取学生信息（学生本人）
 */
export function getStudentInfo() {
  return get<Student>('/app/student/info')
}
