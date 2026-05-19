<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="所属学校">
          <el-select v-model="queryParams.schoolId" placeholder="请选择学校" clearable filterable>
            <el-option
              v-for="item in schoolList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="考试名称">
          <el-input v-model="queryParams.name" placeholder="请输入考试名称" clearable />
        </el-form-item>
        <el-form-item label="考试类型">
          <el-select v-model="queryParams.type" placeholder="请选择" clearable>
            <el-option label="期中考试" :value="1" />
            <el-option label="期末考试" :value="2" />
            <el-option label="月考" :value="3" />
            <el-option label="模拟考试" :value="4" />
            <el-option label="其他" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="学年">
          <el-select v-model="queryParams.academicYear" placeholder="请选择学年" clearable>
            <el-option
              v-for="item in academicYearOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学期">
          <el-select v-model="queryParams.semester" placeholder="请选择" clearable>
            <el-option label="第一学期" :value="1" />
            <el-option label="第二学期" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="待考试" :value="1" />
            <el-option label="考试中" :value="2" />
            <el-option label="阅卷中" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已发布" :value="5" />
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
          <span>考试列表</span>
          <div>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>新增
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
        <el-table-column prop="name" label="考试名称" min-width="200" />
        <el-table-column prop="schoolName" label="所属学校" width="150" />
        <el-table-column prop="type" label="考试类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">{{ getTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="academicYear" label="学年" width="110" />
        <el-table-column prop="semester" label="学期" width="90">
          <template #default="{ row }">
            {{ row.semester === 1 ? '第一学期' : '第二学期' }}
          </template>
        </el-table-column>
        <el-table-column prop="gradeName" label="年级" width="100" />
        <el-table-column prop="subjectCount" label="科目数" width="80" align="center" />
        <el-table-column prop="totalScore" label="总分" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :disabled="row.status >= 2" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleWorkbench(row)">工作台</el-button>
            <el-button type="primary" link @click="handleSubjects(row)">科目</el-button>
            <el-button type="info" link @click="handleCheckPublish(row)">
              {{ row.status >= 4 ? '出分检查' : '检查' }}
            </el-button>
            <el-button type="danger" link :disabled="row.status >= 2" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属学校" prop="schoolId">
              <el-select
                v-model="formData.schoolId"
                placeholder="请选择学校"
                filterable
                @change="handleSchoolChange"
              >
                <el-option
                  v-for="item in schoolList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="考试名称" prop="name">
              <el-input v-model="formData.name" placeholder="请输入考试名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="考试编码">
              <el-input v-model="formData.code" placeholder="请输入考试编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="考试类型" prop="type">
              <el-select v-model="formData.type" placeholder="请选择考试类型">
                <el-option label="期中考试" :value="1" />
                <el-option label="期末考试" :value="2" />
                <el-option label="月考" :value="3" />
                <el-option label="模拟考试" :value="4" />
                <el-option label="其他" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学年" prop="academicYear">
              <el-select v-model="formData.academicYear" placeholder="请选择学年">
                <el-option
                  v-for="item in academicYearOptions"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学期" prop="semester">
              <el-select v-model="formData.semester" placeholder="请选择学期">
                <el-option label="第一学期" :value="1" />
                <el-option label="第二学期" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年级" prop="gradeId">
              <el-select v-model="formData.gradeId" placeholder="请选择年级" clearable @change="handleGradeChange">
                <el-option
                  v-for="item in gradeList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="formData.startTime"
                type="datetime"
                placeholder="选择开始时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="formData.endTime"
                type="datetime"
                placeholder="选择结束时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="参考班级" prop="classIds">
          <el-select
            v-model="formData.classIds"
            multiple
            :disabled="!formData.gradeId"
            :placeholder="formData.gradeId ? '请选择参考班级' : '请先选择年级'"
            style="width: 100%"
          >
            <el-option
              v-for="item in classList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-alert
          title="考试总分将根据已配置科目自动汇总，无需手工填写。"
          type="info"
          :closable="false"
          show-icon
        />
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="2" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 科目管理对话框 -->
    <el-dialog
      v-model="subjectDialogVisible"
      :title="`科目管理 - ${currentExam?.name || ''}`"
      width="900px"
      destroy-on-close
    >
      <div class="subject-toolbar">
        <el-button type="primary" size="small" @click="handleAddSubject">
          <el-icon><Plus /></el-icon>添加科目
        </el-button>
      </div>
      <el-table :data="subjectList" v-loading="subjectLoading">
        <el-table-column prop="subjectName" label="科目名称" width="120" />
        <el-table-column prop="subjectCode" label="科目编码" width="100" />
        <el-table-column prop="fullScore" label="满分" width="80" align="center" />
        <el-table-column prop="passScore" label="及格分" width="80" align="center" />
        <el-table-column prop="excellentScore" label="优秀分" width="80" align="center" />
        <el-table-column prop="duration" label="时长(分钟)" width="100" align="center" />
        <el-table-column prop="questionCount" label="题目数" width="80" align="center" />
        <el-table-column prop="sort" label="排序" width="70" align="center" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEditSubject(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDeleteSubject(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 科目编辑对话框 -->
    <el-dialog
      v-model="subjectFormVisible"
      :title="subjectFormTitle"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="subjectFormRef"
        :model="subjectFormData"
        :rules="subjectFormRules"
        label-width="100px"
      >
        <el-form-item label="科目名称" prop="subjectName">
          <el-input v-model="subjectFormData.subjectName" placeholder="请输入科目名称" />
        </el-form-item>
        <el-form-item label="科目编码">
          <el-input v-model="subjectFormData.subjectCode" placeholder="请输入科目编码" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="满分" prop="fullScore">
              <el-input-number v-model="subjectFormData.fullScore" :min="0" :max="300" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="及格分">
              <el-input-number v-model="subjectFormData.passScore" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="优秀分">
              <el-input-number v-model="subjectFormData.excellentScore" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="时长(分钟)">
              <el-input-number v-model="subjectFormData.duration" :min="0" :max="300" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="排序">
          <el-input-number v-model="subjectFormData.sort" :min="0" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="subjectFormData.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="subjectFormVisible = false">取消</el-button>
        <el-button type="primary" :loading="subjectSubmitLoading" @click="handleSubjectSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import {
  getExamPage,
  getExamDetail,
  getExamPublishCheck,
  createExam,
  updateExam,
  deleteExam,
  deleteExamBatch,
  getExamSubjectList,
  createExamSubject,
  updateExamSubject,
  deleteExamSubject,
  type ExamPublishCheck,
  type Exam,
  type ExamSubject,
} from '@/api/exam'
import { getSchoolSelectList, getGradeListBySchool, getClassListByGrade, type School, type Grade, type ClassInfo } from '@/api/school'
import type { Id } from '@/api/types'

const router = useRouter()

// 下拉列表数据
const schoolList = ref<School[]>([])
const gradeList = ref<Grade[]>([])
const classList = ref<ClassInfo[]>([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  schoolId: undefined as number | undefined,
  name: '',
  code: '',
  type: undefined as number | undefined,
  academicYear: '',
  semester: undefined as number | undefined,
  status: undefined as number | undefined,
})

// 表格数据
const loading = ref(false)
const tableData = ref<Exam[]>([])
const total = ref(0)
const selectedIds = ref<Id[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

interface FormDataType extends Partial<Exam> {
  classIds?: Id[]
}

const formData = reactive<FormDataType>({
  id: undefined,
  schoolId: undefined,
  name: '',
  code: '',
  type: 5,
  academicYear: '',
  semester: 1,
  gradeId: undefined,
  startTime: undefined,
  endTime: undefined,
  description: '',
  remark: '',
  classIds: [],
})

const validateEndTime = (_rule: unknown, value: string | undefined, callback: (error?: Error) => void) => {
  if (!value) {
    callback(new Error('请选择结束时间'))
    return
  }
  if (!formData.startTime) {
    callback()
    return
  }
  if (new Date(formData.startTime).getTime() >= new Date(value).getTime()) {
    callback(new Error('结束时间必须晚于开始时间'))
    return
  }
  callback()
}

const formRules: FormRules = {
  schoolId: [{ required: true, message: '请选择学校', trigger: 'change' }],
  name: [{ required: true, message: '请输入考试名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择考试类型', trigger: 'change' }],
  academicYear: [{ required: true, message: '请选择学年', trigger: 'change' }],
  semester: [{ required: true, message: '请选择学期', trigger: 'change' }],
  gradeId: [{ required: true, message: '请选择年级', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' },
    { validator: validateEndTime, trigger: 'change' },
  ],
  classIds: [{ required: true, message: '请至少选择一个参考班级', trigger: 'change' }],
}

// 科目管理对话框
const subjectDialogVisible = ref(false)
const currentExam = ref<Exam | null>(null)
const subjectList = ref<ExamSubject[]>([])
const subjectLoading = ref(false)

// 科目编辑对话框
const subjectFormVisible = ref(false)
const subjectFormTitle = ref('')
const subjectFormRef = ref<FormInstance>()
const subjectSubmitLoading = ref(false)
const subjectFormData = reactive<Partial<ExamSubject>>({
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
  remark: '',
})

const subjectFormRules: FormRules = {
  subjectName: [{ required: true, message: '请输入科目名称', trigger: 'blur' }],
  fullScore: [{ required: true, message: '请输入满分', trigger: 'blur' }],
}

// 类型名称映射
const typeNames: Record<number, string> = {
  1: '期中考试',
  2: '期末考试',
  3: '月考',
  4: '模拟考试',
  5: '其他',
}

const statusNames: Record<number, string> = {
  0: '草稿',
  1: '待考试',
  2: '考试中',
  3: '阅卷中',
  4: '已完成',
  5: '已发布',
}

const getTypeName = (type: number) => typeNames[type] || '未知'
const getStatusName = (status: number) => statusNames[status] || '未知'

const getTypeTagType = (type: number): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
  const map: Record<number, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
    1: 'primary',
    2: 'success',
    3: 'warning',
    4: 'info',
    5: 'info',
  }
  return map[type] || 'info'
}

const getStatusTagType = (status: number): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
  const map: Record<number, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
    0: 'info',
    1: 'warning',
    2: 'primary',
    3: 'warning',
    4: 'success',
    5: 'success',
  }
  return map[status] || 'info'
}

const getDefaultAcademicYear = () => {
  const now = new Date()
  const year = now.getMonth() >= 7 ? now.getFullYear() : now.getFullYear() - 1
  return `${year}-${year + 1}`
}

const getDefaultSemester = () => {
  const month = new Date().getMonth() + 1
  return month >= 2 && month <= 7 ? 2 : 1
}

const academicYearOptions = (() => {
  const currentYear = new Date().getFullYear()
  const baseYear = new Date().getMonth() >= 7 ? currentYear : currentYear - 1
  const options: string[] = []
  for (let i = 1; i >= -3; i--) {
    const y = baseYear + i
    options.push(`${y}-${y + 1}`)
  }
  return options
})()

const loadGradeOptions = async (schoolId: number) => {
  const res = await getGradeListBySchool(schoolId)
  gradeList.value = res.data
}

const loadClassOptions = async (gradeId: number) => {
  const res = await getClassListByGrade(gradeId)
  classList.value = res.data
}

// 加载下拉数据
const loadSchoolList = async () => {
  const res = await getSchoolSelectList()
  schoolList.value = res.data
}

const handleSchoolChange = async (schoolId: number) => {
  formData.gradeId = undefined
  formData.classIds = []
  gradeList.value = []
  classList.value = []
  if (schoolId) {
    await loadGradeOptions(schoolId)
  }
}

const handleGradeChange = async (gradeId: number | undefined) => {
  formData.classIds = []
  classList.value = []
  if (gradeId) {
    await loadClassOptions(gradeId)
  }
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getExamPage(queryParams)
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
  queryParams.schoolId = undefined
  queryParams.name = ''
  queryParams.code = ''
  queryParams.type = undefined
  queryParams.academicYear = ''
  queryParams.semester = undefined
  queryParams.status = undefined
  handleSearch()
}

// 选择变化
const handleSelectionChange = (rows: Exam[]) => {
  selectedIds.value = rows.map((row) => row.id)
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增考试'
  Object.assign(formData, {
    id: undefined,
    schoolId: undefined,
    name: '',
    code: '',
    type: 5,
    academicYear: getDefaultAcademicYear(),
    semester: getDefaultSemester(),
    gradeId: undefined,
    startTime: undefined,
    endTime: undefined,
    description: '',
    remark: '',
    classIds: [],
  })
  gradeList.value = []
  classList.value = []
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row: Exam) => {
  dialogTitle.value = '编辑考试'
  const res = await getExamDetail(row.id)
  const detail = res.data
  Object.assign(formData, {
    id: detail.id,
    schoolId: detail.schoolId,
    name: detail.name,
    code: detail.code || '',
    type: detail.type,
    academicYear: detail.academicYear,
    semester: detail.semester,
    gradeId: detail.gradeId,
    startTime: detail.startTime,
    endTime: detail.endTime,
    description: detail.description || '',
    remark: detail.remark || '',
    classIds: detail.classes?.map(c => c.classId) || [],
  })
  gradeList.value = []
  classList.value = []
  if (detail.schoolId) {
    await loadGradeOptions(Number(detail.schoolId))
  }
  if (detail.gradeId) {
    await loadClassOptions(Number(detail.gradeId))
  }
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    const data = { ...formData }
    if (formData.id) {
      await updateExam(data)
      ElMessage.success('更新成功')
      await fetchData()
    } else {
      const res = await createExam(data)
      ElMessage.success('创建成功，即将跳转到工作台配置科目')
      dialogVisible.value = false
      router.push({ name: 'ExamWorkbench', params: { id: String(res.data) } })
      return
    }
    dialogVisible.value = false
  } finally {
    submitLoading.value = false
  }
}

