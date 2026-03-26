<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getExamPage, type Exam } from '@/api/exam'
import { pageMarkingTasks, type MarkingTaskVO } from '@/api/marking'
import { getStudentPage } from '@/api/school'
import { getClassPage } from '@/api/school'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)

// 统计数据
const examTotal = ref(0)
const examInProgressCount = ref(0)
const examPublishedCount = ref(0)
const pendingMarkingCount = ref(0)
const nearDeadlineMarkingCount = ref(0)
const studentTotal = ref(0)
const classTotal = ref(0)
const recentExams = ref<Exam[]>([])
const pendingTasks = ref<MarkingTaskVO[]>([])

const todayText = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long',
  })
})

const statsCards = computed(() => [
  {
    title: '考试总量',
    value: examTotal.value.toLocaleString(),
    suffix: '场',
    detail: `已发布 ${examPublishedCount.value} 场，进行中 ${examInProgressCount.value} 场`,
    icon: 'Tickets',
    tone: 'primary',
  },
  {
    title: '待处理阅卷',
    value: pendingMarkingCount.value.toLocaleString(),
    suffix: '项',
    detail: nearDeadlineMarkingCount.value > 0
      ? `其中 ${nearDeadlineMarkingCount.value} 项未完成，需要优先处理`
      : '当前无积压任务',
    icon: 'EditPen',
    tone: 'warning',
  },
  {
    title: '学生覆盖',
    value: studentTotal.value.toLocaleString(),
    suffix: '人',
    detail: `覆盖 ${classTotal.value} 个班级`,
    icon: 'User',
    tone: 'success',
  },
  {
    title: '成绩发布',
    value: examPublishedCount.value.toLocaleString(),
    suffix: '次',
    detail: examPublishedCount.value > 0 ? '已发布考试可在家长端查看' : '暂无已发布成绩',
    icon: 'DataAnalysis',
    tone: 'danger',
  },
])

const quickActions = ref([
  { title: '考试管理', desc: '创建与发布考试计划', path: '/exam/list', icon: 'Document' },
  { title: '答题卡模板', desc: '进入模板维护与编辑', path: '/answer-sheet-design/list', icon: 'DocumentCopy' },
  { title: '阅卷工作台', desc: '处理任务分配与进度', path: '/marking/workspace', icon: 'Edit' },
  { title: '成绩查询', desc: '查看统计与发布情况', path: '/score/list', icon: 'TrendCharts' },
  { title: '用户管理', desc: '维护后台账号与角色', path: '/system/user', icon: 'UserFilled' },
  { title: '学校管理', desc: '管理组织、年级和班级', path: '/school/list', icon: 'School' },
])

const teamBoard = ref([
  { label: '组织管理', value: '学校、年级、班级、教师、学生、家长' },
  { label: '考试资产', value: '考试、知识点、答题卡模板统一管理' },
  { label: '系统配置', value: '角色授权、菜单控制、用户状态管理' },
])

// 动态生成待办提醒
const todoList = computed(() => {
  const items: { title: string; level: string; deadline: string }[] = []

  // 未完成的阅卷任务
  const unfinishedTasks = pendingTasks.value.filter((t) => t.status === 1)
  if (unfinishedTasks.length > 0) {
    items.push({
      title: `${unfinishedTasks.length} 个阅卷任务进行中，注意推进完成`,
      level: '高优先级',
      deadline: '尽快处理',
    })
  }

  // 进行中的考试
  if (examInProgressCount.value > 0) {
    items.push({
      title: `${examInProgressCount.value} 场考试进行中，关注阅卷与出分进度`,
      level: '中优先级',
      deadline: '持续跟进',
    })
  }

  // 没有阅卷码的任务
  const noCodeTasks = pendingTasks.value.filter((t) => t.status !== 2 && !t.accessCode)
  if (noCodeTasks.length > 0) {
    items.push({
      title: `${noCodeTasks.length} 个阅卷任务未生成阅卷码`,
      level: '常规',
      deadline: '分配前补齐',
    })
  }

  if (items.length === 0) {
    items.push({
      title: '当前没有待办事项',
      level: '正常',
      deadline: '-',
    })
  }

  return items
})

onMounted(() => {
  loadDashboardData()
})

