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
            <el-option label="识别中" :value="0" />
            <el-option label="已识别" :value="1" />
            <el-option label="待阅卷" :value="2" />
            <el-option label="阅卷中" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="识别异常" :value="5" />
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
          <div class="header-main">
            <div>
              <div class="header-title">扫描结果列表</div>
              <div class="header-desc">系统会先自动识别条码和学号，再把异常样本推入异常池人工处理。</div>
            </div>
            <div class="header-filters">
              <el-button :type="queryParams.status === undefined ? 'primary' : 'default'" plain @click="applyStatusFilter()">
                全部
              </el-button>
              <el-button :type="queryParams.status === 5 ? 'danger' : 'default'" plain @click="applyStatusFilter(5)">
                异常池
              </el-button>
              <el-button :type="queryParams.status === 2 ? 'warning' : 'default'" plain @click="applyStatusFilter(2)">
                待阅卷
              </el-button>
            </div>
          </div>
          <div class="header-actions">
            <el-button type="primary" @click="handleUpload">
              <el-icon><Upload /></el-icon>批量导入扫描图片
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
        <el-table-column label="学生姓名" width="100">
          <template #default="{ row }">
            {{ row.studentName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="学号" width="140">
          <template #default="{ row }">
            <span :class="{ 'pending-text': !row.studentNumber }">{{ row.studentNumber || '待识别' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="imageCount" label="图片数" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="识别结果" min-width="260" show-overflow-tooltip />
        <el-table-column prop="totalScore" label="总分" width="80" align="center">
          <template #default="{ row }">
            {{ row.totalScore ?? '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="上传时间" width="170" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link @click="handleQuestionDetails(row)">识别明细</el-button>
            <el-button type="success" link @click="handleObjectiveReview(row)">客观题复核</el-button>
            <el-button type="primary" link @click="handleAddImages(row)">补传</el-button>
            <el-button v-if="row.status === 5" type="info" link @click="handleRerunRecognition(row)">重新识别</el-button>
            <el-button v-if="row.status === 5" type="warning" link @click="handleResolve(row)">处理异常</el-button>
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
      :title="uploadDialogTitle"
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
        <el-alert
          title="导入后系统会自动识别条码、学号填涂区、模板区域、客观题区域和主观题裁题区。"
          type="info"
          :closable="false"
          show-icon
          class="upload-alert"
        />
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
        <el-button type="primary" :loading="uploadLoading" @click="handleUploadSubmit">
          {{ uploadMode === 'append' ? '确认补传' : '开始识别' }}
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="resolveDialogVisible"
      title="识别异常处理"
      width="860px"
      destroy-on-close
    >
      <div class="resolve-layout">
        <div class="resolve-panel">
          <div class="resolve-panel__title">识别结果</div>
          <el-form ref="resolveFormRef" :model="resolveForm" :rules="resolveFormRules" label-width="110px">
            <el-form-item label="当前结果">
              <div class="resolve-result">{{ resolveForm.remark || '未识别到有效学号，请人工确认。' }}</div>
            </el-form-item>
            <el-form-item label="候选学号">
              <el-tag :type="resolveForm.studentNumber ? 'warning' : 'info'">
                {{ resolveForm.studentNumber || '未识别到候选学号' }}
              </el-tag>
            </el-form-item>
            <el-form-item label="修正学号" prop="studentNumber">
              <el-input v-model="resolveForm.studentNumber" placeholder="请输入正确学号" />
            </el-form-item>
            <el-form-item label="座位号">
              <el-input v-model="resolveForm.seatNumber" placeholder="可选，补充座位号" />
            </el-form-item>
          </el-form>
        </div>
        <div class="resolve-panel">
          <div class="resolve-panel__title">扫描图片</div>
          <div v-if="resolveImages.length === 0" class="resolve-empty">暂无图片</div>
          <div v-else class="resolve-images">
            <div v-for="(image, index) in resolveImages" :key="image.id" class="resolve-image-item">
              <el-image
                :src="image.imageUrl"
                :preview-src-list="resolveImages.map(item => item.imageUrl)"
                :initial-index="index"
                fit="contain"
              />
              <div class="resolve-image-meta">
                <span>第 {{ image.pageNum }} 页</span>
                <span class="resolve-filename">{{ image.originalName || '未记录原始文件名' }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button :loading="rerunLoading" @click="handleResolveRerun">重新识别</el-button>
        <el-button @click="resolveDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="resolveLoading" @click="handleResolveSubmit">确认入库</el-button>
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

    <el-dialog
      v-model="questionDetailDialogVisible"
      :title="`识别明细 - ${questionDetailSheet?.studentName || questionDetailSheet?.studentNumber || ''}`"
      width="1180px"
      destroy-on-close
    >
      <div class="question-detail-layout">
        <div class="question-detail-table">
          <el-table
            v-loading="questionDetailsLoading"
            :data="questionDetails"
            height="520"
            stripe
            highlight-current-row
            @current-change="handleQuestionCurrentChange"
          >
            <el-table-column prop="questionNo" label="题号" width="90" />
            <el-table-column prop="questionTypeName" label="题型" width="110" />
            <el-table-column prop="regionRoleName" label="区域" width="120" />
            <el-table-column label="答案/状态" min-width="220">
              <template #default="{ row }">
                <div class="question-answer-cell">
                  <div v-if="row.studentAnswer">作答：{{ row.studentAnswer }}</div>
                  <div v-else class="question-answer-empty">尚未识别作答结果</div>
                  <div class="question-status-line">
                    <el-tag size="small" :type="row.status === 1 ? 'success' : row.isObjective === 1 ? 'warning' : 'info'">
                      {{ row.statusName || '待处理' }}
                    </el-tag>
                    <span v-if="row.score !== undefined && row.score !== null" class="question-score">得分 {{ row.score }} / {{ row.fullScore || '-' }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="预览" width="110" align="center">
              <template #default="{ row }">
                <el-button
                  type="primary"
                  link
                  :disabled="!row.previewAvailable"
                  @click.stop="openQuestionPreview(row)"
                >
                  查看题图
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="question-detail-preview">
          <div class="question-detail-preview__header">
            <div class="question-detail-preview__title">
              {{ activeQuestionDetail ? `题号 ${activeQuestionDetail.questionNo}` : '题图预览' }}
            </div>
            <div class="question-detail-preview__meta" v-if="activeQuestionDetail">
              {{ activeQuestionDetail.regionRoleName || '未配置区域' }}
              <span v-if="activeQuestionDetail.cropMode"> · {{ activeQuestionDetail.cropMode }}</span>
            </div>
          </div>

          <div v-loading="questionPreviewLoading" class="question-detail-preview__body">
            <el-empty
              v-if="!activeQuestionPreviewUrl && !questionPreviewLoading"
              description="选择题目后可查看裁题预览"
            />
            <el-image
              v-else
              :src="activeQuestionPreviewUrl"
              fit="contain"
              class="question-preview-image"
              :preview-src-list="activeQuestionPreviewUrl ? [activeQuestionPreviewUrl] : []"
            />
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
import { computed, ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadFile } from 'element-plus'
import { Search, Refresh, Upload, Delete, Plus } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import {
  getAnswerSheetPage,
  getAnswerSheetDetail,
  getAnswerSheetQuestionDetails,
  getAnswerSheetQuestionPreview,
  updateAnswerSheet,
  rerunAnswerSheetRecognition,
  deleteAnswerSheet,
  deleteAnswerSheetBatch,
  deleteAnswerSheetImage,
  uploadFile,
  uploadAnswerSheet,
  uploadAnswerSheetImages,
  type AnswerSheet,
  type AnswerSheetImage,
  type AnswerSheetQuestionDetail,
} from '@/api/answerSheet'
import { getExamPage, getExamSubjectList, type Exam, type ExamSubject } from '@/api/exam'

const route = useRoute()
const router = useRouter()

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
const uploadMode = ref<'create' | 'append'>('create')
const appendTargetId = ref<number>()

const uploadForm = reactive({
  examId: undefined as number | undefined,
  examSubjectId: undefined as number | undefined,
})

const uploadFormRules: FormRules = {
  examId: [{ required: true, message: '请选择考试', trigger: 'change' }],
  examSubjectId: [{ required: true, message: '请选择科目', trigger: 'change' }],
}

const uploadDialogTitle = computed(() => uploadMode.value === 'append' ? '补传扫描图片' : '批量导入扫描图片')

const resolveDialogVisible = ref(false)
const resolveFormRef = ref<FormInstance>()
const resolveLoading = ref(false)
const rerunLoading = ref(false)
const resolveImages = ref<AnswerSheetImage[]>([])
const resolveForm = reactive({
  id: undefined as number | undefined,
  examId: undefined as number | undefined,
  examSubjectId: undefined as number | undefined,
  studentNumber: '',
  seatNumber: '',
  remark: '',
})

const resolveFormRules: FormRules = {
  studentNumber: [{ required: true, message: '请输入正确学号', trigger: 'blur' }],
}

// 预览对话框
const previewDialogVisible = ref(false)
const currentAnswerSheet = ref<AnswerSheet | null>(null)
const currentImages = ref<AnswerSheetImage[]>([])
const questionDetailDialogVisible = ref(false)
const questionDetailsLoading = ref(false)
const questionPreviewLoading = ref(false)
const questionDetailSheet = ref<AnswerSheet | null>(null)
const questionDetails = ref<AnswerSheetQuestionDetail[]>([])
const activeQuestionDetail = ref<AnswerSheetQuestionDetail | null>(null)
const activeQuestionPreviewUrl = ref('')

// 图片预览
const imageViewerVisible = ref(false)
const previewImageUrl = ref('')

// 状态名称映射
const statusNames: Record<number, string> = {
  0: '识别中',
  1: '已识别',
  2: '待阅卷',
  3: '阅卷中',
  4: '已完成',
  5: '识别异常',
}

const getStatusName = (status: number) => statusNames[status] || '未知'

const getStatusTagType = (status: number): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
  const map: Record<number, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
    0: 'info',
    1: 'primary',
    2: 'warning',
    3: 'warning',
    4: 'success',
    5: 'danger',
  }
  return map[status] || 'info'
}

// 加载考试列表
const loadExamList = async () => {
  const res = await getExamPage({ pageNum: 1, pageSize: 100 })
  examList.value = res.data.list
}

const loadSubjectListByExam = async (examId?: number) => {
  subjectList.value = []
  if (!examId) {
    return
  }
  const res = await getExamSubjectList(examId)
  subjectList.value = res.data
}

// 处理考试变化
const handleExamChange = async (examId: number) => {
  queryParams.examSubjectId = undefined
  await loadSubjectListByExam(examId)
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

const applyStatusFilter = (status?: number) => {
  queryParams.status = status
  handleSearch()
}

// 选择变化
const handleSelectionChange = (rows: AnswerSheet[]) => {
  selectedIds.value = rows.map((row) => row.id)
}

// 打开上传对话框
const handleUpload = () => {
  uploadMode.value = 'create'
  appendTargetId.value = undefined
  uploadForm.examId = undefined
  uploadForm.examSubjectId = undefined
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
    if (uploadMode.value === 'append' && appendTargetId.value) {
      const formData = new FormData()
      for (const file of fileList.value) {
        if (file.raw) {
          formData.append('files', file.raw)
        }
      }
      await uploadAnswerSheetImages(appendTargetId.value, formData)
      ElMessage.success('补传成功')
    } else {
      const imageObjectNames: string[] = []
      const imageOriginalNames: string[] = []
      for (const file of fileList.value) {
        if (file.raw) {
          const res = await uploadFile(file.raw, 'answer-sheet')
          imageObjectNames.push(res.data.objectName)
          imageOriginalNames.push(res.data.originalName)
        }
      }

      await uploadAnswerSheet({
        examId: uploadForm.examId!,
        examSubjectId: uploadForm.examSubjectId!,
        imageObjectNames,
        imageOriginalNames,
      })
      ElMessage.success('导入成功，系统已自动开始识别')
    }

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

const handleQuestionDetails = async (row: AnswerSheet) => {
  questionDetailDialogVisible.value = true
  questionDetailsLoading.value = true
  questionDetailSheet.value = row
  activeQuestionDetail.value = null
  activeQuestionPreviewUrl.value = ''
  try {
    const res = await getAnswerSheetQuestionDetails(row.id)
    questionDetails.value = res.data || []
    const firstPreviewRow = questionDetails.value.find((item) => item.previewAvailable)
    if (firstPreviewRow) {
      await openQuestionPreview(firstPreviewRow)
    }
  } finally {
    questionDetailsLoading.value = false
  }
}

const handleObjectiveReview = (row: AnswerSheet) => {
  router.push({
    name: 'AnswerSheetObjectiveReview',
    params: { id: row.id },
    query: {
      examId: row.examId,
    },
  })
}

const handleQuestionCurrentChange = async (row?: AnswerSheetQuestionDetail) => {
  if (!row || !row.previewAvailable) {
    activeQuestionDetail.value = row || null
    activeQuestionPreviewUrl.value = ''
    return
  }
  await openQuestionPreview(row)
}

const openQuestionPreview = async (row: AnswerSheetQuestionDetail) => {
  if (!questionDetailSheet.value) {
    return
  }
  activeQuestionDetail.value = row
  if (!row.previewAvailable) {
    activeQuestionPreviewUrl.value = ''
    return
  }
  questionPreviewLoading.value = true
  try {
    const res = await getAnswerSheetQuestionPreview(questionDetailSheet.value.id, row.questionId)
    activeQuestionPreviewUrl.value = res.data || ''
  } catch (error: any) {
    activeQuestionPreviewUrl.value = ''
    ElMessage.error(error?.message || '加载题图预览失败')
  } finally {
    questionPreviewLoading.value = false
  }
}

// 补传图片
const handleAddImages = (row: AnswerSheet) => {
  uploadMode.value = 'append'
  appendTargetId.value = row.id
  uploadForm.examId = row.examId
  uploadForm.examSubjectId = row.examSubjectId
  fileList.value = []
  if (row.examId) {
    handleUploadExamChange(row.examId)
  }
  uploadDialogVisible.value = true
}

const fillResolveForm = (sheet: AnswerSheet) => {
  resolveForm.id = sheet.id
  resolveForm.examId = sheet.examId
  resolveForm.examSubjectId = sheet.examSubjectId
  resolveForm.studentNumber = sheet.studentNumber || ''
  resolveForm.seatNumber = sheet.seatNumber || ''
  resolveForm.remark = sheet.remark || ''
}

const handleResolve = async (row: AnswerSheet) => {
  const res = await getAnswerSheetDetail(row.id)
  fillResolveForm(res.data)
  resolveImages.value = res.data.images || []
  resolveDialogVisible.value = true
}

const handleResolveSubmit = async () => {
  await resolveFormRef.value?.validate()
  resolveLoading.value = true
  try {
    await updateAnswerSheet({
      id: resolveForm.id,
      examId: resolveForm.examId,
      examSubjectId: resolveForm.examSubjectId,
      studentNumber: resolveForm.studentNumber,
      seatNumber: resolveForm.seatNumber,
    })
    ElMessage.success('异常处理完成，已重新进入待阅卷')
    resolveDialogVisible.value = false
    fetchData()
  } finally {
    resolveLoading.value = false
  }
}

const handleRerunRecognition = async (row: AnswerSheet) => {
  rerunLoading.value = true
  try {
    await rerunAnswerSheetRecognition(row.id)
    ElMessage.success('已重新执行识别')
    await fetchData()
  } finally {
    rerunLoading.value = false
  }
}

const handleResolveRerun = async () => {
  if (!resolveForm.id) {
    return
  }
  rerunLoading.value = true
  try {
    await rerunAnswerSheetRecognition(resolveForm.id)
    const res = await getAnswerSheetDetail(resolveForm.id)
    fillResolveForm(res.data)
    resolveImages.value = res.data.images || []
    ElMessage.success('已重新识别，请确认结果')
    await fetchData()
  } finally {
    rerunLoading.value = false
  }
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

const syncRouteExam = async () => {
  const routeExamId = Number(route.query.examId)
  if (Number.isFinite(routeExamId) && routeExamId > 0) {
    queryParams.examId = routeExamId
    queryParams.examSubjectId = undefined
    await loadSubjectListByExam(routeExamId)
    await fetchData()
    return
  }

  queryParams.examId = undefined
  queryParams.examSubjectId = undefined
  subjectList.value = []
  await fetchData()
}

watch(() => route.query.examId, () => {
  syncRouteExam()
})

onMounted(async () => {
  await loadExamList()
  await syncRouteExam()
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
  gap: 16px;
}
.header-main {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.header-title {
  font-size: 16px;
  font-weight: 600;
}
.header-desc {
  color: #7a8699;
  font-size: 13px;
}
.header-filters,
.header-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
.pending-text {
  color: #909399;
}
.upload-alert {
  margin-bottom: 18px;
}
.resolve-layout {
  display: grid;
  grid-template-columns: minmax(280px, 320px) 1fr;
  gap: 18px;
}
.resolve-panel {
  min-width: 0;
}
.resolve-panel__title {
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 600;
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
.resolve-result {
  line-height: 1.7;
  color: #5b6472;
}
.resolve-empty {
  padding: 40px 0;
  color: #909399;
  text-align: center;
}
.resolve-images {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  max-height: 460px;
  overflow: auto;
}
.resolve-image-item {
  overflow: hidden;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
}
.resolve-image-item .el-image {
  width: 100%;
  height: 220px;
}
.resolve-image-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px 10px;
  background: #f7f8fa;
  font-size: 12px;
}
.resolve-filename {
  color: #7a8699;
  word-break: break-all;
}
.question-detail-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(320px, 0.8fr);
  gap: 16px;
}
.question-detail-table,
.question-detail-preview {
  min-width: 0;
}
.question-detail-preview {
  border: 1px solid #e4e7ed;
  border-radius: 10px;
  overflow: hidden;
  background: #fbfcfd;
}
.question-detail-preview__header {
  padding: 14px 16px;
  border-bottom: 1px solid #e4e7ed;
  background: #f5f7fa;
}
.question-detail-preview__title {
  font-size: 15px;
  font-weight: 600;
}
.question-detail-preview__meta {
  margin-top: 6px;
  color: #7a8699;
  font-size: 12px;
}
.question-detail-preview__body {
  min-height: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 18px;
}
.question-preview-image {
  width: 100%;
  max-height: 480px;
}
.question-answer-cell {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.question-answer-empty {
  color: #909399;
}
.question-status-line {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.question-score {
  color: #5b6472;
  font-size: 12px;
}
@media (max-width: 960px) {
  .resolve-layout {
    grid-template-columns: 1fr;
  }
  .question-detail-layout {
    grid-template-columns: 1fr;
  }
}
</style>
