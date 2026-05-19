<template>
  <div class="exam-workbench-page" v-loading="loading">
    <div class="hero-panel">
      <div class="hero-main">
        <div class="hero-label">考试工作台</div>
        <div class="hero-title-row">
          <div>
            <h1 class="hero-title">{{ examDetail?.name || '考试流程编排' }}</h1>
            <p class="hero-subtitle">{{ heroDescription }}</p>
          </div>
          <el-tag :type="getExamStatusTagType(examDetail?.status)" effect="dark" round size="large">
            {{ examDetail?.statusName || getExamStatusText(examDetail?.status) }}
          </el-tag>
        </div>

        <div class="hero-meta">
          <div class="hero-meta-item">
            <span class="meta-label">学年学期</span>
            <strong>{{ examDetail?.academicYear || '-' }} / {{ getSemesterText(examDetail?.semester) }}</strong>
          </div>
          <div class="hero-meta-item">
            <span class="meta-label">年级</span>
            <strong>{{ examDetail?.gradeName || '-' }}</strong>
          </div>
          <div class="hero-meta-item">
            <span class="meta-label">参考班级</span>
            <strong>{{ overview.classCount }}</strong>
          </div>
          <div class="hero-meta-item">
            <span class="meta-label">考试科目</span>
            <strong>{{ overview.subjectCount }}</strong>
          </div>
        </div>

        <div class="hero-actions">
          <el-button type="primary" @click="handleNextStep">
            {{ nextStep ? `推进到 ${nextStep.index}. ${nextStep.title}` : '查看成绩结果' }}
          </el-button>
          <el-button @click="handleRefresh">刷新工作台</el-button>
          <el-button @click="goExamList">返回考试列表</el-button>
        </div>
      </div>

      <div class="hero-side">
        <div class="hero-progress-card">
          <div class="hero-progress-head">
            <span>流程完成度</span>
            <strong>{{ completedStepCount }}/{{ flowSteps.length }}</strong>
          </div>
          <el-progress
            :percentage="progressPercentage"
            :stroke-width="12"
            :show-text="false"
            color="#f59e0b"
          />
          <div class="hero-progress-note">
            <span>当前阶段</span>
            <strong>{{ nextStep ? nextStep.phase : '已发布' }}</strong>
          </div>
        </div>

        <div class="hero-summary-grid">
          <div v-for="item in summaryCards" :key="item.label" class="summary-tile">
            <span class="summary-label">{{ item.label }}</span>
            <strong class="summary-value" :class="`is-${item.tone}`">{{ item.value }}</strong>
            <span class="summary-desc">{{ item.desc }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="overview-grid">
      <div v-for="card in overviewCards" :key="card.label" class="overview-card">
        <div class="overview-card__head">
          <span>{{ card.label }}</span>
          <span class="overview-card__badge" :class="`is-${card.tone}`">{{ card.badge }}</span>
        </div>
        <div class="overview-card__value">{{ card.value }}</div>
        <div class="overview-card__desc">{{ card.desc }}</div>
      </div>
    </div>

    <div class="workspace-layout">
      <div class="flow-panel">
        <div class="section-head">
          <div>
            <div class="section-title">创建考试主流程</div>
            <div class="section-desc">严格按你文档里的 9 步主线推进，每一步都直达现有功能页。</div>
          </div>
          <el-tag round>{{ completedStepCount }} / {{ flowSteps.length }} 已完成</el-tag>
        </div>

        <div class="flow-track">
          <div
            v-for="step in flowSteps"
            :key="step.key"
            class="flow-step"
            :class="[`is-${step.status}`, { 'is-highlight': nextStep?.key === step.key }]"
          >
            <div class="flow-step__top">
              <div class="flow-step__index">{{ step.index }}</div>
              <div class="flow-step__title-wrap">
                <div class="flow-step__phase">{{ step.phase }}</div>
                <div class="flow-step__title">{{ step.title }}</div>
              </div>
              <el-tag :type="getStepTagType(step.status)" round>{{ getStepStatusText(step.status) }}</el-tag>
            </div>

            <div class="flow-step__desc">{{ step.description }}</div>

            <div class="flow-step__metrics">
              <div v-for="metric in step.metrics" :key="`${step.key}-${metric.label}`" class="flow-metric">
                <span class="flow-metric__label">{{ metric.label }}</span>
                <strong class="flow-metric__value" :class="`is-${metric.tone || 'default'}`">
                  {{ metric.value }}
                </strong>
              </div>
            </div>

            <div class="flow-step__hint">{{ step.hint }}</div>

            <div class="flow-step__actions">
              <el-button
                v-for="action in step.actions"
                :key="`${step.key}-${action.label}`"
                :type="action.primary ? 'primary' : 'default'"
                text
                bg
                @click="navigate(action.to)"
              >
                {{ action.label }}
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div class="side-panel">
        <div class="side-card next-card">
          <div class="side-card__title">下一步建议</div>
          <template v-if="nextStep">
            <div class="next-step-index">STEP {{ nextStep.index }}</div>
            <div class="next-step-title">{{ nextStep.title }}</div>
            <div class="next-step-desc">{{ nextStep.hint }}</div>
            <el-button type="primary" class="side-block-button" @click="handleNextStep">
              立即处理
            </el-button>
          </template>
          <template v-else>
            <div class="next-step-title">流程已闭环</div>
            <div class="next-step-desc">考试已经走完整条链路，可以直接查看成绩、统计和发布结果。</div>
            <el-button type="primary" class="side-block-button" @click="goScorePublishCheck">
              查看发布结果
            </el-button>
          </template>
        </div>

        <div class="side-card">
          <div class="side-card__title">当前阻塞</div>
          <el-empty v-if="issueItems.length === 0" description="没有阻塞项" :image-size="70" />
          <div v-else class="issue-list">
            <div v-for="item in issueItems" :key="item" class="issue-item">
              <span class="issue-dot"></span>
              <span>{{ item }}</span>
            </div>
          </div>
        </div>

        <div class="side-card">
          <div class="side-card__title-row">
            <div>
              <div class="side-card__title">考试科目与答题卡</div>
              <div class="side-card__desc">为每个科目设置答题卡模板，支持上传图片、定位和区域配置</div>
            </div>
            <el-button type="primary" size="small" text bg @click="handleAddSubject">添加科目</el-button>
          </div>
          <el-empty v-if="subjectList.length === 0" description="还没有配置科目" :image-size="70" />
          <div v-else class="subject-list">
            <div v-for="subject in subjectList" :key="subject.id" class="subject-item-enhanced">
              <div class="subject-info">
                <div class="subject-name">{{ subject.subjectName }}</div>
                <div class="subject-meta">满分 {{ subject.fullScore }} / 题目 {{ subject.questionCount || 0 }}</div>
              </div>
              <div class="subject-actions">
                <el-tag
                  :type="getSubjectTemplateStatus(subject) === 'published' ? 'success' : getSubjectTemplateStatus(subject) === 'draft' ? 'warning' : 'info'"
                  size="small"
                >
                  {{ getSubjectTemplateStatusText(subject) }}
                </el-tag>
                <el-button
                  type="primary"
                  size="small"
                  text
                  bg
                  @click="goSubjectAnswerSheet(subject)"
                >
                  {{ getSubjectTemplateStatus(subject) === 'none' ? '设置答题卡' : '编辑答题卡' }}
                </el-button>
                <el-button type="info" size="small" text @click="handleEditSubject(subject)">编辑</el-button>
                <el-button type="danger" size="small" text @click="handleDeleteSubject(subject)">删除</el-button>
              </div>
            </div>
          </div>
        </div>

        <div class="side-card">
          <div class="side-card__title">独立模块</div>
          <div class="module-card">
            <div class="module-title">答题卡设计</div>
            <div class="module-desc">设计模板、下载 PDF、打印使用，不和单场考试强绑定。</div>
            <el-button class="side-block-button" @click="goAnswerSheetDesign">进入模板设计</el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="detail-grid">
      <div class="detail-card">
        <div class="detail-card__head">
          <div>
            <div class="section-title">最近答题卡</div>
            <div class="section-desc">扫描上传、识别异常、待阅卷样本在这里快速查看。</div>
          </div>
          <el-button text bg @click="goAnswerSheetList">查看全部</el-button>
        </div>
        <el-empty v-if="recentSheets.length === 0" description="还没有上传答题卡" :image-size="80" />
        <div v-else class="record-list">
          <div v-for="sheet in recentSheets" :key="sheet.id" class="record-item">
            <div>
              <div class="record-title">{{ sheet.studentName || sheet.studentNumber || '未识别学生' }}</div>
              <div class="record-subtitle">
                {{ sheet.subjectName || '-' }} / {{ sheet.className || '未匹配班级' }} / {{ sheet.createTime || '-' }}
              </div>
            </div>
            <el-tag :type="getAnswerSheetStatusTagType(sheet.status)" round>
              {{ getAnswerSheetStatusText(sheet.status) }}
            </el-tag>
          </div>
        </div>
      </div>

      <div class="detail-card">
        <div class="detail-card__head">
          <div>
            <div class="section-title">阅卷任务</div>
            <div class="section-desc">阅卷码生成、任务推进和完成情况。</div>
          </div>
          <el-button text bg @click="goMarkingTaskList">进入任务页</el-button>
        </div>
        <el-empty v-if="recentTasks.length === 0" description="还没有生成阅卷任务" :image-size="80" />
        <div v-else class="task-list">
          <div v-for="task in recentTasks.slice(0, 6)" :key="task.id" class="task-item">
            <div class="task-item__top">
              <div>
                <div class="record-title">{{ task.subjectName }} 第{{ task.questionNo }}题</div>
                <div class="record-subtitle">{{ task.name }}</div>
              </div>
              <el-tag :type="getMarkingTaskStatusTagType(task.status)" round>
                {{ getMarkingTaskStatusText(task.status) }}
              </el-tag>
            </div>
            <div class="task-progress-row">
              <el-progress
                :percentage="getTaskProgress(task)"
                :stroke-width="10"
                :show-text="false"
                color="#0f766e"
              />
              <span>{{ task.completedCount }} / {{ task.totalCount }}</span>
            </div>
            <div class="task-meta-row">
              <span>{{ task.enableDoubleMarking === 1 ? '双评' : '单评' }}</span>
              <span>{{ task.accessCode ? `阅卷码 ${task.accessCode}` : '未生成阅卷码' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="subjectDialogVisible" :title="subjectDialogTitle" width="500px" destroy-on-close>
      <el-form ref="subjectFormRef" :model="subjectForm" :rules="subjectFormRules" label-width="100px">
        <el-form-item label="科目名称" prop="subjectName">
          <el-input v-model="subjectForm.subjectName" placeholder="请输入科目名称" />
        </el-form-item>
        <el-form-item label="科目编码">
          <el-input v-model="subjectForm.subjectCode" placeholder="请输入科目编码" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="满分" prop="fullScore">
              <el-input-number v-model="subjectForm.fullScore" :min="0" :max="300" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="及格分">
              <el-input-number v-model="subjectForm.passScore" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="优秀分">
              <el-input-number v-model="subjectForm.excellentScore" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="时长(分钟)">
              <el-input-number v-model="subjectForm.duration" :min="0" :max="300" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="排序">
          <el-input-number v-model="subjectForm.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="subjectDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="subjectSubmitting" @click="handleSubjectSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { onBeforeRouteUpdate, useRoute, useRouter, type RouteLocationRaw } from 'vue-router'
import {
  getExamDetail,
  getExamSubjectList,
  createExamSubject,
  updateExamSubject,
  deleteExamSubject,
  type Exam,
  type ExamPublishCheck,
  type ExamSubject,
} from '@/api/exam'
import { getAnswerSheetPage, type AnswerSheet } from '@/api/answerSheet'
import { getTemplatePage, type AnswerSheetTemplate } from '@/api/answerSheetTemplate'
import { pageMarkingTasks, type MarkingTaskVO } from '@/api/marking'
import type { ScorePublishCheck } from '@/api/score'
import { request } from '@/utils/request'

type StepStatus = 'done' | 'active' | 'waiting'

interface StepMetric {
  label: string
  value: number | string
  tone?: 'default' | 'success' | 'warning' | 'danger'
}

interface StepAction {
  label: string
  to: RouteLocationRaw
  primary?: boolean
}

interface FlowStep {
  key: string
  index: number
  phase: string
  title: string
  description: string
  status: StepStatus
  metrics: StepMetric[]
  hint: string
  actions: StepAction[]
}

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const examDetail = ref<Exam | null>(null)
const examPublishCheck = ref<ExamPublishCheck | null>(null)
const scorePublishCheck = ref<ScorePublishCheck | null>(null)
const subjectList = ref<ExamSubject[]>([])
const templateList = ref<AnswerSheetTemplate[]>([])
const recentSheets = ref<AnswerSheet[]>([])
const recentTasks = ref<MarkingTaskVO[]>([])

const examId = computed(() => {
  const id = route.params.id
  if (typeof id === 'string' && id) {
    return id
  }
  if (typeof id === 'number') {
    return String(id)
  }
  if (Array.isArray(id) && id.length > 0) {
    return String(id[0])
  }
  const queryExamId = route.query.examId
  if (typeof queryExamId === 'string' && queryExamId) {
    return queryExamId
  }
  if (Array.isArray(queryExamId) && queryExamId.length > 0) {
    return String(queryExamId[0])
  }
  return ''
})

const overview = computed(() => {
  const classCount = examPublishCheck.value?.classCount ?? examDetail.value?.classes?.length ?? 0
  const subjectCount = subjectList.value.length || examPublishCheck.value?.subjectCount || 0
  const uploadedTemplateCount = templateList.value.length
  const publishedTemplateCount = templateList.value.filter((item) => item.status === 1).length
  const answerSheetCount = scorePublishCheck.value?.answerSheetCount ?? 0
  const completedAnswerSheetCount = scorePublishCheck.value?.completedAnswerSheetCount ?? 0
  const pendingRecognitionCount = scorePublishCheck.value?.pendingRecognitionCount ?? 0
  const recognitionExceptionCount = scorePublishCheck.value?.recognitionExceptionCount ?? 0
  const pendingMarkingAnswerSheetCount = scorePublishCheck.value?.pendingMarkingAnswerSheetCount ?? 0
  const markingTaskCount = scorePublishCheck.value?.markingTaskCount ?? recentTasks.value.length
  const unfinishedTaskCount = scorePublishCheck.value?.unfinishedTaskCount ?? recentTasks.value.filter((item) => item.status !== 2).length
  const pendingArbitrationCount = scorePublishCheck.value?.pendingArbitrationCount ?? 0
  const examScoreCount = scorePublishCheck.value?.examScoreCount ?? 0
  const subjectScoreCount = scorePublishCheck.value?.subjectScoreCount ?? 0
  const statisticsCount = scorePublishCheck.value?.statisticsCount ?? 0
  const requiredAccessCodeCount = recentTasks.value.reduce(
    (count, item) => count + (item.enableDoubleMarking === 1 ? 2 : 1),
    0
  )
  const accessCodeCount = recentTasks.value.reduce((count, item) => {
    let current = count
    if (item.accessCode) {
      current += 1
    }
    if (item.secondAccessCode) {
      current += 1
    }
    return current
  }, 0)

  return {
    classCount,
    subjectCount,
    uploadedTemplateCount,
    publishedTemplateCount,
    answerSheetCount,
    completedAnswerSheetCount,
    pendingRecognitionCount,
    recognitionExceptionCount,
    pendingMarkingAnswerSheetCount,
    markingTaskCount,
    unfinishedTaskCount,
    pendingArbitrationCount,
    examScoreCount,
    subjectScoreCount,
    statisticsCount,
    requiredAccessCodeCount,
    accessCodeCount,
  }
})

const summaryCards = computed(() => [
  {
    label: '答题卡',
    value: overview.value.answerSheetCount,
    desc: overview.value.answerSheetCount > 0 ? '已进入识别链路' : '等待批量上传',
    tone: overview.value.answerSheetCount > 0 ? 'success' : 'default',
  },
  {
    label: '阅卷任务',
    value: overview.value.markingTaskCount,
    desc: overview.value.markingTaskCount > 0
      ? '任务已生成'
      : overview.value.pendingMarkingAnswerSheetCount > 0
        ? '还有答卷未完成评分'
        : overview.value.completedAnswerSheetCount > 0
          ? '自动评分流'
          : '等待裁题完成',
    tone: overview.value.markingTaskCount > 0 || overview.value.completedAnswerSheetCount > 0 ? 'success' : 'default',
  },
  {
    label: '待仲裁',
    value: overview.value.pendingArbitrationCount,
    desc: overview.value.pendingArbitrationCount > 0 ? '需要收口仲裁' : '当前无积压',
    tone: overview.value.pendingArbitrationCount > 0 ? 'warning' : 'success',
  },
  {
    label: '发布状态',
    value: examDetail.value?.status === 5 ? '已发布' : scorePublishCheck.value?.canPublish ? '可发布' : '未就绪',
    desc: examDetail.value?.status === 5 ? '家长端可查询' : '由出分检查决定',
    tone: examDetail.value?.status === 5 ? 'success' : scorePublishCheck.value?.canPublish ? 'warning' : 'default',
  },
])

const overviewCards = computed(() => [
  {
    label: '模板与区域',
    value: `${overview.value.uploadedTemplateCount} / ${overview.value.publishedTemplateCount}`,
    badge: overview.value.publishedTemplateCount >= overview.value.subjectCount && overview.value.subjectCount > 0 ? '已就绪' : '待配置',
    desc: '已上传模板数 / 已发布模板数。模板发布通过后，才算区域真正配置完成。',
    tone: overview.value.publishedTemplateCount >= overview.value.subjectCount && overview.value.subjectCount > 0 ? 'success' : 'warning',
  },
  {
    label: '识别异常',
    value: `${overview.value.recognitionExceptionCount}`,
    badge: overview.value.recognitionExceptionCount > 0 ? '需处理' : '正常',
    desc: '异常池未处理样本，优先清掉避免影响后续阅卷。',
    tone: overview.value.recognitionExceptionCount > 0 ? 'danger' : 'success',
  },
  {
    label: '待处理答卷',
    value: `${overview.value.pendingMarkingAnswerSheetCount}`,
    badge: overview.value.pendingMarkingAnswerSheetCount > 0 ? '需处理' : '已清空',
    desc: overview.value.markingTaskCount > 0
      ? '人工阅卷任务下仍未完成的答题卡数量。'
      : '没有人工任务时，这里通常是客观题或 AI 异常还未收口。',
    tone: overview.value.pendingMarkingAnswerSheetCount > 0 ? 'warning' : 'default',
  },
  {
    label: '成绩汇总',
    value: `${overview.value.examScoreCount} / ${overview.value.statisticsCount}`,
    badge: overview.value.examScoreCount > 0 ? '已生成' : '待汇总',
    desc: '总分记录 / 统计记录，出分前必须补齐。',
    tone: overview.value.examScoreCount > 0 ? 'success' : 'warning',
  },
])

const flowSteps = computed<FlowStep[]>(() => {
  const basicReady = overview.value.classCount > 0 && overview.value.subjectCount > 0
  const requiredTemplateCount = overview.value.subjectCount
  const templateUploaded = requiredTemplateCount > 0 && overview.value.uploadedTemplateCount >= requiredTemplateCount
  const regionConfigured = requiredTemplateCount > 0 && overview.value.publishedTemplateCount >= requiredTemplateCount
  const answerSheetsReady = overview.value.answerSheetCount > 0
  const hasMarkingTasks = overview.value.markingTaskCount > 0
  const allAnswerSheetsCompleted =
    answerSheetsReady &&
    overview.value.completedAnswerSheetCount >= overview.value.answerSheetCount
  const recognitionSettled =
    answerSheetsReady &&
    overview.value.pendingRecognitionCount === 0 &&
    overview.value.recognitionExceptionCount === 0
  const tasklessCompleted =
    !hasMarkingTasks &&
    allAnswerSheetsCompleted &&
    recognitionSettled
  const cropReady = hasMarkingTasks || tasklessCompleted
  const accessReady =
    hasMarkingTasks &&
    overview.value.requiredAccessCodeCount > 0 &&
    overview.value.accessCodeCount >= overview.value.requiredAccessCodeCount
  const markingFinished = hasMarkingTasks && overview.value.unfinishedTaskCount === 0
  const scoreGateReady = tasklessCompleted || markingFinished
  const aggregateReady =
    overview.value.examScoreCount > 0 &&
    overview.value.subjectScoreCount > 0 &&
    overview.value.statisticsCount > 0
  const released = examDetail.value?.status === 5

  return [
    {
      key: 'create-exam',
      index: 1,
      phase: '准备',
      title: '创建考试并补齐科目',
      description: '先填写考试基本信息和参考班级，保存后继续配置考试科目；班级和科目都齐了，这一步才算完成。',
      status: basicReady ? 'done' : 'active',
      metrics: [
        { label: '班级', value: overview.value.classCount, tone: basicReady ? 'success' : 'warning' },
        { label: '科目', value: overview.value.subjectCount, tone: basicReady ? 'success' : 'warning' },
        { label: '状态', value: examDetail.value?.statusName || getExamStatusText(examDetail.value?.status) },
      ],
      hint: basicReady ? '考试基础信息和考试科目都已经齐备，可以继续准备答题卡模板。' : '保存考试弹窗不代表这一步结束，还要回到考试列表补齐科目。',
      actions: [
        { label: '考试管理', to: { name: 'ExamList' }, primary: !basicReady },
        { label: '出分检查', to: { name: 'ScorePublishCheck', params: { id: examId.value } } },
      ],
    },
    {
      key: 'upload-template',
      index: 2,
      phase: '准备',
      title: '上传答题卡模板',
      description: '上传空白答题卡模板，保留四角定位标记，为后续识别和矫正提供基准。',
      status: templateUploaded ? 'done' : basicReady ? 'active' : 'waiting',
      metrics: [
        { label: '应上传', value: requiredTemplateCount, tone: requiredTemplateCount > 0 ? 'default' : 'warning' },
        { label: '已上传', value: overview.value.uploadedTemplateCount, tone: templateUploaded ? 'success' : 'warning' },
        { label: '定位', value: overview.value.uploadedTemplateCount > 0 ? '已录入模板' : '待上传' },
      ],
      hint: templateUploaded
        ? '空白模板已经补齐，下一步进入区域配置和模板发布。'
        : '先按科目把模板传全，别跳过模板层直接进扫描。',
      actions: [
        {
          label: '去模板设计',
          to: { path: '/answer-sheet-design/list', query: { examId: String(examId.value), action: 'create' } },
          primary: !templateUploaded,
        },
      ],
    },
    {
      key: 'configure-region',
      index: 3,
      phase: '准备',
      title: '设置答题卡区域',
      description: '完成信息区、条码区、题目区配置，客观题选项和主观题分值都在这一步收口。',
      status: regionConfigured ? 'done' : templateUploaded ? 'active' : 'waiting',
      metrics: [
        { label: '已发布模板', value: overview.value.publishedTemplateCount, tone: regionConfigured ? 'success' : 'warning' },
        { label: '信息/条码/题目区', value: regionConfigured ? '已配置' : '待发布确认', tone: regionConfigured ? 'success' : 'warning' },
      ],
      hint: regionConfigured
        ? '模板发布已通过校验，说明区域坐标、题号范围和裁题模式已经可用。'
        : '模板上传完不等于区域可用，必须完成标注并发布模板。',
      actions: [
        {
          label: '编辑模板区域',
          to: { path: '/answer-sheet-design/list', query: { examId: String(examId.value) } },
          primary: !regionConfigured,
        },
      ],
    },
    {
      key: 'upload-answer-sheet',
      index: 4,
      phase: '采集',
      title: '批量上传答题卡',
      description: '系统会自动执行四角矫正、条码识别、客观题识别和初步判分，把异常样本送入异常池。',
      status: recognitionSettled ? 'done' : regionConfigured ? 'active' : 'waiting',
      metrics: [
        { label: '已上传', value: overview.value.answerSheetCount, tone: answerSheetsReady ? 'success' : 'default' },
        { label: '待识别', value: overview.value.pendingRecognitionCount, tone: overview.value.pendingRecognitionCount > 0 ? 'warning' : 'success' },
        { label: '异常池', value: overview.value.recognitionExceptionCount, tone: overview.value.recognitionExceptionCount > 0 ? 'danger' : 'success' },
      ],
      hint: !answerSheetsReady
        ? '先导入扫描图片，识别链路没有样本就无从推进。'
        : recognitionSettled
          ? '识别链路已经收口，可以进入裁题和阅卷准备。'
          : '优先处理异常池和识别残留，别把脏数据带进后面环节。',
      actions: [
        { label: '答题卡列表', to: { name: 'AnswerSheetList', query: { examId: String(examId.value) } }, primary: !answerSheetsReady },
        { label: '异常池', to: { name: 'AnswerSheetList', query: { examId: String(examId.value), status: '5' } } },
      ],
    },
    {
      key: 'crop-question',
      index: 5,
      phase: '采集',
      title: '裁题',
      description: '按题目区域裁切生成题目图片，主观题样本进入可阅卷状态，异常样本进入人工核验。',
      status: cropReady ? 'done' : recognitionSettled ? 'active' : 'waiting',
      metrics: [
        { label: '已生成任务', value: overview.value.markingTaskCount, tone: hasMarkingTasks ? 'success' : 'default' },
        {
          label: '已完成答卷',
          value: `${overview.value.completedAnswerSheetCount} / ${overview.value.answerSheetCount}`,
          tone: allAnswerSheetsCompleted ? 'success' : overview.value.completedAnswerSheetCount > 0 ? 'warning' : 'default',
        },
        {
          label: '待处理答卷',
          value: overview.value.pendingMarkingAnswerSheetCount,
          tone: overview.value.pendingMarkingAnswerSheetCount > 0 ? 'warning' : 'success',
        },
      ],
      hint: hasMarkingTasks
        ? '已经能进入阅卷任务，说明题图链路已经跑通。'
        : tasklessCompleted
          ? '本场没有人工阅卷任务，AI 和客观题评分已经收口，可以进入出分检查。'
          : overview.value.pendingMarkingAnswerSheetCount > 0
            ? '还有答题卡未完成评分；优先做客观题复核，或到 AI 批改页保存异常题结果。'
            : '识别收口后还要把题图真正裁出来，再进入阅卷任务分配。',
      actions: [
        { label: '去答题卡列表', to: { name: 'AnswerSheetList', query: { examId: String(examId.value) } }, primary: !cropReady },
        { label: 'AI异常处理', to: { name: 'SystemAiMarking', query: { examName: examDetail.value?.name || undefined } } },
      ],
    },
    {
      key: 'generate-access-code',
      index: 6,
      phase: '阅卷',
      title: '生成阅卷码',
      description: '分配任务时生成 8 位数字阅卷码，老师可免登录进入独立阅卷页。',
      status: tasklessCompleted
        ? 'done'
        : accessReady
          ? 'done'
          : hasMarkingTasks
            ? 'active'
            : 'waiting',
      metrics: [
        { label: '任务数', value: overview.value.markingTaskCount, tone: hasMarkingTasks ? 'success' : 'default' },
        { label: '应有码数', value: overview.value.requiredAccessCodeCount, tone: overview.value.requiredAccessCodeCount > 0 ? 'default' : 'warning' },
        { label: '已有码数', value: overview.value.accessCodeCount, tone: accessReady ? 'success' : 'warning' },
      ],
      hint: tasklessCompleted
        ? '本场没有人工阅卷任务，不需要生成阅卷码。'
        : !hasMarkingTasks
          ? '当前没有人工阅卷任务；先把待处理答卷收口，不要卡在阅卷码步骤。'
        : accessReady
          ? '阅卷码已经补齐，老师可以按角色直接进入 /marking。'
          : '去任务页启动任务并补齐阅卷码，双评任务必须同时具备一评码和二评码。',
      actions: [
        { label: '阅卷任务', to: { name: 'MarkingTask', query: { examId: String(examId.value) } }, primary: !accessReady },
        { label: '老师阅卷入口', to: { path: '/marking' } },
      ],
    },
    {
      key: 'teacher-marking',
      index: 7,
      phase: '阅卷',
      title: '老师阅卷',
      description: '老师通过阅卷码免登录评分，支持问题卷标记、双评分差阈值仲裁和任务进度追踪。',
      status: tasklessCompleted ? 'done' : markingFinished ? 'done' : hasMarkingTasks ? 'active' : 'waiting',
      metrics: [
        { label: '未完成任务', value: overview.value.unfinishedTaskCount, tone: overview.value.unfinishedTaskCount > 0 ? 'warning' : 'success' },
        { label: '已完成答题卡', value: overview.value.completedAnswerSheetCount, tone: overview.value.completedAnswerSheetCount > 0 ? 'success' : 'default' },
        { label: '待仲裁', value: overview.value.pendingArbitrationCount, tone: overview.value.pendingArbitrationCount > 0 ? 'danger' : 'success' },
      ],
      hint: tasklessCompleted
        ? '本场由客观题和 AI 批改自动完成，没有老师阅卷步骤。'
        : markingFinished
          ? '阅卷已经完成，进入汇总和出分阶段。'
          : hasMarkingTasks
          ? '盯住待仲裁和未完成任务，别让老师评分长期悬空。'
          : '当前没有老师阅卷任务；如果仍有待处理答卷，请先处理客观题或 AI 异常。',
      actions: [
        { label: '去阅卷任务', to: { name: 'MarkingTask', query: { examId: String(examId.value) } }, primary: hasMarkingTasks },
        { label: 'AI批改', to: { name: 'SystemAiMarking', query: { examName: examDetail.value?.name || undefined } }, primary: !hasMarkingTasks },
        { label: '出分检查', to: { name: 'ScorePublishCheck', params: { id: examId.value } } },
      ],
    },
    {
      key: 'publish-check',
      index: 8,
      phase: '出分',
      title: '出分检查',
      description: '统一检查识别异常、阅卷任务、仲裁和汇总缺口；总分、排名、统计如缺失，会在正式发布时自动补齐。',
      status: released || scorePublishCheck.value?.canPublish ? 'done' : scoreGateReady ? 'active' : 'waiting',
      metrics: [
        { label: '阻塞项', value: scorePublishCheck.value?.blockingItems?.length || 0, tone: (scorePublishCheck.value?.blockingItems?.length || 0) > 0 ? 'danger' : 'success' },
        { label: '提示项', value: scorePublishCheck.value?.warningItems?.length || 0, tone: (scorePublishCheck.value?.warningItems?.length || 0) > 0 ? 'warning' : 'default' },
        { label: '总分/统计', value: `${overview.value.examScoreCount}/${overview.value.statisticsCount}`, tone: aggregateReady ? 'success' : 'warning' },
      ],
      hint: released || scorePublishCheck.value?.canPublish
        ? '出分检查已经通过，可以进入最终发布。'
        : scoreGateReady
          ? '先看阻塞项；如果只是缺总分、排名、统计，不用手工补，正式发布时会自动生成。'
          : hasMarkingTasks
            ? '阅卷还没结束，暂时不会进入出分检查。'
            : '还有答题卡没有完成评分，先处理客观题复核或 AI 异常。',
      actions: [
        { label: '出分检查', to: { name: 'ScorePublishCheck', params: { id: examId.value } }, primary: true },
        { label: '成绩列表', to: { name: 'ScoreList', query: { examId: String(examId.value), viewMode: 'statistics' } } },
      ],
    },
    {
      key: 'publish-score',
      index: 9,
      phase: '发布',
      title: '发布',
      description: '确认所有阻塞项清零后正式发布，家长端和学生端可以直接查看考试结果。',
      status: released ? 'done' : scorePublishCheck.value?.canPublish ? 'active' : 'waiting',
      metrics: [
        { label: '可发布', value: scorePublishCheck.value?.canPublish ? '是' : '否', tone: scorePublishCheck.value?.canPublish ? 'success' : 'warning' },
        { label: '阻塞项', value: scorePublishCheck.value?.blockingItems?.length || 0, tone: (scorePublishCheck.value?.blockingItems?.length || 0) > 0 ? 'danger' : 'success' },
        { label: '提示项', value: scorePublishCheck.value?.warningItems?.length || 0, tone: (scorePublishCheck.value?.warningItems?.length || 0) > 0 ? 'warning' : 'default' },
      ],
      hint: released
        ? '本场考试已经发布完成，后续只需要回看统计和导出。'
        : scorePublishCheck.value?.canPublish
          ? '可以进入出分检查页完成正式发布。'
          : '先把识别异常、待阅卷、待仲裁和汇总缺口清掉。',
      actions: [
        { label: '出分检查', to: { name: 'ScorePublishCheck', params: { id: examId.value } }, primary: true },
        { label: '成绩列表', to: { name: 'ScoreList', query: { examId: String(examId.value) } } },
      ],
    },
  ]
})

const completedStepCount = computed(() => flowSteps.value.filter((step) => step.status === 'done').length)
const progressPercentage = computed(() => Math.round((completedStepCount.value / flowSteps.value.length) * 100))
const nextStep = computed(() => {
  return flowSteps.value.find((step) => step.status === 'active') || flowSteps.value.find((step) => step.status === 'waiting')
})

const heroDescription = computed(() => {
  if (!examDetail.value) {
    return '按新建考试、模板、上传、裁题、阅卷、汇总、发布这条主线来推进。'
  }

  if (nextStep.value) {
    return `当前建议优先处理「${nextStep.value.index}. ${nextStep.value.title}」，让考试从配置一路推进到正式发布。`
  }

  return '整条链路已经跑通，这场考试可以直接回看成绩、统计和发布结果。'
})

const issueItems = computed(() => {
  const configIssues = examPublishCheck.value?.missingItems || []
  const scoreIssues = scorePublishCheck.value?.blockingItems || []
  return Array.from(new Set([...configIssues, ...scoreIssues]))
})

onMounted(() => {
  loadWorkbench()
})

onBeforeRouteUpdate((to) => {
  if (to.name !== 'ExamWorkbench') {
    return
  }
  const nextId = Array.isArray(to.params.id) ? to.params.id[0] : to.params.id
  if (typeof nextId !== 'string' || !nextId) {
    return
  }
  loadWorkbench(nextId)
})

async function loadWorkbench(targetExamId = examId.value) {
  if (!/^\d+$/.test(targetExamId)) {
    ElMessage.error('考试参数无效')
    router.push({ name: 'ExamList' })
    return
  }

  loading.value = true
  try {
    const [examRes, subjectRes] = await Promise.all([
      getExamDetail(targetExamId),
      getExamSubjectList(targetExamId),
    ])
    examDetail.value = examRes.data
    subjectList.value = subjectRes.data || []

    const [templateRes, examCheckRes, scoreCheckRes, sheetRes, taskRes] = await Promise.allSettled([
      getTemplatePage({ pageNum: 1, pageSize: 200, examId: targetExamId }),
      request.get<ExamPublishCheck>(`/exam/${targetExamId}/publish-check`, { silentError: true }),
      request.get<ScorePublishCheck>(`/score/publish-check/${targetExamId}`, { silentError: true }),
      getAnswerSheetPage({ pageNum: 1, pageSize: 6, examId: targetExamId }),
      pageMarkingTasks({ pageNum: 1, pageSize: 200, examId: targetExamId }),
    ])

    templateList.value = templateRes.status === 'fulfilled' ? templateRes.value.data.list || [] : []
    examPublishCheck.value = examCheckRes.status === 'fulfilled' ? examCheckRes.value.data : null
    scorePublishCheck.value = scoreCheckRes.status === 'fulfilled' ? scoreCheckRes.value.data : null
    recentSheets.value = sheetRes.status === 'fulfilled' ? sheetRes.value.data.list || [] : []
    recentTasks.value = taskRes.status === 'fulfilled' ? taskRes.value.data.list || [] : []
  } catch (error) {
    console.error(error)
    ElMessage.error('加载考试工作台失败')
  } finally {
    loading.value = false
  }
}

function handleNextStep() {
  if (nextStep.value?.actions?.length) {
    navigate(nextStep.value.actions[0].to)
    return
  }
  goScorePublishCheck()
}

function handleRefresh() {
  loadWorkbench()
}

function navigate(to: RouteLocationRaw) {
  router.push(to)
}

function goExamList() {
  router.push({ name: 'ExamList' })
}

function goAnswerSheetDesign() {
  router.push({ path: '/answer-sheet-design/list', query: { examId: String(examId.value), action: 'create' } })
}

function findTemplateForSubject(subject: ExamSubject) {
  if (subject.paperId) {
    const byPaperId = templateList.value.find((template) => String(template.paperId) === String(subject.paperId))
    if (byPaperId) {
      return byPaperId
    }
  }

  return templateList.value.find((template) =>
    String(template.examId || '') === String(examId.value) &&
    template.subjectName === subject.subjectName
  )
}

function getSubjectTemplateStatus(subject: ExamSubject): 'published' | 'draft' | 'none' {
  const template = findTemplateForSubject(subject)

  if (!template) return 'none'
  return template.status === 1 ? 'published' : 'draft'
}

function getSubjectTemplateStatusText(subject: ExamSubject): string {
  const status = getSubjectTemplateStatus(subject)
  switch (status) {
    case 'published':
      return '已发布'
    case 'draft':
      return '草稿'
    default:
      return '未配置'
  }
}

function goSubjectAnswerSheet(subject: ExamSubject) {
  const template = findTemplateForSubject(subject)

  if (template) {
    router.push({
      path: `/answer-sheet-design/edit/${template.id}`,
      query: { examId: String(examId.value) },
    })
  } else {
    router.push({
      path: '/answer-sheet-design/edit',
      query: {
        examId: String(examId.value),
        subjectName: subject.subjectName,
        paperId: subject.paperId ? String(subject.paperId) : undefined,
      },
    })
  }
}

function goAnswerSheetList() {
  router.push({ name: 'AnswerSheetList', query: { examId: String(examId.value) } })
}

function goMarkingTaskList() {
  router.push({ name: 'MarkingTask', query: { examId: String(examId.value) } })
}

function goScorePublishCheck() {
  router.push({ name: 'ScorePublishCheck', params: { id: examId.value } })
}

function getStepTagType(status: StepStatus): 'success' | 'warning' | 'info' {
  if (status === 'done') {
    return 'success'
  }
  if (status === 'active') {
    return 'warning'
  }
  return 'info'
}

function getStepStatusText(status: StepStatus) {
  if (status === 'done') {
    return '已完成'
  }
  if (status === 'active') {
    return '进行中'
  }
  return '待开始'
}

function getSemesterText(semester?: number) {
  if (semester === 1) {
    return '第一学期'
  }
  if (semester === 2) {
    return '第二学期'
  }
  return '-'
}

function getExamStatusText(status?: number) {
  const map: Record<number, string> = {
    0: '草稿',
    1: '待考试',
    2: '考试中',
    3: '阅卷中',
    4: '已完成',
    5: '已发布',
  }
  return status !== undefined ? map[status] || '未知状态' : '未设置'
}

function getExamStatusTagType(status?: number): 'info' | 'warning' | 'primary' | 'success' {
  const map: Record<number, 'info' | 'warning' | 'primary' | 'success'> = {
    0: 'info',
    1: 'warning',
    2: 'primary',
    3: 'primary',
    4: 'success',
    5: 'success',
  }
  return status !== undefined ? map[status] || 'info' : 'info'
}

function getAnswerSheetStatusText(status: number) {
  const map: Record<number, string> = {
    0: '识别中',
    1: '已识别',
    2: '待阅卷',
    3: '阅卷中',
    4: '已完成',
    5: '识别异常',
  }
  return map[status] || '未知状态'
}

function getAnswerSheetStatusTagType(status: number): 'info' | 'success' | 'warning' | 'danger' {
  const map: Record<number, 'info' | 'success' | 'warning' | 'danger'> = {
    0: 'info',
    1: 'success',
    2: 'warning',
    3: 'warning',
    4: 'success',
    5: 'danger',
  }
  return map[status] || 'info'
}

function getMarkingTaskStatusText(status: number) {
  const map: Record<number, string> = {
    0: '未开始',
    1: '进行中',
    2: '已完成',
  }
  return map[status] || '未知状态'
}

function getMarkingTaskStatusTagType(status: number): 'info' | 'warning' | 'success' {
  const map: Record<number, 'info' | 'warning' | 'success'> = {
    0: 'info',
    1: 'warning',
    2: 'success',
  }
  return map[status] || 'info'
}

function getTaskProgress(task: MarkingTaskVO) {
  if (!task.totalCount) {
    return 0
  }
  return Math.min(100, Math.round((task.completedCount / task.totalCount) * 100))
}

const subjectDialogVisible = ref(false)
const subjectDialogTitle = ref('')
const subjectFormRef = ref<FormInstance>()
const subjectSubmitting = ref(false)

const subjectForm = reactive<Partial<ExamSubject>>({
  id: undefined,
  examId: undefined,
  subjectName: '',
  subjectCode: '',
  fullScore: 100,
  passScore: 60,
  excellentScore: 85,
  duration: 120,
  sort: 0,
  status: 1,
})

const subjectFormRules: FormRules = {
  subjectName: [{ required: true, message: '请输入科目名称', trigger: 'blur' }],
  fullScore: [{ required: true, message: '请输入满分', trigger: 'blur' }],
}

function handleAddSubject() {
  subjectDialogTitle.value = '添加科目'
  Object.assign(subjectForm, {
    id: undefined,
    examId: examId.value,
    subjectName: '',
    subjectCode: '',
    fullScore: 100,
    passScore: 60,
    excellentScore: 85,
    duration: 120,
    sort: subjectList.value.length,
    status: 1,
  })
  subjectDialogVisible.value = true
}

function handleEditSubject(subject: ExamSubject) {
  subjectDialogTitle.value = '编辑科目'
  Object.assign(subjectForm, subject)
  subjectDialogVisible.value = true
}

async function handleDeleteSubject(subject: ExamSubject) {
  await ElMessageBox.confirm(`确定要删除科目【${subject.subjectName}】吗？`, '提示', { type: 'warning' })
  await deleteExamSubject(subject.id)
  ElMessage.success('删除成功')
  loadWorkbench()
}

async function handleSubjectSubmit() {
  await subjectFormRef.value?.validate()
  subjectSubmitting.value = true
  try {
    if (subjectForm.id) {
      await updateExamSubject(subjectForm)
      ElMessage.success('更新成功')
    } else {
      await createExamSubject(subjectForm)
      ElMessage.success('添加成功')
    }
    subjectDialogVisible.value = false
    loadWorkbench()
  } finally {
    subjectSubmitting.value = false
  }
}
</script>

<style scoped>
.exam-workbench-page {
  min-height: 100%;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(245, 158, 11, 0.12), transparent 26%),
    radial-gradient(circle at top right, rgba(15, 118, 110, 0.14), transparent 28%),
    linear-gradient(180deg, #f6f8fb 0%, #eef3f6 100%);
}

.hero-panel {
  display: grid;
  grid-template-columns: minmax(0, 1.75fr) minmax(320px, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.hero-main {
  padding: 28px 30px;
  border-radius: 28px;
  color: #f8fafc;
  background:
    linear-gradient(135deg, #0f172a 0%, #134e4a 48%, #115e59 100%);
  box-shadow: 0 24px 70px rgba(15, 23, 42, 0.18);
}

.hero-label {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  color: rgba(248, 250, 252, 0.88);
  font-size: 12px;
  letter-spacing: 0.08em;
}

.hero-title-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-top: 18px;
}

.hero-title {
  margin: 0;
  font-size: 34px;
  line-height: 1.15;
  font-weight: 700;
}

.hero-subtitle {
  margin: 12px 0 0;
  max-width: 720px;
  color: rgba(226, 232, 240, 0.92);
  font-size: 15px;
  line-height: 1.7;
}

.hero-meta {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  margin-top: 24px;
}

.hero-meta-item {
  padding: 16px 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(12px);
}

.meta-label {
  display: block;
  margin-bottom: 8px;
  color: rgba(226, 232, 240, 0.78);
  font-size: 12px;
}

.hero-meta-item strong {
  font-size: 16px;
  font-weight: 600;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 24px;
}

.hero-side {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hero-progress-card,
.summary-tile,
.overview-card,
.flow-step,
.side-card,
.detail-card {
  border: 1px solid rgba(148, 163, 184, 0.14);
  background: rgba(255, 255, 255, 0.88);
  box-shadow: 0 18px 42px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(14px);
}

.hero-progress-card {
  padding: 22px 24px;
  border-radius: 24px;
}

.hero-progress-head,
.hero-progress-note {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.hero-progress-head span,
.hero-progress-note span {
  color: #64748b;
  font-size: 13px;
}

.hero-progress-head strong,
.hero-progress-note strong {
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
}

.hero-progress-note {
  margin-top: 14px;
}

.hero-summary-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.summary-tile {
  padding: 20px;
  border-radius: 22px;
}

.summary-label,
.summary-desc {
  display: block;
  color: #64748b;
  font-size: 12px;
}

.summary-value {
  display: block;
  margin: 10px 0 8px;
  color: #0f172a;
  font-size: 28px;
  font-weight: 700;
  line-height: 1;
}

.summary-value.is-success {
  color: #0f766e;
}

.summary-value.is-warning {
  color: #d97706;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.overview-card {
  padding: 20px 22px;
  border-radius: 24px;
}

.overview-card__head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  color: #475569;
  font-size: 13px;
}

.overview-card__badge {
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
}

.overview-card__badge.is-success {
  color: #0f766e;
  background: rgba(15, 118, 110, 0.12);
}

.overview-card__badge.is-warning {
  color: #b45309;
  background: rgba(245, 158, 11, 0.14);
}

.overview-card__badge.is-danger {
  color: #b91c1c;
  background: rgba(239, 68, 68, 0.12);
}

.overview-card__badge.is-default {
  color: #475569;
  background: rgba(148, 163, 184, 0.16);
}

.overview-card__value {
  margin: 14px 0 10px;
  color: #0f172a;
  font-size: 30px;
  font-weight: 700;
  line-height: 1.1;
}

.overview-card__desc {
  color: #64748b;
  font-size: 13px;
  line-height: 1.7;
}

.workspace-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.7fr) 360px;
  gap: 20px;
  align-items: start;
}

.flow-panel {
  padding: 22px;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.62);
  border: 1px solid rgba(148, 163, 184, 0.16);
}

.section-head,
.detail-card__head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.section-title {
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
}

.section-desc {
  margin-top: 6px;
  color: #64748b;
  font-size: 13px;
  line-height: 1.6;
}

.flow-track {
  display: grid;
  grid-auto-flow: column;
  grid-auto-columns: minmax(310px, 1fr);
  gap: 18px;
  margin-top: 18px;
  overflow-x: auto;
  padding-bottom: 10px;
}

.flow-step {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 292px;
  padding: 20px;
  border-radius: 26px;
}

.flow-step::after {
  content: '';
  position: absolute;
  top: 50%;
  right: -18px;
  width: 18px;
  height: 2px;
  background: linear-gradient(90deg, rgba(15, 118, 110, 0.22), rgba(245, 158, 11, 0.56));
  transform: translateY(-50%);
}

.flow-step:last-child::after {
  display: none;
}

.flow-step.is-done {
  border-color: rgba(15, 118, 110, 0.2);
}

.flow-step.is-active {
  border-color: rgba(245, 158, 11, 0.24);
}

.flow-step.is-highlight {
  transform: translateY(-4px);
  box-shadow: 0 24px 48px rgba(245, 158, 11, 0.14);
}

.flow-step__top {
  display: grid;
  grid-template-columns: 48px minmax(0, 1fr) auto;
  gap: 12px;
  align-items: start;
}

.flow-step__index {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.96), rgba(15, 118, 110, 0.88));
  color: #f8fafc;
  font-size: 18px;
  font-weight: 700;
}

.flow-step__phase {
  color: #0f766e;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.06em;
}

.flow-step__title {
  margin-top: 4px;
  color: #0f172a;
  font-size: 20px;
  font-weight: 700;
  line-height: 1.3;
}

.flow-step__desc,
.flow-step__hint {
  color: #475569;
  font-size: 13px;
  line-height: 1.75;
}

.flow-step__metrics {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.flow-metric {
  padding: 12px 14px;
  border-radius: 18px;
  background: #f8fafc;
}

.flow-metric__label {
  display: block;
  color: #64748b;
  font-size: 12px;
}

.flow-metric__value {
  display: block;
  margin-top: 6px;
  color: #0f172a;
  font-size: 16px;
  font-weight: 700;
}

.flow-metric__value.is-success {
  color: #0f766e;
}

.flow-metric__value.is-warning {
  color: #d97706;
}

.flow-metric__value.is-danger {
  color: #b91c1c;
}

.flow-step__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: auto;
}

.side-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.side-card {
  padding: 20px;
  border-radius: 24px;
}

.side-card__title {
  color: #0f172a;
  font-size: 16px;
  font-weight: 700;
}

.side-card__title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 4px;
}

