<template>
  <div class="marking-task-container">
    <!-- 查询表单 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="考试">
          <el-select v-model="queryForm.examId" placeholder="请选择考试" clearable style="width: 200px">
            <el-option v-for="item in examList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="未开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>阅卷任务列表</span>
          <el-button type="primary" @click="handleGenerate">生成阅卷任务</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="examName" label="考试名称" min-width="150" />
        <el-table-column prop="subjectName" label="科目" width="100" />
        <el-table-column prop="questionNo" label="题号" width="80" />
        <el-table-column prop="name" label="任务名称" min-width="150" />
        <el-table-column label="进度" width="150">
          <template #default="{ row }">
            <el-progress
              :percentage="getProgress(row)"
              :status="getProgressStatus(row)"
              :stroke-width="12"
            />
            <div class="progress-text">{{ row.completedCount }} / {{ row.totalCount }}</div>
          </template>
        </el-table-column>
        <el-table-column label="双评" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.enableDoubleMarking === 1 ? 'success' : 'info'" size="small">
              {{ row.enableDoubleMarking === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleAssign(row)" v-if="row.status === 0">
              分配
            </el-button>
            <el-button type="success" link size="small" @click="handleStart(row)" v-if="row.status === 0">
              开始
            </el-button>
            <el-button type="primary" link size="small" @click="handleAccessCode(row)" v-if="row.status === 1">
              阅卷码
            </el-button>
            <el-button type="warning" link size="small" @click="handleComplete(row)" v-if="row.status === 1">
              完成
            </el-button>
            <el-button type="info" link size="small" @click="handleDetail(row)">
              详情
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)" v-if="row.status === 0">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="loadData"
        @current-change="loadData"
        class="pagination"
      />
    </el-card>

    <!-- 生成任务对话框 -->
    <el-dialog v-model="generateVisible" title="生成阅卷任务" width="500px">
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="考试">
          <el-select v-model="generateForm.examId" placeholder="请选择考试" style="width: 100%" @change="loadExamSubjects">
            <el-option v-for="item in examList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="科目">
          <el-select v-model="generateForm.examSubjectId" placeholder="请选择科目" style="width: 100%">
            <el-option v-for="item in subjectList" :key="item.id" :label="item.subjectName" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmGenerate">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配任务对话框 -->
    <el-dialog v-model="assignVisible" title="分配阅卷任务" width="600px">
      <div class="assign-info">
        <p>任务：{{ currentTask?.name }}</p>
        <p>总份数：{{ currentTask?.totalCount }}</p>
        <p>双评：{{ currentTask?.enableDoubleMarking === 1 ? '是' : '否' }}</p>
      </div>
      <el-divider />
      <el-form>
        <template v-if="currentTask?.enableDoubleMarking !== 1">
          <h4>评阅教师</h4>
          <el-select v-model="assignForm.teachers" multiple placeholder="请选择评阅教师" style="width: 100%">
            <el-option v-for="item in teacherList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </template>
        <template v-else>
          <h4>一评教师</h4>
          <el-select v-model="assignForm.firstTeachers" multiple placeholder="请选择一评教师" style="width: 100%; margin-bottom: 16px">
            <el-option v-for="item in teacherList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
          <h4>二评教师</h4>
          <el-select v-model="assignForm.secondTeachers" multiple placeholder="请选择二评教师" style="width: 100%; margin-bottom: 16px">
            <el-option v-for="item in teacherList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
          <h4>仲裁教师</h4>
          <el-select v-model="assignForm.arbitrationTeachers" multiple placeholder="请选择仲裁教师" style="width: 100%">
            <el-option v-for="item in teacherList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssign">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="任务详情" width="700px">
      <el-descriptions :column="2" border v-if="currentTask">
        <el-descriptions-item label="考试名称">{{ currentTask.examName }}</el-descriptions-item>
        <el-descriptions-item label="科目">{{ currentTask.subjectName }}</el-descriptions-item>
        <el-descriptions-item label="任务名称">{{ currentTask.name }}</el-descriptions-item>
        <el-descriptions-item label="题号">{{ currentTask.questionNo }}</el-descriptions-item>
        <el-descriptions-item label="总份数">{{ currentTask.totalCount }}</el-descriptions-item>
        <el-descriptions-item label="已完成">{{ currentTask.completedCount }}</el-descriptions-item>
        <el-descriptions-item label="待阅">{{ currentTask.pendingCount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentTask.status)">{{ getStatusText(currentTask.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="双评">{{ currentTask.enableDoubleMarking === 1 ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="双评阈值">{{ currentTask.doubleMarkingThreshold || '-' }}</el-descriptions-item>
        <el-descriptions-item label="阅卷码" :span="2" v-if="hasAnyAccessCode(currentTask)">
          <div class="detail-code-list">
            <div v-for="item in getAccessCodeEntries(currentTask)" :key="item.label" class="detail-code-item">
              <span class="detail-code-label">{{ item.label }}</span>
              <el-tag type="success" size="large" class="access-code-tag">{{ item.code }}</el-tag>
              <el-button type="primary" link size="small" @click="copyAccessCode(item.code, item.label)">复制</el-button>
              <el-button link size="small" @click="copyAccessUrl(item.code, `${item.label}链接`)">复制链接</el-button>
            </div>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="阅卷码有效期" :span="2" v-if="currentTask.accessCodeExpireTime">
          {{ currentTask.accessCodeExpireTime }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ currentTask.createTime }}</el-descriptions-item>
      </el-descriptions>
      <el-divider>分配教师</el-divider>
      <el-table :data="currentTask?.assigns || []" stripe size="small">
        <el-table-column prop="teacherName" label="教师姓名" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            {{ row.markingRole === 1 ? '一评' : row.markingRole === 2 ? '二评' : '仲裁' }}
          </template>
        </el-table-column>
        <el-table-column prop="assignCount" label="分配数" width="80" />
        <el-table-column prop="completedCount" label="已完成" width="80" />
        <el-table-column label="进度" width="120">
          <template #default="{ row }">
            <el-progress
              :percentage="row.assignCount > 0 ? Math.round(row.completedCount / row.assignCount * 100) : 0"
              :stroke-width="8"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 阅卷码对话框 -->
    <el-dialog v-model="accessCodeVisible" title="阅卷码" width="450px">
      <div class="access-code-container" v-if="currentTask">
        <div class="task-info">
          <p><strong>任务：</strong>{{ currentTask.name }}</p>
          <p><strong>考试：</strong>{{ currentTask.examName }} - {{ currentTask.subjectName }}</p>
        </div>

        <div v-if="!hasAnyAccessCode(currentTask)" class="no-code">
          <el-empty description="尚未生成阅卷码" :image-size="60">
            <el-button type="primary" @click="handleGenerateCode">生成阅卷码</el-button>
          </el-empty>
        </div>

        <div v-else class="code-display">
          <div class="access-code-grid">
            <div v-for="item in getAccessCodeEntries(currentTask)" :key="item.label" class="code-card">
              <div class="code-card-header">
                <span>{{ item.label }}</span>
                <el-tag size="small" :type="item.role === 2 ? 'warning' : 'success'">
                  {{ item.role === 2 ? '二评入口' : '一评入口' }}
                </el-tag>
              </div>
              <div class="code-value">{{ item.code }}</div>
              <div class="code-actions">
                <el-button type="primary" @click="copyAccessCode(item.code, item.label)">
                  复制{{ item.label }}
                </el-button>
                <el-button @click="copyAccessUrl(item.code, `${item.label}链接`)">复制链接</el-button>
              </div>
            </div>
          </div>
          <p class="code-expire" v-if="currentTask.accessCodeExpireTime">
            有效期至：{{ currentTask.accessCodeExpireTime }}
          </p>
          <div class="code-toolbar">
            <el-button @click="handleRefreshCode">刷新有效期</el-button>
          </div>
        </div>

        <el-divider />

        <div class="code-tips">
          <h4>使用说明</h4>
          <ol>
            <li>将对应角色的阅卷码或链接发给老师</li>
            <li>老师访问 <strong>{{ accessBaseUrl }}/marking</strong>，可直接使用复制的链接进入</li>
            <li>双评任务请区分一评码、二评码，避免角色混用</li>
          </ol>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute } from 'vue-router'
