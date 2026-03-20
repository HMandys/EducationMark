<template>
  <div class="exam-workbench" v-loading="loading">
    <el-card class="hero-card" shadow="never">
      <div class="hero-top">
        <div class="hero-main">
          <div class="hero-actions">
            <el-button text @click="goBack">返回考试列表</el-button>
            <el-tag :type="getExamStatusType(exam?.status)">{{ getExamStatusName(exam?.status) }}</el-tag>
          </div>
          <h1 class="hero-title">{{ exam?.name || '考试工作台' }}</h1>
          <p class="hero-subtitle">
            这里不再按模块拆开看，而是按一场考试的处理链收口：
            扫描识别池、客观题复核池、主观题核验池、待仲裁池，最后直接进入出分检查。
          </p>
          <div v-if="exam" class="hero-meta">
            <span>学校：{{ exam.schoolName || '-' }}</span>
            <span>学年：{{ exam.academicYear || '-' }}</span>
            <span>年级：{{ exam.gradeName || '-' }}</span>
            <span>科目数：{{ exam.subjectCount || subjectList.length }}</span>
            <span>参考人数：{{ exam.studentCount || 0 }}</span>
          </div>
        </div>

        <div class="hero-quick-actions">
          <el-button type="primary" @click="openAnswerSheetPage()">批量上传扫描图</el-button>
          <el-button @click="() => openMarkingPage()">阅卷任务</el-button>
          <el-button @click="openPublishCheck">出分检查</el-button>
          <el-button @click="openScorePage()">成绩中心</el-button>
        </div>
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
            {{ subject.subjectName }}{{ subject.fullScore ? ` · ${subject.fullScore}分` : '' }}
          </el-tag>
          <span v-if="subjectList.length === 0" class="empty-text">还没有配置科目</span>
        </div>
      </div>
    </el-card>

    <el-card class="next-card" shadow="never">
      <div class="next-card__header">
        <div>
          <div class="next-card__label">当前下一步</div>
          <div class="next-card__title">{{ nextAction.title }}</div>
        </div>
        <el-button type="primary" @click="nextAction.action">{{ nextAction.buttonText }}</el-button>
      </div>
      <div class="next-card__desc">{{ nextAction.description }}</div>
    </el-card>

    <el-card class="overview-card" shadow="never">
      <div class="overview-header">
        <div>
          <div class="overview-label">流程总览</div>
          <div class="overview-title">按“识别 -> 异常复核 -> 阅卷仲裁 -> 出分检查”收口</div>
        </div>
        <el-tag :type="publishCheck?.canPublish ? 'success' : 'warning'" effect="plain">
          {{ publishCheck?.canPublish ? '当前可出分' : '仍有流程积压' }}
        </el-tag>
      </div>

      <div class="overview-grid">
        <div class="overview-item">
          <div class="overview-item__label">扫描识别</div>
          <div class="overview-item__value">{{ scanPoolCount }}</div>
          <div class="overview-item__desc">
            识别中 {{ answerSheetStats.pendingRecognition }} / 异常 {{ answerSheetStats.exceptions }}
          </div>
        </div>
        <div class="overview-item">
          <div class="overview-item__label">异常复核</div>
          <div class="overview-item__value">{{ reviewBacklogCount }}</div>
          <div class="overview-item__desc">
            客观题 {{ objectivePoolQuestionCount }} / 主观题 {{ subjectivePoolQuestionCount }}
          </div>
        </div>
        <div class="overview-item">
          <div class="overview-item__label">待阅卷流</div>
          <div class="overview-item__value">{{ readyForScoringCount }}</div>
          <div class="overview-item__desc">
            待阅卷 {{ answerSheetStats.readyForMarking }} / 阅卷中 {{ answerSheetStats.marking }} / 已完成 {{ answerSheetStats.completed }}
          </div>
        </div>
        <div class="overview-item">
          <div class="overview-item__label">阅卷仲裁</div>
          <div class="overview-item__value">{{ markingBacklogCount }}</div>
          <div class="overview-item__desc">
            未完成任务 {{ publishCheck?.unfinishedTaskCount || 0 }} / 待仲裁 {{ publishCheck?.pendingArbitrationCount || 0 }}
          </div>
        </div>
      </div>
    </el-card>

    <div class="pool-grid">
      <div class="pool-card scan-pool">
        <div class="pool-card__header">
          <div>
            <div class="pool-card__label">扫描识别池</div>
            <div class="pool-card__value">{{ scanPoolCount }}</div>
          </div>
          <el-button text @click="openAnswerSheetPage()">进入</el-button>
        </div>
        <div class="pool-card__desc">
          扫描上传后的入口池。只要识别中或识别异常还没清掉，后续流程都不应该继续推进。
        </div>
        <div class="pool-card__tags">
          <el-tag effect="plain">识别中 {{ answerSheetStats.pendingRecognition }}</el-tag>
          <el-tag effect="plain" type="danger">异常池 {{ answerSheetStats.exceptions }}</el-tag>
          <el-tag effect="plain" type="success">已识别 {{ scanResolvedCount }}</el-tag>
        </div>
      </div>

      <div class="pool-card objective-pool">
        <div class="pool-card__header">
          <div>
            <div class="pool-card__label">客观题复核池</div>
            <div class="pool-card__value">{{ objectivePoolQuestionCount }}</div>
          </div>
          <el-button text :disabled="objectivePoolList.length === 0" @click="openFirstObjectivePool">
            进入
          </el-button>
        </div>
        <div class="pool-card__desc">
          这里只保留待确认的客观题项。正常识别结果不需要人工逐题过一遍。
        </div>
        <div class="pool-card__tags">
          <el-tag effect="plain">待复核答题卡 {{ objectivePoolList.length }}</el-tag>
          <el-tag effect="plain" type="primary">待复核题数 {{ objectivePoolQuestionCount }}</el-tag>
        </div>
      </div>

      <div class="pool-card subjective-pool">
        <div class="pool-card__header">
          <div>
            <div class="pool-card__label">主观题核验池</div>
            <div class="pool-card__value">{{ subjectivePoolQuestionCount }}</div>
          </div>
          <el-button text :disabled="subjectivePoolList.length === 0" @click="openFirstSubjectivePool">
            进入
          </el-button>
        </div>
        <div class="pool-card__desc">
          这里只放真正的裁题异常项，例如未绑定裁题区域、缺少对应扫描页、无法生成题图。
        </div>
        <div class="pool-card__tags">
          <el-tag effect="plain">异常答题卡 {{ subjectivePoolList.length }}</el-tag>
          <el-tag effect="plain" type="warning">异常题数 {{ subjectivePoolQuestionCount }}</el-tag>
        </div>
      </div>

      <div class="pool-card arbitration-pool">
        <div class="pool-card__header">
          <div>
            <div class="pool-card__label">待仲裁池</div>
            <div class="pool-card__value">{{ publishCheck?.pendingArbitrationCount || 0 }}</div>
          </div>
          <el-button text @click="openMarkingWorkspace">进入</el-button>
        </div>
        <div class="pool-card__desc">
          阅卷任务先清零，再收口待仲裁。仲裁没有处理完，就不应该进入正式出分。
        </div>
        <div class="pool-card__tags">
          <el-tag effect="plain">未完成任务 {{ publishCheck?.unfinishedTaskCount || 0 }}</el-tag>
          <el-tag effect="plain">双评任务 {{ doubleMarkingTaskCount }}</el-tag>
          <el-tag effect="plain" type="danger">待仲裁 {{ publishCheck?.pendingArbitrationCount || 0 }}</el-tag>
        </div>
      </div>
    </div>

    <el-card class="section-card subject-workflow-card" shadow="never">
      <template #header>
        <div class="section-card__header">
          <span>科目推进视图</span>
          <span class="section-card__meta">直接看每一科当前卡点，不再自己拼状态</span>
        </div>
      </template>

      <el-table :data="subjectWorkflowList" stripe empty-text="当前考试还没有科目">
        <el-table-column prop="subjectName" label="科目" min-width="120">
          <template #default="{ row }">
            <div class="subject-cell">
              <strong>{{ row.subjectName }}</strong>
              <span>{{ row.fullScore ? `${row.fullScore}分` : '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="扫描积压" width="100" align="center">
          <template #default="{ row }">{{ row.scanBacklogCount }}</template>
        </el-table-column>
        <el-table-column label="客观复核" width="100" align="center">
          <template #default="{ row }">{{ row.objectiveReviewCount }}</template>
        </el-table-column>
        <el-table-column label="主观核验" width="100" align="center">
          <template #default="{ row }">{{ row.subjectiveReviewCount }}</template>
        </el-table-column>
        <el-table-column label="待阅卷流" width="120" align="center">
          <template #default="{ row }">
            {{ row.readyForMarkingCount + row.markingCount + row.completedCount }}
          </template>
        </el-table-column>
        <el-table-column label="任务状态" min-width="170">
          <template #default="{ row }">
            <div class="task-state-cell">
              <span>未开始 {{ row.notStartedTaskCount }}</span>
              <span>进行中 {{ row.activeTaskCount }}</span>
              <span>已完成 {{ row.completedTaskCount }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="当前卡点" min-width="160">
          <template #default="{ row }">
            <el-tag :type="row.stepType" effect="plain">{{ row.currentStep }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleSubjectPrimaryAction(row)">
              {{ row.primaryActionText }}
            </el-button>
            <el-button link @click="openAnswerSheetPage(undefined, row.examSubjectId)">查看答题卡</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <div class="section-grid">
      <el-card class="section-card" shadow="never">
        <template #header>
          <div class="section-card__header">
            <span>扫描识别池</span>
            <div class="section-card__actions">
              <el-tag effect="plain">待处理 {{ scanPoolCount }}</el-tag>
              <el-button text @click="openAnswerSheetPage(5)">只看异常池</el-button>
              <el-button text @click="openAnswerSheetPage(0)">只看识别中</el-button>
            </div>
          </div>
        </template>

        <el-table :data="scanPoolList" stripe empty-text="当前没有识别中或异常卷">
          <el-table-column prop="subjectName" label="科目" width="100" />
          <el-table-column prop="studentName" label="学生" width="110" />
          <el-table-column prop="studentNumber" label="学号" width="130" />
          <el-table-column prop="remark" label="识别结果" min-width="220" show-overflow-tooltip />
          <el-table-column label="状态" width="110" align="center">
            <template #default="{ row }">
              <el-tag :type="getAnswerSheetStatusType(row.status)">{{ getAnswerSheetStatusName(row.status) }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card class="section-card" shadow="never">
        <template #header>
          <div class="section-card__header">
            <span>客观题复核池</span>
            <span class="section-card__meta">人工只处理待确认项，共 {{ objectivePoolQuestionCount }} 题</span>
          </div>
        </template>

        <el-table :data="objectivePoolList" stripe empty-text="当前没有待复核客观题">
          <el-table-column prop="subjectName" label="科目" width="100" />
          <el-table-column prop="studentName" label="学生" width="110" />
          <el-table-column prop="studentNumber" label="学号" width="130" />
          <el-table-column prop="pendingCount" label="待复核题数" width="110" align="center" />
          <el-table-column prop="sampleQuestions" label="涉及题号" min-width="180" />
          <el-table-column label="操作" width="120" align="center">
            <template #default="{ row }">
              <el-button type="primary" link @click="openObjectiveReview(row.answerSheetId)">进入复核</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card class="section-card" shadow="never">
        <template #header>
          <div class="section-card__header">
            <span>主观题核验池</span>
            <span class="section-card__meta">只显示真正的裁题异常项，共 {{ subjectivePoolQuestionCount }} 题</span>
          </div>
        </template>

        <el-table :data="subjectivePoolList" stripe empty-text="当前没有主观题裁题异常">
          <el-table-column prop="subjectName" label="科目" width="100" />
          <el-table-column prop="studentName" label="学生" width="110" />
          <el-table-column prop="studentNumber" label="学号" width="130" />
          <el-table-column prop="pendingCount" label="异常题数" width="110" align="center" />
          <el-table-column prop="sampleQuestions" label="涉及题号" min-width="180" />
          <el-table-column prop="sampleReason" label="异常原因" min-width="220" show-overflow-tooltip />
          <el-table-column label="操作" width="120" align="center">
            <template #default="{ row }">
              <el-button type="warning" link @click="openSubjectiveReview(row.answerSheetId)">进入核验</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card class="section-card" shadow="never">
        <template #header>
          <div class="section-card__header">
            <span>待仲裁与出分检查</span>
            <div class="section-card__actions">
              <el-tag effect="plain">流程积压 {{ markingBacklogCount }}</el-tag>
              <el-button text @click="() => openMarkingPage()">阅卷任务</el-button>
              <el-button text @click="openPublishCheck">出分检查</el-button>
            </div>
          </div>
        </template>

        <div class="check-panel">
          <div class="check-banner" :class="publishCheck?.canPublish ? 'is-pass' : 'is-blocked'">
            <div>
              <div class="check-banner__title">
                {{ publishCheck?.canPublish ? '当前可直接进入出分检查并发布' : '当前仍有阻塞项，不能直接出分' }}
              </div>
              <div class="check-banner__desc">
                {{ publishCheckDescription }}
              </div>
            </div>
            <el-tag :type="publishCheck?.canPublish ? 'success' : 'danger'" effect="dark">
              {{ publishCheck?.canPublish ? '可发布' : '待处理' }}
            </el-tag>
          </div>

          <div class="check-metrics">
            <div class="mini-metric">
              <span>待仲裁</span>
              <strong>{{ publishCheck?.pendingArbitrationCount || 0 }}</strong>
            </div>
            <div class="mini-metric">
              <span>未完成任务</span>
              <strong>{{ publishCheck?.unfinishedTaskCount || 0 }}</strong>
            </div>
            <div class="mini-metric">
              <span>总分记录</span>
              <strong>{{ publishCheck?.examScoreCount || 0 }}</strong>
            </div>
            <div class="mini-metric">
              <span>统计记录</span>
              <strong>{{ publishCheck?.statisticsCount || 0 }}</strong>
            </div>
          </div>

          <div class="issue-columns">
            <div>
              <div class="issue-title">阻塞项</div>
              <div v-if="publishCheck?.blockingItems?.length" class="issue-list">
                <div v-for="item in publishCheck.blockingItems" :key="item" class="issue-item is-blocking">
                  {{ item }}
                </div>
              </div>
              <el-empty v-else description="没有阻塞项" :image-size="56" />
            </div>
            <div>
              <div class="issue-title">提示项</div>
              <div v-if="publishCheck?.warningItems?.length" class="issue-list">
                <div v-for="item in publishCheck.warningItems" :key="item" class="issue-item is-warning">
                  {{ item }}
                </div>
              </div>
              <el-empty v-else description="没有额外提示" :image-size="56" />
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import {
  getAnswerSheetPage,
  getAnswerSheetQuestionDetails,
  type AnswerSheet,
  type AnswerSheetQuestionDetail,
} from '@/api/answerSheet'
import { getExamDetail, getExamSubjectList, type Exam, type ExamSubject } from '@/api/exam'
import { pageMarkingTasks, type MarkingTaskVO } from '@/api/marking'
import { getScorePublishCheck, type ScorePublishCheck } from '@/api/score'

interface ReviewPoolItem {
  answerSheetId: number
  subjectName?: string
  studentName?: string
  studentNumber?: string
  pendingCount: number
  sampleQuestions: string
  sampleReason?: string
  createTime?: string
}

interface SubjectReviewStats {
  objectivePendingCount: number
  subjectivePendingCount: number
  objectiveAnswerSheetId?: number
  subjectiveAnswerSheetId?: number
}

interface SubjectWorkflowItem {
  examSubjectId: number
  subjectName: string
  fullScore?: number
  answerSheetCount: number
  scanBacklogCount: number
  objectiveReviewCount: number
  subjectiveReviewCount: number
  readyForMarkingCount: number
  markingCount: number
  completedCount: number
  taskCount: number
  notStartedTaskCount: number
  activeTaskCount: number
  completedTaskCount: number
  currentStep: string
  stepType: 'info' | 'primary' | 'warning' | 'success' | 'danger'
  primaryActionText: string
  primaryActionMode: 'scan' | 'objective' | 'subjective' | 'task' | 'publish'
  objectiveAnswerSheetId?: number
  subjectiveAnswerSheetId?: number
}

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const exam = ref<Exam | null>(null)
const subjectList = ref<ExamSubject[]>([])
const allAnswerSheets = ref<AnswerSheet[]>([])
const scanPoolList = ref<AnswerSheet[]>([])
const objectivePoolList = ref<ReviewPoolItem[]>([])
const subjectivePoolList = ref<ReviewPoolItem[]>([])
const subjectReviewStatsMap = ref<Record<number, SubjectReviewStats>>({})
const markingTaskList = ref<MarkingTaskVO[]>([])
const publishCheck = ref<ScorePublishCheck | null>(null)

const answerSheetStats = reactive({
  total: 0,
  pendingRecognition: 0,
  recognized: 0,
  readyForMarking: 0,
  exceptions: 0,
  marking: 0,
  completed: 0,
})

const examId = computed(() => {
  const value = Number(route.params.id)
  return Number.isFinite(value) && value > 0 ? value : 0
})

const scanPoolCount = computed(() => answerSheetStats.pendingRecognition + answerSheetStats.exceptions)
const scanResolvedCount = computed(() => Math.max(answerSheetStats.total - scanPoolCount.value, 0))
const objectivePoolQuestionCount = computed(() => objectivePoolList.value.reduce((sum, item) => sum + item.pendingCount, 0))
const subjectivePoolQuestionCount = computed(() => subjectivePoolList.value.reduce((sum, item) => sum + item.pendingCount, 0))
const reviewBacklogCount = computed(() => objectivePoolQuestionCount.value + subjectivePoolQuestionCount.value)
const readyForScoringCount = computed(() => answerSheetStats.readyForMarking + answerSheetStats.marking + answerSheetStats.completed)
const markingBacklogCount = computed(() =>
  (publishCheck.value?.unfinishedTaskCount || 0) + (publishCheck.value?.pendingArbitrationCount || 0)
)
const doubleMarkingTaskCount = computed(() => markingTaskList.value.filter((item) => item.enableDoubleMarking === 1).length)
const subjectWorkflowList = computed<SubjectWorkflowItem[]>(() =>
  subjectList.value.map((subject) => buildSubjectWorkflowItem(subject))
)

const publishCheckDescription = computed(() => {
  if (!publishCheck.value) {
    return '正在读取出分前检查结果'
  }
  if (publishCheck.value.canPublish) {
    return '识别、复核、阅卷、仲裁已经收口，可以直接进入出分检查并发布。'
  }
  return '请先把阻塞项清零，再进入正式出分。'
})

const nextAction = computed(() => {
  if (scanPoolCount.value > 0) {
    return {
      title: '先处理扫描识别池',
      description: '优先清掉识别中和异常池，正常卷才能稳定进入待阅卷。',
      buttonText: '去扫描识别池',
      action: () => openAnswerSheetPage(),
    }
  }
  if (objectivePoolQuestionCount.value > 0) {
    return {
      title: '处理客观题复核池',
      description: '先把待复核的客观题收口，避免后续成绩汇总前还残留人工确认项。',
      buttonText: '去客观题复核',
      action: () => openFirstObjectivePool(),
    }
  }
  if (subjectivePoolQuestionCount.value > 0) {
    return {
      title: '处理主观题核验池',
      description: '这里只处理真正的裁题异常项，正常主观题不再进人工核验池。',
      buttonText: '去主观题核验',
      action: () => openFirstSubjectivePool(),
    }
  }
  if ((publishCheck.value?.unfinishedTaskCount || 0) > 0 || answerSheetStats.readyForMarking > 0 || answerSheetStats.marking > 0) {
    return {
      title: '推进阅卷任务收口',
      description: '异常项已经清掉后，下一步应该先把待阅卷和阅卷中任务推进到完成，再进入仲裁。',
      buttonText: '去阅卷任务',
      action: () => openMarkingPage(),
    }
  }
  if ((publishCheck.value?.pendingArbitrationCount || 0) > 0) {
    return {
      title: '处理待仲裁池',
      description: '阅卷任务已经基本完成，但双评分差还没有收口，仲裁清零后才能进入正式出分。',
      buttonText: '看出分阻塞项',
      action: () => openPublishCheck(),
    }
  }
  return {
    title: '进入出分检查并发布',
    description: '当前主链已经基本收口，可以直接去出分检查页确认发布。',
    buttonText: '去出分检查',
    action: () => openPublishCheck(),
  }
})

async function loadExamBase() {
  const [detailRes, subjectRes] = await Promise.all([
    getExamDetail(examId.value),
    getExamSubjectList(examId.value),
  ])
  exam.value = detailRes.data
  subjectList.value = subjectRes.data || []
}

async function loadAllAnswerSheets() {
  const pageSize = 100
  let pageNum = 1
  let total = 0
  const result: AnswerSheet[] = []

  do {
    const res = await getAnswerSheetPage({
      pageNum,
      pageSize,
      examId: examId.value,
    })
    const list = res.data.list || []
    total = res.data.total || 0
    result.push(...list)
    pageNum += 1
  } while (result.length < total)

  allAnswerSheets.value = result
  answerSheetStats.total = result.length
  answerSheetStats.pendingRecognition = result.filter((item) => item.status === 0).length
  answerSheetStats.recognized = result.filter((item) => item.status === 1).length
  answerSheetStats.readyForMarking = result.filter((item) => item.status === 2).length
  answerSheetStats.exceptions = result.filter((item) => item.status === 5).length
  answerSheetStats.marking = result.filter((item) => item.status === 3).length
  answerSheetStats.completed = result.filter((item) => item.status === 4).length
  scanPoolList.value = result
    .filter((item) => item.status === 0 || item.status === 5)
    .sort((left, right) => (left.status === 5 ? -1 : 1) - (right.status === 5 ? -1 : 1))
    .slice(0, 8)
}

async function loadMarkingTasks() {
  const res = await pageMarkingTasks({
    pageNum: 1,
    pageSize: 50,
    examId: examId.value,
  })
  markingTaskList.value = res.data.list || []
}

async function loadPublishCheck() {
  const res = await getScorePublishCheck(examId.value)
  publishCheck.value = res.data
}

async function loadReviewPools() {
  const reviewSheets = allAnswerSheets.value.filter((item) => ![0, 5].includes(item.status))
  const detailBundles: Array<{ sheet: AnswerSheet; details: AnswerSheetQuestionDetail[] }> = []

  for (let index = 0; index < reviewSheets.length; index += 8) {
    const chunk = reviewSheets.slice(index, index + 8)
    const chunkResults = await Promise.all(chunk.map(async (sheet) => {
      const res = await getAnswerSheetQuestionDetails(sheet.id)
      return {
        sheet,
        details: res.data || [],
      }
    }))
    detailBundles.push(...chunkResults)
  }

  const subjectStats: Record<number, SubjectReviewStats> = {}
  detailBundles.forEach(({ sheet, details }) => {
    const key = sheet.examSubjectId
    if (!key) {
      return
    }
    if (!subjectStats[key]) {
      subjectStats[key] = {
        objectivePendingCount: 0,
        subjectivePendingCount: 0,
      }
    }

    const objectivePool = buildPoolItem(sheet, details, true)
    if (objectivePool) {
      subjectStats[key].objectivePendingCount += objectivePool.pendingCount
      subjectStats[key].objectiveAnswerSheetId ||= sheet.id
    }

    const subjectivePool = buildPoolItem(sheet, details, false)
    if (subjectivePool) {
      subjectStats[key].subjectivePendingCount += subjectivePool.pendingCount
      subjectStats[key].subjectiveAnswerSheetId ||= sheet.id
    }
  })
  subjectReviewStatsMap.value = subjectStats

  objectivePoolList.value = detailBundles
    .map(({ sheet, details }) => buildPoolItem(sheet, details, true))
    .filter(isReviewPoolItem)
    .sort((left, right) => right.pendingCount - left.pendingCount)
    .slice(0, 12)

  subjectivePoolList.value = detailBundles
    .map(({ sheet, details }) => buildPoolItem(sheet, details, false))
    .filter(isReviewPoolItem)
    .sort((left, right) => right.pendingCount - left.pendingCount)
    .slice(0, 12)
}

function buildPoolItem(
  sheet: AnswerSheet,
  details: AnswerSheetQuestionDetail[],
  objective: boolean
): ReviewPoolItem | null {
  const filtered = details.filter((item) => {
    const isObjective = item.isObjective === 1
    if (objective !== isObjective) {
      return false
    }
    if (!objective && item.anomalyFlag !== true) {
      return false
    }
    return item.status !== 1
  })

  if (filtered.length === 0) {
    return null
  }

  return {
    answerSheetId: sheet.id,
    subjectName: sheet.subjectName,
    studentName: sheet.studentName,
    studentNumber: sheet.studentNumber,
    pendingCount: filtered.length,
    sampleQuestions: filtered.slice(0, 5).map((item) => item.questionNo || String(item.questionId)).join('、'),
    sampleReason: filtered[0]?.anomalyReason,
    createTime: sheet.createTime,
  }
}

function isReviewPoolItem(item: ReviewPoolItem | null): item is ReviewPoolItem {
  return item !== null
}

function buildSubjectWorkflowItem(subject: ExamSubject): SubjectWorkflowItem {
  const sheets = allAnswerSheets.value.filter((item) => item.examSubjectId === subject.id)
  const subjectTasks = markingTaskList.value.filter((item) => item.examSubjectId === subject.id)
  const reviewStats = subjectReviewStatsMap.value[subject.id] || {
    objectivePendingCount: 0,
    subjectivePendingCount: 0,
  }

  const scanBacklogCount = sheets.filter((item) => [0, 5].includes(item.status)).length
  const readyForMarkingCount = sheets.filter((item) => item.status === 2).length
  const markingCount = sheets.filter((item) => item.status === 3).length
  const completedCount = sheets.filter((item) => item.status === 4).length
  const notStartedTaskCount = subjectTasks.filter((item) => item.status === 0).length
  const activeTaskCount = subjectTasks.filter((item) => item.status === 1).length
  const completedTaskCount = subjectTasks.filter((item) => item.status === 2).length

  if (sheets.length === 0) {
    return {
      examSubjectId: subject.id,
      subjectName: subject.subjectName,
      fullScore: subject.fullScore,
      answerSheetCount: 0,
      scanBacklogCount: 0,
      objectiveReviewCount: 0,
      subjectiveReviewCount: 0,
      readyForMarkingCount: 0,
      markingCount: 0,
      completedCount: 0,
      taskCount: 0,
      notStartedTaskCount: 0,
      activeTaskCount: 0,
      completedTaskCount: 0,
      currentStep: '待上传扫描图',
      stepType: 'info',
      primaryActionText: '去上传扫描图',
      primaryActionMode: 'scan',
    }
  }

  let currentStep = '等待出分检查'
  let stepType: SubjectWorkflowItem['stepType'] = 'success'
  let primaryActionText = '查看出分检查'
  let primaryActionMode: SubjectWorkflowItem['primaryActionMode'] = 'publish'

  if (scanBacklogCount > 0) {
    currentStep = '扫描识别待处理'
    stepType = 'danger'
    primaryActionText = '处理扫描池'
    primaryActionMode = 'scan'
  } else if (reviewStats.objectivePendingCount > 0) {
    currentStep = '客观题待复核'
    stepType = 'warning'
    primaryActionText = '进入客观题复核'
    primaryActionMode = 'objective'
  } else if (reviewStats.subjectivePendingCount > 0) {
    currentStep = '主观题待核验'
    stepType = 'warning'
    primaryActionText = '进入主观题核验'
    primaryActionMode = 'subjective'
  } else if (subjectTasks.length === 0 && (readyForMarkingCount > 0 || markingCount > 0 || completedCount > 0)) {
    currentStep = '待生成阅卷任务'
    stepType = 'primary'
    primaryActionText = '查看阅卷任务'
    primaryActionMode = 'task'
  } else if (notStartedTaskCount > 0) {
    currentStep = '阅卷任务待开始'
    stepType = 'primary'
    primaryActionText = '启动阅卷任务'
    primaryActionMode = 'task'
  } else if (activeTaskCount > 0 || readyForMarkingCount > 0 || markingCount > 0) {
    currentStep = '阅卷进行中'
    stepType = 'primary'
    primaryActionText = '查看阅卷任务'
    primaryActionMode = 'task'
  }

  return {
    examSubjectId: subject.id,
    subjectName: subject.subjectName,
    fullScore: subject.fullScore,
    answerSheetCount: sheets.length,
    scanBacklogCount,
    objectiveReviewCount: reviewStats.objectivePendingCount,
    subjectiveReviewCount: reviewStats.subjectivePendingCount,
    readyForMarkingCount,
    markingCount,
    completedCount,
    taskCount: subjectTasks.length,
    notStartedTaskCount,
    activeTaskCount,
    completedTaskCount,
    currentStep,
    stepType,
    primaryActionText,
    primaryActionMode,
    objectiveAnswerSheetId: reviewStats.objectiveAnswerSheetId,
    subjectiveAnswerSheetId: reviewStats.subjectiveAnswerSheetId,
  }
}

async function loadWorkbench() {
  if (!examId.value) {
    ElMessage.error('考试参数无效')
    router.replace('/exam/list')
    return
  }

  loading.value = true
  try {
    await Promise.all([
      loadExamBase(),
      loadAllAnswerSheets(),
      loadMarkingTasks(),
      loadPublishCheck(),
    ])
    await loadReviewPools()
  } catch (error) {
    console.error('加载考试工作台失败', error)
    ElMessage.error('加载考试工作台失败')
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.push('/exam/list')
}

function openAnswerSheetPage(status?: number, examSubjectId?: number) {
  router.push({
    path: '/exam/answer-sheet',
    query: {
      examId: String(examId.value),
      ...(status !== undefined ? { status: String(status) } : {}),
      ...(examSubjectId !== undefined ? { examSubjectId: String(examSubjectId) } : {}),
    },
  })
}

function openObjectiveReview(answerSheetId: number) {
  router.push({
    name: 'AnswerSheetObjectiveReview',
    params: { id: String(answerSheetId) },
    query: { examId: String(examId.value) },
  })
}

function openSubjectiveReview(answerSheetId: number) {
  router.push({
    name: 'AnswerSheetSubjectiveReview',
    params: { id: String(answerSheetId) },
    query: { examId: String(examId.value) },
  })
}

function openFirstObjectivePool() {
  if (objectivePoolList.value.length === 0) {
    openAnswerSheetPage()
    return
  }
  openObjectiveReview(objectivePoolList.value[0].answerSheetId)
}

function openFirstSubjectivePool() {
  if (subjectivePoolList.value.length === 0) {
    openAnswerSheetPage()
    return
  }
  openSubjectiveReview(subjectivePoolList.value[0].answerSheetId)
}

function openMarkingPage(examSubjectId?: number) {
  router.push({
    path: '/marking/task',
    query: {
      examId: String(examId.value),
      ...(examSubjectId !== undefined ? { examSubjectId: String(examSubjectId) } : {}),
    },
  })
}

function openMarkingWorkspace() {
  router.push('/marking/workspace')
}

function openPublishCheck() {
  router.push({
    name: 'ScorePublishCheck',
    params: { id: String(examId.value) },
  })
}

function openScorePage(viewMode: 'score' | 'statistics' = 'score') {
  router.push({
    path: '/score/list',
    query: {
      examId: String(examId.value),
      viewMode,
    },
  })
}

function handleSubjectPrimaryAction(item: SubjectWorkflowItem) {
  switch (item.primaryActionMode) {
    case 'scan':
      openAnswerSheetPage(undefined, item.examSubjectId)
      return
    case 'objective':
      if (item.objectiveAnswerSheetId) {
        openObjectiveReview(item.objectiveAnswerSheetId)
        return
      }
      openAnswerSheetPage(undefined, item.examSubjectId)
      return
    case 'subjective':
      if (item.subjectiveAnswerSheetId) {
        openSubjectiveReview(item.subjectiveAnswerSheetId)
        return
      }
      openAnswerSheetPage(undefined, item.examSubjectId)
      return
    case 'task':
      openMarkingPage(item.examSubjectId)
      return
    case 'publish':
      openPublishCheck()
      return
    default:
      openAnswerSheetPage(undefined, item.examSubjectId)
  }
}

function getExamStatusName(status?: number) {
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

function getExamStatusType(status?: number): 'info' | 'warning' | 'success' | 'primary' {
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

function getAnswerSheetStatusName(status: number) {
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

function getAnswerSheetStatusType(status: number): 'info' | 'primary' | 'warning' | 'success' | 'danger' {
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

watch(() => route.params.id, () => {
  loadWorkbench()
})

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
.next-card,
.overview-card,
.section-card {
  border: 1px solid #e6eaef;
  border-radius: 18px;
  background: #fff;
}

.hero-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
}

.hero-main {
  min-width: 0;
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
  max-width: 780px;
  margin: 10px 0 0;
  color: #5b6472;
  line-height: 1.8;
}

.hero-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
  margin-top: 18px;
  color: #5b6472;
}

.hero-quick-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.subject-strip {
  margin-top: 20px;
  padding-top: 18px;
  border-top: 1px solid #eceff3;
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

.next-card {
  margin-top: 16px;
}

.overview-card {
  margin-top: 16px;
}

.next-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.next-card__label {
  color: #8b93a1;
  font-size: 12px;
}

.next-card__title {
  margin-top: 6px;
  color: #1f2937;
  font-size: 22px;
  font-weight: 700;
}

.next-card__desc {
  margin-top: 12px;
  color: #5b6472;
  line-height: 1.7;
}

.overview-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.overview-label {
  color: #8b93a1;
  font-size: 12px;
}

.overview-title {
  margin-top: 6px;
  color: #1f2937;
  font-size: 20px;
  font-weight: 700;
}

.overview-grid {
  margin-top: 16px;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.overview-item {
  padding: 16px;
  border-radius: 14px;
  border: 1px solid #e6eaef;
  background: #f8fafc;
}

.overview-item__label {
  color: #8b93a1;
  font-size: 12px;
}

.overview-item__value {
  margin-top: 10px;
  color: #1f2937;
  font-size: 30px;
  font-weight: 700;
}

.overview-item__desc {
  margin-top: 8px;
  color: #5b6472;
  line-height: 1.7;
}

.pool-grid {
  margin-top: 16px;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.pool-card {
  padding: 18px;
  border-radius: 18px;
  border: 1px solid #e6eaef;
  background: #fff;
}

.subject-workflow-card {
  margin-top: 16px;
}

.scan-pool {
  background: #fafcff;
}

.objective-pool {
  background: #f8fbff;
}

.subjective-pool {
  background: #fffaf4;
}

.arbitration-pool {
  background: #fff7f5;
}

.pool-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.pool-card__label {
  color: #8b93a1;
  font-size: 12px;
}

.pool-card__value {
  margin-top: 10px;
  color: #1f2937;
  font-size: 32px;
  font-weight: 700;
}

.pool-card__desc {
  margin-top: 12px;
  color: #5b6472;
  min-height: 48px;
  line-height: 1.7;
}

.pool-card__tags {
  margin-top: 14px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.section-grid {
  margin-top: 16px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.section-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.section-card__actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.section-card__meta {
  color: #8b93a1;
  font-size: 12px;
}

.subject-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.subject-cell strong {
  color: #1f2937;
}

.subject-cell span,
.task-state-cell span {
  color: #5b6472;
  font-size: 12px;
}

.task-state-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.check-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.check-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  padding: 16px;
  border-radius: 14px;
}

.check-banner.is-pass {
  background: #effaf3;
  border: 1px solid #bfe3cb;
}

.check-banner.is-blocked {
  background: #fff3f1;
  border: 1px solid #efc4bc;
}

.check-banner__title {
  color: #1f2937;
  font-size: 18px;
  font-weight: 700;
}

.check-banner__desc {
  margin-top: 6px;
  color: #5b6472;
  line-height: 1.7;
}

.check-metrics {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.mini-metric {
  padding: 14px;
  border-radius: 12px;
  background: #f7f9fb;
  border: 1px solid #e6eaef;
}

.mini-metric span {
  display: block;
  color: #8b93a1;
  font-size: 12px;
}

.mini-metric strong {
  display: block;
  margin-top: 8px;
  color: #1f2937;
  font-size: 22px;
}

.issue-columns {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.issue-title {
  margin-bottom: 10px;
  color: #1f2937;
  font-weight: 700;
}

.issue-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.issue-item {
  padding: 12px 14px;
  border-radius: 10px;
  line-height: 1.6;
}

.issue-item.is-blocking {
  background: #fff5f2;
  color: #9a3412;
}

.issue-item.is-warning {
  background: #fff9ed;
  color: #9a6700;
}

@media (max-width: 1280px) {
  .overview-grid,
  .pool-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .section-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .hero-top,
  .overview-header,
  .next-card__header,
  .check-banner {
    flex-direction: column;
    align-items: flex-start;
  }

  .check-metrics,
  .issue-columns {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .exam-workbench {
    padding: 14px;
  }

  .pool-grid {
    grid-template-columns: 1fr;
  }

  .overview-grid {
    grid-template-columns: 1fr;
  }
}
</style>
