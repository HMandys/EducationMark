/**
 * 本地存储工具
 */

const TOKEN_KEY = 'edumark_token'
const USER_INFO_KEY = 'edumark_user_info'
const CURRENT_STUDENT_KEY = 'edumark_current_student'

/**
 * 保存Token
 */
export function setToken(token: string) {
  uni.setStorageSync(TOKEN_KEY, token)
}

/**
 * 获取Token
 */
export function getToken(): string {
  return uni.getStorageSync(TOKEN_KEY) || ''
}

/**
 * 移除Token
 */
export function removeToken() {
  uni.removeStorageSync(TOKEN_KEY)
}

/**
 * 保存用户信息
 */
export function setUserInfo(userInfo: any) {
  uni.setStorageSync(USER_INFO_KEY, JSON.stringify(userInfo))
}

/**
 * 获取用户信息
 */
export function getUserInfo(): any {
  const data = uni.getStorageSync(USER_INFO_KEY)
  return data ? JSON.parse(data) : null
}

/**
 * 移除用户信息
 */
export function removeUserInfo() {
  uni.removeStorageSync(USER_INFO_KEY)
}

/**
 * 保存当前选中的学生
 */
export function setCurrentStudent(student: any) {
  uni.setStorageSync(CURRENT_STUDENT_KEY, JSON.stringify(student))
}

/**
 * 获取当前选中的学生
 */
export function getCurrentStudent(): any {
  const data = uni.getStorageSync(CURRENT_STUDENT_KEY)
  return data ? JSON.parse(data) : null
}

/**
 * 移除当前选中的学生
 */
export function removeCurrentStudent() {
  uni.removeStorageSync(CURRENT_STUDENT_KEY)
}

/**
 * 清除所有存储
 */
export function clearStorage() {
  removeToken()
  removeUserInfo()
  removeCurrentStudent()
}

export default {
  setToken,
  getToken,
  removeToken,
  setUserInfo,
  getUserInfo,
  removeUserInfo,
  setCurrentStudent,
  getCurrentStudent,
  removeCurrentStudent,
  clearStorage,
}
