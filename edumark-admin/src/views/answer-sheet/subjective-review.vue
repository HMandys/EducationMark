<template>
  <div class="subjective-review-page">
    <div class="page-toolbar">
      <el-page-header @back="goBack">
        <template #content>
          <div class="page-title-group">
            <span class="page-title">主观题裁题核验</span>
            <span class="page-subtitle">
              {{ answerSheet?.examName || '-' }} / {{ answerSheet?.subjectName || '-' }}
            </span>
          </div>
        </template>
        <template #extra>
          <div class="toolbar-actions">
            <el-button :loading="rerunning" @click="handleBatchRerun">批量重跑异常项</el-button>
            <el-button type="primary" @click="goMarkingTask">打开阅卷任务</el-button>
          </div>
        </template>
      </el-page-header>
    </div>

    <div class="summary-grid">
      <el-card shadow="never" class="summary-card">
        <div class="summary-label">学生</div>
        <div class="summary-value">{{ answerSheet?.studentName || '-' }}</div>
        <div class="summary-desc">学号 {{ answerSheet?.studentNumber || '未识别' }}</div>
      </el-card>
      <el-card shadow="never" class="summary-card">
        <div class="summary-label">主观题数</div>
        <div class="summary-value">{{ subjectiveQuestions.length }}</div>
        <div class="summary-desc">当前答题卡主观题明细</div>
      </el-card>
      <el-card shadow="never" class="summary-card">
        <div class="summary-label">待处理异常</div>
        <div class="summary-value">{{ pendingAnomalyCount }}</div>
        <div class="summary-desc">仍停留在异常池的题目</div>
      </el-card>
      <el-card shadow="never" class="summary-card">
        <div class="summary-label">已核验通过</div>
        <div class="summary-value">{{ verifiedCount }}</div>
        <div class="summary-desc">已人工确认可进入阅卷</div>
      </el-card>
      <el-card shadow="never" class="summary-card">
        <div class="summary-label">已完成评分</div>
        <div class="summary-value">{{ completedCount }}</div>
        <div class="summary-desc">已完成主观题评分</div>
      </el-card>
    </div>

    <div class="workspace-grid" v-loading="loading">
      <el-card shadow="never" class="question-panel">
        <template #header>
          <div class="panel-header">
            <div class="panel-title-block">
              <span>题目列表</span>
              <el-tag effect="plain">{{ displayQuestions.length }} 题</el-tag>
            </div>
            <el-switch
              v-model="onlyShowAnomalies"
              inline-prompt
              active-text="仅异常"
              inactive-text="全部"
            />
          </div>
        </template>

        <el-empty v-if="displayQuestions.length === 0" description="当前筛选下没有主观题明细" />

        <div v-else class="question-list">
          <button
            v-for="item in displayQuestions"
            :key="item.questionId"
            type="button"
            class="question-item"
            :class="{
              'is-active': currentQuestion?.questionId === item.questionId,
              'is-anomaly': isPendingSubjectiveAnomaly(item)
            }"
            @click="handleSelectQuestion(item)"
          >
            <div class="question-item__top">
              <span class="question-item__no">{{ item.questionNo || item.questionId }}</span>
              <el-tag size="small" :type="resolveTagType(item)" effect="plain">
                {{ item.statusName || '待处理' }}
              </el-tag>
            </div>
            <div class="question-item__meta">
              <span>{{ item.questionTypeName || '-' }}</span>
              <span>{{ item.regionRoleName || '未配置区域' }}</span>
            </div>
            <div v-if="item.anomalyReason" class="question-item__reason">
              {{ item.anomalyReason }}
            </div>
            <div class="question-item__score">
              <span>得分 {{ item.score ?? 0 }} / {{ item.fullScore || 0 }}</span>
              <span>页码 {{ item.pageNo ?? '-' }}</span>
            </div>
          </button>
        </div>
      </el-card>

      <el-card shadow="never" class="preview-panel">
        <template #header>
          <div class="panel-header">
            <span>{{ currentQuestion ? `题号 ${currentQuestion.questionNo}` : '裁题预览' }}</span>
            <span class="panel-meta">{{ currentQuestion?.cropMode || '-' }}</span>
          </div>
        </template>

        <div v-loading="previewLoading" class="preview-body">
          <el-empty
            v-if="!previewUrl && !previewLoading"
            :description="currentQuestion?.anomalyReason || '选择题目后显示裁题预览'"
          />
          <el-image
            v-else
            :src="previewUrl"
            fit="contain"
            class="preview-image"
            :preview-src-list="previewUrl ? [previewUrl] : []"
          />
        </div>
      </el-card>

      <el-card shadow="never" class="info-panel">
        <template #header>
          <div class="panel-header">
            <span>核验操作</span>
            <el-tag v-if="currentQuestion" :type="resolveTagType(currentQuestion)" effect="plain">
              {{ currentQuestion.statusName || '待处理' }}
            </el-tag>
          </div>
        </template>

        <el-empty v-if="!currentQuestion" description="请选择左侧题目" />

        <div v-else class="info-body">
          <div class="meta-grid">
            <div class="meta-item">
              <span class="meta-label">区域用途</span>
              <span class="meta-value">{{ currentQuestion.regionRoleName || '-' }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">裁题模式</span>
              <span class="meta-value">{{ currentQuestion.cropMode || '-' }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">当前状态</span>
              <span class="meta-value">{{ currentQuestion.statusName || '-' }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">当前得分</span>
              <span class="meta-value">{{ currentQuestion.score ?? 0 }} / {{ currentQuestion.fullScore || 0 }}</span>
            </div>
          </div>

          <el-alert
            v-if="currentQuestion.anomalyFlag"
            :title="currentQuestion.anomalyReason || '当前题目仍在异常池，需要人工处理'"
            type="warning"
            :closable="false"
            show-icon
          />
          <el-alert
            v-else-if="currentQuestion.status === SUBJECTIVE_REVIEW_STATUS_VERIFIED"
            title="该题已人工确认正常，已退出异常池，可直接进入待阅卷。"
            type="success"
            :closable="false"
            show-icon
          />
          <el-alert
            v-else
            title="该题当前没有异常阻塞，可直接进入待阅卷。"
            type="info"
            :closable="false"
            show-icon
          />

          <div class="action-box">
            <div class="action-text">
              系统会优先把结构异常拦进异常池。你只需要处理异常项：题图正常就确认通过，题图不对就标记异常并回模板修正后重跑。
            </div>
            <div class="action-buttons">
              <el-button :loading="previewLoading" @click="reloadCurrentPreview">重新生成题图</el-button>
              <el-button
                type="success"
                :loading="reviewSubmitting"
                :disabled="currentQuestion.status === SUBJECTIVE_REVIEW_STATUS_COMPLETED"
                @click="handleReviewStatus(SUBJECTIVE_REVIEW_STATUS_VERIFIED)"
              >
                确认正常
              </el-button>
              <el-button
                type="danger"
                plain
                :loading="reviewSubmitting"
                :disabled="currentQuestion.status === SUBJECTIVE_REVIEW_STATUS_COMPLETED"
                @click="handleReviewStatus(SUBJECTIVE_REVIEW_STATUS_ANOMALY)"
              >
                标记异常
              </el-button>
              <el-button
                :loading="reviewSubmitting"
                :disabled="currentQuestion.status === SUBJECTIVE_REVIEW_STATUS_COMPLETED"
                @click="handleReviewStatus(SUBJECTIVE_REVIEW_STATUS_PENDING)"
              >
                重置待处理
              </el-button>
              <el-button type="primary" plain @click="goMarkingTask">打开阅卷任务</el-button>
            </div>
          </div>

          <div class="navigation-row">
            <el-button :disabled="currentIndex <= 0" @click="selectRelativeQuestion(-1)">上一题</el-button>
            <el-button :disabled="currentIndex >= displayQuestions.length - 1" @click="selectRelativeQuestion(1)">下一题</el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import {
  getAnswerSheetDetail,
  getAnswerSheetQuestionDetails,
  getAnswerSheetQuestionPreview,
  rerunSubjectiveReview,
  updateSubjectiveReviewStatus,
  type AnswerSheet,
  type AnswerSheetQuestionDetail,
} from '@/api/answerSheet'

const SUBJECTIVE_REVIEW_STATUS_PENDING = 0
const SUBJECTIVE_REVIEW_STATUS_COMPLETED = 1
const SUBJECTIVE_REVIEW_STATUS_VERIFIED = 2
const SUBJECTIVE_REVIEW_STATUS_ANOMALY = 3

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const previewLoading = ref(false)
const rerunning = ref(false)
const reviewSubmitting = ref(false)

const answerSheet = ref<AnswerSheet | null>(null)
const subjectiveQuestions = ref<AnswerSheetQuestionDetail[]>([])
const currentQuestionId = ref<string | number>()
const previewUrl = ref('')
const onlyShowAnomalies = ref(true)

const answerSheetId = computed(() => (typeof route.params.id === 'string' ? route.params.id : ''))
const displayQuestions = computed(() =>
  onlyShowAnomalies.value
    ? subjectiveQuestions.value.filter(isPendingSubjectiveAnomaly)
    : subjectiveQuestions.value
)
const currentQuestion = computed(() =>
  subjectiveQuestions.value.find((item) => item.questionId === currentQuestionId.value) || null
)
const currentIndex = computed(() =>
  displayQuestions.value.findIndex((item) => item.questionId === currentQuestionId.value)
)
const pendingAnomalyCount = computed(() =>
  subjectiveQuestions.value.filter(isPendingSubjectiveAnomaly).length
)
const verifiedCount = computed(() =>
  subjectiveQuestions.value.filter((item) => item.status === SUBJECTIVE_REVIEW_STATUS_VERIFIED).length
)
const completedCount = computed(() =>
  subjectiveQuestions.value.filter((item) => item.status === SUBJECTIVE_REVIEW_STATUS_COMPLETED).length
)

watch(displayQuestions, async () => {
  await ensureCurrentQuestionVisible()
})

onMounted(() => {
  loadPage()
})

async function loadPage() {
  if (!/^\d+$/.test(answerSheetId.value)) {
    ElMessage.error('答题卡参数无效')
    goBack()
    return
  }

  loading.value = true
  try {
    const [sheetRes, detailRes] = await Promise.all([
      getAnswerSheetDetail(answerSheetId.value),
      getAnswerSheetQuestionDetails(answerSheetId.value),
    ])

    answerSheet.value = sheetRes.data
    subjectiveQuestions.value = (detailRes.data || []).filter((item) => item.isObjective !== 1)
    await ensureCurrentQuestionVisible()
  } finally {
    loading.value = false
  }
}

async function ensureCurrentQuestionVisible() {
  if (displayQuestions.value.length === 0) {
    currentQuestionId.value = undefined
    previewUrl.value = ''
    return
  }

  const currentExists = displayQuestions.value.some((item) => item.questionId === currentQuestionId.value)
  const target = currentExists
    ? displayQuestions.value.find((item) => item.questionId === currentQuestionId.value)
    : displayQuestions.value[0]

  if (target) {
    await handleSelectQuestion(target)
  }
}

async function handleSelectQuestion(detail: AnswerSheetQuestionDetail) {
  currentQuestionId.value = detail.questionId
  await loadPreview(detail)
}

async function loadPreview(detail: AnswerSheetQuestionDetail) {
  if (!detail.previewAvailable) {
    previewUrl.value = ''
    return
  }

  previewLoading.value = true
  try {
    const res = await getAnswerSheetQuestionPreview(answerSheetId.value, detail.questionId)
    previewUrl.value = res.data || ''
  } catch (error: any) {
    previewUrl.value = ''
    ElMessage.error(error?.message || '加载题图失败')
  } finally {
    previewLoading.value = false
  }
}

async function reloadCurrentPreview() {
  if (!currentQuestion.value) {
    return
  }
  await loadPreview(currentQuestion.value)
  ElMessage.success('已重新生成题图')
}

async function handleReviewStatus(status: number) {
  if (!currentQuestion.value) {
    return
  }

  if (status === SUBJECTIVE_REVIEW_STATUS_ANOMALY) {
    try {
      await ElMessageBox.confirm(
        '标记异常后，该题会继续停留在异常池，不会进入正常待阅卷流。',
        '确认标记异常',
        {
          type: 'warning',
          confirmButtonText: '确认',
          cancelButtonText: '取消',
        }
      )
    } catch {
      return
    }
  }

  reviewSubmitting.value = true
  try {
    const res = await updateSubjectiveReviewStatus(answerSheetId.value, currentQuestion.value.questionId, status)
    syncQuestion(res.data)

    if (status === SUBJECTIVE_REVIEW_STATUS_VERIFIED) {
      ElMessage.success('已确认正常，题目已退出异常池')
    } else if (status === SUBJECTIVE_REVIEW_STATUS_ANOMALY) {
      ElMessage.success('已标记异常，题目继续保留在异常池')
    } else {
      ElMessage.success('已重置为待处理')
    }

    if (status === SUBJECTIVE_REVIEW_STATUS_VERIFIED && onlyShowAnomalies.value) {
      await ensureCurrentQuestionVisible()
    } else if (currentQuestion.value) {
      await loadPreview(currentQuestion.value)
    }
  } finally {
    reviewSubmitting.value = false
  }
}

async function handleBatchRerun() {
  rerunning.value = true
  try {
    const selectedQuestionId = currentQuestionId.value
    const res = await rerunSubjectiveReview(answerSheetId.value)
    subjectiveQuestions.value = (res.data || []).filter((item) => item.isObjective !== 1)
    currentQuestionId.value = selectedQuestionId
    await ensureCurrentQuestionVisible()
    ElMessage.success('主观题核验已重跑')
  } finally {
    rerunning.value = false
  }
}

function syncQuestion(detail: AnswerSheetQuestionDetail) {
  subjectiveQuestions.value = subjectiveQuestions.value.map((item) =>
    item.questionId === detail.questionId ? detail : item
  )
}

function isPendingSubjectiveAnomaly(detail: AnswerSheetQuestionDetail) {
  if (detail.status === SUBJECTIVE_REVIEW_STATUS_COMPLETED) {
    return false
  }
  if (detail.status === SUBJECTIVE_REVIEW_STATUS_VERIFIED) {
    return false
  }
  if (detail.status === SUBJECTIVE_REVIEW_STATUS_ANOMALY) {
    return true
  }
  return detail.anomalyFlag === true
}

function resolveTagType(detail: AnswerSheetQuestionDetail) {
  if (detail.status === SUBJECTIVE_REVIEW_STATUS_COMPLETED) {
    return 'success'
  }
  if (detail.status === SUBJECTIVE_REVIEW_STATUS_VERIFIED) {
    return 'success'
  }
  if (detail.status === SUBJECTIVE_REVIEW_STATUS_ANOMALY) {
    return 'danger'
  }
  if (detail.anomalyFlag) {
    return 'warning'
  }
  return 'info'
}

async function selectRelativeQuestion(offset: number) {
  const target = displayQuestions.value[currentIndex.value + offset]
  if (!target) {
    return
  }
  await handleSelectQuestion(target)
}

function goMarkingTask() {
  router.push({
    name: 'MarkingTask',
    query: route.query.examId ? { examId: String(route.query.examId) } : undefined,
  })
}

function goBack() {
  if (route.query.examId) {
    router.push({
      name: 'ExamWorkbench',
      params: { id: String(route.query.examId) },
    })
    return
  }

  router.push({
    name: 'AnswerSheetList',
  })
}
</script>

<style scoped>
.subjective-review-page {
  padding: 20px;
  min-height: 100%;
  background: #eef2f6;
}

.page-toolbar {
  margin-bottom: 16px;
}

.toolbar-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.page-title-group {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: #16202a;
}

.page-subtitle {
  color: #667382;
  font-size: 13px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 16px;
}

.summary-card,
.question-panel,
.preview-panel,
.info-panel {
  border-radius: 14px;
  border: 1px solid #d7dee7;
}

.summary-label {
  color: #6f7d8b;
  font-size: 12px;
  margin-bottom: 8px;
}

.summary-value {
  font-size: 28px;
  line-height: 1;
  font-weight: 700;
  color: #16202a;
  margin-bottom: 8px;
}

.summary-desc {
  color: #667382;
  font-size: 13px;
}

.workspace-grid {
  display: grid;
  grid-template-columns: minmax(280px, 340px) minmax(420px, 1fr) minmax(360px, 430px);
  gap: 16px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.panel-title-block {
  display: flex;
  align-items: center;
  gap: 8px;
}

.panel-meta {
  color: #667382;
  font-size: 12px;
}

.question-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-height: 760px;
  overflow: auto;
}

.question-item {
  width: 100%;
  padding: 14px;
  border: 1px solid #d7dee7;
  border-radius: 12px;
  background: #fff;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.18s ease, box-shadow 0.18s ease, transform 0.18s ease;
}

.question-item:hover,
.question-item.is-active {
  border-color: #1f6feb;
  box-shadow: 0 10px 24px rgba(18, 35, 61, 0.08);
  transform: translateY(-1px);
}

.question-item.is-anomaly {
  border-left: 4px solid #e67e22;
}

.question-item__top,
.question-item__meta,
.question-item__score {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

.question-item__top {
  margin-bottom: 10px;
}

.question-item__no {
  font-size: 16px;
  font-weight: 700;
  color: #16202a;
}

.question-item__meta {
  color: #5b6673;
  font-size: 13px;
}

.question-item__reason {
  margin-top: 8px;
  padding: 8px 10px;
  border-radius: 10px;
  background: #fff6e8;
  color: #9a5d00;
  font-size: 12px;
  line-height: 1.5;
}

.question-item__score {
  margin-top: 10px;
  color: #16202a;
  font-size: 13px;
}

.preview-body {
  min-height: 680px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f6f8fb;
  border-radius: 12px;
}

.preview-image {
  width: 100%;
  min-height: 640px;
}

.info-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.meta-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.meta-item {
  padding: 14px;
  border-radius: 12px;
  background: #f6f8fb;
  border: 1px solid #dfe6ee;
}

.meta-label {
  display: block;
  font-size: 12px;
  color: #667382;
  margin-bottom: 8px;
}

.meta-value {
  color: #16202a;
  font-size: 15px;
  font-weight: 600;
}

.action-box {
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #dfe6ee;
  background: #fbfcfd;
}

.action-text {
  color: #5b6673;
  font-size: 14px;
  line-height: 1.7;
}

.action-buttons,
.navigation-row {
  margin-top: 14px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.navigation-row {
  justify-content: space-between;
}

@media (max-width: 1440px) {
  .summary-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 1280px) {
  .workspace-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .subjective-review-page {
    padding: 14px;
  }

  .page-title-group {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .summary-grid,
  .meta-grid {
    grid-template-columns: 1fr;
  }
}
</style>
