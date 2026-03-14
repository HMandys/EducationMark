<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="所属学校">
          <el-select
            v-model="queryParams.schoolId"
            placeholder="请选择学校"
            clearable
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
        <el-form-item label="科目">
          <el-select v-model="queryParams.subjectName" placeholder="请选择科目" clearable>
            <el-option label="语文" value="语文" />
            <el-option label="数学" value="数学" />
            <el-option label="英语" value="英语" />
            <el-option label="物理" value="物理" />
            <el-option label="化学" value="化学" />
            <el-option label="生物" value="生物" />
            <el-option label="历史" value="历史" />
            <el-option label="地理" value="地理" />
            <el-option label="政治" value="政治" />
          </el-select>
        </el-form-item>
        <el-form-item label="知识点名称">
          <el-input v-model="queryParams.name" placeholder="请输入知识点名称" clearable />
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

    <!-- 内容区 -->
    <el-row :gutter="16">
      <!-- 左侧树形结构 -->
      <el-col :span="8">
        <el-card class="tree-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>知识点结构</span>
              <el-button type="primary" size="small" @click="handleAddRoot">
                <el-icon><Plus /></el-icon>添加根节点
              </el-button>
            </div>
          </template>
          <div class="tree-filter">
            <el-select v-model="treeSchoolId" placeholder="选择学校" size="small" @change="loadTree">
              <el-option
                v-for="item in schoolList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
            <el-select v-model="treeSubject" placeholder="选择科目" size="small" @change="loadTree">
              <el-option label="语文" value="语文" />
              <el-option label="数学" value="数学" />
              <el-option label="英语" value="英语" />
              <el-option label="物理" value="物理" />
              <el-option label="化学" value="化学" />
              <el-option label="生物" value="生物" />
              <el-option label="历史" value="历史" />
              <el-option label="地理" value="地理" />
              <el-option label="政治" value="政治" />
            </el-select>
          </div>
          <el-tree
            v-loading="treeLoading"
            :data="treeData"
            :props="{ label: 'name', children: 'children' }"
            node-key="id"
            default-expand-all
            highlight-current
            @node-click="handleNodeClick"
          >
            <template #default="{ node, data }">
              <span class="tree-node">
                <span>{{ node.label }}</span>
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

      <!-- 右侧列表 -->
      <el-col :span="16">
        <el-card class="table-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>知识点列表</span>
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
            <el-table-column prop="name" label="知识点名称" min-width="180" />
            <el-table-column prop="code" label="编码" width="120" />
            <el-table-column prop="subjectName" label="科目" width="100" />
            <el-table-column prop="schoolName" label="学校" width="150" />
            <el-table-column prop="parentName" label="上级知识点" width="150" />
            <el-table-column prop="level" label="层级" width="70" align="center" />
            <el-table-column prop="sort" label="排序" width="70" align="center" />
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
            <el-table-column label="操作" width="120" fixed="right">
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
      </el-col>
    </el-row>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="550px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="所属学校" prop="schoolId">
          <el-select
            v-model="formData.schoolId"
            placeholder="请选择学校"
            filterable
            :disabled="!!formData.id"
          >
            <el-option
              v-for="item in schoolList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="科目" prop="subjectName">
          <el-select v-model="formData.subjectName" placeholder="请选择科目" :disabled="!!formData.id">
            <el-option label="语文" value="语文" />
            <el-option label="数学" value="数学" />
            <el-option label="英语" value="英语" />
            <el-option label="物理" value="物理" />
            <el-option label="化学" value="化学" />
            <el-option label="生物" value="生物" />
            <el-option label="历史" value="历史" />
            <el-option label="地理" value="地理" />
            <el-option label="政治" value="政治" />
          </el-select>
        </el-form-item>
        <el-form-item label="上级知识点">
          <el-tree-select
            v-model="formData.parentId"
            :data="parentTreeData"
            :props="{ label: 'name', children: 'children' }"
            node-key="id"
            value-key="id"
            placeholder="请选择上级知识点(可选)"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="知识点名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入知识点名称" />
        </el-form-item>
        <el-form-item label="编码">
          <el-input v-model="formData.code" placeholder="请输入编码" />
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
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
  getKnowledgePointPage,
  getKnowledgePointTree,
  createKnowledgePoint,
  updateKnowledgePoint,
  deleteKnowledgePoint,
  deleteKnowledgePointBatch,
  type KnowledgePoint,
} from '@/api/exam'
import { getSchoolSelectList, type School } from '@/api/school'

// 下拉列表数据
const schoolList = ref<School[]>([])

// 树形数据
const treeSchoolId = ref<number | undefined>(undefined)
const treeSubject = ref<string>('')
const treeLoading = ref(false)
const treeData = ref<KnowledgePoint[]>([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  schoolId: undefined as number | undefined,
  subjectName: '',
  name: '',
  parentId: undefined as number | undefined,
  status: undefined as number | undefined,
})

