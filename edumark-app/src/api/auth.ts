/**
 * 认证相关接口
 */
import { post, get } from '@/utils/request'

// 登录参数
export interface LoginParams {
  phone: string
  password: string
  userType: 'parent' | 'student'
}

// 登录结果
export interface LoginResult {
  token: string
  userInfo: {
    id: number
    username: string
    nickname: string
    phone: string
    avatar?: string
    role: string
  }
}

// 注册参数
export interface RegisterParams {
  phone: string
  password: string
  nickname: string
  userType: 'parent'
}

/**
 * 家长/学生登录
 */
export function login(data: LoginParams) {
  return post<LoginResult>('/app/auth/login', data)
}

/**
 * 注册
 */
export function register(data: RegisterParams) {
  return post<LoginResult>('/app/auth/register', data)
}

/**
 * 发送验证码
 */
export function sendVerifyCode(phone: string) {
  return post<void>('/app/auth/send-code', { phone })
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return get<LoginResult['userInfo']>('/app/auth/user-info')
}

/**
 * 修改密码
 */
export function changePassword(data: { oldPassword: string; newPassword: string }) {
  return post<void>('/app/auth/change-password', data)
}

/**
 * 退出登录
 */
export function logout() {
  return post<void>('/app/auth/logout')
}
