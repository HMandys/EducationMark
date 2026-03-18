import { request } from '@/utils/request'
import type { MenuItem, PageResult } from './types'

export interface SysUserQuery {
  pageNum: number
  pageSize: number
  username?: string
  realName?: string
  phone?: string
  userType?: number
  schoolId?: number
  roleId?: number
  status?: number
}

export interface SysUser {
  id: number
  username: string
  password?: string
  realName: string
  phone?: string
  email?: string
  avatar?: string
  gender?: number
  userType?: number
  schoolId?: number
  schoolName?: string
  status?: number
  remark?: string
  roleIds?: number[]
  roleNames?: string[]
  createTime?: string
}

export interface SysRole {
  id: number
  roleName: string
  roleCode: string
  description?: string
  sort?: number
  status?: number
  dataScope?: number
  permissionIds?: number[]
  createTime?: string
}

export interface SysRoleQuery {
  pageNum: number
  pageSize: number
  roleName?: string
  roleCode?: string
  status?: number
  dataScope?: number
}

export interface SysPermission {
  id: number
  parentId?: number
  permissionName: string
  permissionCode?: string
  permissionType?: number
  path?: string
  component?: string
  icon?: string
  sort?: number
  visible?: number
  status?: number
  children?: SysPermission[]
}

export interface SysPermissionQuery {
  pageNum: number
  pageSize: number
  permissionName?: string
  permissionCode?: string
  permissionType?: number
  status?: number
  visible?: number
  parentId?: number
}

export type RouteMenuItem = MenuItem

export function getUserPage(params: SysUserQuery) {
  return request.get<PageResult<SysUser>>('/system/user/page', { params })
}

export function getUserDetail(id: number) {
  return request.get<SysUser>(`/system/user/${id}`)
}

export function createUser(data: Partial<SysUser>) {
  return request.post<number>('/system/user', data)
}

export function updateUser(data: Partial<SysUser>) {
  return request.put<void>('/system/user', data)
}

export function deleteUser(id: number) {
  return request.delete<void>(`/system/user/${id}`)
}

export function deleteUserBatch(ids: number[]) {
  return request.delete<void>('/system/user/batch', { data: ids })
}

export function resetUserPassword(id: number, newPassword: string) {
  return request.put<void>(`/system/user/${id}/password/reset`, null, {
    params: { newPassword },
  })
}

export function updateUserStatus(id: number, status: number) {
  return request.put<void>(`/system/user/${id}/status`, null, { params: { status } })
}

export function getRoleList() {
  return request.get<SysRole[]>('/system/role/list')
}

export function getRolePage(params: SysRoleQuery) {
  return request.get<PageResult<SysRole>>('/system/role/page', { params })
}

export function getRoleDetail(id: number) {
  return request.get<SysRole>(`/system/role/${id}`)
}

export function createRole(data: Partial<SysRole>) {
  return request.post<number>('/system/role', data)
}

export function updateRole(data: Partial<SysRole>) {
  return request.put<void>('/system/role', data)
}

export function deleteRole(id: number) {
  return request.delete<void>(`/system/role/${id}`)
}

export function updateRoleStatus(id: number, status: number) {
  return request.put<void>(`/system/role/${id}/status`, null, { params: { status } })
}

export function getPermissionTree() {
  return request.get<SysPermission[]>('/system/permission/tree')
}

export function getPermissionPage(params: SysPermissionQuery) {
  return request.get<PageResult<SysPermission>>('/system/permission/page', { params })
}

export function getPermissionDetail(id: number) {
  return request.get<SysPermission>(`/system/permission/${id}`)
}

export function createPermission(data: Partial<SysPermission>) {
  return request.post<number>('/system/permission', data)
}

export function updatePermission(data: Partial<SysPermission>) {
  return request.put<void>('/system/permission', data)
}

export function deletePermission(id: number) {
  return request.delete<void>(`/system/permission/${id}`)
}

export function updatePermissionStatus(id: number, status: number) {
  return request.put<void>(`/system/permission/${id}/status`, null, { params: { status } })
}