// 表格数据
const loading = ref(false)
const tableData = ref<KnowledgePoint[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const parentTreeData = ref<KnowledgePoint[]>([])

const formData = reactive<Partial<KnowledgePoint>>({
  id: undefined,
  schoolId: undefined,
  subjectName: '',
  parentId: 0,
  name: '',
  code: '',
  sort: 0,
  status: 1,
  remark: '',
})

const formRules: FormRules = {
  schoolId: [{ required: true, message: '请选择学校', trigger: 'change' }],
  subjectName: [{ required: true, message: '请选择科目', trigger: 'change' }],
  name: [{ required: true, message: '请输入知识点名称', trigger: 'blur' }],
}

// 加载下拉数据
const loadSchoolList = async () => {
  const res = await getSchoolSelectList()
  schoolList.value = res.data
  if (schoolList.value.length > 0) {
    treeSchoolId.value = schoolList.value[0].id
    treeSubject.value = '语文'
  }
}

// 加载树形数据
const loadTree = async () => {
  if (!treeSchoolId.value || !treeSubject.value) {
    treeData.value = []
    return
  }
  treeLoading.value = true
  try {
    const res = await getKnowledgePointTree(treeSchoolId.value, treeSubject.value)
    treeData.value = buildTree(res.data)
  } finally {
    treeLoading.value = false
  }
}

// 构建树形结构
const buildTree = (list: KnowledgePoint[]): KnowledgePoint[] => {
  const map = new Map<number, KnowledgePoint>()
  const roots: KnowledgePoint[] = []

  list.forEach(item => {
    map.set(item.id, { ...item, children: [] })
  })

  list.forEach(item => {
    const node = map.get(item.id)!
    if (item.parentId === 0 || !map.has(item.parentId)) {
      roots.push(node)
    } else {
      const parent = map.get(item.parentId)!
      if (!parent.children) parent.children = []
      parent.children.push(node)
    }
  })

  return roots
}

// 处理学校变化
const handleSchoolChange = () => {
  queryParams.parentId = undefined
  handleSearch()
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getKnowledgePointPage(queryParams)
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
  queryParams.subjectName = ''
  queryParams.name = ''
  queryParams.parentId = undefined
  queryParams.status = undefined
  handleSearch()
}

// 选择变化
const handleSelectionChange = (rows: KnowledgePoint[]) => {
  selectedIds.value = rows.map((row) => row.id)
}

// 树节点点击
const handleNodeClick = (data: KnowledgePoint) => {
  queryParams.schoolId = data.schoolId
  queryParams.subjectName = data.subjectName
  queryParams.parentId = data.id
  handleSearch()
}

// 加载上级知识点树
const loadParentTree = async () => {
  if (!formData.schoolId || !formData.subjectName) {
    parentTreeData.value = []
    return
  }
  const res = await getKnowledgePointTree(formData.schoolId, formData.subjectName)
  parentTreeData.value = buildTree(res.data)
}

// 新增根节点
const handleAddRoot = () => {
  if (!treeSchoolId.value || !treeSubject.value) {
    ElMessage.warning('请先选择学校和科目')
    return
  }
  dialogTitle.value = '新增知识点'
  Object.assign(formData, {
    id: undefined,
    schoolId: treeSchoolId.value,
    subjectName: treeSubject.value,
    parentId: 0,
    name: '',
    code: '',
    sort: 0,
    status: 1,
    remark: '',
  })
  loadParentTree()
  dialogVisible.value = true
}

// 新增子节点
const handleAddChild = (parent: KnowledgePoint) => {
  dialogTitle.value = '新增子知识点'
  Object.assign(formData, {
    id: undefined,
    schoolId: parent.schoolId,
    subjectName: parent.subjectName,
    parentId: parent.id,
    name: '',
    code: '',
    sort: 0,
    status: 1,
    remark: '',
  })
  loadParentTree()
  dialogVisible.value = true
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增知识点'
  Object.assign(formData, {
    id: undefined,
    schoolId: undefined,
    subjectName: '',
    parentId: 0,
    name: '',
    code: '',
    sort: 0,
    status: 1,
    remark: '',
  })
  parentTreeData.value = []
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: KnowledgePoint) => {
  dialogTitle.value = '编辑知识点'
  Object.assign(formData, row)
  loadParentTree()
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (formData.id) {
      await updateKnowledgePoint(formData)
      ElMessage.success('更新成功')
    } else {
      await createKnowledgePoint(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
    loadTree()
  } finally {
    submitLoading.value = false
  }
}

// 删除
const handleDelete = async (row: KnowledgePoint) => {
  await ElMessageBox.confirm(`确定要删除知识点【${row.name}】吗？`, '提示', {
    type: 'warning',
  })
  await deleteKnowledgePoint(row.id)
  ElMessage.success('删除成功')
  fetchData()
  loadTree()
}

// 批量删除
const handleBatchDelete = async () => {
  await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个知识点吗？`, '提示', {
    type: 'warning',
  })
  await deleteKnowledgePointBatch(selectedIds.value)
  ElMessage.success('删除成功')
  fetchData()
  loadTree()
}

// 状态变更
const handleStatusChange = async (row: KnowledgePoint) => {
  try {
    await updateKnowledgePoint({ id: row.id, status: row.status })
    ElMessage.success('状态更新成功')
  } catch {
    row.status = row.status === 1 ? 0 : 1
  }
}

// 监听表单学校和科目变化，重新加载上级树
watch(() => [formData.schoolId, formData.subjectName], () => {
  if (formData.schoolId && formData.subjectName) {
    loadParentTree()
  }
})

onMounted(async () => {
  await loadSchoolList()
  loadTree()
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
.tree-card {
  height: calc(100vh - 180px);
  overflow: auto;
}
.tree-card :deep(.el-card__body) {
  padding: 10px;
}
.tree-filter {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}
.tree-filter .el-select {
  flex: 1;
}
.tree-node {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-right: 8px;
}
.tree-node-actions {
  display: none;
}
.tree-node:hover .tree-node-actions {
  display: inline-flex;
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
