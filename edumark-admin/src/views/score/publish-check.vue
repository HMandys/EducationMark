<template>
  <div class="publish-check-page">
    <div class="page-toolbar">
      <el-page-header @back="goBack">
        <template #content>
          <div class="title-group">
            <span class="page-title">出分检查</span>
            <span class="page-subtitle">{{ currentExam?.name || checkResult?.examName || '-' }}</span>
          </div>
        </template>
        <template #extra>
          <div class="toolbar-actions">
            <el-button @click="loadPage" :loading="loading">刷新检查</el-button>
            <el-button
              v-if="currentExam?.status === 4"
              type="primary"
              :disabled="!checkResult?.canPublish"
              :loading="publishing"
              @click="handlePublish"
            >
              确认发布
            </el-button>
            <el-button
              v-if="currentExam?.status === 5"
              type="warning"
              :loading="unpublishing"
              @click="handleUnpublish"
            >
              撤回发布
            </el-button>
          </div>
        </template>
      </el-page-header>
    </div>

    <div v-loading="loading" class="content-wrap">
      <div class="result-banner" :class="checkResult?.canPublish ? 'is-pass' : 'is-blocked'">
        <div>
          <div class="result-title">
            {{ checkResult?.canPublish ? '当前可以出分' : '当前不可以出分' }}
          </div>
          <div class="result-desc">
            {{ resultDescription }}
          </div>
        </div>
        <el-tag :type="checkResult?.canPublish ? 'success' : 'danger'" size="large" effect="dark">
          {{ checkResult?.canPublish ? '可发布' : '存在阻塞' }}
        </el-tag>
      </div>

      <div class="metric-grid" v-if="checkResult">
        <el-card shadow="never" class="metric-card">
          <div class="metric-label">答题卡</div>
          <div class="metric-value">{{ checkResult.completedAnswerSheetCount }} / {{ checkResult.answerSheetCount }}</div>
          <div class="metric-desc">已完成阅卷 / 总数</div>
        </el-card>
        <el-card shadow="never" class="metric-card">
          <div class="metric-label">识别异常</div>
          <div class="metric-value danger">{{ checkResult.recognitionExceptionCount }}</div>
          <div class="metric-desc">异常池待处理数量</div>
        </el-card>
        <el-card shadow="never" class="metric-card">
          <div class="metric-label">待阅卷任务</div>
          <div class="metric-value warning">{{ checkResult.unfinishedTaskCount }}</div>
          <div class="metric-desc">未完成任务 / 共 {{ checkResult.markingTaskCount }}</div>
        </el-card>
        <el-card shadow="never" class="metric-card">
          <div class="metric-label">待仲裁</div>
          <div class="metric-value warning">{{ checkResult.pendingArbitrationCount }}</div>
          <div class="metric-desc">仲裁池未处理记录</div>
        </el-card>
      </div>

      <div class="detail-grid" v-if="checkResult">
        <el-card shadow="never" class="detail-card">
          <template #header>
            <div class="card-title">阻塞项</div>
          </template>
          <el-empty
            v-if="!checkResult.blockingItems?.length"
            description="没有阻塞项，可以进入发布"
          />
          <div v-else class="issue-list">
            <div v-for="item in checkResult.blockingItems" :key="item" class="issue-item is-blocking">
              <span class="issue-dot"></span>
              <span>{{ item }}</span>
            </div>
          </div>
        </el-card>

        <el-card shadow="never" class="detail-card">
          <template #header>
            <div class="card-title">提示项</div>
          </template>
          <el-empty
            v-if="!checkResult.warningItems?.length"
            description="没有额外提示"
          />
          <div v-else class="issue-list">
            <div v-for="item in checkResult.warningItems" :key="item" class="issue-item is-warning">
              <span class="issue-dot"></span>
              <span>{{ item }}</span>
            </div>
          </div>
        </el-card>
      </div>

      <el-card v-if="checkResult" shadow="never" class="path-card">
        <template #header>
          <div class="card-title">处理入口</div>
        </template>
        <div class="path-actions">
          <el-button @click="goAnswerSheet(5)">去异常池</el-button>
          <el-button @click="goAnswerSheet()">去答题卡列表</el-button>
          <el-button @click="goMarkingTask">去阅卷任务</el-button>
          <el-button @click="goScoreList">回成绩列表</el-button>
        </div>

        <div class="storage-grid">
          <div class="storage-item">
            <span class="storage-label">科目数</span>
            <strong>{{ checkResult.subjectCount }}</strong>
          </div>
          <div class="storage-item">
            <span class="storage-label">总分汇总记录</span>
            <strong>{{ checkResult.examScoreCount }}</strong>
          </div>
          <div class="storage-item">
            <span class="storage-label">科目成绩记录</span>
            <strong>{{ checkResult.subjectScoreCount }}</strong>
          </div>
          <div class="storage-item">
            <span class="storage-label">统计记录</span>
            <strong>{{ checkResult.statisticsCount }}</strong>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getExamDetail, type Exam } from '@/api/exam'
