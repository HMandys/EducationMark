import { request } from '@/utils/request'
import type { PageResult } from './types'

// 学校类型定义
export interface School {
  id: number
  name: string
  code: string
  type: number
  typeName?: string
  province: string
  city: string
  district: string
  address: string
  phone: string
  contactName: string
  contactPhone: string
  logo: string
  status: number
  sort: number
  remark: string
  gradeCount?: number
  teacherCount?: number
  studentCount?: number
  createTime?: string
}

export interface SchoolQuery {
  pageNum: number
  pageSize: number
  name?: string
  code?: string
  type?: number
  province?: string
  city?: string
  status?: number
}

// 年级类型定义
export interface Grade {
  id: number
  schoolId: number
  schoolName?: string
  name: string
  code: string
  enrollYear: number
  gradeNum: number
  status: number
  sort: number
  remark: string
  classCount?: number
  studentCount?: number
  createTime?: string
}

export interface GradeQuery {
  pageNum: number
  pageSize: number
  schoolId?: number
  name?: string
  enrollYear?: number
  status?: number
}

// 班级类型定义
export interface ClassInfo {
  id: number
  schoolId: number
  schoolName?: string
  gradeId: number
  gradeName?: string
  name: string
  code: string
  classNum: number
  headTeacherId?: number
  headTeacherName?: string
  status: number
  sort: number
  remark: string
  studentCount?: number
  createTime?: string
}

export interface ClassInfoQuery {
  pageNum: number
  pageSize: number
  schoolId?: number
  gradeId?: number
  name?: string
  headTeacherId?: number
  status?: number
}

// 教师类型定义
export interface Teacher {
  id: number
  schoolId: number
  schoolName?: string
  userId?: number
  username?: string
  jobNumber: string
  name: string
  gender: number
  phone: string
  email: string
  idCard: string
  subject: string
  title: string
  entryDate: string
  status: number
  remark: string
  createTime?: string
}

export interface TeacherQuery {
  pageNum: number
  pageSize: number
  schoolId?: number
  jobNumber?: string
  name?: string
  phone?: string
  subject?: string
  status?: number
}

// 学生类型定义
export interface Student {
  id: number
  schoolId: number
  schoolName?: string
  classId: number
  className?: string
  gradeName?: string
  userId?: number
  studentNumber: string
  name: string
  gender: number
  phone: string
  idCard: string
  birthday: string
  enrollDate: string
  bindCode?: string
  status: number
  remark: string
  createTime?: string
}

export interface StudentQuery {
  pageNum: number
  pageSize: number
  schoolId?: number
  classId?: number
  gradeId?: number
  studentNumber?: string
  name?: string
  status?: number
}

// 家长类型定义
export interface Parent {
  id: number
  userId?: number
  username?: string
  name: string
  gender: number
  phone: string
  idCard: string
  status: number
  remark: string
  students?: StudentBind[]
  createTime?: string
}

export interface StudentBind {
  bindId: number
  studentId: number
  studentName: string
  studentNumber: string
  className: string
  relation: number
  relationName?: string
}

export interface ParentQuery {
  pageNum: number
  pageSize: number
  name?: string
  phone?: string
  status?: number
  studentId?: number
}

export interface ParentBindParams {
  studentName: string
  studentNumber: string
  bindCode: string
  relation?: number
}

// ============ 学校 API ============
export function getSchoolPage(params: SchoolQuery) {
  return request.get<PageResult<School>>('/school/page', { params })
}

export function getSchoolDetail(id: number) {
  return request.get<School>(`/school/${id}`)
}

export function createSchool(data: Partial<School>) {
  return request.post<number>('/school', data)
}

export function updateSchool(data: Partial<School>) {
  return request.put<void>('/school', data)
}

export function deleteSchool(id: number) {
  return request.delete<void>(`/school/${id}`)
}

export function deleteSchoolBatch(ids: number[]) {
  return request.delete<void>('/school/batch', { data: ids })
}

export function updateSchoolStatus(id: number, status: number) {
  return request.put<void>(`/school/${id}/status`, null, { params: { status } })
}

export function getSchoolSelectList() {
  return request.get<School[]>('/school/select')
}

// ============ 年级 API ============
export function getGradePage(params: GradeQuery) {
  return request.get<PageResult<Grade>>('/grade/page', { params })
}

export function getGradeDetail(id: number) {
  return request.get<Grade>(`/grade/${id}`)
}

export function createGrade(data: Partial<Grade>) {
  return request.post<number>('/grade', data)
}

export function updateGrade(data: Partial<Grade>) {
  return request.put<void>('/grade', data)
}

export function deleteGrade(id: number) {
  return request.delete<void>(`/grade/${id}`)
}

