<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const todayText = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long',
  })
})

const statsCards = ref([
  {
    title: '考试总量',
    value: '128',
    suffix: '场',
    detail: '本学期已归档 93 场，进行中 12 场',
    icon: 'Tickets',
    tone: 'primary',
  },
  {
    title: '待处理阅卷',
    value: '24',
    suffix: '项',
    detail: '其中 6 项接近截止时间，需要优先处理',
    icon: 'EditPen',
    tone: 'warning',
  },
  {
    title: '学生覆盖',
    value: '3,680',
    suffix: '人',
    detail: '覆盖 42 个班级，在线绑定率 88%',
    icon: 'User',
    tone: 'success',
  },
  {
    title: '成绩发布',
    value: '16',
    suffix: '次',
    detail: '最近 7 天新增 5 次发布操作',
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

const recentExams = ref([
  { id: 1, name: '高三二模联考', subject: '综合', schedule: '03-20 08:30', status: '进行中', owner: '教务处' },
  { id: 2, name: '初二月考', subject: '数学', schedule: '03-21 14:00', status: '待发布', owner: '数学组' },
  { id: 3, name: '高一英语阶段测', subject: '英语', schedule: '03-18 09:00', status: '已完成', owner: '英语组' },
  { id: 4, name: '九年级期中统测', subject: '全科', schedule: '03-25 08:00', status: '待执行', owner: '年级组' },
])

const teamBoard = ref([
  { label: '组织管理', value: '学校、年级、班级、教师、学生、家长' },
  { label: '考试资产', value: '考试、知识点、答题卡模板统一管理' },
  { label: '系统配置', value: '角色授权、菜单控制、用户状态管理' },
])

const todoList = ref([
  { title: '完成高三二模联考主观题复核', level: '高优先级', deadline: '今天 18:00 前' },
  { title: '校验成绩发布名单与班级映射', level: '中优先级', deadline: '明天 10:00 前' },
  { title: '补充初二英语知识点层级', level: '常规', deadline: '本周内' },
])

function handleNavigate(path: string) {
  router.push(path)
}

function getStatusType(status: string): 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, 'success' | 'warning' | 'info' | 'danger'> = {
    已完成: 'success',
    进行中: 'warning',
    待发布: 'info',
    待执行: 'danger',
  }
  return map[status] || 'info'
}
</script>

<template>
  <div class="page-container dashboard-page">
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

          <div class="exam-list">
            <div v-for="item in recentExams" :key="item.id" class="exam-list__item">
              <div>
                <div class="exam-list__title">{{ item.name }}</div>
                <div class="exam-list__meta">
                  <span>{{ item.subject }}</span>
                  <span>{{ item.schedule }}</span>
                  <span>{{ item.owner }}</span>
                </div>
              </div>
              <el-tag :type="getStatusType(item.status)">{{ item.status }}</el-tag>
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
                <div><el-tag type="warning">3 项</el-tag></div>
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
