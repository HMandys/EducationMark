<template>
  <div class="marking-access-page">
    <!-- 阅卷码输入页面 -->
    <div v-if="!sessionToken" class="login-container">
      <div class="login-card">
        <h1>阅卷系统</h1>
        <p class="subtitle">请输入8位阅卷码开始阅卷</p>

        <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" @submit.prevent="handleLogin">
          <el-form-item prop="accessCode">
            <el-input
              v-model="loginForm.accessCode"
              placeholder="请输入8位阅卷码"
              size="large"
              maxlength="8"
              clearable
              autofocus
              @input="formatAccessCode"
            >
              <template #prefix>
                <el-icon><Key /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              style="width: 100%"
              native-type="submit"
            >
              进入阅卷
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 阅卷工作页面 -->
    <div v-else class="workspace-container">
      <header class="workspace-header">
        <div class="header-left">
          <h2>{{ taskInfo?.examName }} - {{ taskInfo?.subjectName }}</h2>
          <div class="header-meta">
            <span class="question-no">题目 {{ taskInfo?.questionNo }}</span>
            <el-tag v-if="taskInfo" :type="taskInfo.markingRole === 2 ? 'warning' : 'success'" effect="dark">
              {{ taskInfo.markingRoleName }}
            </el-tag>
          </div>
        </div>
        <div class="header-right">
          <div class="progress-info">
            <span>进度：{{ taskInfo?.completedCount }} / {{ taskInfo?.totalCount }}</span>
            <el-progress
              :percentage="progressPercent"
              :stroke-width="8"
              :show-text="false"
              style="width: 120px"
            />
          </div>
          <el-button type="danger" plain @click="handleLogout">退出</el-button>
        </div>
      </header>

      <main class="workspace-main">
        <div class="image-panel">
          <div v-if="currentItem?.questionImage" class="image-container">
            <el-image
              :src="currentItem.questionImage"
              fit="contain"
              :preview-src-list="[currentItem.questionImage]"
              :z-index="9999"
            />
          </div>
          <el-empty v-else-if="!loadingItem" description="暂无待阅记录">
            <el-button type="primary" @click="loadNextItem">刷新</el-button>
          </el-empty>
          <div v-else class="loading-placeholder">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
        </div>

        <div class="scoring-panel">
          <div v-if="currentItem" class="scoring-content">
            <div class="score-display">
              <span class="label">当前序号</span>
              <span class="value">{{ currentItem.currentIndex }} / {{ currentItem.totalCount }}</span>
            </div>

            <div class="score-display">
              <span class="label">题目满分</span>
              <span class="value highlight">{{ currentItem.fullScore }} 分</span>
            </div>

            <el-divider />

            <div class="score-input-section">
              <span class="section-title">评分</span>
              <el-input-number
                v-model="scoreForm.score"
                :min="0"
                :max="currentItem.fullScore"
                :precision="0"
                size="large"
                class="score-input"
              />
            </div>

            <div class="quick-score-buttons">
              <el-button @click="scoreForm.score = 0">0 分</el-button>
              <el-button @click="scoreForm.score = Math.floor(currentItem.fullScore / 2)">
                {{ Math.floor(currentItem.fullScore / 2) }} 分
              </el-button>
              <el-button type="success" @click="scoreForm.score = currentItem.fullScore">
                满分
              </el-button>
            </div>

            <div class="keyboard-hint">
              <el-icon><InfoFilled /></el-icon>
              <span>快捷键：数字键 0-9 直接输入分数，Enter 提交</span>
            </div>

            <el-input
              v-model="scoreForm.comment"
              type="textarea"
              :rows="3"
              placeholder="评语（可选）"
              class="comment-input"
            />

            <div class="action-buttons">
              <el-button
                type="primary"
                size="large"
                :loading="submitting"
                @click="submitScore"
              >
                提交并下一份
              </el-button>
              <el-button size="large" :loading="skipping" @click="handleSkip">跳过</el-button>
            </div>
          </div>

          <el-empty v-else description="暂无待阅记录">
            <el-button type="primary" @click="loadNextItem">刷新</el-button>
          </el-empty>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Key, Loading, InfoFilled } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'