import {
  pageMarkingTasks,
  getMarkingTaskDetail,
  generateMarkingTasks,
  deleteMarkingTask,
  assignMarkingTask,
  startMarkingTask,
  completeMarkingTask,
  generateAccessCode,
  refreshAccessCode,
  type MarkingTaskVO,
  type MarkingTaskAssignDTO
} from '@/api/marking'
import { getExamPage, getExamSubjectList } from '@/api/exam'
import { getTeacherPage } from '@/api/school'

const route = useRoute()

const loading = ref(false)
const tableData = ref<MarkingTaskVO[]>([])
const total = ref(0)
const examList = ref<any[]>([])
const subjectList = ref<any[]>([])
const teacherList = ref<any[]>([])

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  examId: undefined as string | number | undefined,
  examSubjectId: undefined as string | number | undefined,
  status: undefined as number | undefined
})

const generateVisible = ref(false)
const generateForm = reactive({
  examId: undefined as string | number | undefined,
  examSubjectId: undefined as string | number | undefined
})

const assignVisible = ref(false)
const currentTask = ref<MarkingTaskVO | null>(null)
const assignForm = reactive({
  teachers: [] as number[],
  firstTeachers: [] as number[],
  secondTeachers: [] as number[],
  arbitrationTeachers: [] as number[]
})

