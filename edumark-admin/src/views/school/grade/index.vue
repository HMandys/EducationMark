<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="学校">
          <el-select v-model="queryParams.schoolId" placeholder="请选择学校" clearable filterable @change="handleSearch">
            <el-option v-for="item in schoolList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="年级名称">
          <el-input v-model="queryParams.name" placeholder="请输入年级名称" clearable />
        </el-form-item>
        <el-form-item label="入学年份">
          <el-input v-model.number="queryParams.enrollYear" placeholder="如: 2024" clearable />
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
          <span>年级列表</span>
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
        <el-table-column prop="schoolName" label="学校" min-width="180" />
        <el-table-column prop="name" label="年级名称" width="150" />
        <el-table-column prop="code" label="年级编码" width="100" />
        <el-table-column prop="enrollYear" label="入学年份" width="100" align="center" />
        <el-table-column prop="gradeNum" label="年级序号" width="100" align="center" />
        <el-table-column prop="classCount" label="班级数" width="80" align="center" />
        <el-table-column prop="studentCount" label="学生数" width="80" align="center" />
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
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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
        <el-form-item label="所属学校" prop="schoolId">
          <el-select v-model="formData.schoolId" placeholder="请选择学校" filterable>
            <el-option v-for="item in schoolList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="年级名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入年级名称" />
        </el-form-item>
        <el-form-item label="年级编码" prop="code">
          <el-input v-model="formData.code" placeholder="请输入年级编码" />
        </el-form-item>
        <el-form-item label="入学年份" prop="enrollYear">
          <el-input-number v-model="formData.enrollYear" :min="2000" :max="2100" />
        </el-form-item>
        <el-form-item label="年级序号" prop="gradeNum">
          <el-input-number v-model="formData.gradeNum" :min="1" :max="12" />
        </el-form-item>
        <el-form-item label="排序号">
          <el-input-number v-model="formData.sort" :min="0" />
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import {
  getGradePage,
  createGrade,
  updateGrade,
  deleteGrade,
  deleteGradeBatch,
  updateGradeStatus,
  getSchoolSelectList,
  type Grade,
  type School,
} from '@/api/school'

// 学校列表
const schoolList = ref<School[]>([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  schoolId: undefined as number | undefined,
  name: '',
  enrollYear: undefined as number | undefined,
  status: undefined as number | undefined,
})

// 表格数据
const loading = ref(false)
const tableData = ref<Grade[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const formData = reactive<Partial<Grade>>({
  id: undefined,
  schoolId: undefined,
  name: '',
  code: '',
  enrollYear: new Date().getFullYear(),
  gradeNum: 1,
  sort: 0,
  status: 1,
  remark: '',
})

const formRules: FormRules = {
  schoolId: [{ required: true, message: '请选择学校', trigger: 'change' }],
  name: [{ required: true, message: '请输入年级名称', trigger: 'blur' }],
}

// 加载学校列表
const loadSchoolList = async () => {
  const res = await getSchoolSelectList()
  schoolList.value = res.data
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getGradePage(queryParams)
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
  queryParams.enrollYear = undefined
  queryParams.status = undefined
  handleSearch()
}

// 选择变化
const handleSelectionChange = (rows: Grade[]) => {
  selectedIds.value = rows.map((row) => row.id)
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增年级'
  Object.assign(formData, {
    id: undefined,
    schoolId: queryParams.schoolId,
    name: '',
    code: '',
    enrollYear: new Date().getFullYear(),
    gradeNum: 1,
    sort: 0,
    status: 1,
    remark: '',
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: Grade) => {
  dialogTitle.value = '编辑年级'
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (formData.id) {
      await updateGrade(formData)
      ElMessage.success('更新成功')
    } else {
      await createGrade(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

// 删除
const handleDelete = async (row: Grade) => {
  await ElMessageBox.confirm(`确定要删除年级【${row.name}】吗？`, '提示', {
    type: 'warning',
  })
  await deleteGrade(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

// 批量删除
const handleBatchDelete = async () => {
  await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个年级吗？`, '提示', {
    type: 'warning',
  })
  await deleteGradeBatch(selectedIds.value)
  ElMessage.success('删除成功')
  fetchData()
}

// 状态变更
const handleStatusChange = async (row: Grade) => {
  try {
    await updateGradeStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch {
    row.status = row.status === 1 ? 0 : 1
  }
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
