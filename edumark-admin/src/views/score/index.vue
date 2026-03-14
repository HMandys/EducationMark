<template>
  <div class="score-container">
    <!-- 搜索表单 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="考试">
          <el-select
            v-model="queryForm.examId"
            placeholder="请选择考试"
            clearable
            filterable
            style="width: 220px"
            @change="handleExamChange"
          >
            <el-option
              v-for="exam in examList"
              :key="exam.id"
              :label="exam.name"
              :value="exam.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="科目">
          <el-select
            v-model="queryForm.examSubjectId"
            placeholder="请选择科目"
            clearable
            style="width: 150px"
          >
            <el-option label="全部科目(总分)" :value="undefined" />
            <el-option
              v-for="subject in subjectList"
              :key="subject.id"
              :label="subject.subjectName"
              :value="subject.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select
            v-model="queryForm.classId"
            placeholder="请选择班级"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="cls in classList"
              :key="cls.classId"
              :label="cls.className"
              :value="cls.classId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input
            v-model="queryForm.studentName"
            placeholder="请输入学生姓名"
            clearable
            style="width: 140px"
          />
        </el-form-item>
        <el-form-item label="学号">
          <el-input
            v-model="queryForm.studentNumber"
            placeholder="请输入学号"
            clearable
            style="width: 140px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 功能区 -->
    <el-card shadow="never" style="margin-top: 16px">
      <template #header>
        <div class="card-header">
          <el-radio-group v-model="viewMode" @change="handleViewModeChange">
            <el-radio-button value="score">成绩列表</el-radio-button>
            <el-radio-button value="statistics">统计分析</el-radio-button>
          </el-radio-group>
          <div class="header-actions">
            <el-button
              v-if="currentExam && currentExam.status === 4"
              type="success"
              :icon="Upload"
              @click="handlePublish"
            >
              发布成绩
            </el-button>
            <el-button
              v-if="currentExam && currentExam.status === 5"
              type="warning"
              :icon="Download"
              @click="handleUnpublish"
            >
              撤回成绩
            </el-button>
            <el-button :icon="Download" @click="handleExport" :disabled="!queryForm.examId">
              导出Excel
            </el-button>
          </div>
        </div>
      </template>

      <!-- 成绩列表视图 -->
      <template v-if="viewMode === 'score'">
        <!-- 总分成绩表格 -->
        <el-table
          v-if="!queryForm.examSubjectId"
          v-loading="loading"
          :data="examScoreList"
          stripe
          border
        >
          <el-table-column prop="studentName" label="学生姓名" width="120" />
          <el-table-column prop="studentNumber" label="学号" width="120" />
          <el-table-column prop="className" label="班级" width="120" />
          <el-table-column prop="totalScore" label="总分" width="100" align="center">
            <template #default="{ row }">
              <span class="score-value">{{ row.totalScore }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="subjectCount" label="科目数" width="80" align="center" />
          <el-table-column prop="classRank" label="班级排名" width="100" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.classRank <= 3" type="success">{{ row.classRank }}</el-tag>
              <span v-else>{{ row.classRank }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="gradeRank" label="年级排名" width="100" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.gradeRank <= 10" type="warning">{{ row.gradeRank }}</el-tag>
              <span v-else>{{ row.gradeRank }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleViewDetail(row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 科目成绩表格 -->
        <el-table
          v-else
          v-loading="loading"
          :data="subjectScoreList"
          stripe
          border
        >
          <el-table-column prop="studentName" label="学生姓名" width="120" />
          <el-table-column prop="studentNumber" label="学号" width="120" />
          <el-table-column prop="className" label="班级" width="120" />
          <el-table-column prop="subjectName" label="科目" width="100" />
          <el-table-column label="分数" width="140" align="center">
            <template #default="{ row }">
              <span class="score-value">{{ row.score }}</span>
              <span class="score-full"> / {{ row.fullScore }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="objectiveScore" label="客观题" width="80" align="center" />
          <el-table-column prop="subjectiveScore" label="主观题" width="80" align="center" />
          <el-table-column prop="classRank" label="班级排名" width="100" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.classRank <= 3" type="success">{{ row.classRank }}</el-tag>
              <span v-else>{{ row.classRank }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="gradeRank" label="年级排名" width="100" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.gradeRank <= 10" type="warning">{{ row.gradeRank }}</el-tag>
              <span v-else>{{ row.gradeRank }}</span>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="queryForm.pageNum"
            v-model:page-size="queryForm.pageSize"
            :total="total"
            :page-sizes="[20, 50, 100, 200]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSearch"
            @current-change="handleSearch"
          />
        </div>
      </template>

      <!-- 统计分析视图 -->
      <template v-else>
        <div class="statistics-container">
          <!-- 汇总统计卡片 -->
          <el-row :gutter="16" class="stat-cards">
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card">
                <el-statistic title="参考人数" :value="gradeStats?.studentCount || 0" />
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card">
                <el-statistic title="平均分" :value="gradeStats?.avgScore || 0" :precision="1" />
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card">
                <el-statistic title="及格率" :value="gradeStats?.passRate || 0" suffix="%" :precision="1" />
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card">
                <el-statistic title="优秀率" :value="gradeStats?.excellentRate || 0" suffix="%" :precision="1" />
              </el-card>
            </el-col>
          </el-row>

          <!-- 分数分布图 -->
          <el-row :gutter="16" style="margin-top: 20px">
            <el-col :span="12">
              <el-card shadow="hover">
                <template #header>分数段分布</template>
                <div ref="segmentChartRef" style="height: 300px" />
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card shadow="hover">
                <template #header>班级对比</template>
                <div ref="classCompareChartRef" style="height: 300px" />
              </el-card>
            </el-col>
          </el-row>

          <!-- 详细统计表格 -->
          <el-card shadow="hover" style="margin-top: 20px">
            <template #header>详细统计</template>
            <el-table :data="statisticsList" stripe border>
              <el-table-column prop="subjectName" label="科目" width="120">
                <template #default="{ row }">
                  {{ row.subjectName || (row.statType === 4 ? '总分' : '班级总分') }}
                </template>
              </el-table-column>
              <el-table-column prop="className" label="班级" width="120">
                <template #default="{ row }">
                  {{ row.className || '年级' }}
                </template>
              </el-table-column>
              <el-table-column prop="studentCount" label="人数" width="80" align="center" />
              <el-table-column prop="fullScore" label="满分" width="80" align="center" />
              <el-table-column prop="maxScore" label="最高分" width="80" align="center" />
              <el-table-column prop="minScore" label="最低分" width="80" align="center" />
              <el-table-column prop="avgScore" label="平均分" width="80" align="center" />
              <el-table-column prop="passCount" label="及格人数" width="90" align="center" />
              <el-table-column label="及格率" width="80" align="center">
                <template #default="{ row }">
                  {{ row.passRate }}%
                </template>
              </el-table-column>
              <el-table-column prop="excellentCount" label="优秀人数" width="90" align="center" />
              <el-table-column label="优秀率" width="80" align="center">
                <template #default="{ row }">
                  {{ row.excellentRate }}%
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </template>
    </el-card>

    <!-- 学生成绩详情弹窗 -->
    <el-dialog v-model="detailVisible" title="学生成绩详情" width="700px">
      <template v-if="currentStudent">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="学生姓名">{{ currentStudent.studentName }}</el-descriptions-item>
          <el-descriptions-item label="学号">{{ currentStudent.studentNumber }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{ currentStudent.className }}</el-descriptions-item>
          <el-descriptions-item label="总分">
            <span class="score-value">{{ currentStudent.totalScore }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="班级排名">{{ currentStudent.classRank }}</el-descriptions-item>
          <el-descriptions-item label="年级排名">{{ currentStudent.gradeRank }}</el-descriptions-item>
        </el-descriptions>

        <el-table :data="currentStudent.subjectScores" stripe style="margin-top: 16px">
          <el-table-column prop="subjectName" label="科目" width="120" />
          <el-table-column label="分数" width="120" align="center">
            <template #default="{ row }">
              {{ row.score }} / {{ row.fullScore }}
            </template>
          </el-table-column>
          <el-table-column prop="objectiveScore" label="客观题" width="80" align="center" />
          <el-table-column prop="subjectiveScore" label="主观题" width="80" align="center" />
          <el-table-column prop="classRank" label="班级排名" width="100" align="center" />
          <el-table-column prop="gradeRank" label="年级排名" width="100" align="center" />
        </el-table>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Download, Upload } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import * as echarts from 'echarts'
import {
  getExamScorePage,
  getSubjectScorePage,
  getStudentExamScore,
  getScoreStatistics,
  publishScore,
  unpublishScore,
  exportScoreExcel,
  type ExamScore,
  type SubjectScore,
  type ScoreStatistics,
} from '@/api/score'
import { getExamPage, getExamDetail, getExamSubjectList, type Exam, type ExamSubject, type ExamClass } from '@/api/exam'

const userStore = useUserStore()

// 查询表单
const queryForm = reactive({
  pageNum: 1,
  pageSize: 20,
  examId: undefined as number | undefined,
  examSubjectId: undefined as number | undefined,
  classId: undefined as number | undefined,
  studentName: '',
  studentNumber: '',
})

// 数据
const loading = ref(false)
const total = ref(0)
const viewMode = ref<'score' | 'statistics'>('score')
const examList = ref<Exam[]>([])
const subjectList = ref<ExamSubject[]>([])
const classList = ref<ExamClass[]>([])
const currentExam = ref<Exam | null>(null)
const examScoreList = ref<ExamScore[]>([])
const subjectScoreList = ref<SubjectScore[]>([])
const statisticsList = ref<ScoreStatistics[]>([])
const gradeStats = ref<ScoreStatistics | null>(null)

// 详情弹窗
const detailVisible = ref(false)
const currentStudent = ref<ExamScore | null>(null)

// 图表引用
const segmentChartRef = ref<HTMLElement>()
const classCompareChartRef = ref<HTMLElement>()
let segmentChart: echarts.ECharts | null = null
let classCompareChart: echarts.ECharts | null = null

// 加载考试列表
const loadExamList = async () => {
  try {
    const res = await getExamPage({ pageNum: 1, pageSize: 100, status: 5 }) // 已发布成绩的考试
    examList.value = res.data.records

    // 同时加载阅卷完成待发布的考试
    const res2 = await getExamPage({ pageNum: 1, pageSize: 100, status: 4 })
    examList.value = [...examList.value, ...res2.data.records]
  } catch (error) {
    console.error('加载考试列表失败', error)
  }
}

// 考试切换处理
const handleExamChange = async () => {
  if (queryForm.examId) {
    try {
      const res = await getExamDetail(queryForm.examId)
      currentExam.value = res.data
      classList.value = res.data.classes || []

      const subjectRes = await getExamSubjectList(queryForm.examId)
      subjectList.value = subjectRes.data
    } catch (error) {
      console.error('加载考试详情失败', error)
    }
  } else {
    currentExam.value = null
    subjectList.value = []
    classList.value = []
  }

  queryForm.examSubjectId = undefined
  queryForm.classId = undefined
  handleSearch()
}

// 搜索
const handleSearch = async () => {
  if (!queryForm.examId) {
    examScoreList.value = []
    subjectScoreList.value = []
    total.value = 0
    return
  }

  loading.value = true
  try {
    if (queryForm.examSubjectId) {
      // 查询科目成绩
      const res = await getSubjectScorePage(queryForm)
      subjectScoreList.value = res.data.records
      total.value = res.data.total
    } else {
      // 查询总分成绩
      const res = await getExamScorePage(queryForm)
      examScoreList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('查询成绩失败', error)
  } finally {
    loading.value = false
  }
}

// 重置
const handleReset = () => {
  queryForm.pageNum = 1
  queryForm.examSubjectId = undefined
  queryForm.classId = undefined
  queryForm.studentName = ''
  queryForm.studentNumber = ''
  handleSearch()
}

// 视图模式切换
const handleViewModeChange = async () => {
  if (viewMode.value === 'statistics' && queryForm.examId) {
    await loadStatistics()
  }
}

// 加载统计数据
const loadStatistics = async () => {
  if (!queryForm.examId) return

  try {
    const res = await getScoreStatistics(queryForm.examId, queryForm.examSubjectId, queryForm.classId)
    statisticsList.value = res.data

    // 找到年级总分统计
    gradeStats.value = res.data.find(s => s.statType === 4) || res.data.find(s => s.statType === 3) || null

    await nextTick()
    renderCharts()
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

// 渲染图表
const renderCharts = () => {
  // 分数段分布图
  if (segmentChartRef.value) {
    if (!segmentChart) {
      segmentChart = echarts.init(segmentChartRef.value)
    }

    const segments = gradeStats.value?.segmentList || []
    segmentChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: segments.map(s => s.range),
      },
      yAxis: { type: 'value', name: '人数' },
      series: [{
        type: 'bar',
        data: segments.map(s => s.count),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409EFF' },
            { offset: 1, color: '#67C23A' },
          ]),
        },
      }],
    })
  }

  // 班级对比图
  if (classCompareChartRef.value) {
    if (!classCompareChart) {
      classCompareChart = echarts.init(classCompareChartRef.value)
    }

    // 获取班级统计数据
    const classStats = statisticsList.value.filter(s => s.statType === 2 || (s.statType === 1 && s.classId))
    const classNames = [...new Set(classStats.map(s => s.className))].filter(Boolean) as string[]

    classCompareChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['平均分', '及格率', '优秀率'] },
      xAxis: {
        type: 'category',
        data: classNames,
      },
      yAxis: [
        { type: 'value', name: '分数', position: 'left' },
        { type: 'value', name: '百分比', position: 'right', max: 100 },
      ],
      series: [
        {
          name: '平均分',
          type: 'bar',
          data: classNames.map(name => {
            const stat = classStats.find(s => s.className === name && (s.statType === 2 || !s.examSubjectId))
            return stat?.avgScore || 0
          }),
        },
        {
          name: '及格率',
          type: 'line',
          yAxisIndex: 1,
          data: classNames.map(name => {
            const stat = classStats.find(s => s.className === name && (s.statType === 2 || !s.examSubjectId))
            return stat?.passRate || 0
          }),
        },
        {
          name: '优秀率',
          type: 'line',
          yAxisIndex: 1,
          data: classNames.map(name => {
            const stat = classStats.find(s => s.className === name && (s.statType === 2 || !s.examSubjectId))
            return stat?.excellentRate || 0
          }),
        },
      ],
    })
  }
}