.next-card {
  background:
    linear-gradient(180deg, rgba(15, 118, 110, 0.08), rgba(255, 255, 255, 0.96));
}

.next-step-index {
  margin-top: 14px;
  color: #0f766e;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.next-step-title {
  margin-top: 8px;
  color: #0f172a;
  font-size: 24px;
  font-weight: 700;
}

.next-step-desc {
  margin-top: 10px;
  color: #475569;
  font-size: 14px;
  line-height: 1.7;
}

.side-block-button {
  width: 100%;
  margin-top: 18px;
}

.issue-list,
.subject-list,
.record-list,
.task-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 16px;
}

.issue-item,
.subject-item,
.record-item,
.task-item {
  padding: 14px 16px;
  border-radius: 18px;
  background: #f8fafc;
}

.issue-item {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  color: #475569;
  font-size: 13px;
  line-height: 1.7;
}

.issue-dot {
  width: 8px;
  height: 8px;
  margin-top: 8px;
  border-radius: 999px;
  background: #dc2626;
  flex-shrink: 0;
}

.module-card {
  margin-top: 16px;
  padding: 16px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.96), rgba(31, 41, 55, 0.92));
  color: #f8fafc;
}

.module-title {
  font-size: 18px;
  font-weight: 700;
}

.module-desc {
  margin-top: 10px;
  color: rgba(226, 232, 240, 0.9);
  font-size: 13px;
  line-height: 1.7;
}