const detailVisible = ref(false)
const accessCodeVisible = ref(false)
const accessBaseUrl = window.location.origin

async function loadData() {
  loading.value = true
  try {
    const res = await pageMarkingTasks(queryForm)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

async function loadExams() {
  const res = await getExamPage({ pageNum: 1, pageSize: 1000 })
  examList.value = res.data.list || []
}

async function loadExamSubjects() {
  if (generateForm.examId) {
    const res = await getExamSubjectList(generateForm.examId)
    subjectList.value = res.data || []
  }
}

async function loadTeachers() {
  const res = await getTeacherPage({ pageNum: 1, pageSize: 1000 })
  teacherList.value = res.data.list || []
}

function handleSearch() {
  queryForm.pageNum = 1
  loadData()
}

function handleReset() {
  queryForm.examId = undefined
  queryForm.examSubjectId = undefined
  queryForm.status = undefined
  handleSearch()
}

async function syncRouteExam() {
  const routeExamId = typeof route.query.examId === 'string' ? route.query.examId : undefined
  const routeExamSubjectId = typeof route.query.examSubjectId === 'string' ? route.query.examSubjectId : undefined
  if (routeExamId && /^\d+$/.test(routeExamId)) {
    queryForm.examId = routeExamId
  } else {
    queryForm.examId = undefined
  }
  queryForm.examSubjectId = routeExamSubjectId && /^\d+$/.test(routeExamSubjectId)
    ? routeExamSubjectId
    : undefined
  queryForm.pageNum = 1
  await loadData()
}

function handleGenerate() {
  generateForm.examId = undefined
  generateForm.examSubjectId = undefined
  subjectList.value = []
  generateVisible.value = true
}

async function confirmGenerate() {
  if (!generateForm.examSubjectId) {
    ElMessage.warning('请选择科目')
    return
  }
  try {
    await generateMarkingTasks(generateForm.examSubjectId)
    ElMessage.success('生成成功')
    generateVisible.value = false
    loadData()
  } catch (e: any) {
    ElMessage.error(e.message || '生成失败')
  }
}

async function handleAssign(row: MarkingTaskVO) {
  currentTask.value = row
  assignForm.teachers = []
  assignForm.firstTeachers = []
  assignForm.secondTeachers = []
  assignForm.arbitrationTeachers = []

  // 加载任务详情获取已分配的教师
  const res = await getMarkingTaskDetail(row.id)
  currentTask.value = res.data

  assignVisible.value = true
}

async function confirmAssign() {
  if (!currentTask.value) return

  if (currentTask.value.enableDoubleMarking === 1) {
    if (assignForm.firstTeachers.length === 0) {
      ElMessage.warning('请选择一评教师')
      return
    }
    if (assignForm.secondTeachers.length === 0) {
      ElMessage.warning('请选择二评教师')
      return
    }
    if (assignForm.arbitrationTeachers.length === 0) {
      ElMessage.warning('请选择仲裁教师')
      return
    }
  }

  const assigns: MarkingTaskAssignDTO['assigns'] = []

  if (currentTask.value.enableDoubleMarking !== 1) {
    // 单评模式
    assignForm.teachers.forEach(teacherId => {
      assigns.push({ teacherId, markingRole: 1 })
    })
  } else {
    // 双评模式
    assignForm.firstTeachers.forEach(teacherId => {
      assigns.push({ teacherId, markingRole: 1 })
    })
    assignForm.secondTeachers.forEach(teacherId => {
      assigns.push({ teacherId, markingRole: 2 })
    })
    assignForm.arbitrationTeachers.forEach(teacherId => {
      assigns.push({ teacherId, markingRole: 3 })
    })
  }

  if (assigns.length === 0) {
    ElMessage.warning('请选择教师')
    return
  }

  try {
    await assignMarkingTask({ taskId: currentTask.value.id, assigns })
    ElMessage.success('分配成功')
    assignVisible.value = false
    loadData()
  } catch (e: any) {
    ElMessage.error(e.message || '分配失败')
  }
}

async function handleStart(row: MarkingTaskVO) {
  try {
    await ElMessageBox.confirm('确定要开始此阅卷任务吗？开始后将生成阅卷记录。', '提示')
    await startMarkingTask(row.id)
    const accessRes = await generateAccessCode(row.id)
    if (currentTask.value?.id === row.id) {
      currentTask.value = accessRes.data
    }
    ElMessage.success('任务已开始，阅卷码已同步生成')
    loadData()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '操作失败')
    }
  }
}

