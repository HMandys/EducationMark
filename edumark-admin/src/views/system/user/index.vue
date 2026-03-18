<template>
  <div class="page-container">
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="学校">
          <el-select v-model="queryParams.schoolId" placeholder="请选择学校" clearable filterable>
            <el-option v-for="item in schoolList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="queryParams.roleId" placeholder="请选择角色" clearable filterable>
            <el-option v-for="item in roleList" :key="item.id" :label="item.roleName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.realName" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="queryParams.userType" placeholder="请选择" clearable>
            <el-option
              v-for="item in userTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
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

    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <div>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>新增用户
            </el-button>
            <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
              <el-icon><Delete /></el-icon>批量删除
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        row-key="id"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="username" label="用户名" min-width="140" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="schoolName" label="所属学校" min-width="180" />
        <el-table-column label="角色" min-width="180">
          <template #default="{ row }">
            <el-space wrap>
              <el-tag v-for="name in row.roleNames || []" :key="name" size="small">
                {{ name }}
              </el-tag>
              <span v-if="!row.roleNames?.length" class="text-muted">未分配</span>
            </el-space>
          </template>
        </el-table-column>
        <el-table-column label="用户类型" width="120" align="center">
          <template #default="{ row }">
            {{ getUserTypeLabel(row.userType) }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
              inline-prompt
              active-text="启"
              inactive-text="停"
              @change="handleStatusChange(row, $event)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleResetPassword(row)">重置密码</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="760px"
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
            <el-form-item label="用户名" prop="username">
              <el-input v-model="formData.username" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
          <el-col v-if="!formData.id" :span="12">
            <el-form-item label="登录密码" prop="password">
              <el-input
                v-model="formData.password"
                type="password"
                show-password
                placeholder="请输入初始密码"
              />
            </el-form-item>
          </el-col>
          <el-col v-else :span="12">
            <el-form-item label="账号状态">
              <el-radio-group v-model="formData.status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="formData.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="formData.email" placeholder="请输入邮箱" />
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
            <el-form-item label="用户类型" prop="userType">
              <el-select v-model="formData.userType" placeholder="请选择用户类型" style="width: 100%">
                <el-option
                  v-for="item in userTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属学校">
              <el-select v-model="formData.schoolId" placeholder="请选择学校" clearable filterable style="width: 100%">
                <el-option v-for="item in schoolList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="角色分配" prop="roleIds">
              <el-select
                v-model="formData.roleIds"
                multiple
                collapse-tags
                collapse-tags-tooltip
                placeholder="请选择角色"
                style="width: 100%"
              >
                <el-option
                  v-for="item in roleList"
                  :key="item.id"
                  :label="item.roleName"
                  :value="item.id"
                />
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
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Delete, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { getSchoolSelectList, type School } from '@/api/school'
import {
  createUser,
  deleteUser,
  deleteUserBatch,
  getRoleList,
  getUserDetail,
  getUserPage,
  resetUserPassword,
  updateUser,
  updateUserStatus,
  type SysRole,
  type SysUser,
} from '@/api/system'

const userTypeOptions = [
  { label: '超级管理员', value: 1 },
  { label: '学校管理员', value: 2 },
  { label: '教务主任', value: 3 },
  { label: '阅卷组长', value: 4 },
  { label: '任课教师', value: 5 },
  { label: '班主任', value: 6 },
  { label: '家长', value: 7 },
  { label: '学生', value: 8 },
]

const schoolList = ref<School[]>([])
const roleList = ref<SysRole[]>([])
const loading = ref(false)
const tableData = ref<SysUser[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  realName: '',
  phone: '',
  userType: undefined as number | undefined,
  schoolId: undefined as number | undefined,
  roleId: undefined as number | undefined,
  status: undefined as number | undefined,
})

const createInitialForm = (): Partial<SysUser> => ({
  id: undefined,
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  gender: 1,
  userType: undefined,
  schoolId: undefined,
  status: 1,
  remark: '',
  roleIds: [],
})

const formData = reactive<Partial<SysUser>>(createInitialForm())

const formRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{
    validator: (_rule, value, callback) => {
      if (!formData.id && !value) {
        callback(new Error('请输入初始密码'))
        return
      }
      callback()
    },
    trigger: 'blur',
  }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }],
  roleIds: [{
    validator: (_rule, value, callback) => {
      if (!Array.isArray(value) || value.length === 0) {
        callback(new Error('请至少选择一个角色'))
        return
      }
      callback()
    },
    trigger: 'change',
  }],
}

const getUserTypeLabel = (value?: number) => {
  return userTypeOptions.find(item => item.value === value)?.label || '-'
}

const loadBaseData = async () => {
  const [schoolRes, roleRes] = await Promise.all([
    getSchoolSelectList(),
    getRoleList(),
  ])
  schoolList.value = schoolRes.data
  roleList.value = roleRes.data
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getUserPage(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNum = 1
  fetchData()
}

const handleReset = () => {
  queryParams.username = ''
  queryParams.realName = ''
  queryParams.phone = ''
  queryParams.userType = undefined
  queryParams.schoolId = undefined
  queryParams.roleId = undefined
  queryParams.status = undefined
  handleSearch()
}

const handleSelectionChange = (rows: SysUser[]) => {
  selectedIds.value = rows.map(row => row.id)
}

const resetFormData = () => {
  Object.assign(formData, createInitialForm())
}

const handleAdd = () => {
  dialogTitle.value = '新增用户'
  resetFormData()
  formData.schoolId = queryParams.schoolId
  dialogVisible.value = true
}

const handleEdit = async (row: SysUser) => {
  dialogTitle.value = '编辑用户'
  resetFormData()
  const res = await getUserDetail(row.id)
  Object.assign(formData, res.data, {
    password: '',
    roleIds: res.data.roleIds || [],
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (formData.id) {
      await updateUser(formData)
      ElMessage.success('更新成功')
    } else {
      await createUser(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: SysUser) => {
  await ElMessageBox.confirm(`确定要删除用户【${row.realName}】吗？`, '提示', {
    type: 'warning',
  })
  await deleteUser(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

const handleBatchDelete = async () => {
  await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个用户吗？`, '提示', {
    type: 'warning',
  })
  await deleteUserBatch(selectedIds.value)
  ElMessage.success('删除成功')
  fetchData()
}

const handleResetPassword = async (row: SysUser) => {
  const { value } = await ElMessageBox.prompt(`请输入用户【${row.realName}】的新密码`, '重置密码', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPlaceholder: '不少于 6 位',
    inputPattern: /^.{6,}$/,
    inputErrorMessage: '密码长度不能少于 6 位',
  })

  await resetUserPassword(row.id, value)
  ElMessage.success('密码重置成功')
}

const handleStatusChange = async (row: SysUser, value: string | number | boolean) => {
  try {
    const status = value ? 1 : 0
    await updateUserStatus(row.id, status)
    row.status = status
    ElMessage.success(status === 1 ? '已启用' : '已禁用')
  } catch (_error) {
    // 保持原状态，等待服务端结果
  }
}

onMounted(async () => {
  await loadBaseData()
  await fetchData()
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

.text-muted {
  color: #909399;
}
</style>
