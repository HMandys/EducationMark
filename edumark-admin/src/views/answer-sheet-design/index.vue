<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="考试">
          <el-select v-model="queryParams.examId" placeholder="请选择考试" clearable filterable @change="handleExamChange">
            <el-option
              v-for="item in examList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="科目">
          <el-input v-model="queryParams.subjectName" placeholder="请输入科目名称" clearable />
        </el-form-item>
        <el-form-item label="模板名称">
          <el-input v-model="queryParams.name" placeholder="请输入模板名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 工具栏 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>答题卡模板列表</span>
          <div>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>新建模板
            </el-button>
          </div>
        </div>
      </template>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="tableData" row-key="id">
        <el-table-column prop="name" label="模板名称" min-width="200" />
        <el-table-column prop="examName" label="考试名称" width="180" />
        <el-table-column prop="subjectName" label="科目" width="100" />
        <el-table-column prop="paperName" label="试卷名称" width="150" />
        <el-table-column prop="pageSize" label="纸张" width="80" align="center" />
        <el-table-column prop="orientation" label="方向" width="80" align="center">
          <template #default="{ row }">
            {{ row.orientation === 1 ? '纵向' : '横向' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handlePublish(row)" v-if="row.status === 0">发布</el-button>
            <el-button type="primary" link @click="handlePreview(row)" v-if="row.status === 1">预览</el-button>
            <el-button type="primary" link @click="handleDownload(row)" v-if="row.status === 1">下载</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </el-card>

    <!-- 新建模板对话框 -->
    <el-dialog v-model="createDialogVisible" title="新建答题卡模板" width="500px" destroy-on-close>
      <el-form ref="createFormRef" :model="createForm" :rules="createFormRules" label-width="100px">
        <el-form-item label="选择考试" prop="examId">
          <el-select
            v-model="createForm.examId"
            placeholder="请选择考试"
            filterable
            @change="handleCreateExamChange"
            style="width: 100%"
          >
            <el-option
              v-for="item in examList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择科目" prop="subjectId">
          <el-select
            v-model="createForm.subjectId"
            placeholder="请选择科目"
            filterable
            @change="handleSubjectChange"
            style="width: 100%"
          >
            <el-option
              v-for="item in subjectList"
              :key="item.id"
              :label="item.subjectName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择试卷" prop="paperId">
          <el-select v-model="createForm.paperId" placeholder="请选择试卷" filterable style="width: 100%">
            <el-option
              v-for="item in paperList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="创建方式">
          <el-radio-group v-model="createForm.mode">
            <el-radio value="auto">自动生成</el-radio>
            <el-radio value="manual">手动创建</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="createLoading" @click="handleCreateSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- PDF预览对话框 -->
    <el-dialog v-model="previewDialogVisible" title="PDF预览" width="80%" destroy-on-close>
      <iframe v-if="previewUrl" :src="previewUrl" style="width: 100%; height: 70vh; border: none;"></iframe>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import {
  getTemplatePage,
  deleteTemplate,
  generateTemplateFromPaper,
  publishTemplate,
  getTemplatePreviewUrl,
  getTemplateDownloadUrl,
  type AnswerSheetTemplate,
} from '@/api/answerSheetTemplate'
import { getExamPage, getExamSubjectList, type Exam, type ExamSubject } from '@/api/exam'

interface Paper {
  id: number
  name: string
  examSubjectId: number
}

const router = useRouter()

// 下拉列表数据
const examList = ref<Exam[]>([])
const subjectList = ref<ExamSubject[]>([])
const paperList = ref<Paper[]>([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  examId: undefined as number | undefined,
  subjectName: '',
  name: '',
  status: undefined as number | undefined,
})

// 表格数据
const loading = ref(false)
const tableData = ref<AnswerSheetTemplate[]>([])
const total = ref(0)

// 新建对话框
const createDialogVisible = ref(false)
const createFormRef = ref<FormInstance>()
const createLoading = ref(false)
const createForm = reactive({
  examId: undefined as number | undefined,
  subjectId: undefined as number | undefined,
  paperId: undefined as number | undefined,
  mode: 'auto',
})

const createFormRules: FormRules = {
  examId: [{ required: true, message: '请选择考试', trigger: 'change' }],
  subjectId: [{ required: true, message: '请选择科目', trigger: 'change' }],
  paperId: [{ required: true, message: '请选择试卷', trigger: 'change' }],
}

// 预览对话框
const previewDialogVisible = ref(false)
const previewUrl = ref('')

// 加载考试列表
const loadExamList = async () => {
  const res = await getExamPage({ pageNum: 1, pageSize: 100 })
  examList.value = res.data.list
}

// 考试变化
const handleExamChange = async () => {
  queryParams.subjectName = ''
  handleSearch()
}

// 新建表单考试变化
const handleCreateExamChange = async (examId: number) => {
  createForm.subjectId = undefined
  createForm.paperId = undefined
  subjectList.value = []
  paperList.value = []
  if (examId) {
    const res = await getExamSubjectList(examId)
    subjectList.value = res.data
  }
}

// 科目变化
const handleSubjectChange = async (subjectId: number) => {
  createForm.paperId = undefined
  paperList.value = []
  if (subjectId) {
    // 获取该科目下的试卷列表
    // TODO: 调用获取试卷列表的API
    const subject = subjectList.value.find(s => s.id === subjectId)
    if (subject && subject.paperId) {
      paperList.value = [{
        id: subject.paperId,
        name: subject.subjectName + ' - 试卷',
        examSubjectId: subject.id,
      }]
    }
  }
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getTemplatePage(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1
  fetchData()
}

// 重置
const handleReset = () => {
  queryParams.examId = undefined
  queryParams.subjectName = ''
  queryParams.name = ''
  queryParams.status = undefined
  handleSearch()
}

// 新增
const handleAdd = () => {
  Object.assign(createForm, {
    examId: undefined,
    subjectId: undefined,
    paperId: undefined,
    mode: 'auto',
  })
  subjectList.value = []
  paperList.value = []
  createDialogVisible.value = true
}

// 新建提交
const handleCreateSubmit = async () => {
  await createFormRef.value?.validate()
  createLoading.value = true
  try {
    if (createForm.mode === 'auto') {
      // 自动生成
      const id = await generateTemplateFromPaper(createForm.paperId!)
      ElMessage.success('模板创建成功')
      createDialogVisible.value = false
      // 跳转到编辑页
      router.push(`/answer-sheet-design/edit/${id.data}`)
    } else {
      // 手动创建，跳转到编辑页
      router.push({
        path: '/answer-sheet-design/edit',
        query: { paperId: createForm.paperId },
      })
      createDialogVisible.value = false
    }
  } finally {
    createLoading.value = false
  }
}

// 编辑
const handleEdit = (row: AnswerSheetTemplate) => {
  router.push(`/answer-sheet-design/edit/${row.id}`)
}

// 发布
const handlePublish = async (row: AnswerSheetTemplate) => {
  await ElMessageBox.confirm(`确定要发布模板【${row.name}】吗？发布后将生成PDF文件。`, '提示', {
    type: 'warning',
  })
  await publishTemplate(row.id)
  ElMessage.success('发布成功')
  fetchData()
}

// 预览
const handlePreview = async (row: AnswerSheetTemplate) => {
  const res = await getTemplatePreviewUrl(row.id)
  previewUrl.value = res.data
  previewDialogVisible.value = true
}

// 下载
const handleDownload = async (row: AnswerSheetTemplate) => {
  const res = await getTemplateDownloadUrl(row.id)
  window.open(res.data, '_blank')
}

// 删除
const handleDelete = async (row: AnswerSheetTemplate) => {
  await ElMessageBox.confirm(`确定要删除模板【${row.name}】吗？`, '提示', {
    type: 'warning',
  })
  await deleteTemplate(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(() => {
  loadExamList()
  fetchData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 16px;
}
.table-card :deep(.el-card__header) {
  padding: 12px 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