async function handleComplete(row: MarkingTaskVO) {
  try {
    await ElMessageBox.confirm('确定要完成此阅卷任务吗？', '提示')
    await completeMarkingTask(row.id)
    ElMessage.success('任务已完成')
    loadData()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '操作失败')
    }
  }
}

async function handleDetail(row: MarkingTaskVO) {
  const res = await getMarkingTaskDetail(row.id)
  currentTask.value = res.data
  detailVisible.value = true
}

async function handleAccessCode(row: MarkingTaskVO) {
  const res = await getMarkingTaskDetail(row.id)
  currentTask.value = res.data
  accessCodeVisible.value = true
}

async function handleGenerateCode() {
  if (!currentTask.value) return
  try {
    const res = await generateAccessCode(currentTask.value.id)
    currentTask.value = res.data
    ElMessage.success('阅卷码已生成')
    loadData()
  } catch (e: any) {
    ElMessage.error(e.message || '生成失败')
  }
}

async function handleRefreshCode() {
  if (!currentTask.value) return
  try {
    await refreshAccessCode(currentTask.value.id, 24)
    const res = await getMarkingTaskDetail(currentTask.value.id)
    currentTask.value = res.data
    ElMessage.success('有效期已刷新')
    loadData()
  } catch (e: any) {
    ElMessage.error(e.message || '刷新失败')
  }
}

