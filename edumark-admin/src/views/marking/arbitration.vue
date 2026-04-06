<template>
  <div class="arbitration-page">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ stats.pendingCount }}</div>
            <div class="stat-label">待仲裁</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value completed">{{ stats.completedCount }}</div>
            <div class="stat-label">已完成</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ stats.totalCount }}</div>
            <div class="stat-label">总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ stats.avgScoreDiff }}</div>
            <div class="stat-label">平均分差</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="阅卷任务">
          <el-select v-model="queryForm.taskId" placeholder="请选择" clearable style="width: 350px" @change="loadList">
            <el-option
              v-for="task in taskOptions"
              :key="task.id"
              :label="`${task.examName} - ${task.subjectName} - 第${task.questionNo}题`"
              :value="task.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable @change="loadList">
            <el-option label="待仲裁" :value="0" />
            <el-option label="已完成" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 仲裁列表 -->
    <el-card class="table-card">
      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="studentName" label="学生" width="120" />
        <el-table-column prop="questionNo" label="题号" width="80" />
        <el-table-column label="一评" width="150">
          <template #default="{ row }">
            <div>{{ row.firstScore }} 分</div>
            <div class="teacher-name">{{ row.firstTeacherName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="二评" width="150">
          <template #default="{ row }">
            <div>{{ row.secondScore }} 分</div>
            <div class="teacher-name">{{ row.secondTeacherName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="分差" width="80">
          <template #default="{ row }">
            <el-tag :type="row.scoreDiff > 5 ? 'danger' : 'warning'">
              {{ row.scoreDiff }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="仲裁结果" width="150">
          <template #default="{ row }">
            <div v-if="row.status === 1">
              <div>{{ row.arbitrationScore }} 分</div>
              <div class="teacher-name">{{ row.arbitrationTeacherName }}</div>
            </div>
            <el-tag v-else type="info">待仲裁</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="arbitrationComment" label="仲裁说明" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">查看详情</el-button>
            <el-button v-if="row.status === 0" link type="primary" @click="goToWorkspace(row)">
              去仲裁
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="loadList"
        @size-change="loadList"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="仲裁详情" width="900px">
      <div v-if="currentDetail" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="学生">{{ currentDetail.studentName }}</el-descriptions-item>
          <el-descriptions-item label="题号">第 {{ currentDetail.questionNo }} 题</el-descriptions-item>
          <el-descriptions-item label="一评教师">{{ currentDetail.firstTeacherName }}</el-descriptions-item>
          <el-descriptions-item label="一评分数">{{ currentDetail.firstScore }} 分</el-descriptions-item>
          <el-descriptions-item label="二评教师">{{ currentDetail.secondTeacherName }}</el-descriptions-item>
          <el-descriptions-item label="二评分数">{{ currentDetail.secondScore }} 分</el-descriptions-item>
          <el-descriptions-item label="分差">
            <el-tag :type="currentDetail.scoreDiff > 5 ? 'danger' : 'warning'">
              {{ currentDetail.scoreDiff }} 分
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="仲裁教师">
            {{ currentDetail.arbitrationTeacherName || '未仲裁' }}
          </el-descriptions-item>
          <el-descriptions-item label="仲裁分数" :span="2">
            {{ currentDetail.arbitrationScore !== null ? currentDetail.arbitrationScore + ' 分' : '未仲裁' }}
          </el-descriptions-item>
          <el-descriptions-item label="仲裁说明" :span="2">
            {{ currentDetail.arbitrationComment || '无' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentDetail.createTime }}</el-descriptions-item>
          <el-descriptions-item label="仲裁时间">
            {{ currentDetail.arbitrationTime || '未仲裁' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 答题卡图片 -->
        <div class="answer-image-section" v-if="currentDetail.answerImageUrl">
          <h3>答题卡</h3>
          <el-image
            :src="currentDetail.answerImageUrl"
            :preview-src-list="[currentDetail.answerImageUrl]"
            fit="contain"
            style="max-width: 100%; max-height: 500px"
          />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import {
  pageArbitrations,
  getArbitrationDetail,
  pageMarkingTasks,
  type MarkingArbitrationVO,
  type MarkingTaskVO
} from '@/api/marking'

const router = useRouter()

const queryForm = reactive({
  taskId: undefined as number | undefined,
  status: undefined as number | undefined,
  pageNum: 1,
  pageSize: 20
})

const list = ref<MarkingArbitrationVO[]>([])
const total = ref(0)
const loading = ref(false)
const taskOptions = ref<MarkingTaskVO[]>([])

const stats = reactive({
  pendingCount: 0,
  completedCount: 0,
  totalCount: 0,
  avgScoreDiff: 0
})

const detailVisible = ref(false)
const currentDetail = ref<MarkingArbitrationVO | null>(null)

// 加载任务选项
async function loadTaskOptions() {
  try {
    const res = await pageMarkingTasks({ pageNum: 1, pageSize: 1000 })
    taskOptions.value = res.data.list
  } catch (error: any) {
    ElMessage.error(error.message || '加载任务列表失败')
  }
}

// 加载列表
async function loadList() {
  if (!queryForm.taskId) {
    ElMessage.warning('请先选择阅卷任务')
    return
  }
  const taskId = queryForm.taskId

  loading.value = true
  try {
    const res = await pageArbitrations({
      ...queryForm,
      taskId,
    })
    list.value = res.data.list
    total.value = res.data.total

    // 计算统计数据
    const pending = list.value.filter(item => item.status === 0)
    const completed = list.value.filter(item => item.status === 1)
    stats.pendingCount = pending.length
    stats.completedCount = completed.length
    stats.totalCount = list.value.length
    stats.avgScoreDiff = list.value.length > 0
      ? Math.round(list.value.reduce((sum, item) => sum + item.scoreDiff, 0) / list.value.length * 10) / 10
      : 0
  } catch (error: any) {
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

// 查看详情
async function viewDetail(row: MarkingArbitrationVO) {
  try {
    const res = await getArbitrationDetail(row.id)
    currentDetail.value = res.data
    detailVisible.value = true
  } catch (error: any) {
    ElMessage.error(error.message || '加载详情失败')
  }
}

// 跳转到仲裁工作台
function goToWorkspace(row: MarkingArbitrationVO) {
  router.push(`/marking/workspace?taskId=${row.taskId}&role=3`)
}

// 重置查询
function resetQuery() {
  queryForm.taskId = undefined
  queryForm.status = undefined
  queryForm.pageNum = 1
  list.value = []
  total.value = 0
}

onMounted(() => {
  loadTaskOptions()
})
</script>

<style scoped>
.arbitration-page {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-value.completed {
  color: #67c23a;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.filter-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.teacher-name {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.detail-content {
  padding: 20px 0;
}

.answer-image-section {
  margin-top: 20px;
}

.answer-image-section h3 {
  margin-bottom: 12px;
  font-size: 16px;
  color: #303133;
}
</style>