async function loadDashboardData() {
  loading.value = true
  try {
    const [
      examRes,
      examInProgressRes,
      examPublishedRes,
      markingRes,
      studentRes,
      classRes,
    ] = await Promise.allSettled([
      getExamPage({ pageNum: 1, pageSize: 4 }),
      getExamPage({ pageNum: 1, pageSize: 1, status: 2 }),
      getExamPage({ pageNum: 1, pageSize: 1, status: 5 }),
      pageMarkingTasks({ pageNum: 1, pageSize: 200 }),
      getStudentPage({ pageNum: 1, pageSize: 1 }),
      getClassPage({ pageNum: 1, pageSize: 1 }),
    ])

    if (examRes.status === 'fulfilled') {
      examTotal.value = examRes.value.data.total
      recentExams.value = examRes.value.data.list || []
    }
    if (examInProgressRes.status === 'fulfilled') {
      examInProgressCount.value = examInProgressRes.value.data.total
    }
    if (examPublishedRes.status === 'fulfilled') {
      examPublishedCount.value = examPublishedRes.value.data.total
    }
    if (markingRes.status === 'fulfilled') {
      const allTasks = markingRes.value.data.list || []
      pendingTasks.value = allTasks
      pendingMarkingCount.value = allTasks.filter((t) => t.status !== 2).length
      nearDeadlineMarkingCount.value = allTasks.filter((t) => t.status === 1).length
    }
    if (studentRes.status === 'fulfilled') {
      studentTotal.value = studentRes.value.data.total
    }
    if (classRes.status === 'fulfilled') {
      classTotal.value = classRes.value.data.total
    }
  } catch (error) {
    console.error('加载工作台数据失败', error)
  } finally {
    loading.value = false
  }
}

function handleNavigate(path: string) {
  router.push(path)
}

function getExamStatusText(status: number): string {
  const map: Record<number, string> = {
    0: '草稿',
    1: '待考试',
    2: '考试中',
    3: '阅卷中',
    4: '已完成',
    5: '已发布',
  }
  return map[status] || '未知'
}

function getStatusType(status: number): 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<number, 'success' | 'warning' | 'info' | 'danger'> = {
    0: 'info',
    1: 'info',
    2: 'warning',
    3: 'warning',
    4: 'success',
    5: 'success',
  }
  return map[status] || 'info'
}
</script>