import {
  getScorePublishCheck,
  publishScore,
  unpublishScore,
  type ScorePublishCheck,
} from '@/api/score'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const publishing = ref(false)
const unpublishing = ref(false)
const currentExam = ref<Exam | null>(null)
const checkResult = ref<ScorePublishCheck | null>(null)

const examId = computed(() => (typeof route.params.id === 'string' ? route.params.id : ''))

const resultDescription = computed(() => {
  if (!checkResult.value) {
    return '正在读取检查结果'
  }
  if (checkResult.value.canPublish) {
    return '识别、阅卷、仲裁链路已收口，发布时会自动补全汇总、排名和统计。'
  }
  return '先处理下面这些阻塞项，再进行正式出分。'
})

onMounted(() => {
  loadPage()
})

async function loadPage() {
  if (!/^\d+$/.test(examId.value)) {
    ElMessage.error('考试参数无效')
    goBack()
    return
  }

  loading.value = true
  try {
    const [examRes, checkRes] = await Promise.all([
      getExamDetail(examId.value),
      getScorePublishCheck(examId.value),
    ])
    currentExam.value = examRes.data
    checkResult.value = checkRes.data
  } finally {
    loading.value = false
  }
}

async function handlePublish() {
  if (!checkResult.value?.canPublish) {
    ElMessage.warning('请先处理阻塞项')
    return
  }

  await ElMessageBox.confirm('确认发布成绩后，学生和家长端将可以查看本次考试成绩。', '提示', {
    type: 'warning',
  })

  publishing.value = true
  try {
    await publishScore(examId.value, userStore.userInfo?.userId || 0)
    ElMessage.success('成绩已发布')
    await loadPage()
  } finally {
    publishing.value = false
  }
}

async function handleUnpublish() {
  await ElMessageBox.confirm('确认撤回后，学生和家长端将暂时无法查看成绩。', '提示', {
    type: 'warning',
  })

  unpublishing.value = true
  try {
    await unpublishScore(examId.value, userStore.userInfo?.userId || 0)
    ElMessage.success('成绩已撤回')
    await loadPage()
  } finally {
    unpublishing.value = false
  }
}

function goAnswerSheet(status?: number) {
  router.push({
    name: 'AnswerSheetList',
    query: {
      examId: String(examId.value),
      ...(status !== undefined ? { status: String(status) } : {}),
    },
  })
}

function goMarkingTask() {
  router.push({
    name: 'MarkingTask',
    query: {
      examId: String(examId.value),
    },
  })
}

function goScoreList() {
  router.push({
    name: 'ScoreList',
    query: {
      examId: String(examId.value),
    },
  })
}

function goBack() {
  goScoreList()
}
</script>

<style scoped>
.publish-check-page {
  padding: 20px;
  min-height: 100%;
  background: #f3f5f7;
}

.page-toolbar {
  margin-bottom: 16px;
}

.title-group {
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
  flex-wrap: wrap;
}

.content-wrap {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.result-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  border-radius: 16px;
  border: 1px solid transparent;
}

.result-banner.is-pass {
  background: #effaf3;
  border-color: #bfe3cb;
}

.result-banner.is-blocked {
  background: #fff3f1;
  border-color: #efc4bc;
}

.result-title {
  font-size: 22px;
  font-weight: 700;
  color: #18222c;
}

.result-desc {
  margin-top: 8px;
  color: #5f6b79;
  font-size: 14px;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.metric-card,
.detail-card,
.path-card {
  border-radius: 14px;
  border: 1px solid #dbe2ea;
}

.metric-label {
  color: #708090;
  font-size: 12px;
  margin-bottom: 8px;
}

.metric-value {
  font-size: 28px;
  line-height: 1;
  font-weight: 700;
  color: #18222c;
  margin-bottom: 8px;
}

.metric-value.danger {
  color: #c2410c;
}

.metric-value.warning {
  color: #b45309;
}

.metric-desc {
  color: #6b7785;
  font-size: 13px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.card-title {
  font-size: 15px;
  font-weight: 700;
  color: #18222c;
}

.issue-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.issue-item {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  padding: 12px 14px;
  border-radius: 10px;
}

.issue-item.is-blocking {
  background: #fff5f2;
  color: #9a3412;
}

.issue-item.is-warning {
  background: #fff9ed;
  color: #9a6700;
}

.issue-dot {
  width: 8px;
  height: 8px;
  margin-top: 7px;
  border-radius: 999px;
  background: currentColor;
  flex: 0 0 auto;
}

.path-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.storage-grid {
  margin-top: 18px;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.storage-item {
  padding: 14px;
  border-radius: 10px;
  background: #f7f9fb;
  border: 1px solid #e2e8f0;
}

.storage-label {
  display: block;
  margin-bottom: 8px;
  color: #708090;
  font-size: 12px;
}

.storage-item strong {
  font-size: 20px;
  color: #18222c;
}

@media (max-width: 1100px) {
  .metric-grid,
  .storage-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .publish-check-page {
    padding: 14px;
  }

  .title-group {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .result-banner {
    flex-direction: column;
    align-items: flex-start;
  }

  .metric-grid,
  .storage-grid {
    grid-template-columns: 1fr;
  }
}
</style>