import {
  accessCodeLogin,
  getNextAccessItem,
  submitAccessScore,
  skipAccessRecord,
  getAccessTaskInfo,
  type MarkingSessionVO,
  type MarkingItemVO,
} from '@/api/marking'

const route = useRoute()

// 登录相关
const loginFormRef = ref()
const loginForm = reactive({
  accessCode: '',
})
const loginRules = {
  accessCode: [
    { required: true, message: '请输入阅卷码', trigger: 'blur' },
    { len: 8, message: '阅卷码必须为8位', trigger: 'blur' },
  ],
}
const loading = ref(false)

// 会话相关
const sessionToken = ref<string>('')
const taskInfo = ref<MarkingSessionVO | null>(null)
const currentItem = ref<MarkingItemVO | null>(null)
const loadingItem = ref(false)
const submitting = ref(false)
const skipping = ref(false)

const scoreForm = reactive({
  score: 0,
  comment: '',
})

const progressPercent = computed(() => {
  if (!taskInfo.value || taskInfo.value.totalCount === 0) return 0
  return Math.round((taskInfo.value.completedCount / taskInfo.value.totalCount) * 100)
})

// 格式化阅卷码输入（只允许数字）
function formatAccessCode() {
  loginForm.accessCode = loginForm.accessCode.replace(/\D/g, '')
}

// 阅卷码登录
async function handleLogin() {
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await accessCodeLogin(loginForm.accessCode)
    sessionToken.value = res.data.sessionToken
    taskInfo.value = res.data
    // 保存到 localStorage
    localStorage.setItem('marking_session', res.data.sessionToken)
    localStorage.setItem('marking_task_id', String(res.data.taskId))

    await loadNextItem()
  } catch (error: any) {
    ElMessage.error(error.message || '阅卷码无效或已过期')
  } finally {
    loading.value = false
  }
}

// 加载下一份待阅记录
async function loadNextItem() {
  if (!sessionToken.value) return

  loadingItem.value = true
  try {
    const res = await getNextAccessItem(sessionToken.value)
    currentItem.value = res.data
    if (currentItem.value) {
      scoreForm.score = 0
      scoreForm.comment = ''
    }
    // 更新任务进度
    await refreshTaskInfo()
  } catch (error: any) {
    ElMessage.error(error.message || '加载失败')
    currentItem.value = null
  } finally {
    loadingItem.value = false
  }
}

// 刷新任务信息
async function refreshTaskInfo() {
  if (!sessionToken.value) return
  try {
    const res = await getAccessTaskInfo(sessionToken.value)
    taskInfo.value = res.data
  } catch {
    // 忽略
  }
}

