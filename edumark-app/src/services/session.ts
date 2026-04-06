import { getBindStudents, getStudentInfo } from '@/api/student'
import { useUserStore } from '@/stores/user'

/**
 * 初始化本地会话并补齐学生上下文
 */
export async function initializeSession() {
  const userStore = useUserStore()
  userStore.checkLogin()

  if (!userStore.isLoggedIn) {
    return
  }

  await loadStudentContext()
}

/**
 * 根据当前角色加载学生上下文
 */
export async function loadStudentContext() {
  const userStore = useUserStore()

  if (!userStore.isLoggedIn || !userStore.userInfo) {
    return
  }

  if (userStore.isParent) {
    const res = await getBindStudents()
    userStore.setStudents(res.data)
    return
  }

  if (userStore.isStudent) {
    const res = await getStudentInfo()
    userStore.setStudents([res.data])
  }
}
