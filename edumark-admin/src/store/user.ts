import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getCurrentUserInfo, logout as logoutApi } from '@/api/auth'
import type { LoginParams, UserInfo } from '@/api/types'
import router from '@/router'

export const useUserStore = defineStore(
  'user',
  () => {
    // 状态
    const token = ref<string>('')
    const userInfo = ref<UserInfo | null>(null)

    // 计算属性
    const isLoggedIn = computed(() => !!token.value)
    const permissions = computed(() => userInfo.value?.permissions || [])
    const realName = computed(() => userInfo.value?.realName || '')
    const roleName = computed(() => userInfo.value?.roleName || '')

    // 登录
    async function login(loginParams: LoginParams) {
      const res = await loginApi(loginParams)
      token.value = res.data.token
      userInfo.value = res.data.userInfo
      return res
    }

    // 获取用户信息
    async function fetchUserInfo() {
      const res = await getCurrentUserInfo()
      userInfo.value = res.data
      return res
    }

    // 退出登录
    async function logout() {
      try {
        await logoutApi()
      } catch (e) {
        // 忽略错误
      }
      token.value = ''
      userInfo.value = null
      router.push('/login')
    }

    // 检查权限
    function hasPermission(permission: string): boolean {
      if (!permission) return true
      return permissions.value.includes(permission) || permissions.value.includes('*:*:*')
    }

    // 重置状态
    function resetState() {
      token.value = ''
      userInfo.value = null
    }

    return {
      token,
      userInfo,
      isLoggedIn,
      permissions,
      realName,
      roleName,
      login,
      fetchUserInfo,
      logout,
      hasPermission,
      resetState,
    }
  },
  {
    persist: {
      key: 'edumark-user',
      storage: localStorage,
      paths: ['token', 'userInfo'],
    },
  }
)
