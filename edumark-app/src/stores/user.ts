import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  getToken,
  setToken,
  removeToken,
  getUserInfo,
  setUserInfo,
  removeUserInfo,
  getCurrentStudent,
  setCurrentStudent,
  removeCurrentStudent,
  clearStorage,
} from '@/utils/storage'

export interface UserInfo {
  id: number
  username: string
  nickname: string
  phone: string
  avatar?: string
  role: string // 'parent' | 'student'
}

export interface Student {
  id: number
  name: string
  studentNumber: string
  className: string
  gradeName: string
  schoolName: string
}

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>('')
  const userInfo = ref<UserInfo | null>(null)
  const students = ref<Student[]>([])
  const currentStudent = ref<Student | null>(null)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isParent = computed(() => userInfo.value?.role === 'parent')
  const isStudent = computed(() => userInfo.value?.role === 'student')

  // 检查登录状态
  function checkLogin() {
    const storedToken = getToken()
    if (storedToken) {
      token.value = storedToken
      userInfo.value = getUserInfo()
      currentStudent.value = getCurrentStudent()
    }
  }

  // 设置Token
  function setTokenValue(newToken: string) {
    token.value = newToken
    setToken(newToken)
  }

  // 设置用户信息
  function setUserInfoValue(info: UserInfo) {
    userInfo.value = info
    setUserInfo(info)
  }

  // 设置学生列表
  function setStudents(list: Student[]) {
    students.value = list
    // 如果只有一个学生，自动选中
    if (list.length === 1) {
      setCurrentStudentValue(list[0])
    }
  }

  // 设置当前选中的学生
  function setCurrentStudentValue(student: Student) {
    currentStudent.value = student
    setCurrentStudent(student)
  }

  // 登出
  function logout() {
    token.value = ''
    userInfo.value = null
    students.value = []
    currentStudent.value = null
    clearStorage()
  }

  return {
    token,
    userInfo,
    students,
    currentStudent,
    isLoggedIn,
    isParent,
    isStudent,
    checkLogin,
    setTokenValue,
    setUserInfoValue,
    setStudents,
    setCurrentStudentValue,
    logout,
  }
})