// 提交评分
async function submitScore() {
  if (!currentItem.value || !sessionToken.value) return

  submitting.value = true
  try {
    await submitAccessScore(sessionToken.value, {
      recordId: currentItem.value.recordId,
      score: scoreForm.score,
      comment: scoreForm.comment,
    })
    ElMessage.success('提交成功')
    await loadNextItem()
  } catch (error: any) {
    ElMessage.error(error.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

// 跳过当前记录
async function handleSkip() {
  if (!currentItem.value || !sessionToken.value) return

  skipping.value = true
  try {
    await skipAccessRecord(sessionToken.value, currentItem.value.recordId)
    await loadNextItem()
  } catch (error: any) {
    ElMessage.error(error.message || '跳过失败')
  } finally {
    skipping.value = false
  }
}

// 退出登录
function handleLogout() {
  sessionToken.value = ''
  taskInfo.value = null
  currentItem.value = null
  localStorage.removeItem('marking_session')
  localStorage.removeItem('marking_task_id')
}

// 键盘快捷键
function handleKeydown(e: KeyboardEvent) {
  if (!currentItem.value || submitting.value) return

  // 数字键 0-9 快速输入分数
  if (e.key >= '0' && e.key <= '9' && !e.ctrlKey && !e.altKey && !e.metaKey) {
    const target = e.target as HTMLElement
    if (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA') return

    const num = parseInt(e.key)
    const currentScore = scoreForm.score
    const newScore = currentScore * 10 + num

    if (newScore <= currentItem.value.fullScore) {
      scoreForm.score = newScore
    } else if (num <= currentItem.value.fullScore) {
      scoreForm.score = num
    }
    e.preventDefault()
  }

  // Enter 提交
  if (e.key === 'Enter' && !e.shiftKey) {
    const target = e.target as HTMLElement
    if (target.tagName === 'TEXTAREA') return

    submitScore()
    e.preventDefault()
  }
}

onMounted(() => {
  // 恢复会话
  const savedSession = localStorage.getItem('marking_session')
  if (savedSession) {
    sessionToken.value = savedSession
    getAccessTaskInfo(savedSession).then(res => {
      taskInfo.value = res.data
      loadNextItem()
    }).catch(() => {
      // 会话过期，清除
      handleLogout()
    })
  } else {
    const presetAccessCode = typeof route.query.accessCode === 'string' ? route.query.accessCode : ''
    if (presetAccessCode) {
      loginForm.accessCode = presetAccessCode.replace(/\D/g, '').slice(0, 8)
      if (loginForm.accessCode.length === 8) {
        handleLogin()
      }
    }
  }

  // 添加键盘监听
  window.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
.marking-access-page {
  min-height: 100vh;
  background: #f0f2f5;
}

/* 登录页面样式 */
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  background: #fff;
  padding: 48px 40px;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  width: 400px;
  max-width: 90%;
}

.login-card h1 {
  text-align: center;
  margin: 0 0 8px;
  font-size: 28px;
  color: #1a1a1a;
}

.login-card .subtitle {
  text-align: center;
  color: #666;
  margin-bottom: 32px;
}

/* 工作页面样式 */
.workspace-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.workspace-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-left h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.header-left .question-no {
  margin-left: 12px;
  padding: 2px 10px;
  background: #e6f7ff;
  color: #1890ff;
  border-radius: 4px;
  font-size: 13px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.progress-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #666;
}

.workspace-main {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 380px;
  overflow: hidden;
}

.image-panel {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: auto;
  padding: 20px;
}

.image-container {
  max-width: 100%;
  max-height: 100%;
}

.image-container :deep(.el-image) {
  max-height: calc(100vh - 120px);
}

.loading-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #999;
}

.loading-placeholder .el-icon {
  font-size: 32px;
}

.scoring-panel {
  background: #fff;
  border-left: 1px solid #e4e7ed;
  padding: 24px;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.scoring-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.score-display {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.score-display .label {
  color: #666;
  font-size: 14px;
}

.score-display .value {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}

.score-display .value.highlight {
  color: #1890ff;
  font-size: 20px;
}

.score-input-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.section-title {
  font-size: 14px;
  color: #666;
}

.score-input {
  width: 100%;
}

.score-input :deep(.el-input-number__decrease),
.score-input :deep(.el-input-number__increase) {
  display: none;
}

.score-input :deep(.el-input__inner) {
  text-align: center;
  font-size: 24px;
  font-weight: 600;
}

.quick-score-buttons {
  display: flex;
  gap: 8px;
}

.quick-score-buttons .el-button {
  flex: 1;
}

.keyboard-hint {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #999;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.comment-input {
  margin-top: 8px;
}

.action-buttons {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.action-buttons .el-button {
  flex: 1;
}

@media (max-width: 900px) {
  .workspace-main {
    grid-template-columns: 1fr;
  }

  .image-panel {
    max-height: 50vh;
  }

  .scoring-panel {
    border-left: none;
    border-top: 1px solid #e4e7ed;
  }
}

@media (max-width: 600px) {
  .workspace-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .header-right {
    width: 100%;
    justify-content: space-between;
  }

  .progress-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .quick-score-buttons {
    flex-wrap: wrap;
  }

  .quick-score-buttons .el-button {
    flex: 1 1 30%;
  }
}
</style>
