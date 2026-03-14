<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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
          <span>家长列表</span>
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
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="70" align="center">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="绑定学生" min-width="300">
          <template #default="{ row }">
            <template v-if="row.students && row.students.length > 0">
              <el-tag v-for="student in row.students" :key="student.bindId" class="student-tag">
                {{ student.studentName }} ({{ student.className }})
              </el-tag>
            </template>
            <el-text v-else type="info">暂未绑定</el-text>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleBind(row)">绑定学生</el-button>
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
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="formData.gender">
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 绑定学生对话框 -->
    <el-dialog
      v-model="bindDialogVisible"
      title="绑定学生"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="bindFormRef"
        :model="bindFormData"
        :rules="bindFormRules"
        label-width="100px"
      >
        <el-form-item label="学生姓名" prop="studentName">
          <el-input v-model="bindFormData.studentName" placeholder="请输入学生姓名" />
        </el-form-item>
        <el-form-item label="学号" prop="studentNumber">
          <el-input v-model="bindFormData.studentNumber" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="绑定码" prop="bindCode">
          <el-input v-model="bindFormData.bindCode" placeholder="请输入绑定码" />
        </el-form-item>
        <el-form-item label="关系" prop="relation">
          <el-select v-model="bindFormData.relation" placeholder="请选择">
            <el-option label="父亲" :value="1" />
            <el-option label="母亲" :value="2" />
            <el-option label="爷爷" :value="3" />
            <el-option label="奶奶" :value="4" />
            <el-option label="外公" :value="5" />
            <el-option label="外婆" :value="6" />
            <el-option label="其他" :value="9" />
          </el-select>
        </el-form-item>
      </el-form>

      <!-- 已绑定学生列表 -->
      <el-divider content-position="left">已绑定学生</el-divider>
      <el-table :data="boundStudents" size="small">
        <el-table-column prop="studentName" label="姓名" />
        <el-table-column prop="studentNumber" label="学号" />
        <el-table-column prop="className" label="班级" />
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button type="danger" link size="small" @click="handleUnbind(row)">解绑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="bindDialogVisible = false">关闭</el-button>
        <el-button type="primary" :loading="bindLoading" @click="handleBindSubmit">绑定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import {
  getParentPage,
  createParent,
  updateParent,
  deleteParent,
  deleteParentBatch,
  updateParentStatus,
  bindStudent,
  unbindStudent,
  getParentBoundStudents,
  type Parent,
  type StudentBind,
  type ParentBindParams,
} from '@/api/school'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  phone: '',
  status: undefined as number | undefined,
})

// 表格数据
const loading = ref(false)
const tableData = ref<Parent[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const formData = reactive<Partial<Parent>>({
  id: undefined,
  name: '',
  gender: 1,
  phone: '',
  idCard: '',
  status: 1,
  remark: '',
})

const formRules: FormRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
}

// 绑定对话框
const bindDialogVisible = ref(false)
const bindFormRef = ref<FormInstance>()
const bindLoading = ref(false)
const currentParentId = ref<number>()
const boundStudents = ref<StudentBind[]>([])
const bindFormData = reactive<ParentBindParams>({
  studentName: '',
  studentNumber: '',
  bindCode: '',
  relation: 9,
})

const bindFormRules: FormRules = {
  studentName: [{ required: true, message: '请输入学生姓名', trigger: 'blur' }],
  studentNumber: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  bindCode: [{ required: true, message: '请输入绑定码', trigger: 'blur' }],
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getParentPage(queryParams)
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
  queryParams.name = ''
  queryParams.phone = ''
  queryParams.status = undefined
  handleSearch()
}

// 选择变化
const handleSelectionChange = (rows: Parent[]) => {
  selectedIds.value = rows.map((row) => row.id)
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增家长'
  Object.assign(formData, {
    id: undefined,
    name: '',
    gender: 1,
    phone: '',
    idCard: '',
    status: 1,
    remark: '',
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: Parent) => {
  dialogTitle.value = '编辑家长'
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (formData.id) {
      await updateParent(formData)
      ElMessage.success('更新成功')
    } else {
      await createParent(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

// 删除
const handleDelete = async (row: Parent) => {
  await ElMessageBox.confirm(`确定要删除家长【${row.name}】吗？`, '提示', {
    type: 'warning',
  })
  await deleteParent(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

// 批量删除
const handleBatchDelete = async () => {
  await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个家长吗？`, '提示', {
    type: 'warning',
  })
  await deleteParentBatch(selectedIds.value)
  ElMessage.success('删除成功')
  fetchData()
}

// 状态变更
const handleStatusChange = async (row: Parent) => {
  try {
    await updateParentStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch {
    row.status = row.status === 1 ? 0 : 1
  }
}

// 打开绑定对话框
const handleBind = async (row: Parent) => {
  currentParentId.value = row.id
  Object.assign(bindFormData, {
    studentName: '',
    studentNumber: '',
    bindCode: '',
    relation: 9,
  })
  const res = await getParentBoundStudents(row.id)
  boundStudents.value = res.data
  bindDialogVisible.value = true
}

// 绑定学生提交
const handleBindSubmit = async () => {
  await bindFormRef.value?.validate()
  bindLoading.value = true
  try {
    await bindStudent(currentParentId.value!, bindFormData)
    ElMessage.success('绑定成功')
    const res = await getParentBoundStudents(currentParentId.value!)
    boundStudents.value = res.data
    // 清空表单
    Object.assign(bindFormData, {
      studentName: '',
      studentNumber: '',
      bindCode: '',
      relation: 9,
    })
    fetchData()
  } finally {
    bindLoading.value = false
  }
}

// 解绑学生
const handleUnbind = async (student: StudentBind) => {
  await ElMessageBox.confirm(`确定要解绑学生【${student.studentName}】吗？`, '提示', {
    type: 'warning',
  })
  await unbindStudent(currentParentId.value!, student.studentId)
  ElMessage.success('解绑成功')
  const res = await getParentBoundStudents(currentParentId.value!)
  boundStudents.value = res.data
  fetchData()
}

onMounted(() => {
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
.student-tag {
  margin-right: 8px;
  margin-bottom: 4px;
}
</style>