export function deleteGradeBatch(ids: number[]) {
  return request.delete<void>('/grade/batch', { data: ids })
}

export function updateGradeStatus(id: number, status: number) {
  return request.put<void>(`/grade/${id}/status`, null, { params: { status } })
}

export function getGradeListBySchool(schoolId: number) {
  return request.get<Grade[]>(`/grade/list/${schoolId}`)
}

// ============ 班级 API ============
export function getClassPage(params: ClassInfoQuery) {
  return request.get<PageResult<ClassInfo>>('/class/page', { params })
}

export function getClassDetail(id: number) {
  return request.get<ClassInfo>(`/class/${id}`)
}

export function createClass(data: Partial<ClassInfo>) {
  return request.post<number>('/class', data)
}

export function updateClass(data: Partial<ClassInfo>) {
  return request.put<void>('/class', data)
}

export function deleteClass(id: number) {
  return request.delete<void>(`/class/${id}`)
}

export function deleteClassBatch(ids: number[]) {
  return request.delete<void>('/class/batch', { data: ids })
}

export function updateClassStatus(id: number, status: number) {
  return request.put<void>(`/class/${id}/status`, null, { params: { status } })
}

export function getClassListByGrade(gradeId: number) {
  return request.get<ClassInfo[]>(`/class/list/grade/${gradeId}`)
}

export function getClassListBySchool(schoolId: number) {
  return request.get<ClassInfo[]>(`/class/list/school/${schoolId}`)
}

// ============ 教师 API ============
export function getTeacherPage(params: TeacherQuery) {
  return request.get<PageResult<Teacher>>('/teacher/page', { params })
}

export function getTeacherDetail(id: number) {
  return request.get<Teacher>(`/teacher/${id}`)
}

export function createTeacher(data: Partial<Teacher>) {
  return request.post<number>('/teacher', data)
}

export function updateTeacher(data: Partial<Teacher>) {
  return request.put<void>('/teacher', data)
}

export function deleteTeacher(id: number) {
  return request.delete<void>(`/teacher/${id}`)
}

export function deleteTeacherBatch(ids: number[]) {
  return request.delete<void>('/teacher/batch', { data: ids })
}

export function updateTeacherStatus(id: number, status: number) {
  return request.put<void>(`/teacher/${id}/status`, null, { params: { status } })
}

export function getTeacherListBySchool(schoolId: number) {
  return request.get<Teacher[]>(`/teacher/list/${schoolId}`)
}

// ============ 学生 API ============
export function getStudentPage(params: StudentQuery) {
  return request.get<PageResult<Student>>('/student/page', { params })
}

export function getStudentDetail(id: number) {
  return request.get<Student>(`/student/${id}`)
}

export function createStudent(data: Partial<Student>) {
  return request.post<number>('/student', data)
}

export function updateStudent(data: Partial<Student>) {
  return request.put<void>('/student', data)
}

export function deleteStudent(id: number) {
  return request.delete<void>(`/student/${id}`)
}

export function deleteStudentBatch(ids: number[]) {
  return request.delete<void>('/student/batch', { data: ids })
}

export function updateStudentStatus(id: number, status: number) {
  return request.put<void>(`/student/${id}/status`, null, { params: { status } })
}

export function getStudentListByClass(classId: number) {
  return request.get<Student[]>(`/student/list/${classId}`)
}

export function refreshStudentBindCode(id: number) {
  return request.post<string>(`/student/${id}/refresh-bind-code`)
}

// ============ 家长 API ============
export function getParentPage(params: ParentQuery) {
  return request.get<PageResult<Parent>>('/parent/page', { params })
}

export function getParentDetail(id: number) {
  return request.get<Parent>(`/parent/${id}`)
}

export function createParent(data: Partial<Parent>) {
  return request.post<number>('/parent', data)
}

export function updateParent(data: Partial<Parent>) {
  return request.put<void>('/parent', data)
}

export function deleteParent(id: number) {
  return request.delete<void>(`/parent/${id}`)
}

export function deleteParentBatch(ids: number[]) {
  return request.delete<void>('/parent/batch', { data: ids })
}

export function updateParentStatus(id: number, status: number) {
  return request.put<void>(`/parent/${id}/status`, null, { params: { status } })
}

export function bindStudent(parentId: number, data: ParentBindParams) {
  return request.post<void>(`/parent/${parentId}/bind`, data)
}

export function unbindStudent(parentId: number, studentId: number) {
  return request.delete<void>(`/parent/${parentId}/unbind/${studentId}`)
}

export function getParentBoundStudents(parentId: number) {
  return request.get<StudentBind[]>(`/parent/${parentId}/students`)
}
