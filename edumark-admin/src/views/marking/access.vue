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
          <div v-if="currentItem?.questionImage" class="annotation-area">
            <div class="annotation-toolbar">
              <div class="tool-group">
                <button
                  class="tool-btn"
                  :class="{ 'is-active': annotationTool === 'none' }"
                  title="选择（不标注）"
                  @click="annotationTool = 'none'"
                >
                  <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 3l7.07 16.97 2.51-7.39 7.39-2.51L3 3z"/></svg>
                </button>
                <button
                  class="tool-btn"
                  :class="{ 'is-active': annotationTool === 'draw' }"
                  title="涂鸦"
                  @click="annotationTool = 'draw'"
                >
                  <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 19l7-7 3 3-7 7-3-3z"/><path d="M18 13l-1.5-7.5L2 2l3.5 14.5L13 18l5-5z"/><path d="M2 2l7.586 7.586"/></svg>
                </button>
                <button
                  class="tool-btn tool-btn--correct"
                  :class="{ 'is-active': annotationTool === 'check' }"
                  title="对号 ✓"
                  @click="annotationTool = 'check'"
                >
                  <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="4 12 9 17 20 6"/></svg>
                </button>
                <button
                  class="tool-btn tool-btn--wrong"
                  :class="{ 'is-active': annotationTool === 'cross' }"
                  title="错号 ✗"
                  @click="annotationTool = 'cross'"
                >
                  <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><line x1="5" y1="5" x2="19" y2="19"/><line x1="19" y1="5" x2="5" y2="19"/></svg>
                </button>
              </div>
              <div class="tool-group">
                <button class="tool-btn" title="撤销" :disabled="annotationHistory.length === 0" @click="undoAnnotation">
                  <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><polyline points="1 4 1 10 7 10"/><path d="M3.51 15a9 9 0 1 0 2.13-9.36L1 10"/></svg>
                </button>
                <button class="tool-btn" title="清除全部标注" @click="clearAnnotations">
                  <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                </button>
              </div>
            </div>
            <div
              ref="canvasWrapRef"
              class="canvas-wrap"
              :class="{ 'cursor-draw': annotationTool === 'draw', 'cursor-stamp': annotationTool === 'check' || annotationTool === 'cross' }"
            >
              <div class="image-canvas-stack" ref="stackRef">
                <img
                  ref="baseImageRef"
                  :src="currentItem.questionImage"
                  class="base-image"
                  crossorigin="anonymous"
                  @load="onImageLoad"
                />
                <canvas
                  ref="annotationCanvasRef"
                  class="annotation-canvas"
                  @pointerdown="onCanvasPointerDown"
                  @pointermove="onCanvasPointerMove"
                  @pointerup="onCanvasPointerUp"
                  @pointerleave="onCanvasPointerUp"
                />
              </div>
            </div>
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

            <div class="quick-score-grid">
              <button
                v-for="n in quickScoreList"
                :key="n"
                class="quick-score-btn"
                :class="{ 'is-active': scoreForm.score === n, 'is-zero': n === 0, 'is-full': n === currentItem.fullScore }"
                :disabled="submitting"
                @click="quickSubmit(n)"
              >
                {{ n }}
              </button>
            </div>

            <div class="keyboard-hint">
              <el-icon><InfoFilled /></el-icon>
              <span>点击分数按钮自动提交并跳下一份 | 数字键 0-9 输入，Enter 提交</span>
            </div>

            <el-input
              v-model="scoreForm.comment"
              type="textarea"
              :rows="2"
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
import { computed, nextTick, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
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

// 快捷分数按钮列表：0 + 1~满分（最多到满分）
const quickScoreList = computed(() => {
  const full = currentItem.value?.fullScore ?? 10
  const list: number[] = [0]
  for (let i = 1; i <= full; i++) {
    list.push(i)
  }
  return list
})

// 点击快捷分数按钮：自动提交并跳下一份
async function quickSubmit(score: number) {
  if (!currentItem.value || !sessionToken.value || submitting.value) return

  scoreForm.score = score
  submitting.value = true
  try {
    await submitAccessScore(sessionToken.value, {
      recordId: currentItem.value.recordId,
      score,
      comment: scoreForm.comment || undefined,
      annotations: serializeAnnotations(),
    })
    await loadNextItem()
  } catch (error: any) {
    ElMessage.error(error.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

// ==================== 标注功能 ====================
type AnnotationToolType = 'none' | 'draw' | 'check' | 'cross'

interface AnnotationStroke {
  type: 'draw'
  points: { x: number; y: number }[]
}

interface AnnotationStamp {
  type: 'check' | 'cross'
  x: number
  y: number
}

type AnnotationItem = AnnotationStroke | AnnotationStamp

const annotationTool = ref<AnnotationToolType>('none')
const canvasWrapRef = ref<HTMLDivElement>()
const stackRef = ref<HTMLDivElement>()
const baseImageRef = ref<HTMLImageElement>()
const annotationCanvasRef = ref<HTMLCanvasElement>()
const annotationHistory = ref<AnnotationItem[]>([])
const isDrawing = ref(false)
const currentStroke = ref<{ x: number; y: number }[]>([])

function getCanvasPoint(e: PointerEvent): { x: number; y: number } | null {
  const canvas = annotationCanvasRef.value
  if (!canvas) return null
  const rect = canvas.getBoundingClientRect()
  return {
    x: (e.clientX - rect.left) * (canvas.width / rect.width),
    y: (e.clientY - rect.top) * (canvas.height / rect.height),
  }
}

function onImageLoad() {
  nextTick(() => resizeCanvas())
}

function resizeCanvas() {
  const img = baseImageRef.value
  const canvas = annotationCanvasRef.value
  if (!img || !canvas) return
  canvas.width = img.naturalWidth
  canvas.height = img.naturalHeight
  redrawAnnotations()
}

function onCanvasPointerDown(e: PointerEvent) {
  if (annotationTool.value === 'none') return
  const pt = getCanvasPoint(e)
  if (!pt) return

  if (annotationTool.value === 'draw') {
    isDrawing.value = true
    currentStroke.value = [pt]
    const canvas = annotationCanvasRef.value
    if (canvas) {
      canvas.setPointerCapture(e.pointerId)
    }
  } else if (annotationTool.value === 'check' || annotationTool.value === 'cross') {
    annotationHistory.value.push({ type: annotationTool.value, x: pt.x, y: pt.y })
    redrawAnnotations()
  }
}

function onCanvasPointerMove(e: PointerEvent) {
  if (!isDrawing.value || annotationTool.value !== 'draw') return
  const pt = getCanvasPoint(e)
  if (!pt) return
  currentStroke.value.push(pt)
  // 实时绘制当前笔画
  redrawAnnotations()
  const ctx = annotationCanvasRef.value?.getContext('2d')
  if (ctx && currentStroke.value.length > 1) {
    drawStroke(ctx, currentStroke.value)
  }
}

function onCanvasPointerUp(_e: PointerEvent) {
  if (!isDrawing.value) return
  isDrawing.value = false
  if (currentStroke.value.length > 1) {
    annotationHistory.value.push({ type: 'draw', points: [...currentStroke.value] })
  }
  currentStroke.value = []
  redrawAnnotations()
}

function redrawAnnotations() {
  const canvas = annotationCanvasRef.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  if (!ctx) return

  ctx.clearRect(0, 0, canvas.width, canvas.height)

  for (const item of annotationHistory.value) {
    if (item.type === 'draw') {
      drawStroke(ctx, item.points)
    } else if (item.type === 'check') {
      drawCheck(ctx, item.x, item.y, canvas.width)
    } else if (item.type === 'cross') {
      drawCross(ctx, item.x, item.y, canvas.width)
    }
  }
}

function drawStroke(ctx: CanvasRenderingContext2D, points: { x: number; y: number }[]) {
  if (points.length < 2) return
  ctx.save()
  ctx.strokeStyle = '#f56c6c'
  ctx.lineWidth = Math.max(3, ctx.canvas.width * 0.003)
  ctx.lineCap = 'round'
  ctx.lineJoin = 'round'
  ctx.beginPath()
  ctx.moveTo(points[0].x, points[0].y)
  for (let i = 1; i < points.length; i++) {
    ctx.lineTo(points[i].x, points[i].y)
  }
  ctx.stroke()
  ctx.restore()
}

function drawCheck(ctx: CanvasRenderingContext2D, cx: number, cy: number, canvasWidth: number) {
  const size = Math.max(24, canvasWidth * 0.03)
  ctx.save()
  ctx.strokeStyle = '#f56c6c'
  ctx.lineWidth = Math.max(3, size * 0.18)
  ctx.lineCap = 'round'
  ctx.lineJoin = 'round'
  ctx.beginPath()
  ctx.moveTo(cx - size * 0.45, cy)
  ctx.lineTo(cx - size * 0.1, cy + size * 0.35)
  ctx.lineTo(cx + size * 0.45, cy - size * 0.35)
  ctx.stroke()
  ctx.restore()
}

function drawCross(ctx: CanvasRenderingContext2D, cx: number, cy: number, canvasWidth: number) {
  const size = Math.max(24, canvasWidth * 0.03)
  const half = size * 0.35
  ctx.save()
  ctx.strokeStyle = '#f56c6c'
  ctx.lineWidth = Math.max(3, size * 0.18)
  ctx.lineCap = 'round'
  ctx.beginPath()
  ctx.moveTo(cx - half, cy - half)
  ctx.lineTo(cx + half, cy + half)
  ctx.moveTo(cx + half, cy - half)
  ctx.lineTo(cx - half, cy + half)
  ctx.stroke()
  ctx.restore()
}

function undoAnnotation() {
  if (annotationHistory.value.length === 0) return
  annotationHistory.value.pop()
  redrawAnnotations()
}

function clearAnnotations() {
  annotationHistory.value = []
  redrawAnnotations()
}

// 序列化标注数据为 JSON 字符串
function serializeAnnotations(): string | undefined {
  if (annotationHistory.value.length === 0) return undefined
  return JSON.stringify(annotationHistory.value)
}

// 从 JSON 字符串还原标注数据
function restoreAnnotations(json?: string | null) {
  annotationHistory.value = []
  if (!json) return
  try {
    const data = JSON.parse(json)
    if (Array.isArray(data)) {
      annotationHistory.value = data
    }
  } catch {
    // 忽略解析错误
  }
  nextTick(() => redrawAnnotations())
}

// 切换题目时清空标注并尝试还原
watch(currentItem, (item) => {
  annotationTool.value = 'none'
  nextTick(() => {
    resizeCanvas()
    restoreAnnotations(item?.annotations)
  })
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
      annotations: serializeAnnotations(),
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
  background: #f5f7fa;
  display: flex;
  align-items: stretch;
  justify-content: center;
  overflow: hidden;
}

.annotation-area {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.annotation-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 14px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  flex-shrink: 0;
}

.tool-group {
  display: flex;
  gap: 4px;
}

.tool-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border: 1.5px solid #dcdfe6;
  border-radius: 10px;
  background: #fff;
  color: #606266;
  cursor: pointer;
  transition: all 0.15s;
}

.tool-btn:hover:not(:disabled) {
  border-color: #409eff;
  color: #409eff;
  background: #ecf5ff;
}

.tool-btn.is-active {
  border-color: #409eff;
  background: #409eff;
  color: #fff;
}

.tool-btn--correct.is-active {
  border-color: #f56c6c;
  background: #f56c6c;
  color: #fff;
}

.tool-btn--wrong.is-active {
  border-color: #f56c6c;
  background: #f56c6c;
  color: #fff;
}

.tool-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.canvas-wrap {
  flex: 1;
  position: relative;
  overflow: auto;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e8eaed;
  padding: 12px;
}

.canvas-wrap.cursor-draw {
  cursor: crosshair;
}

.canvas-wrap.cursor-stamp {
  cursor: pointer;
}

.image-canvas-stack {
  position: relative;
  display: inline-block;
  max-width: 100%;
  max-height: 100%;
}

.base-image {
  display: block;
  max-width: 100%;
  max-height: calc(100vh - 180px);
  object-fit: contain;
  user-select: none;
  -webkit-user-drag: none;
}

.annotation-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

/* 当有工具选中时，canvas 接收事件 */
.canvas-wrap.cursor-draw .annotation-canvas,
.canvas-wrap.cursor-stamp .annotation-canvas {
  pointer-events: auto;
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

.quick-score-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(56px, 1fr));
  gap: 8px;
}

.quick-score-btn {
  height: 48px;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  background: #fff;
  color: #303133;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.15s ease;
}

.quick-score-btn:hover:not(:disabled) {
  border-color: #409eff;
  color: #409eff;
  background: #ecf5ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.quick-score-btn:active:not(:disabled) {
  transform: translateY(0);
}

.quick-score-btn.is-active {
  border-color: #409eff;
  background: #409eff;
  color: #fff;
}

.quick-score-btn.is-zero {
  border-color: #f56c6c;
  color: #f56c6c;
}

.quick-score-btn.is-zero:hover:not(:disabled) {
  background: #fef0f0;
  border-color: #f56c6c;
  color: #f56c6c;
}

.quick-score-btn.is-full {
  border-color: #67c23a;
  color: #67c23a;
}

.quick-score-btn.is-full:hover:not(:disabled) {
  background: #f0f9eb;
  border-color: #67c23a;
  color: #67c23a;
}

.quick-score-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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

  .quick-score-grid {
    grid-template-columns: repeat(auto-fill, minmax(48px, 1fr));
  }
}
</style>
