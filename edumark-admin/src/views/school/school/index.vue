<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="学校名称">
          <el-input v-model="queryParams.name" placeholder="请输入学校名称" clearable />
        </el-form-item>
        <el-form-item label="学校编码">
          <el-input v-model="queryParams.code" placeholder="请输入学校编码" clearable />
        </el-form-item>
        <el-form-item label="学校类型">
          <el-select v-model="queryParams.type" placeholder="请选择" clearable>
            <el-option label="小学" :value="1" />
            <el-option label="初中" :value="2" />
            <el-option label="高中" :value="3" />
            <el-option label="完全中学" :value="4" />
            <el-option label="九年一贯制" :value="5" />
          </el-select>
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
          <span>学校列表</span>
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
        <el-table-column prop="name" label="学校名称" min-width="180" />
        <el-table-column prop="code" label="学校编码" width="120" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ getTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contactName" label="负责人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="gradeCount" label="年级数" width="80" align="center" />
        <el-table-column prop="teacherCount" label="教师数" width="80" align="center" />
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
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="学校名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入学校名称" />
        </el-form-item>
        <el-form-item label="学校编码" prop="code">
          <el-input v-model="formData.code" placeholder="请输入学校编码" />
        </el-form-item>
        <el-form-item label="学校类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择学校类型">
            <el-option label="小学" :value="1" />
            <el-option label="初中" :value="2" />
            <el-option label="高中" :value="3" />
            <el-option label="完全中学" :value="4" />
            <el-option label="九年一贯制" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="省市区">
          <el-col :span="8">
            <el-input v-model="formData.province" placeholder="省份" />
          </el-col>
          <el-col :span="8">
            <el-input v-model="formData.city" placeholder="城市" />
          </el-col>
          <el-col :span="8">
            <el-input v-model="formData.district" placeholder="区县" />
          </el-col>
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input v-model="formData.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="formData.contactName" placeholder="请输入负责人姓名" />
        </el-form-item>
        <el-form-item label="负责人电话">
          <el-input v-model="formData.contactPhone" placeholder="请输入负责人电话" />
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
  getSchoolPage,
  createSchool,
  updateSchool,
  deleteSchool,
  deleteSchoolBatch,
  updateSchoolStatus,
  type School,
} from '@/api/school'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  code: '',
  type: undefined as number | undefined,
  status: undefined as number | undefined,
})

// 表格数据
const loading = ref(false)
const tableData = ref<School[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const formData = reactive<Partial<School>>({
  id: undefined,
  name: '',
  code: '',
  type: 1,
  province: '',
  city: '',
  district: '',
  address: '',
  phone: '',
  contactName: '',
  contactPhone: '',
  sort: 0,
  status: 1,
  remark: '',
})

const formRules: FormRules = {
  name: [{ required: true, message: '请输入学校名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择学校类型', trigger: 'change' }],
}

// 类型名称映射
const typeNames: Record<number, string> = {
  1: '小学',
  2: '初中',
  3: '高中',
  4: '完全中学',
  5: '九年一贯制',
}

const getTypeName = (type: number) => typeNames[type] || '未知'

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getSchoolPage(queryParams)
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
  queryParams.code = ''
  queryParams.type = undefined
  queryParams.status = undefined
  handleSearch()
}

// 选择变化
const handleSelectionChange = (rows: School[]) => {
  selectedIds.value = rows.map((row) => row.id)
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增学校'
  Object.assign(formData, {
    id: undefined,
    name: '',
    code: '',
    type: 1,
    province: '',
    city: '',
    district: '',
    address: '',
    phone: '',
    contactName: '',
    contactPhone: '',
    sort: 0,
    status: 1,
    remark: '',
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: School) => {
  dialogTitle.value = '编辑学校'
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (formData.id) {
      await updateSchool(formData)
      ElMessage.success('更新成功')
    } else {
      await createSchool(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

// 删除
const handleDelete = async (row: School) => {
  await ElMessageBox.confirm(`确定要删除学校【${row.name}】吗？`, '提示', {
    type: 'warning',
  })
  await deleteSchool(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

// 批量删除
const handleBatchDelete = async () => {
  await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个学校吗？`, '提示', {
    type: 'warning',
  })
  await deleteSchoolBatch(selectedIds.value)
  ElMessage.success('删除成功')
  fetchData()
}

// 状态变更
const handleStatusChange = async (row: School) => {
  try {
    await updateSchoolStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch {
    row.status = row.status === 1 ? 0 : 1
  }
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
</style>
