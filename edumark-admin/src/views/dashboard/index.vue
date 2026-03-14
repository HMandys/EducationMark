<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

// 统计卡片数据
const statsCards = ref([
  {
    title: '考试总数',
    value: 128,
    icon: 'Document',
    color: '#409eff',
    bgColor: 'rgba(64, 158, 255, 0.1)',
    suffix: '场',
  },
  {
    title: '待阅卷任务',
    value: 24,
    icon: 'Edit',
    color: '#e6a23c',
    bgColor: 'rgba(230, 162, 60, 0.1)',
    suffix: '份',
  },
  {
    title: '学生总数',
    value: 3680,
    icon: 'User',
    color: '#67c23a',
    bgColor: 'rgba(103, 194, 58, 0.1)',
    suffix: '人',
  },
  {
    title: '今日访问',
    value: 512,
    icon: 'View',
    color: '#909399',
    bgColor: 'rgba(144, 147, 153, 0.1)',
    suffix: '次',
  },
])

// 快捷入口
const quickActions = ref([
  { title: '创建考试', icon: 'Plus', path: '/exam/create', color: '#409eff' },
  { title: '阅卷工作台', icon: 'Edit', path: '/marking/workspace', color: '#e6a23c' },
  { title: '成绩查询', icon: 'Search', path: '/score/query', color: '#67c23a' },
  { title: '数据报表', icon: 'DataAnalysis', path: '/report', color: '#f56c6c' },
])

// 最近考试
const recentExams = ref([
  { id: 1, name: '2024年春季期中考试', status: '已完成', date: '2024-04-15', subject: '数学' },
  { id: 2, name: '高三模拟考试（一）', status: '阅卷中', date: '2024-04-12', subject: '综合' },
  { id: 3, name: '初二单元测试', status: '待发布', date: '2024-04-10', subject: '英语' },
  { id: 4, name: '高一月考', status: '已完成', date: '2024-04-08', subject: '物理' },
])

// 待办事项
const todoList = ref([
  { id: 1, title: '完成高三模拟考试阅卷', priority: 'high', deadline: '2024-04-20' },
  { id: 2, title: '发布期中考试成绩', priority: 'medium', deadline: '2024-04-22' },
  { id: 3, title: '生成班级学情报告', priority: 'low', deadline: '2024-04-25' },
])

const getPriorityTag = (priority: string): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
  const map: Record<string, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
    high: 'danger',
    medium: 'warning',
    low: 'info',
  }
  return map[priority] || 'info'
}

const getPriorityText = (priority: string) => {
  const map: Record<string, string> = {
    high: '紧急',
    medium: '一般',
    low: '低',
  }
  return map[priority] || '未知'
}

const getStatusTag = (status: string): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
  const map: Record<string, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
    '已完成': 'success',
    '阅卷中': 'warning',
    '待发布': 'info',
  }
  return map[status] || 'info'
}
</script>

<template>
  <div class="dashboard">
    <!-- 欢迎信息 -->
    <div class="welcome-section">
      <h2>
        欢迎回来，{{ userStore.realName || '管理员' }}
        <span class="wave">👋</span>
      </h2>
      <p>{{ userStore.roleName || '超级管理员' }} | 今天是个好日子，继续努力！</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col v-for="card in statsCards" :key="card.title" :xs="24" :sm="12" :lg="6">
        <div class="stats-card" :style="{ backgroundColor: card.bgColor }">
          <div class="stats-icon" :style="{ backgroundColor: card.color }">
            <el-icon :size="24"><component :is="card.icon" /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-value">
              {{ card.value.toLocaleString() }}
              <span class="stats-suffix">{{ card.suffix }}</span>
            </div>
            <div class="stats-title">{{ card.title }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 快捷入口 -->
    <div class="section">
      <h3 class="section-title">快捷入口</h3>
      <el-row :gutter="20">
        <el-col v-for="action in quickActions" :key="action.title" :xs="12" :sm="6">
          <div class="quick-action" @click="$router.push(action.path)">
            <div class="action-icon" :style="{ backgroundColor: action.color }">
              <el-icon :size="28"><component :is="action.icon" /></el-icon>
            </div>
            <span class="action-title">{{ action.title }}</span>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 内容区域 -->
    <el-row :gutter="20">
      <!-- 最近考试 -->
      <el-col :xs="24" :lg="14">
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>最近考试</span>
              <el-button type="primary" link>查看全部</el-button>
            </div>
          </template>

          <el-table :data="recentExams" stripe>
            <el-table-column prop="name" label="考试名称" min-width="180" />
            <el-table-column prop="subject" label="科目" width="80" />
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusTag(row.status)" size="small">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 待办事项 -->
      <el-col :xs="24" :lg="10">
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>待办事项</span>
              <el-button type="primary" link>添加</el-button>
            </div>
          </template>

          <div class="todo-list">
            <div v-for="todo in todoList" :key="todo.id" class="todo-item">
              <el-checkbox />
              <div class="todo-content">
                <div class="todo-title">{{ todo.title }}</div>
                <div class="todo-meta">
                  <el-tag :type="getPriorityTag(todo.priority)" size="small">
                    {{ getPriorityText(todo.priority) }}
                  </el-tag>
                  <span class="todo-deadline">截止: {{ todo.deadline }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style lang="scss" scoped>
.dashboard {
  max-width: 1400px;
}

.welcome-section {
  margin-bottom: 24px;

  h2 {
    font-size: 24px;
    font-weight: 600;
    color: #333;
    margin: 0 0 8px;

    .wave {
      display: inline-block;
      animation: wave 1.5s infinite;
    }
  }

  p {
    color: #666;
    margin: 0;
  }
}

@keyframes wave {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(20deg); }
  75% { transform: rotate(-10deg); }
}

.stats-row {
  margin-bottom: 24px;
}

.stats-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 12px;
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  }

  .stats-icon {
    width: 56px;
    height: 56px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    margin-right: 16px;
  }

  .stats-info {
    .stats-value {
      font-size: 28px;
      font-weight: 600;
      color: #333;
      line-height: 1.2;

      .stats-suffix {
        font-size: 14px;
        font-weight: normal;
        color: #999;
        margin-left: 4px;
      }
    }

    .stats-title {
      font-size: 14px;
      color: #666;
      margin-top: 4px;
    }
  }
}

.section {
  margin-bottom: 24px;

  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin: 0 0 16px;
  }
}

.quick-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 16px;
  background: #fff;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  }

  .action-icon {
    width: 56px;
    height: 56px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    margin-bottom: 12px;
  }

  .action-title {
    font-size: 14px;
    color: #333;
  }
}

.section-card {
  margin-bottom: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.todo-list {
  .todo-item {
    display: flex;
    align-items: flex-start;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .el-checkbox {
      margin-top: 2px;
    }

    .todo-content {
      flex: 1;
      margin-left: 12px;

      .todo-title {
        font-size: 14px;
        color: #333;
        margin-bottom: 6px;
      }

      .todo-meta {
        display: flex;
        align-items: center;
        gap: 8px;

        .todo-deadline {
          font-size: 12px;
          color: #999;
        }
      }
    }
  }
}
</style>