function copyAccessCode(code: string, label = '阅卷码') {
  navigator.clipboard.writeText(code).then(() => {
    ElMessage.success(`${label}已复制`)
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

function copyAccessUrl(code: string, label = '阅卷链接') {
  const url = `${accessBaseUrl}/marking?accessCode=${code}`
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success(`${label}已复制`)
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

function hasAnyAccessCode(task: MarkingTaskVO | null | undefined) {
  return Boolean(task?.accessCode || task?.secondAccessCode)
}

function getAccessCodeEntries(task: MarkingTaskVO | null | undefined) {
  if (!task) return []

  const entries: Array<{ label: string; code: string; role: number }> = []
  if (task.accessCode) {
    entries.push({
      label: task.enableDoubleMarking === 1 ? '一评阅卷码' : '阅卷码',
      code: task.accessCode,
      role: 1,
    })
  }
  if (task.enableDoubleMarking === 1 && task.secondAccessCode) {
    entries.push({
      label: '二评阅卷码',
      code: task.secondAccessCode,
      role: 2,
    })
  }
  return entries
}

async function handleDelete(row: MarkingTaskVO) {
  try {
    await ElMessageBox.confirm('确定要删除此阅卷任务吗？', '提示', { type: 'warning' })
    await deleteMarkingTask(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

function getProgress(row: MarkingTaskVO) {
  if (row.totalCount === 0) return 0
  return Math.round(row.completedCount / row.totalCount * 100)
}

function getProgressStatus(row: MarkingTaskVO) {
  const progress = getProgress(row)
  if (progress === 100) return 'success'
  if (progress > 0) return undefined
  return 'exception'
}

function getStatusType(status: number): 'info' | 'warning' | 'success' {
  switch (status) {
    case 0: return 'info'
    case 1: return 'warning'
    case 2: return 'success'
    default: return 'info'
  }
}

onMounted(async () => {
  await Promise.all([loadExams(), loadTeachers()])
  await syncRouteExam()
})

watch(() => [route.query.examId, route.query.examSubjectId], () => {
  syncRouteExam()
})

function getStatusText(status: number) {
  switch (status) {
    case 0: return '未开始'
    case 1: return '进行中'
    case 2: return '已完成'
    default: return '未知'
  }
}
</script>

<style scoped>
.marking-task-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

.progress-text {
  text-align: center;
  font-size: 12px;
  color: #909399;
}

.assign-info {
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.assign-info p {
  margin: 5px 0;
}

.access-code-tag {
  font-size: 16px;
  letter-spacing: 2px;
  font-family: monospace;
}

.detail-code-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.detail-code-item {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.detail-code-label {
  min-width: 72px;
  color: #606266;
}

.access-code-container {
  padding: 10px 0;
}

.access-code-container .task-info {
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
  margin-bottom: 20px;
}

.access-code-container .task-info p {
  margin: 4px 0;
  color: #606266;
}

.no-code {
  padding: 20px 0;
}

.code-display {
  text-align: center;
  padding: 20px 0;
}

.access-code-grid {
  display: grid;
  gap: 16px;
}

.code-card {
  border: 1px solid #ebeef5;
  border-radius: 12px;
  padding: 20px;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.code-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #606266;
}

.code-value {
  font-size: 36px;
  font-weight: bold;
  letter-spacing: 6px;
  color: #409eff;
  font-family: monospace;
  margin-bottom: 10px;
}

.code-expire {
  color: #909399;
  font-size: 13px;
  margin-bottom: 16px;
}

.code-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}

.code-toolbar {
  display: flex;
  justify-content: center;
}

.code-tips {
  background: #fdf6ec;
  padding: 12px 16px;
  border-radius: 6px;
  border: 1px solid #faecd8;
}

.code-tips h4 {
  margin: 0 0 8px;
  color: #e6a23c;
}

.code-tips ol {
  margin: 0;
  padding-left: 20px;
  color: #909399;
  font-size: 13px;
}

.code-tips li {
  margin: 4px 0;
}
</style>
