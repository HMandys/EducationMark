/**
 * 请求封装
 */
import { useUserStore } from '@/stores/user'

// 基础URL配置
const BASE_URL = '/api'

interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: Record<string, any>
  header?: Record<string, string>
  showLoading?: boolean
  loadingText?: string
}

interface ResponseData<T = any> {
  code: number
  msg: string
  data: T
}

/**
 * 请求函数
 */
export function request<T = any>(options: RequestOptions): Promise<ResponseData<T>> {
  const userStore = useUserStore()

  // 默认参数
  const {
    url,
    method = 'GET',
    data,
    header = {},
    showLoading = true,
    loadingText = '加载中...',
  } = options

  // 添加token
  if (userStore.token) {
    header['Authorization'] = `Bearer ${userStore.token}`
  }

  // 显示加载
  if (showLoading) {
    uni.showLoading({ title: loadingText, mask: true })
  }

  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + url,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        ...header,
      },
      success: (res) => {
        if (showLoading) {
          uni.hideLoading()
        }

        const result = res.data as ResponseData<T>

        // 请求成功
        if (result.code === 200) {
          resolve(result)
        }
        // token过期
        else if (result.code === 401) {
          userStore.logout()
          uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
          uni.reLaunch({ url: '/pages/login/index' })
          reject(result)
        }
        // 其他错误
        else {
          uni.showToast({ title: result.msg || '请求失败', icon: 'none' })
          reject(result)
        }
      },
      fail: (err) => {
        if (showLoading) {
          uni.hideLoading()
        }
        uni.showToast({ title: '网络请求失败', icon: 'none' })
        reject(err)
      },
    })
  })
}

/**
 * GET请求
 */
export function get<T = any>(url: string, data?: Record<string, any>, options?: Partial<RequestOptions>) {
  return request<T>({ url, method: 'GET', data, ...options })
}

/**
 * POST请求
 */
export function post<T = any>(url: string, data?: Record<string, any>, options?: Partial<RequestOptions>) {
  return request<T>({ url, method: 'POST', data, ...options })
}

/**
 * PUT请求
 */
export function put<T = any>(url: string, data?: Record<string, any>, options?: Partial<RequestOptions>) {
  return request<T>({ url, method: 'PUT', data, ...options })
}

/**
 * DELETE请求
 */
export function del<T = any>(url: string, data?: Record<string, any>, options?: Partial<RequestOptions>) {
  return request<T>({ url, method: 'DELETE', data, ...options })
}

export default {
  request,
  get,
  post,
  put,
  del,
}
