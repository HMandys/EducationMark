<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="考试">
          <el-select
            v-model="queryParams.examId"
            placeholder="请选择考试"
            clearable
            filterable
            @change="handleExamChange"
          >
            <el-option
              v-for="item in examList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="科目">
          <el-select v-model="queryParams.examSubjectId" placeholder="请选择科目" clearable>
            <el-option
              v-for="item in subjectList"
              :key="item.id"
              :label="item.subjectName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="queryParams.studentNumber" placeholder="请输入学号" clearable />
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="待识别" :value="0" />
            <el-option label="已识别" :value="1" />
            <el-option label="待阅卷" :value="2" />
            <el-option label="阅卷中" :value="3" />
            <el-option label="已完成" :value="4" />
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
          <span>答题卡列表</span>
          <div>
            <el-button type="primary" @click="handleUpload">
              <el-icon><Upload /></el-icon>上传答题卡
            </el-button>
            <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
              <el-icon><Delete /></el-icon>批量删除
            </el-button>
          </div>
        </div>
      </template>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="handleSelectionChange"
        row-key="id"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="examName" label="考试名称" min-width="180" />
        <el-table-column prop="subjectName" label="科目" width="100" />
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <el-table-column prop="studentNumber" label="学号" width="120" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="imageCount" label="图片数" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总分" width="80" align="center">
          <template #default="{ row }">
            {{ row.totalScore ?? '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="上传时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link @click="handleAddImages(row)">补传</el-button>
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

    <!-- 上传对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传答题卡"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="uploadFormRef"
        :model="uploadForm"
        :rules="uploadFormRules"
        label-width="100px"
      >
        <el-form-item label="考试" prop="examId">
          <el-select
            v-model="uploadForm.examId"
            placeholder="请选择考试"
            filterable
            @change="handleUploadExamChange"
          >
            <el-option
              v-for="item in examList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="科目" prop="examSubjectId">
          <el-select v-model="uploadForm.examSubjectId" placeholder="请选择科目">
            <el-option
              v-for="item in uploadSubjectList"
              :key="item.id"
              :label="item.subjectName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学号" prop="studentNumber">
          <el-input v-model="uploadForm.studentNumber" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="座位号">
          <el-input v-model="uploadForm.seatNumber" placeholder="请输入座位号" />
        </el-form-item>
        <el-form-item label="答题卡图片" prop="files">
          <el-upload
            ref="uploadRef"
            v-model:file-list="fileList"
            :auto-upload="false"
            :multiple="true"
            accept="image/*"
            list-type="picture-card"
            :on-preview="handlePreview"
            :on-remove="handleRemove"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploadLoading" @click="handleUploadSubmit">确定上传</el-button>
      </template>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      :title="`答题卡图片 - ${currentAnswerSheet?.studentName || ''}`"
      width="900px"
      destroy-on-close
    >
      <div class="image-preview-container">
        <div v-if="currentImages.length === 0" class="no-images">暂无图片</div>
        <div v-else class="image-list">
          <div
            v-for="(image, index) in currentImages"
            :key="image.id"
            class="image-item"
          >
            <el-image
              :src="image.imageUrl"
              :preview-src-list="currentImages.map(i => i.imageUrl)"
              :initial-index="index"
              fit="contain"
            />
            <div class="image-info">
              <span>第 {{ image.pageNum }} 页</span>
              <el-button type="danger" link size="small" @click="handleDeleteImage(image)">
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="imageViewerVisible"
      :url-list="[previewImageUrl]"
      @close="imageViewerVisible = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadFile } from 'element-plus'
import { Search, Refresh, Upload, Delete, Plus } from '@element-plus/icons-vue'
import {
  getAnswerSheetPage,
  getAnswerSheetDetail,
  deleteAnswerSheet,
  deleteAnswerSheetBatch,
  deleteAnswerSheetImage,
  uploadFile,
  uploadAnswerSheet,
  type AnswerSheet,
  type AnswerSheetImage,
} from '@/api/answerSheet'
import { getExamPage, getExamSubjectList, type Exam, type ExamSubject } from '@/api/exam'

// 下拉列表数据
const examList = ref<Exam[]>([])
const subjectList = ref<ExamSubject[]>([])
const uploadSubjectList = ref<ExamSubject[]>([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  examId: undefined as number | undefined,
  examSubjectId: undefined as number | undefined,
  studentNumber: '',
  studentName: '',
  status: undefined as number | undefined,
})

// 表格数据
const loading = ref(false)
const tableData = ref<AnswerSheet[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

// 上传对话框
const uploadDialogVisible = ref(false)
const uploadFormRef = ref<FormInstance>()
const uploadLoading = ref(false)
const uploadRef = ref()
const fileList = ref<UploadFile[]>([])

const uploadForm = reactive({
  examId: undefined as number | undefined,
  examSubjectId: undefined as number | undefined,
  studentNumber: '',
  seatNumber: '',
})

const uploadFormRules: FormRules = {
  examId: [{ required: true, message: '请选择考试', trigger: 'change' }],
  examSubjectId: [{ required: true, message: '请选择科目', trigger: 'change' }],
  studentNumber: [{ required: true, message: '请输入学号', trigger: 'blur' }],
}

// 预览对话框
const previewDialogVisible = ref(false)
const currentAnswerSheet = ref<AnswerSheet | null>(null)
const currentImages = ref<AnswerSheetImage[]>([])

// 图片预览
const imageViewerVisible = ref(false)
const previewImageUrl = ref('')

// 状态名称映射
const statusNames: Record<number, string> = {
  0: '待识别',
  1: '已识别',
  2: '待阅卷',
  3: '阅卷中',
  4: '已完成',
}

const getStatusName = (status: number) => statusNames[status] || '未知'

const getStatusTagType = (status: number): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
  const map: Record<number, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
    0: 'info',
    1: 'primary',
    2: 'warning',
    3: 'warning',
    4: 'success',
  }
  return map[status] || 'info'
}

// 加载考试列表
const loadExamList = async () => {
  const res = await getExamPage({ pageNum: 1, pageSize: 100 })
  examList.value = res.data.list
}

// 处理考试变化
const handleExamChange = async (examId: number) => {
  queryParams.examSubjectId = undefined
  subjectList.value = []
  if (examId) {
    const res = await getExamSubjectList(examId)
    subjectList.value = res.data
  }
  handleSearch()
}

// 处理上传对话框考试变化
const handleUploadExamChange = async (examId: number) => {
  uploadForm.examSubjectId = undefined
  uploadSubjectList.value = []
  if (examId) {
    const res = await getExamSubjectList(examId)
    uploadSubjectList.value = res.data
  }
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAnswerSheetPage(queryParams)
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
  queryParams.examSubjectId = undefined
  queryParams.studentNumber = ''
  queryParams.studentName = ''
  queryParams.status = undefined
  subjectList.value = []
  handleSearch()
}

// 选择变化
const handleSelectionChange = (rows: AnswerSheet[]) => {
  selectedIds.value = rows.map((row) => row.id)
}

// 打开上传对话框
const handleUpload = () => {
  uploadForm.examId = undefined
  uploadForm.examSubjectId = undefined
  uploadForm.studentNumber = ''
  uploadForm.seatNumber = ''
  fileList.value = []
  uploadSubjectList.value = []
  uploadDialogVisible.value = true
}

// 处理图片预览
const handlePreview = (file: UploadFile) => {
  previewImageUrl.value = file.url || ''
  imageViewerVisible.value = true
}

// 处理图片移除
const handleRemove = () => {
  // 文件列表会自动更新
}

// 提交上传
const handleUploadSubmit = async () => {
  await uploadFormRef.value?.validate()
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择要上传的图片')
    return
  }

  uploadLoading.value = true
  try {
    // 先上传图片
    const imageObjectNames: string[] = []
    for (const file of fileList.value) {
      if (file.raw) {
        const res = await uploadFile(file.raw, 'answer-sheet')
        imageObjectNames.push(res.data.objectName)
      }
    }

    // 创建答题卡
    await uploadAnswerSheet({
      examId: uploadForm.examId!,
      examSubjectId: uploadForm.examSubjectId!,
      studentNumber: uploadForm.studentNumber,
      seatNumber: uploadForm.seatNumber,
      imageObjectNames,
    })

    ElMessage.success('上传成功')
    uploadDialogVisible.value = false
    fetchData()
  } finally {
    uploadLoading.value = false
  }
}