// 查看学生成绩详情
const handleViewDetail = async (row: ExamScore) => {
  try {
    const res = await getStudentExamScore(queryForm.examId!, row.studentId)
    currentStudent.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error('加载学生成绩详情失败', error)
  }
}

// 发布成绩
const handlePublish = async () => {
  await ElMessageBox.confirm('确定要发布成绩吗？发布后学生和家长可以查看成绩。', '提示', { type: 'warning' })

  try {
    await publishScore(queryForm.examId!, userStore.userInfo?.id || 0)
    ElMessage.success('发布成功')
    await loadExamList()
    await handleExamChange()
  } catch (error) {
    console.error('发布失败', error)
  }
}

// 撤回成绩
const handleUnpublish = async () => {
  await ElMessageBox.confirm('确定要撤回成绩吗？撤回后学生和家长将无法查看成绩。', '提示', { type: 'warning' })

  try {
    await unpublishScore(queryForm.examId!, userStore.userInfo?.id || 0)
    ElMessage.success('撤回成功')
    await loadExamList()
    await handleExamChange()
  } catch (error) {
    console.error('撤回失败', error)
  }
}

// 导出Excel
const handleExport = async () => {
  try {
    const res = await exportScoreExcel(queryForm.examId!, queryForm.classId)
    const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '成绩表.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败', error)
  }
}

// 监听视图模式和查询条件变化
watch([() => viewMode.value, () => queryForm.examSubjectId, () => queryForm.classId], () => {
  if (viewMode.value === 'statistics' && queryForm.examId) {
    loadStatistics()
  }
})

onMounted(() => {
  loadExamList()
})
</script>

<style scoped lang="scss">
.score-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.score-value {
  font-weight: bold;
  font-size: 16px;
  color: #409EFF;
}

.score-full {
  color: #909399;
  font-size: 12px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.statistics-container {
  .stat-cards {
    margin-bottom: 20px;
  }

  .stat-card {
    text-align: center;

    :deep(.el-statistic__head) {
      font-size: 14px;
      color: #909399;
    }

    :deep(.el-statistic__number) {
      font-size: 28px;
      font-weight: bold;
    }
  }
}
</style>