// 删除
const handleDelete = async (row: Exam) => {
  await ElMessageBox.confirm(`确定要删除考试【${row.name}】吗？`, '提示', {
    type: 'warning',
  })
  await deleteExam(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

// 批量删除
const handleBatchDelete = async () => {
  await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个考试吗？`, '提示', {
    type: 'warning',
  })
  try {
    await deleteExamBatch(selectedIds.value)
    ElMessage.success('删除成功')
  } catch {
    // 全局拦截器已展示错误提示（含部分成功信息）
  } finally {
    selectedIds.value = []
    fetchData()
  }
}

const handleCheckPublish = async (row: Exam) => {
  if (row.status >= 4) {
    router.push({
      name: 'ScorePublishCheck',
      params: { id: String(row.id) },
    })
    return
  }

  const res = await getExamPublishCheck(row.id)
  const check: ExamPublishCheck = res.data
  if (check.canPublish) {
    await ElMessageBox.alert(
      `参考班级 ${check.classCount} 个，考试科目 ${check.subjectCount} 个，已完成试卷 ${check.completedPaperCount} 个，已发布模板 ${check.publishedTemplateCount} 个。`,
      '检查通过',
      { type: 'success' }
    )
    return
  }

  const content = check.missingItems.map((item) => `- ${item}`).join('<br/>')
  await ElMessageBox.alert(content, '配置未完成', {
    type: 'warning',
    dangerouslyUseHTMLString: true,
  })
}

const handleWorkbench = (row: Exam) => {
  if (!row.id) {
    ElMessage.error('考试ID无效')
    return
  }
  router.push({
    name: 'ExamWorkbench',
    params: { id: String(row.id) },
  })
}

// 科目管理
const handleSubjects = async (row: Exam) => {
  currentExam.value = row
  subjectDialogVisible.value = true
  await loadSubjectList()
}

const loadSubjectList = async () => {
  if (!currentExam.value) return
  subjectLoading.value = true
  try {
    const res = await getExamSubjectList(currentExam.value.id)
    subjectList.value = res.data
  } finally {
    subjectLoading.value = false
  }
}

const handleAddSubject = () => {
  subjectFormTitle.value = '添加科目'
  Object.assign(subjectFormData, {
    id: undefined,
    examId: currentExam.value?.id,
    subjectName: '',
    subjectCode: '',
    fullScore: 100,
    passScore: 60,
    excellentScore: 85,
    duration: 120,
    sort: subjectList.value.length,
    status: 1,
    remark: '',
  })
  subjectFormVisible.value = true
}

const handleEditSubject = (row: ExamSubject) => {
  subjectFormTitle.value = '编辑科目'
  Object.assign(subjectFormData, row)
  subjectFormVisible.value = true
}

const handleSubjectSubmit = async () => {
  await subjectFormRef.value?.validate()
  subjectSubmitLoading.value = true
  try {
    if (subjectFormData.id) {
      await updateExamSubject(subjectFormData)
      ElMessage.success('更新成功')
    } else {
      await createExamSubject(subjectFormData)
      ElMessage.success('添加成功')
    }
    subjectFormVisible.value = false
    await loadSubjectList()
    await fetchData()
  } finally {
    subjectSubmitLoading.value = false
  }
}

const handleDeleteSubject = async (row: ExamSubject) => {
  await ElMessageBox.confirm(`确定要删除科目【${row.subjectName}】吗？`, '提示', {
    type: 'warning',
  })
  await deleteExamSubject(row.id)
  ElMessage.success('删除成功')
  await loadSubjectList()
  await fetchData()
}

onMounted(() => {
  loadSchoolList()
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
.subject-toolbar {
  margin-bottom: 16px;
}
</style>
