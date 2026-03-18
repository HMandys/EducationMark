<template>
  <div class="page-container">
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="角色名称">
          <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="queryParams.roleCode" placeholder="请输入角色编码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据范围">
          <el-select v-model="queryParams.dataScope" placeholder="请选择数据范围" clearable>
            <el-option
              v-for="item in dataScopeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
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

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>角色列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增角色
          </el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="tableData" row-key="id">
        <el-table-column prop="roleName" label="角色名称" min-width="160" />
        <el-table-column prop="roleCode" label="角色编码" min-width="180" />
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
        <el-table-column label="数据范围" width="140">
          <template #default="{ row }">
            {{ getDataScopeLabel(row.dataScope) }}
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="90" align="center" />
        <el-table-column label="权限数" width="90" align="center">
          <template #default="{ row }">
            {{ row.permissionIds?.length || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="角色说明" min-width="220" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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
            <el-form-item label="角色名称" prop="roleName">
              <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色编码" prop="roleCode">
              <el-input v-model="formData.roleCode" placeholder="请输入角色编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="formData.sort" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="formData.status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="数据范围">
              <el-radio-group v-model="formData.dataScope">
                <el-radio
                  v-for="item in dataScopeOptions"
                  :key="item.value"
                  :value="item.value"
                >
                  {{ item.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="角色说明">
              <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入角色说明" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="权限分配">
              <el-tree-select
                v-model="formData.permissionIds"
                :data="permissionTree"
                :props="treeProps"
                node-key="id"
                multiple
                show-checkbox
                check-strictly
                default-expand-all
                clearable
                style="width: 100%"
              />
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
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import {
  createRole,
  deleteRole,
  getPermissionTree,
  getRoleDetail,
  getRolePage,
  updateRole,
  updateRoleStatus,
  type SysPermission,
  type SysRole,
} from '@/api/system'

const dataScopeOptions = [
  { label: '全部数据', value: 1 },
  { label: '本校数据', value: 2 },
  { label: '本人数据', value: 3 },
  { label: '自定义数据', value: 4 },
]

const treeProps = {
  label: 'permissionName',
  value: 'id',
  children: 'children',
}

const loading = ref(false)
const tableData = ref<SysRole[]>([])
const total = ref(0)
const permissionTree = ref<SysPermission[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleName: '',
  roleCode: '',
  status: undefined as number | undefined,
  dataScope: undefined as number | undefined,
})

const createInitialForm = (): Partial<SysRole> => ({
  id: undefined,
  roleName: '',
  roleCode: '',
  description: '',
  sort: 0,
  status: 1,
  dataScope: 2,
  permissionIds: [],
})

const formData = reactive<Partial<SysRole>>(createInitialForm())

const formRules: FormRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
}

const getDataScopeLabel = (value?: number) => {
  return dataScopeOptions.find(item => item.value === value)?.label || '未配置'
}

const loadPermissionTree = async () => {
  const res = await getPermissionTree()
  permissionTree.value = res.data
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getRolePage(queryParams)
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
  queryParams.roleName = ''
  queryParams.roleCode = ''
  queryParams.status = undefined
  queryParams.dataScope = undefined
  handleSearch()
}

const resetFormData = () => {
  Object.assign(formData, createInitialForm())
}

const handleAdd = () => {
  dialogTitle.value = '新增角色'
  resetFormData()
  dialogVisible.value = true
}

const handleEdit = async (row: SysRole) => {
  dialogTitle.value = '编辑角色'
  resetFormData()
  const res = await getRoleDetail(row.id)
  Object.assign(formData, res.data, {
    permissionIds: res.data.permissionIds || [],
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (formData.id) {
      await updateRole(formData)
      ElMessage.success('更新成功')
    } else {
      await createRole(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: SysRole) => {
  await ElMessageBox.confirm(`确定要删除角色【${row.roleName}】吗？`, '提示', {
    type: 'warning',
  })
  await deleteRole(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

const handleStatusChange = async (row: SysRole, value: string | number | boolean) => {
  const status = value ? 1 : 0
  await updateRoleStatus(row.id, status)
  row.status = status
  ElMessage.success(status === 1 ? '已启用' : '已禁用')
}

onMounted(async () => {
  await Promise.all([loadPermissionTree(), fetchData()])
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 16px;
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
