<template>
  <div class="exam-workbench" v-loading="loading">
    <el-card class="hero-card" shadow="never">
      <div class="hero-top">
        <div>
          <div class="hero-actions">
            <el-button text @click="goBack">返回考试列表</el-button>
            <el-tag :type="getExamStatusType(exam?.status)">{{ getExamStatusName(exam?.status) }}</el-tag>
          </div>
          <h1 class="hero-title">{{ exam?.name || '考试工作台' }}</h1>
          <p class="hero-subtitle">
            paomadengpaomadeng
          </p>
        </div>
        <div class="hero-quick-actions">
          <el-button type="primary" @click="openAnswerSheetPage">扫描与识别</el-button>
          <el-button @click="openMarkingPage">阅卷任务</el-button>
          <el-button @click="openScorePage()">成绩中心</el-button>
        </div>
      </div>

      <div v-if="exam" class="hero-meta">
        <span>学校：{{ exam.schoolName || '-' }}</span>
        <span>学年：{{ exam.academicYear || '-' }}</span>
        <span>学期：{{ exam.semester === 1 ? '第一学期' : '第二学期' }}</span>
        <span>年级：{{ exam.gradeName || '-' }}</span>
        <span>科目数：{{ exam.subjectCount || subjectList.length }}</span>
      </div>

      <div class="subject-strip">
        <span class="subject-strip__label">考试科目</span>
        <div class="subject-strip__items">
          <el-tag
            v-for="subject in subjectList"
            :key="subject.id"
            effect="plain"
            class="subject-tag"
          >
            {{ subject.subjectName }} {{ subject.fullScore ? `· ${subject.fullScore}分` : '' }}
          </el-tag>
          <span v-if="subjectList.length === 0" class="empty-text">还没有配置科目</span>
        </div>
      </div>
    </el-card>

    <el-row :gutter="16" class="metric-grid">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="metric-card">
          <div class="metric-card__label">答题卡总量</div>
          <div class="metric-card__value">{{ answerSheetStats.total }}</div>
          <div class="metric-card__desc">待阅卷 {{ answerSheetStats.readyForMarking }}，识别异常 {{ answerSheetStats.exceptions }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="metric-card">
          <div class="metric-card__label">阅卷任务</div>
          <div class="metric-card__value">{{ markingStats.total }}</div>
          <div class="metric-card__desc">进行中 {{ markingStats.inProgress }}，已完成 {{ markingStats.completed }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="metric-card">
          <div class="metric-card__label">成绩人数</div>
          <div class="metric-card__value">{{ scoreSummary.studentCount }}</div>
          <div class="metric-card__desc">均分 {{ formatNumber(scoreSummary.avgScore) }}，及格率 {{ formatRate(scoreSummary.passRate) }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="metric-card">
          <div class="metric-card__label">当前出分状态</div>
          <div class="metric-card__value">{{ getExamStatusName(exam?.status) }}</div>
          <div class="metric-card__desc">
            {{ exam?.status === 5 ? '成绩已经发布，可直接查看与导出。' : '建议在这里完成流程检查后再进入出分。' }}
          </div>
        </div>
      </el-col>
    </el-row>

    <el-card class="step-card" shadow="never">
      <el-steps :active="stepActive" finish-status="success" align-center>
        <el-step title="扫描上传" description="答题卡进入系统" />
        <el-step title="识别处理" description="识别与异常修正" />
        <el-step title="阅卷执行" description="任务分配与评分" />
        <el-step title="成绩汇总" description="汇总、排名、统计" />
        <el-step title="出分发布" description="发布与导出" />
      </el-steps>
    </el-card>

    <el-card class="tab-card" shadow="never">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="扫描与识别" name="scan">
          <div class="section-toolbar">
            <div class="section-summary">
              <el-tag effect="plain">识别中 {{ answerSheetStats.pendingRecognition }}</el-tag>
              <el-tag effect="plain" type="primary">已识别 {{ answerSheetStats.recognized }}</el-tag>
              <el-tag effect="plain" type="warning">待阅卷 {{ answerSheetStats.readyForMarking }}</el-tag>
              <el-tag effect="plain" type="danger">异常池 {{ answerSheetStats.exceptions }}</el-tag>
              <el-tag effect="plain" type="success">已完成 {{ answerSheetStats.completed }}</el-tag>
            </div>
            <el-button type="primary" @click="openAnswerSheetPage">打开扫描页</el-button>
          </div>

          <el-table :data="answerSheetList" stripe>
            <el-table-column prop="subjectName" label="科目" width="120" />
            <el-table-column prop="studentName" label="学生" width="120" />
            <el-table-column prop="studentNumber" label="学号" width="140" />
            <el-table-column prop="imageCount" label="图片数" width="90" align="center" />
            <el-table-column label="状态" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="getAnswerSheetStatusType(row.status)">{{ getAnswerSheetStatusName(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="上传时间" min-width="180" />
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="阅卷推进" name="marking">
          <div class="section-toolbar">
            <div class="section-summary">
              <el-tag effect="plain">未开始 {{ markingStats.notStarted }}</el-tag>
              <el-tag effect="plain" type="warning">进行中 {{ markingStats.inProgress }}</el-tag>
              <el-tag effect="plain" type="success">已完成 {{ markingStats.completed }}</el-tag>
            </div>
            <div class="section-actions">
              <el-button @click="openMarkingPage">任务列表</el-button>
              <el-button type="primary" plain @click="openMarkingWorkspace">阅卷工作台</el-button>
            </div>
          </div>

          <el-table :data="markingTaskList" stripe>
            <el-table-column prop="subjectName" label="科目" width="100" />
            <el-table-column prop="questionNo" label="题号" width="90" />
            <el-table-column prop="name" label="任务名称" min-width="180" />
            <el-table-column label="进度" min-width="220">
              <template #default="{ row }">
                <div class="progress-cell">
                  <el-progress :percentage="getTaskProgress(row)" :stroke-width="10" />
                  <span>{{ row.completedCount }} / {{ row.totalCount }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="getMarkingStatusType(row.status)">{{ getMarkingStatusName(row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="成绩与出分" name="score">
          <div class="section-toolbar">
            <div class="section-summary">
              <el-tag effect="plain">参考人数 {{ scoreSummary.studentCount }}</el-tag>
              <el-tag effect="plain" type="primary">平均分 {{ formatNumber(scoreSummary.avgScore) }}</el-tag>
              <el-tag effect="plain" type="success">及格率 {{ formatRate(scoreSummary.passRate) }}</el-tag>
              <el-tag effect="plain" type="warning">优秀率 {{ formatRate(scoreSummary.excellentRate) }}</el-tag>
            </div>
            <div class="section-actions">
              <el-button @click="openScorePage()">成绩列表</el-button>
              <el-button type="primary" plain @click="openScorePage('statistics')">统计分析</el-button>
            </div>
          </div>

          <el-alert
            :title="scoreAlertTitle"
            :description="scoreAlertDescription"
            :type="exam?.status === 5 ? 'success' : 'info'"
            :closable="false"
            show-icon
            class="score-alert"
          />

          <el-table :data="examScoreList" stripe>
            <el-table-column prop="studentName" label="学生" width="120" />
            <el-table-column prop="studentNumber" label="学号" width="140" />
            <el-table-column prop="className" label="班级" width="140" />
            <el-table-column prop="totalScore" label="总分" width="100" align="center" />
            <el-table-column prop="classRank" label="班排" width="90" align="center" />
            <el-table-column prop="gradeRank" label="年排" width="90" align="center" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { getAnswerSheetPage, type AnswerSheet } from '@/api/answerSheet'
import { getExamDetail, getExamSubjectList, type Exam, type ExamSubject } from '@/api/exam'
import { pageMarkingTasks, type MarkingTaskVO } from '@/api/marking'
import { getExamScorePage, getScoreStatistics, type ExamScore, type ScoreStatistics } from '@/api/score'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const exam = ref<Exam | null>(null)
const subjectList = ref<ExamSubject[]>([])
const answerSheetList = ref<AnswerSheet[]>([])
const markingTaskList = ref<MarkingTaskVO[]>([])
const examScoreList = ref<ExamScore[]>([])
const statisticsList = ref<ScoreStatistics[]>([])
const activeTab = ref<'scan' | 'marking' | 'score'>('scan')

const answerSheetStats = reactive({
  total: 0,
  pendingRecognition: 0,
  recognized: 0,
  readyForMarking: 0,
  exceptions: 0,
  marking: 0,
  completed: 0,
})

const markingStats = reactive({
  total: 0,
  notStarted: 0,
  inProgress: 0,
  completed: 0,
})

const examId = computed(() => {
  const value = Number(route.params.id)
  return Number.isFinite(value) && value > 0 ? value : 0
})

const gradeSummary = computed(() => {
  return statisticsList.value.find((item) => item.statType === 4) || statisticsList.value.find((item) => item.statType === 3) || null
})

const scoreSummary = computed(() => ({
  studentCount: gradeSummary.value?.studentCount || 0,
  avgScore: gradeSummary.value?.avgScore || 0,
  passRate: gradeSummary.value?.passRate || 0,
  excellentRate: gradeSummary.value?.excellentRate || 0,
}))

const stepActive = computed(() => {
  const status = exam.value?.status ?? 0
  if (status <= 1) return 0
  if (status === 2) return 1
  if (status === 3) return 2
  if (status === 4) return 3
  return 4
})

const scoreAlertTitle = computed(() => {
  if (exam.value?.status === 5) {
    return '当前考试已经出分，学生和家长端可查询成绩。'
  }
  if (exam.value?.status === 4) {
    return '当前考试已完成，建议先检查统计结果，再进入发布。'
  }
  return '当前考试还未到正式出分阶段，建议先完成扫描、阅卷和汇总。'
})

const scoreAlertDescription = computed(() => {
  return `当前已收拢 ${markingStats.completed} 个完成任务，成绩人数 ${scoreSummary.value.studentCount}。如果还缺识别异常处理或阅卷收尾，不建议直接发布。`
})

const fetchAnswerSheetStatusTotal = async (status?: number) => {
  const res = await getAnswerSheetPage({
    pageNum: 1,
    pageSize: 1,
    examId: examId.value,
    status,
  })
  return res.data.total || 0
}

const fetchMarkingStatusTotal = async (status?: number) => {
  const res = await pageMarkingTasks({
    pageNum: 1,
    pageSize: 1,
    examId: examId.value,
    status,
  })
  return res.data.total || 0
}

const loadExamBase = async () => {
  const [detailRes, subjectRes] = await Promise.all([
    getExamDetail(examId.value),
    getExamSubjectList(examId.value),
  ])
  exam.value = detailRes.data
  subjectList.value = subjectRes.data || []
}

const loadAnswerSheetPanel = async () => {
  const [listRes, total, pendingRecognition, recognized, readyForMarking, exceptions, marking, completed] = await Promise.all([
    getAnswerSheetPage({ pageNum: 1, pageSize: 8, examId: examId.value }),
    fetchAnswerSheetStatusTotal(),
    fetchAnswerSheetStatusTotal(0),
    fetchAnswerSheetStatusTotal(1),
    fetchAnswerSheetStatusTotal(2),
    fetchAnswerSheetStatusTotal(5),
    fetchAnswerSheetStatusTotal(3),
    fetchAnswerSheetStatusTotal(4),
  ])

  answerSheetList.value = listRes.data.list || []
  answerSheetStats.total = total
  answerSheetStats.pendingRecognition = pendingRecognition
  answerSheetStats.recognized = recognized
  answerSheetStats.readyForMarking = readyForMarking
  answerSheetStats.exceptions = exceptions
  answerSheetStats.marking = marking
  answerSheetStats.completed = completed
}

const loadMarkingPanel = async () => {
  const [listRes, total, notStarted, inProgress, completed] = await Promise.all([
    pageMarkingTasks({ pageNum: 1, pageSize: 8, examId: examId.value }),
    fetchMarkingStatusTotal(),
    fetchMarkingStatusTotal(0),
    fetchMarkingStatusTotal(1),
    fetchMarkingStatusTotal(2),
  ])

  markingTaskList.value = listRes.data.list || []
  markingStats.total = total
  markingStats.notStarted = notStarted
  markingStats.inProgress = inProgress
  markingStats.completed = completed
}

const loadScorePanel = async () => {
  const [scoreRes, statRes] = await Promise.all([
    getExamScorePage({ pageNum: 1, pageSize: 8, examId: examId.value }),
    getScoreStatistics(examId.value),
  ])

  examScoreList.value = scoreRes.data.list || []
  statisticsList.value = statRes.data || []
}

const loadWorkbench = async () => {
  if (!examId.value) {
    ElMessage.error('考试参数无效')
    router.replace('/exam/list')
    return
  }

  loading.value = true
  try {
    await Promise.all([
      loadExamBase(),
      loadAnswerSheetPanel(),
      loadMarkingPanel(),
      loadScorePanel(),
    ])
  } catch (error) {
    console.error('加载考试工作台失败', error)
    ElMessage.error('加载考试工作台失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/exam/list')
}

const openAnswerSheetPage = () => {
  router.push({
    path: '/exam/answer-sheet',
    query: { examId: String(examId.value) },
  })
}

const openMarkingPage = () => {
  router.push({
    path: '/marking/task',
    query: { examId: String(examId.value) },
  })
}

const openMarkingWorkspace = () => {
  router.push('/marking/workspace')
}

const openScorePage = (viewMode: 'score' | 'statistics' = 'score') => {
  router.push({
    path: '/score/list',
    query: {
      examId: String(examId.value),
      viewMode,
    },
  })
}

const getExamStatusName = (status?: number) => {
  const statusMap: Record<number, string> = {
    0: '草稿',
    1: '待考试',
    2: '考试中',
    3: '阅卷中',
    4: '已完成',
    5: '已发布',
  }
  return statusMap[status ?? 0] || '未知'
}

const getExamStatusType = (status?: number): 'info' | 'warning' | 'success' | 'primary' => {
  const typeMap: Record<number, 'info' | 'warning' | 'success' | 'primary'> = {
    0: 'info',
    1: 'info',
    2: 'warning',
    3: 'warning',
    4: 'primary',
    5: 'success',
  }
  return typeMap[status ?? 0] || 'info'
}

const getAnswerSheetStatusName = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '识别中',
    1: '已识别',
    2: '待阅卷',
    3: '阅卷中',
    4: '已完成',
    5: '识别异常',
  }
  return statusMap[status] || '未知'
}

const getAnswerSheetStatusType = (status: number): 'info' | 'primary' | 'warning' | 'success' | 'danger' => {
  const typeMap: Record<number, 'info' | 'primary' | 'warning' | 'success' | 'danger'> = {
    0: 'info',
    1: 'primary',
    2: 'warning',
    3: 'warning',
    4: 'success',
    5: 'danger',
  }
  return typeMap[status] || 'info'
}

const getMarkingStatusName = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '未开始',
    1: '进行中',
    2: '已完成',
  }
  return statusMap[status] || '未知'
}

const getMarkingStatusType = (status: number): 'info' | 'warning' | 'success' => {
  const typeMap: Record<number, 'info' | 'warning' | 'success'> = {
    0: 'info',
    1: 'warning',
    2: 'success',
  }
  return typeMap[status] || 'info'
}

const getTaskProgress = (task: MarkingTaskVO) => {
  if (!task.totalCount) {
    return 0
  }
  return Math.round(task.completedCount / task.totalCount * 100)
}

const formatRate = (value: number) => `${Number(value || 0).toFixed(1)}%`
const formatNumber = (value: number) => Number(value || 0).toFixed(1)

watch(activeTab, (value) => {
  if (route.query.tab === value) {
    return
  }
  router.replace({
    query: {
      ...route.query,
      tab: value,
    },
  })
})

watch(() => route.params.id, () => {
  loadWorkbench()
})

watch(() => route.query.tab, (value) => {
  const tab = value === 'marking' || value === 'score' ? value : 'scan'
  activeTab.value = tab
}, { immediate: true })

onMounted(() => {
  loadWorkbench()
})
</script>

<style scoped lang="scss">
.exam-workbench {
  min-height: 100%;
  padding: 20px;
  background: #f4f6f8;
}

.hero-card,
.step-card,
.tab-card {
  border: 1px solid #e6eaef;
  border-radius: 20px;
  background: #fffdf8;
}

.hero-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
}

.hero-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.hero-title {
  margin: 0;
  color: #1f2937;
  font-size: 30px;
  line-height: 1.2;
}

.hero-subtitle {
  max-width: 720px;
  margin: 10px 0 0;
  color: #5b6472;
  line-height: 1.7;
}

.hero-quick-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.hero-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
  margin-top: 18px;
  color: #5b6472;
}

.subject-strip {
  margin-top: 20px;
  padding-top: 18px;
  border-top: 1px solid #ece8df;
}

.subject-strip__label {
  display: block;
  margin-bottom: 12px;
  color: #8b93a1;
  font-size: 13px;
}

.subject-strip__items {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.subject-tag {
  padding-inline: 12px;
}

.empty-text {
  color: #8b93a1;
}

.metric-grid {
  margin-top: 16px;
}

.metric-card {
  height: 100%;
  padding: 20px;
  border: 1px solid #e6eaef;
  border-radius: 18px;
  background: #ffffff;
}

.metric-card__label {
  color: #8b93a1;
  font-size: 13px;
}

.metric-card__value {
  margin-top: 10px;
  color: #1f2937;
  font-size: 30px;
  font-weight: 700;
}

.metric-card__desc {
  margin-top: 10px;
  color: #5b6472;
  line-height: 1.6;
}

.step-card,
.tab-card {
  margin-top: 16px;
}

.section-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.section-summary,
.section-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.progress-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.score-alert {
  margin-bottom: 16px;
}

@media (max-width: 960px) {
  .hero-top,
  .section-toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .hero-title {
    font-size: 24px;
  }
}
</style>
