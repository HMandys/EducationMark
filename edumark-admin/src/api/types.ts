export type Id = string | number

/**
 * 通用响应结果
 */
export interface Result<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

/**
 * 分页结果
 */
export interface PageResult<T = any> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
  pages: number
}

/**
 * 分页查询参数
 */
export interface PageQuery {
  pageNum?: number
  pageSize?: number
  orderBy?: string
  orderType?: 'asc' | 'desc'
}

/**
 * 登录参数
 */
export interface LoginParams {
  username: string
  password: string
}

/**
 * 登录响应
 */
export interface LoginResult {
  token: string
  tokenType: string
  expiresIn: number
  userInfo: UserInfo
}

/**
 * 用户信息
 */
export interface UserInfo {
  userId: number
  username: string
  realName: string
  avatar: string
  roleCode: string
  roleName: string
  schoolId: number
  schoolName: string
  permissions: string[]
}

/**
 * 菜单项
 */
export interface MenuItem {
  id: number
  parentId: number
  permissionName: string
  permissionCode: string
  permissionType: number
  path: string
  component: string
  icon: string
  sort: number
  visible: number
  status: number
  children?: MenuItem[]
}
