<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="学校">
          <el-select v-model="queryParams.schoolId" placeholder="请选择学校" clearable filterable @change="handleSchoolChange">
            <el-option v-for="item in schoolList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="年级">
          <el-select v-model="queryParams.gradeId" placeholder="请选择年级" clearable filterable @change="handleGradeChange">
            <el-option v-for="item in gradeList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="queryParams.classId" placeholder="请选择班级" clearable filterable @change="handleSearch">
            <el-option v-for="item in classList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="queryParams.studentNumber" placeholder="请输入学号" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable />
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
          <span>学生列表</span>
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
        <el-table-column prop="schoolName" label="学校" width="150" show-overflow-tooltip />
        <el-table-column prop="gradeName" label="年级" width="100" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="studentNumber" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="70" align="center">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="bindCode" label="绑定码" width="100" align="center">
          <template #default="{ row }">
            <el-tag>{{ row.bindCode }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleRefreshBindCode(row)">刷新绑定码</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
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
              <el-select v-model="formData.schoolId" placeholder="请选择学校" filterable @change="handleFormSchoolChange">
                <el-option v-for="item in schoolList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属班级" prop="classId">
              <el-select v-model="formData.classId" placeholder="请选择班级" filterable>
                <el-option v-for="item in formClassList" :key="item.id" :label="`${item.gradeName} - ${item.name}`" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNumber">
              <el-input v-model="formData.studentNumber" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="formData.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-radio-group v-model="formData.gender">
                <el-radio :value="1">男</el-radio>
                <el-radio :value="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="formData.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号">
              <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期">
              <el-date-picker
                v-model="formData.birthday"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入学日期">
              <el-date-picker
                v-model="formData.enrollDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="formData.status" placeholder="请选择">
                <el-option label="在读" :value="1" />
                <el-option label="休学" :value="0" />
                <el-option label="毕业" :value="2" />
                <el-option label="退学" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import {
  getStudentPage,
  createStudent,
  updateStudent,
  deleteStudent,
  deleteStudentBatch,
  refreshStudentBindCode,
  getSchoolSelectList,
  getGradeListBySchool,
  getClassListByGrade,
  getClassListBySchool,
  type Student,
  type School,
  type Grade,
  type ClassInfo,
} from '@/api/school'

// 学校、年级、班级列表
const schoolList = ref<School[]>([])
const gradeList = ref<Grade[]>([])
const classList = ref<ClassInfo[]>([])
const formClassList = ref<ClassInfo[]>([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  schoolId: undefined as number | undefined,
  gradeId: undefined as number | undefined,
  classId: undefined as number | undefined,
  studentNumber: '',
  name: '',
  status: undefined as number | undefined,
})

// 表格数据
const loading = ref(false)
const tableData = ref<Student[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const formData = reactive<Partial<Student>>({
  id: undefined,
  schoolId: undefined,
  classId: undefined,
  studentNumber: '',
  name: '',
  gender: 1,
  phone: '',
  idCard: '',
  birthday: '',
  enrollDate: '',
  status: 1,
  remark: '',
})

const formRules: FormRules = {
  schoolId: [{ required: true, message: '请选择学校', trigger: 'change' }],
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  studentNumber: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
}

// 状态名称
const getStatusName = (status: number) => {
  const map: Record<number, string> = { 0: '休学', 1: '在读', 2: '毕业', 3: '退学' }
  return map[status] || '未知'
}

const getStatusType = (status: number): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
  const map: Record<number, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = { 0: 'warning', 1: 'success', 2: 'info', 3: 'danger' }
  return map[status] || 'info'
}

// 加载学校列表
const loadSchoolList = async () => {
  const res = await getSchoolSelectList()
  schoolList.value = res.data
}

// 学校变化
const handleSchoolChange = async () => {
  queryParams.gradeId = undefined
  queryParams.classId = undefined
  gradeList.value = []
  classList.value = []
  if (queryParams.schoolId) {
    const res = await getGradeListBySchool(queryParams.schoolId)
    gradeList.value = res.data
  }
  handleSearch()
}

// 年级变化
const handleGradeChange = async () => {
  queryParams.classId = undefined
  classList.value = []
  if (queryParams.gradeId) {
    const res = await getClassListByGrade(queryParams.gradeId)
    classList.value = res.data
  }
  handleSearch()
}

// 表单学校变化
const handleFormSchoolChange = async () => {
  formData.classId = undefined
  formClassList.value = []
  if (formData.schoolId) {
    const res = await getClassListBySchool(formData.schoolId)
    formClassList.value = res.data
  }
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getStudentPage(queryParams)
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
  queryParams.gradeId = undefined
  queryParams.classId = undefined
  queryParams.studentNumber = ''
  queryParams.name = ''
  queryParams.status = undefined
  gradeList.value = []
  classList.value = []
  handleSearch()
}

// 选择变化
const handleSelectionChange = (rows: Student[]) => {
  selectedIds.value = rows.map((row) => row.id)
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增学生'
  Object.assign(formData, {
    id: undefined,
    schoolId: queryParams.schoolId,
    classId: queryParams.classId,
    studentNumber: '',
    name: '',
    gender: 1,
    phone: '',
    idCard: '',
    birthday: '',
    enrollDate: '',
    status: 1,
    remark: '',
  })
  if (formData.schoolId) {
    handleFormSchoolChange()
  }
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row: Student) => {
  dialogTitle.value = '编辑学生'
  Object.assign(formData, row)
  if (formData.schoolId) {
    const res = await getClassListBySchool(formData.schoolId)
    formClassList.value = res.data
  }
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (formData.id) {
      await updateStudent(formData)
      ElMessage.success('更新成功')
    } else {
      await createStudent(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

// 删除
const handleDelete = async (row: Student) => {
  await ElMessageBox.confirm(`确定要删除学生【${row.name}】吗？`, '提示', {
    type: 'warning',
  })
  await deleteStudent(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

// 批量删除
const handleBatchDelete = async () => {
  await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个学生吗？`, '提示', {
    type: 'warning',
  })
  await deleteStudentBatch(selectedIds.value)
  ElMessage.success('删除成功')
  fetchData()
}

// 刷新绑定码
const handleRefreshBindCode = async (row: Student) => {
  await ElMessageBox.confirm(`确定要刷新学生【${row.name}】的绑定码吗？刷新后原绑定码将失效。`, '提示', {
    type: 'warning',
  })
  const res = await refreshStudentBindCode(row.id)
  ElMessage.success(`新绑定码: ${res.data}`)
  fetchData()
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
</style>
