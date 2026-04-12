import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  getToken,
  setToken,
  getUserInfo,
  setUserInfo,
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
  classId?: number
  className: string
  gradeId?: number
  gradeName: string
  schoolId?: number
  schoolName: string
  bindTime?: string
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>('')
  const userInfo = ref<UserInfo | null>(null)
  const students = ref<Student[]>([])
  const currentStudent = ref<Student | null>(null)

  const isLoggedIn = computed(() => !!token.value)
  const isParent = computed(() => userInfo.value?.role === 'parent')
  const isStudent = computed(() => userInfo.value?.role === 'student')

  function checkLogin() {
    const storedToken = getToken()
    if (storedToken) {
      token.value = storedToken
      userInfo.value = getUserInfo()
      currentStudent.value = getCurrentStudent()
    }
  }

  function setTokenValue(newToken: string) {
    token.value = newToken
    setToken(newToken)
  }

  function setUserInfoValue(info: UserInfo) {
    userInfo.value = info
    setUserInfo(info)
  }

  function setStudents(list: Student[]) {
    students.value = list

    if (list.length === 0) {
      setCurrentStudentValue(null)
      return
    }

    const matchedStudent = currentStudent.value
      ? list.find(student => student.id === currentStudent.value?.id) || null
      : null

    if (matchedStudent) {
      setCurrentStudentValue(matchedStudent)
      return
    }

    setCurrentStudentValue(list[0])
  }

  function setCurrentStudentValue(student: Student | null) {
    currentStudent.value = student

    if (student) {
      setCurrentStudent(student)
      return
    }

    removeCurrentStudent()
  }

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