.subject-item,
.record-item {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.subject-item-enhanced {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 18px;
  background: #f8fafc;
}

.subject-item-enhanced .subject-info {
  flex: 1;
}

.subject-item-enhanced .subject-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.side-card__desc {
  margin-top: 6px;
  font-size: 12px;
  color: #64748b;
  line-height: 1.5;
}

.subject-name,
.record-title {
  color: #0f172a;
  font-size: 15px;
  font-weight: 600;
}

.subject-meta,
.record-subtitle,
.task-meta-row {
  margin-top: 6px;
  color: #64748b;
  font-size: 12px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.detail-card {
  padding: 22px;
  border-radius: 28px;
}

.task-item__top,
.task-progress-row,
.task-meta-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.task-progress-row {
  margin-top: 14px;
}

.task-progress-row .el-progress {
  flex: 1;
}

.task-meta-row {
  margin-top: 10px;
}

@media (max-width: 1440px) {
  .hero-meta {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .overview-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .workspace-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 1024px) {
  .exam-workbench-page {
    padding: 16px;
  }

  .hero-panel,
  .detail-grid {
    grid-template-columns: 1fr;
  }

  .hero-title-row {
    flex-direction: column;
  }

  .hero-summary-grid,
  .overview-grid {
    grid-template-columns: 1fr;
  }

  .flow-track {
    grid-auto-flow: row;
    grid-auto-columns: auto;
    overflow-x: visible;
  }

  .flow-step::after {
    top: auto;
    right: auto;
    left: 50%;
    bottom: -18px;
    width: 2px;
    height: 18px;
    transform: translateX(-50%);
  }
}

@media (max-width: 768px) {
  .hero-main,
  .flow-panel,
  .detail-card,
  .side-card {
    padding: 18px;
  }

  .hero-title {
    font-size: 28px;
  }

  .hero-meta {
    grid-template-columns: 1fr;
  }

  .section-head,
  .detail-card__head {
    flex-direction: column;
  }

  .flow-step__top {
    grid-template-columns: 42px minmax(0, 1fr);
  }

  .flow-step__top :deep(.el-tag) {
    grid-column: 1 / -1;
    justify-self: start;
  }

  .flow-step__metrics {
    grid-template-columns: 1fr;
  }

  .subject-item,
  .record-item,
  .task-item__top,
  .task-progress-row,
  .task-meta-row {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
