import { request } from '@/utils/request'
import type { LoginParams, LoginResult, UserInfo } from './types'

/**
 * 登录
 */
export function login(data: LoginParams) {
  return request.post<LoginResult>('/auth/login', data)
}

/**
 * 获取当前用户信息
 */
export function getCurrentUserInfo() {
  return request.get<UserInfo>('/auth/info')
}

/**
 * 退出登录
 */
export function logout() {
  return request.post<void>('/auth/logout')
}
