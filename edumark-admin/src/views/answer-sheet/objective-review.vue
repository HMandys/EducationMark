<template>
  <div class="objective-review-page">
    <div class="page-toolbar">
      <el-page-header @back="goBack">
        <template #content>
          <div class="page-title-group">
            <span class="page-title">客观题复核</span>
            <span class="page-subtitle">
              {{ answerSheet?.examName || '-' }} / {{ answerSheet?.subjectName || '-' }}
            </span>
          </div>
        </template>
        <template #extra>
          <div class="toolbar-actions">
            <el-button :loading="recognizing" @click="handleRecognizeAgain">重新识别</el-button>
            <el-button
              type="primary"
              :loading="saving"
              :disabled="!currentQuestion"
              @click="handleSaveCurrent"
            >
              保存本题
            </el-button>
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
        <div class="summary-label">客观题数</div>
        <div class="summary-value">{{ objectiveQuestions.length }}</div>
        <div class="summary-desc">当前答题卡可复核题目</div>
      </el-card>
      <el-card shadow="never" class="summary-card">
        <div class="summary-label">待复核</div>
        <div class="summary-value">{{ pendingReviewCount }}</div>
        <div class="summary-desc">已识别但尚未人工确认</div>
      </el-card>
      <el-card shadow="never" class="summary-card">
        <div class="summary-label">客观得分</div>
        <div class="summary-value">{{ answerSheet?.objectiveScore ?? 0 }}</div>
        <div class="summary-desc">总分 {{ answerSheet?.totalScore ?? 0 }}</div>
      </el-card>
    </div>

    <div class="workspace-grid" v-loading="loading">
      <el-card shadow="never" class="question-panel">
        <template #header>
          <div class="panel-header">
            <span>题目列表</span>
            <el-tag type="info" effect="plain">{{ objectiveQuestions.length }} 题</el-tag>
          </div>
        </template>

        <el-empty v-if="objectiveQuestions.length === 0" description="当前答题卡没有客观题明细" />

        <div v-else class="question-list">
          <button
            v-for="item in objectiveQuestions"
            :key="item.questionId"
            type="button"
            class="question-item"
            :class="{ 'is-active': currentQuestion?.questionId === item.questionId }"
            @click="handleSelectQuestion(item)"
          >
            <div class="question-item__top">
              <span class="question-item__no">{{ item.questionNo || item.questionId }}</span>
              <el-tag
                size="small"
                :type="item.status === 1 ? 'success' : item.studentAnswer ? 'warning' : 'info'"
                effect="plain"
              >
                {{ item.statusName || '待处理' }}
              </el-tag>
            </div>
            <div class="question-item__answer">
              <span>识别 {{ item.studentAnswer || '未识别' }}</span>
              <span>标准 {{ item.correctAnswer || '-' }}</span>
            </div>
            <div class="question-item__score">
              得分 {{ item.score ?? 0 }} / {{ item.fullScore || 0 }}
            </div>
          </button>
        </div>
      </el-card>

      <el-card shadow="never" class="preview-panel">
        <template #header>
          <div class="panel-header">
            <span>{{ currentQuestion ? `题号 ${currentQuestion.questionNo}` : '题图预览' }}</span>
            <span class="panel-meta">{{ currentQuestion?.regionRoleName || '未配置区域' }}</span>
          </div>
        </template>

        <div v-loading="previewLoading" class="preview-body">
          <el-empty
            v-if="!previewUrl && !previewLoading"
            description="选择题目后显示识别区域预览"
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

      <el-card shadow="never" class="review-panel">
        <template #header>
          <div class="panel-header">
            <span>复核操作</span>
            <el-tag v-if="currentQuestion" effect="plain">
              {{ currentQuestion.questionTypeName || '客观题' }}
            </el-tag>
          </div>
        </template>

        <el-empty v-if="!currentQuestion" description="请选择左侧题目" />

        <div v-else class="review-body">
          <div class="meta-grid">
            <div class="meta-item">
              <span class="meta-label">标准答案</span>
              <span class="meta-value">{{ currentQuestion.correctAnswer || '-' }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">识别结果</span>
              <span class="meta-value">{{ currentQuestion.studentAnswer || '未识别' }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">当前得分</span>
              <span class="meta-value">{{ currentQuestion.score ?? 0 }} / {{ currentQuestion.fullScore || 0 }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">复核状态</span>
              <span class="meta-value">{{ currentQuestion.statusName || '待处理' }}</span>
            </div>
          </div>

          <div class="editor-section">
            <div class="editor-title">修正答案</div>
            <div class="editor-desc">
              {{ isMultipleChoice(currentQuestion) ? '可多选，系统会自动排序并重算得分。' : '单击切换选项，再保存本题。' }}
            </div>

            <div class="option-grid">
              <button
                v-for="option in currentOptions"
                :key="option"
                type="button"
                class="option-button"
                :class="{ 'is-selected': draftAnswerSet.has(option) }"
                @click="toggleOption(option)"
              >
                {{ option }}
              </button>
            </div>

            <div class="draft-row">
              <div class="draft-answer">
                当前修正：<strong>{{ draftAnswer || '空白' }}</strong>
              </div>
              <div class="draft-actions">
                <el-button @click="resetDraft">恢复识别值</el-button>
                <el-button @click="clearDraft">清空答案</el-button>
              </div>
            </div>
          </div>

          <div class="navigation-row">
            <el-button :disabled="currentIndex <= 0" @click="selectRelativeQuestion(-1)">上一题</el-button>
            <el-button :disabled="currentIndex >= objectiveQuestions.length - 1" @click="selectRelativeQuestion(1)">下一题</el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import {
  getAnswerSheetDetail,
  getAnswerSheetQuestionDetails,
  getAnswerSheetQuestionPreview,
  recognizeObjectiveAnswers,
  updateObjectiveAnswer,
  type AnswerSheet,
  type AnswerSheetQuestionDetail,
} from '@/api/answerSheet'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const previewLoading = ref(false)
const saving = ref(false)
const recognizing = ref(false)

const answerSheet = ref<AnswerSheet | null>(null)
const objectiveQuestions = ref<AnswerSheetQuestionDetail[]>([])
const currentQuestionId = ref<string | number>()
const previewUrl = ref('')
const draftAnswer = ref('')

const answerSheetId = computed(() => (typeof route.params.id === 'string' ? route.params.id : ''))

const currentQuestion = computed(() =>
  objectiveQuestions.value.find((item) => item.questionId === currentQuestionId.value) || null
)

const currentIndex = computed(() =>
  objectiveQuestions.value.findIndex((item) => item.questionId === currentQuestionId.value)
)

const pendingReviewCount = computed(() =>
  objectiveQuestions.value.filter((item) => item.status !== 1 && !!item.studentAnswer).length
)

const draftAnswerSet = computed(() => new Set(splitAnswer(draftAnswer.value)))

const currentOptions = computed(() => {
  const detail = currentQuestion.value
  if (!detail) {
    return ['A', 'B', 'C', 'D']
  }

  const maxIndexFromAnswer = Math.max(
    ...[detail.correctAnswer, detail.studentAnswer, draftAnswer.value]
      .flatMap(splitAnswer)
      .map((option) => option.charCodeAt(0) - 64),
    0
  )

  const optionCount = Math.max(detail.optionCount || 4, maxIndexFromAnswer || 0, 4)
  return Array.from({ length: optionCount }, (_, index) => String.fromCharCode(65 + index))
})

const loadPage = async () => {
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
    objectiveQuestions.value = (detailRes.data || []).filter((item) => item.isObjective === 1)

    if (objectiveQuestions.value.length > 0) {
      const nextQuestion = objectiveQuestions.value.find((item) => item.questionId === currentQuestionId.value)
        || objectiveQuestions.value[0]
      await handleSelectQuestion(nextQuestion)
    } else {
      currentQuestionId.value = undefined
      draftAnswer.value = ''
      previewUrl.value = ''
    }
  } finally {
    loading.value = false
  }
}

const loadPreview = async (detail: AnswerSheetQuestionDetail) => {
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

const handleSelectQuestion = async (detail: AnswerSheetQuestionDetail) => {
  currentQuestionId.value = detail.questionId
  draftAnswer.value = detail.studentAnswer || ''
  await loadPreview(detail)
}

const splitAnswer = (answer?: string) =>
  Array.from(new Set((answer || '').toUpperCase().replace(/[^A-Z0-9]/g, '').split('').filter(Boolean))).sort()

const normalizeAnswer = (answer?: string) => {
  const parts = splitAnswer(answer)
  return parts.length > 0 ? parts.join('') : ''
}

const isMultipleChoice = (detail: AnswerSheetQuestionDetail) => {
  if (detail.questionType === 2) {
    return true
  }
  return splitAnswer(detail.correctAnswer).length > 1
}

const toggleOption = (option: string) => {
  const detail = currentQuestion.value
  if (!detail) {
    return
  }

  if (!isMultipleChoice(detail)) {
    draftAnswer.value = draftAnswerSet.value.has(option) ? '' : option
    return
  }

  const next = new Set(draftAnswerSet.value)
  if (next.has(option)) {
    next.delete(option)
  } else {
    next.add(option)
  }
  draftAnswer.value = Array.from(next).sort().join('')
}

const resetDraft = () => {
  draftAnswer.value = currentQuestion.value?.studentAnswer || ''
}

const clearDraft = () => {
  draftAnswer.value = ''
}

const updateLocalQuestion = (next: AnswerSheetQuestionDetail) => {
  const index = objectiveQuestions.value.findIndex((item) => item.questionId === next.questionId)
  if (index < 0) {
    return
  }
  objectiveQuestions.value.splice(index, 1, next)
  currentQuestionId.value = next.questionId
  draftAnswer.value = next.studentAnswer || ''
}

const handleSaveCurrent = async () => {
  if (!currentQuestion.value) {
    return
  }

  saving.value = true
  try {
    const res = await updateObjectiveAnswer(answerSheetId.value, currentQuestion.value.questionId, {
      studentAnswer: normalizeAnswer(draftAnswer.value),
    })
    updateLocalQuestion(res.data)
    if (answerSheet.value) {
      answerSheet.value.objectiveScore = objectiveQuestions.value.reduce((sum, item) => sum + (item.score || 0), 0)
      answerSheet.value.totalScore = (answerSheet.value.objectiveScore || 0) + (answerSheet.value.subjectiveScore || 0)
    }
    ElMessage.success('本题已更新')
  } finally {
    saving.value = false
  }
}

const handleRecognizeAgain = async () => {
  recognizing.value = true
  try {
    const res = await recognizeObjectiveAnswers(answerSheetId.value)
    objectiveQuestions.value = (res.data || []).filter((item) => item.isObjective === 1)
    if (objectiveQuestions.value.length > 0) {
      await handleSelectQuestion(
        objectiveQuestions.value.find((item) => item.questionId === currentQuestionId.value) || objectiveQuestions.value[0]
      )
    }
    await refreshSheetScore()
    ElMessage.success('已重新识别客观题')
  } finally {
    recognizing.value = false
  }
}

const refreshSheetScore = async () => {
  const res = await getAnswerSheetDetail(answerSheetId.value)
  answerSheet.value = res.data
}

const selectRelativeQuestion = async (offset: number) => {
  const target = objectiveQuestions.value[currentIndex.value + offset]
  if (!target) {
    return
  }
  await handleSelectQuestion(target)
}

const goBack = () => {
  router.push({
    name: 'AnswerSheetList',
    query: route.query.examId ? { examId: String(route.query.examId) } : undefined,
  })
}

onMounted(() => {
  loadPage()
})
</script>

<style scoped>
.objective-review-page {
  padding: 20px;
  background: #f3f5f7;
  min-height: 100%;
}

.page-toolbar {
  margin-bottom: 16px;
}

.page-title-group {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: #18222c;
}

.page-subtitle {
  color: #6b7785;
  font-size: 13px;
}

.toolbar-actions {
  display: flex;
  gap: 10px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 16px;
}

.summary-card {
  border: 1px solid #dbe2ea;
  border-radius: 14px;
}

.summary-label {
  color: #708090;
  font-size: 12px;
  margin-bottom: 8px;
}

.summary-value {
  font-size: 28px;
  line-height: 1;
  font-weight: 700;
  color: #18222c;
  margin-bottom: 8px;
}

.summary-desc {
  color: #6b7785;
  font-size: 13px;
}

.workspace-grid {
  display: grid;
  grid-template-columns: minmax(260px, 320px) minmax(380px, 1fr) minmax(340px, 420px);
  gap: 16px;
}

.question-panel,
.preview-panel,
.review-panel {
  border-radius: 14px;
  border: 1px solid #dbe2ea;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.panel-meta {
  color: #6b7785;
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
  border: 1px solid #d8e0e8;
  border-radius: 12px;
  background: #fff;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.18s ease, box-shadow 0.18s ease, transform 0.18s ease;
}

.question-item:hover,
.question-item.is-active {
  border-color: #1f6feb;
  box-shadow: 0 10px 24px rgba(20, 45, 90, 0.08);
  transform: translateY(-1px);
}

.question-item__top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.question-item__no {
  font-size: 16px;
  font-weight: 700;
  color: #18222c;
}

.question-item__answer,
.question-item__score {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  color: #5f6b79;
  font-size: 13px;
}

.question-item__score {
  margin-top: 8px;
  color: #18222c;
}

.preview-body {
  min-height: 680px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f7f9fb;
  border-radius: 12px;
}

.preview-image {
  width: 100%;
  min-height: 640px;
}

.review-body {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.meta-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.meta-item {
  padding: 14px;
  border-radius: 12px;
  background: #f7f9fb;
  border: 1px solid #e2e8f0;
}

.meta-label {
  display: block;
  font-size: 12px;
  color: #6b7785;
  margin-bottom: 8px;
}

.meta-value {
  color: #18222c;
  font-size: 15px;
  font-weight: 600;
}

.editor-section {
  padding: 16px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #fbfcfd;
}

.editor-title {
  font-size: 15px;
  font-weight: 700;
  color: #18222c;
}

.editor-desc {
  margin-top: 6px;
  color: #6b7785;
  font-size: 13px;
}

.option-grid {
  margin-top: 14px;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}

.option-button {
  height: 48px;
  border: 1px solid #c9d5e3;
  border-radius: 10px;
  background: #fff;
  color: #18222c;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.18s ease;
}

.option-button:hover,
.option-button.is-selected {
  border-color: #1f6feb;
  background: #edf4ff;
  color: #1f6feb;
}

.draft-row {
  margin-top: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.draft-answer {
  color: #18222c;
  font-size: 14px;
}

.draft-actions,
.navigation-row {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.navigation-row {
  justify-content: space-between;
}

@media (max-width: 1280px) {
  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .workspace-grid {
    grid-template-columns: 1fr;
  }

  .preview-body {
    min-height: 480px;
  }
}

@media (max-width: 768px) {
  .objective-review-page {
    padding: 14px;
  }

  .page-title-group {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .summary-grid,
  .meta-grid,
  .option-grid {
    grid-template-columns: 1fr;
  }
}
</style>