// 查看答题卡
const handleView = async (row: AnswerSheet) => {
  const res = await getAnswerSheetDetail(row.id)
  currentAnswerSheet.value = res.data
  currentImages.value = res.data.images || []
  previewDialogVisible.value = true
}

// 补传图片
const handleAddImages = (row: AnswerSheet) => {
  // 使用与上传相同的对话框，但预填信息
  uploadForm.examId = row.examId
  uploadForm.examSubjectId = row.examSubjectId
  uploadForm.studentNumber = row.studentNumber || ''
  uploadForm.seatNumber = row.seatNumber || ''
  fileList.value = []
  if (row.examId) {
    handleUploadExamChange(row.examId)
  }
  uploadDialogVisible.value = true
}

// 删除答题卡图片
const handleDeleteImage = async (image: AnswerSheetImage) => {
  await ElMessageBox.confirm('确定要删除这张图片吗？', '提示', { type: 'warning' })
  await deleteAnswerSheetImage(image.id)
  ElMessage.success('删除成功')
  // 重新加载图片列表
  if (currentAnswerSheet.value) {
    const res = await getAnswerSheetDetail(currentAnswerSheet.value.id)
    currentImages.value = res.data.images || []
  }
}

// 删除答题卡
const handleDelete = async (row: AnswerSheet) => {
  await ElMessageBox.confirm(`确定要删除该答题卡吗？`, '提示', { type: 'warning' })
  await deleteAnswerSheet(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

// 批量删除
const handleBatchDelete = async () => {
  await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个答题卡吗？`, '提示', { type: 'warning' })
  await deleteAnswerSheetBatch(selectedIds.value)
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
.image-preview-container {
  min-height: 300px;
}
.no-images {
  text-align: center;
  color: #999;
  padding: 50px 0;
}
.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}
.image-item {
  width: 200px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}
.image-item .el-image {
  width: 100%;
  height: 280px;
}
.image-info {
  padding: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f5f7fa;
}
</style>
