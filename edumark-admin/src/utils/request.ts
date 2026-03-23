import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import NProgress from 'nprogress'
import { useUserStore } from '@/store/user'
import router from '@/router'
import type { Result } from '@/api/types'

export interface RequestConfig extends AxiosRequestConfig {
  silentError?: boolean
}

function quoteLargeIntegers(json: string) {
  let result = ''
  let inString = false
  let escaped = false

  for (let index = 0; index < json.length; index++) {
    const char = json[index]

    if (inString) {
      result += char
      if (escaped) {
        escaped = false
      } else if (char === '\\') {
        escaped = true
      } else if (char === '"') {
        inString = false
      }
      continue
    }

    if (char === '"') {
      inString = true
      result += char
      continue
    }

    const previousChar = result.trimEnd().slice(-1)
    const canStartNumber = !previousChar || previousChar === ':' || previousChar === ',' || previousChar === '['
    const isNegativeNumber = char === '-' && /\d/.test(json[index + 1] || '')
    const isPositiveNumber = /\d/.test(char)

    if (canStartNumber && (isNegativeNumber || isPositiveNumber)) {
      let end = index + (char === '-' ? 1 : 0)
      while (/\d/.test(json[end] || '')) {
        end++
      }

      const numberText = json.slice(index, end)
      const nextChar = json[end]
      const isInteger = nextChar !== '.' && nextChar !== 'e' && nextChar !== 'E'
      const digitCount = numberText.startsWith('-') ? numberText.length - 1 : numberText.length

      if (isInteger && digitCount >= 16) {
        result += `"${numberText}"`
        index = end - 1
        continue
      }
    }

    result += char
  }

  return result
}

function parseJsonSafely(data: unknown) {
  if (typeof data !== 'string') {
    return data
  }

  const trimmed = data.trim()
  if (!trimmed) {
    return data
  }

  if (!trimmed.startsWith('{') && !trimmed.startsWith('[')) {
    return data
  }

  try {
    return JSON.parse(quoteLargeIntegers(trimmed))
  } catch {
    return data
  }
}

// 创建 axios 实例
const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
  transformResponse: [(data) => parseJsonSafely(data)],
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    NProgress.start()
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    NProgress.done()
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse<Result>) => {
    NProgress.done()

    if (response.config.responseType === 'blob' || response.config.responseType === 'arraybuffer') {
      return response
    }

    const res = response.data
    const silentError = (response.config as RequestConfig).silentError === true

    // 成功
    if (res.code === 200) {
      return res as any
    }

    // 未登录或 Token 过期
    if (res.code === 401) {
      const userStore = useUserStore()
      ElMessageBox.confirm('登录已过期，请重新登录', '提示', {
        confirmButtonText: '重新登录',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        userStore.logout()
        router.push('/login')
      })
      return Promise.reject(new Error(res.message || '未登录'))
    }

    // 其他错误
    if (!silentError) {
      ElMessage.error(res.message || '请求失败')
    }
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    NProgress.done()
    let message = '请求失败'
    const silentError = (error.config as RequestConfig | undefined)?.silentError === true
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未授权，请重新登录'
          const userStore = useUserStore()
          userStore.logout()
          router.push('/login')
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求地址不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = error.response.data?.message || '请求失败'
      }
    } else if (error.message.includes('timeout')) {
      message = '请求超时'
    } else if (error.message.includes('Network Error')) {
      message = '网络错误'
    }
    if (!silentError) {
      ElMessage.error(message)
    }
    return Promise.reject(error)
  }
)

// 封装请求方法
export const request = {
  get<T = any>(url: string, config?: RequestConfig): Promise<Result<T>> {
    return service.get(url, config)
  },
  getRaw<T = any>(url: string, config?: RequestConfig): Promise<AxiosResponse<T>> {
    return service.get(url, config)
  },
  post<T = any>(url: string, data?: any, config?: RequestConfig): Promise<Result<T>> {
    return service.post(url, data, config)
  },
  put<T = any>(url: string, data?: any, config?: RequestConfig): Promise<Result<T>> {
    return service.put(url, data, config)
  },
  delete<T = any>(url: string, config?: RequestConfig): Promise<Result<T>> {
    return service.delete(url, config)
  },
}

export default service