<template>
  <div class="page-container dashboard-page" v-loading="loading">
    <section class="page-hero dashboard-hero">
      <div>
        <div class="page-hero__title">
          {{ userStore.realName || '管理员' }}，今天的后台状态正常
        </div>
        <div class="page-hero__desc">
          你当前以「{{ userStore.roleName || '超级管理员' }}」身份登录。{{ todayText }}，建议优先查看阅卷任务与成绩发布。
        </div>
      </div>

      <div class="page-hero__meta">
        <div class="metric-chip">
          <span>当前账号</span>
          <strong>{{ userStore.userInfo?.username || 'admin' }}</strong>
        </div>
        <div class="metric-chip">
          <span>权限数</span>
          <strong>{{ userStore.permissions.length }}</strong>
        </div>
        <div class="metric-chip">
          <span>系统状态</span>
          <strong>运行中</strong>
        </div>
      </div>
    </section>

    <section class="stats-grid">
      <article
        v-for="card in statsCards"
        :key="card.title"
        class="stats-card"
        :class="`stats-card--${card.tone}`"
      >
        <div class="stats-card__head">
          <span>{{ card.title }}</span>
          <el-icon :size="20"><component :is="card.icon" /></el-icon>
        </div>
        <div class="stats-card__value">
          {{ card.value }}
          <small>{{ card.suffix }}</small>
        </div>
        <p class="stats-card__detail">{{ card.detail }}</p>
      </article>
    </section>

    <section class="dashboard-main">
      <el-card class="panel-card quick-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>高频入口</span>
            <div><el-tag type="primary">可直接跳转</el-tag></div>
          </div>
        </template>

        <div class="quick-grid">
          <button
            v-for="action in quickActions"
            :key="action.title"
            class="quick-item"
            type="button"
            @click="handleNavigate(action.path)"
          >
            <div class="quick-item__icon">
              <el-icon :size="18"><component :is="action.icon" /></el-icon>
            </div>
            <div class="quick-item__content">
              <strong>{{ action.title }}</strong>
              <span>{{ action.desc }}</span>
            </div>
          </button>
        </div>
      </el-card>

      <div class="dashboard-columns">
        <el-card class="panel-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>近期考试</span>
              <div><el-button type="primary" link @click="handleNavigate('/exam/list')">进入考试管理</el-button></div>
            </div>
          </template>

          <el-empty v-if="recentExams.length === 0" description="暂无考试数据" :image-size="80" />
          <div v-else class="exam-list">
            <div v-for="item in recentExams" :key="item.id" class="exam-list__item">
              <div>
                <div class="exam-list__title">{{ item.name }}</div>
                <div class="exam-list__meta">
                  <span>{{ item.gradeName || '-' }}</span>
                  <span>{{ item.academicYear }} {{ item.semester === 1 ? '第一学期' : item.semester === 2 ? '第二学期' : '' }}</span>
                  <span>{{ item.createTime?.substring(0, 10) || '-' }}</span>
                </div>
              </div>
              <el-tag :type="getStatusType(item.status)">{{ item.statusName || getExamStatusText(item.status) }}</el-tag>
            </div>
          </div>
        </el-card>

        <div class="side-stack">
          <el-card class="panel-card" shadow="never">
            <template #header>
              <div class="card-header">
                <span>管理范围</span>
                <div><el-tag type="info">全链路</el-tag></div>
              </div>
            </template>

            <div class="board-list">
              <div v-for="item in teamBoard" :key="item.label" class="board-list__item">
                <span class="board-list__label">{{ item.label }}</span>
                <span class="board-list__value">{{ item.value }}</span>
              </div>
            </div>
          </el-card>

          <el-card class="panel-card" shadow="never">
            <template #header>
              <div class="card-header">
                <span>待办提醒</span>
                <div><el-tag type="warning">{{ todoList.length }} 项</el-tag></div>
              </div>
            </template>

            <div class="todo-list">
              <div v-for="item in todoList" :key="item.title" class="todo-item">
                <div class="todo-item__dot"></div>
                <div class="todo-item__content">
                  <strong>{{ item.title }}</strong>
                  <span>{{ item.level }} · {{ item.deadline }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </section>
  </div>
</template>

<style lang="scss" scoped>
.dashboard-page {
  gap: 18px;
}

.dashboard-hero {
  align-items: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.stats-card {
  padding: 20px;
  border: 1px solid var(--app-border);
  border-radius: var(--app-radius-lg);
  background: var(--app-surface);
  box-shadow: var(--app-shadow);
}

.stats-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: var(--app-text-secondary);
}

.stats-card__value {
  margin-top: 20px;
  font-size: 34px;
  font-weight: 700;
  color: var(--app-text);
}

.stats-card__value small {
  margin-left: 6px;
  font-size: 14px;
  color: var(--app-text-tertiary);
}

.stats-card__detail {
  margin-top: 12px;
  line-height: 1.7;
  color: var(--app-text-secondary);
}

.stats-card--primary {
  border-color: #bfdbfe;
}

.stats-card--warning {
  border-color: #fcd34d;
}

.stats-card--success {
  border-color: #86efac;
}

.stats-card--danger {
  border-color: #fca5a5;
}

.dashboard-main,
.side-stack {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dashboard-columns {
  display: grid;
  grid-template-columns: minmax(0, 1.3fr) minmax(320px, 0.7fr);
  gap: 16px;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.quick-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  border: 1px solid var(--app-border);
  border-radius: 16px;
  background: #fff;
  text-align: left;
  cursor: pointer;
  transition: transform 0.18s ease, border-color 0.18s ease;
}

.quick-item:hover {
  transform: translateY(-2px);
  border-color: #bfd2ee;
}

.quick-item__icon {
  width: 38px;
  height: 38px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  background: var(--app-primary-soft);
  color: var(--app-primary);
}

.quick-item__content {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.quick-item__content strong {
  font-size: 15px;
  color: var(--app-text);
}

.quick-item__content span {
  line-height: 1.6;
  color: var(--app-text-secondary);
}

.exam-list,
.board-list,
.todo-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.exam-list__item,
.board-list__item,
.todo-item {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid var(--app-border);
  border-radius: 14px;
  background: #fff;
}

.exam-list__title {
  font-size: 15px;
  font-weight: 600;
  color: var(--app-text);
}

.exam-list__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 8px;
  color: var(--app-text-tertiary);
}

.board-list__label {
  min-width: 88px;
  color: var(--app-text-secondary);
}

.board-list__value {
  color: var(--app-text);
  line-height: 1.7;
}

.todo-item {
  justify-content: flex-start;
}

.todo-item__dot {
  width: 10px;
  height: 10px;
  margin-top: 6px;
  border-radius: 50%;
  background: var(--app-primary);
}

.todo-item__content {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.todo-item__content strong {
  color: var(--app-text);
}

.todo-item__content span {
  color: var(--app-text-secondary);
}

@media (max-width: 1200px) {
  .stats-grid,
  .quick-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .dashboard-columns {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-grid,
  .quick-grid {
    grid-template-columns: 1fr;
  }
}
</style>
