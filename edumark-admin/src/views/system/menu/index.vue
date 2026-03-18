<template>
  <div class="page-container">
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="菜单名称">
          <el-input v-model="queryParams.permissionName" placeholder="请输入菜单名称" clearable />
        </el-form-item>
        <el-form-item label="权限码">
          <el-input v-model="queryParams.permissionCode" placeholder="请输入权限码" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.permissionType" placeholder="请选择类型" clearable>
            <el-option
              v-for="item in permissionTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="显示">
          <el-select v-model="queryParams.visible" placeholder="请选择" clearable>
            <el-option label="显示" :value="1" />
            <el-option label="隐藏" :value="0" />
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

    <el-row :gutter="16">
      <el-col :span="9">
        <el-card class="tree-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>菜单树</span>
              <el-button type="primary" size="small" @click="handleAddRoot">
                <el-icon><Plus /></el-icon>新增根节点
              </el-button>
            </div>
          </template>

          <el-tree
            v-loading="treeLoading"
            :data="treeData"
            :props="{ label: 'permissionName', children: 'children' }"
            node-key="id"
            default-expand-all
            highlight-current
            @node-click="handleNodeClick"
          >
            <template #default="{ node, data }">
              <span class="tree-node">
                <span>
                  <el-tag size="small" :type="getPermissionTypeTagType(data.permissionType)">
                    {{ getPermissionTypeLabel(data.permissionType) }}
                  </el-tag>
                  <span class="tree-node-title">{{ node.label }}</span>
                </span>
                <span class="tree-node-actions">
                  <el-button type="primary" link size="small" @click.stop="handleAddChild(data)">
                    <el-icon><Plus /></el-icon>
                  </el-button>
                  <el-button type="primary" link size="small" @click.stop="handleEdit(data)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                  <el-button type="danger" link size="small" @click.stop="handleDelete(data)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </span>
              </span>
            </template>
          </el-tree>
        </el-card>
      </el-col>

      <el-col :span="15">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>菜单列表</span>
              <el-button type="primary" @click="handleAdd">
                <el-icon><Plus /></el-icon>新增菜单
              </el-button>
            </div>
          </template>

          <el-table v-loading="loading" :data="tableData" row-key="id">
            <el-table-column prop="permissionName" label="菜单名称" min-width="180" />
            <el-table-column label="类型" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getPermissionTypeTagType(row.permissionType)">
                  {{ getPermissionTypeLabel(row.permissionType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="path" label="路由路径" min-width="160" />
            <el-table-column prop="component" label="组件路径" min-width="160" show-overflow-tooltip />
            <el-table-column prop="permissionCode" label="权限码" min-width="160" show-overflow-tooltip />
            <el-table-column label="显示" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="row.visible === 1 ? 'success' : 'info'">
                  {{ row.visible === 1 ? '显示' : '隐藏' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-switch
                  v-model="row.status"
                  :active-value="1"
                  :inactive-value="0"
                  inline-prompt
                  active-text="启"
                  inactive-text="停"
                  @change="handleStatusChange(row)"
                />
              </template>
            </el-table-column>
            <el-table-column prop="sort" label="排序" width="70" align="center" />
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
      </el-col>
    </el-row>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="640px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="上级菜单">
              <el-tree-select
                v-model="formData.parentId"
                :data="parentTreeData"
                :props="{ label: 'permissionName', children: 'children' }"
                node-key="id"
                value-key="id"
                placeholder="请选择上级菜单"
                clearable
                check-strictly
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="permissionName">
              <el-input v-model="formData.permissionName" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型" prop="permissionType">
              <el-select v-model="formData.permissionType" placeholder="请选择类型" style="width: 100%">
                <el-option
                  v-for="item in permissionTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="路由路径">
              <el-input v-model="formData.path" placeholder="如 /system/user 或 user" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="组件路径">
              <el-input v-model="formData.component" placeholder="如 system/user/index" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="权限编码">
              <el-input v-model="formData.permissionCode" placeholder="如 system:user:list" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="图标">
              <el-input v-model="formData.icon" placeholder="如 User / Menu" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="formData.sort" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示">
              <el-radio-group v-model="formData.visible">
                <el-radio :value="1">显示</el-radio>
                <el-radio :value="0">隐藏</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="状态">
              <el-radio-group v-model="formData.status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
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
import { Delete, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue'
import {
  createPermission,
  deletePermission,
  getPermissionDetail,
  getPermissionPage,
  getPermissionTree,
  updatePermission,
  updatePermissionStatus,
  type SysPermission,
} from '@/api/system'

const permissionTypeOptions = [
  { label: '目录', value: 1 },
  { label: '菜单', value: 2 },
  { label: '按钮', value: 3 },
]

const treeLoading = ref(false)
const treeData = ref<SysPermission[]>([])
const parentTreeData = ref<SysPermission[]>([])
const loading = ref(false)
const tableData = ref<SysPermission[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  permissionName: '',
  permissionCode: '',
  permissionType: undefined as number | undefined,
  status: undefined as number | undefined,
  visible: undefined as number | undefined,
  parentId: undefined as number | undefined,
})

const createInitialForm = (): Partial<SysPermission> => ({
  id: undefined,
  parentId: 0,
  permissionName: '',
  permissionCode: '',
  permissionType: 2,
  path: '',
  component: '',
  icon: '',
  sort: 0,
  visible: 1,
  status: 1,
})

const formData = reactive<Partial<SysPermission>>(createInitialForm())

const formRules: FormRules = {
  permissionName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  permissionType: [{ required: true, message: '请选择类型', trigger: 'change' }],
}

const getPermissionTypeLabel = (value?: number) => {
  return permissionTypeOptions.find(item => item.value === value)?.label || '-'
}

const getPermissionTypeTagType = (value?: number) => {
  if (value === 1) return 'success'
  if (value === 2) return 'warning'
  return 'info'
}

const loadTree = async () => {
  treeLoading.value = true
  try {
    const res = await getPermissionTree()
    treeData.value = res.data
    parentTreeData.value = res.data
  } finally {
    treeLoading.value = false
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getPermissionPage(queryParams)
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
  queryParams.permissionName = ''
  queryParams.permissionCode = ''
  queryParams.permissionType = undefined
  queryParams.status = undefined
  queryParams.visible = undefined
  queryParams.parentId = undefined
  handleSearch()
}

const handleNodeClick = (data: SysPermission) => {
  queryParams.parentId = data.id
  handleSearch()
}

const resetFormData = () => {
  Object.assign(formData, createInitialForm())
}

const handleAddRoot = () => {
  dialogTitle.value = '新增根菜单'
  resetFormData()
  formData.parentId = 0
  dialogVisible.value = true
}

const handleAddChild = (parent: SysPermission) => {
  dialogTitle.value = '新增子菜单'
  resetFormData()
  formData.parentId = parent.id
  dialogVisible.value = true
}

const handleAdd = () => {
  dialogTitle.value = '新增菜单'
  resetFormData()
  dialogVisible.value = true
}

const handleEdit = async (row: SysPermission) => {
  dialogTitle.value = '编辑菜单'
  resetFormData()
  const res = await getPermissionDetail(row.id)
  Object.assign(formData, res.data)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (formData.id) {
      await updatePermission(formData)
      ElMessage.success('更新成功')
    } else {
      await createPermission(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await Promise.all([fetchData(), loadTree()])
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: SysPermission) => {
  await ElMessageBox.confirm(`确定要删除菜单【${row.permissionName}】吗？`, '提示', {
    type: 'warning',
  })
  await deletePermission(row.id)
  ElMessage.success('删除成功')
  await Promise.all([fetchData(), loadTree()])
}

const handleStatusChange = async (row: SysPermission) => {
  try {
    await updatePermissionStatus(row.id, row.status || 0)
    ElMessage.success('状态更新成功')
    loadTree()
  } catch {
    row.status = row.status === 1 ? 0 : 1
  }
}

onMounted(async () => {
  await Promise.all([loadTree(), fetchData()])
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 16px;
}

.tree-card {
  height: calc(100vh - 180px);
  overflow: auto;
}

.tree-card :deep(.el-card__body) {
  padding: 12px;
}

.tree-node {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  padding-right: 8px;
}

.tree-node-title {
  margin-left: 8px;
}

.tree-node-actions {
  display: none;
}

.tree-node:hover .tree-node-actions {
  display: inline-flex;
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
