<template>
  <div class="page-container ai-marking-page">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="提供商配置" name="provider">
        <el-card class="search-card" shadow="never">
          <el-form :model="queryParams" inline>
            <el-form-item label="提供商">
              <el-input v-model="queryParams.providerName" placeholder="请输入提供商名称" clearable />
            </el-form-item>
            <el-form-item label="协议">
              <el-select v-model="queryParams.protocol" placeholder="请选择协议" clearable>
                <el-option
                  v-for="item in protocolOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="queryParams.enabled" placeholder="请选择状态" clearable>
                <el-option label="启用" :value="1" />
                <el-option label="停用" :value="0" />
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
              <span>多模态模型提供商</span>
              <el-button type="primary" @click="handleAddProvider">
                <el-icon><Plus /></el-icon>新增提供商
              </el-button>
            </div>
          </template>

          <el-table v-loading="providerLoading" :data="providerTableData" row-key="id">
            <el-table-column prop="providerName" label="提供商" min-width="140" />
            <el-table-column label="协议" width="140">
              <template #default="{ row }">
                <el-tag effect="plain">{{ getProtocolLabel(row.protocol) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="baseUrl" label="Base URL" min-width="220" show-overflow-tooltip />
            <el-table-column prop="model" label="模型" min-width="160" />
            <el-table-column label="API Key" min-width="140">
              <template #default="{ row }">
                <span>{{ row.apiKeyMasked || '未配置' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="默认" width="100" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.isDefault === 1" type="success">默认</el-tag>
                <el-button v-else type="primary" link @click="handleSetDefault(row)">设为默认</el-button>
              </template>
            </el-table-column>
            <el-table-column label="启用" width="100" align="center">
              <template #default="{ row }">
                <el-switch
                  :model-value="row.enabled === 1"
                  inline-prompt
                  active-text="启"
                  inactive-text="停"
                  @change="handleToggleProvider(row, $event)"
                />
              </template>
            </el-table-column>
            <el-table-column prop="priority" label="优先级" width="90" align="center" />
            <el-table-column prop="updateTime" label="更新时间" width="170" />
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditProvider(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteProvider(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="queryParams.pageNum"
            v-model:page-size="queryParams.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="providerTotal"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="fetchProviderData"
            @current-change="fetchProviderData"
          />
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="批改策略" name="policy">
        <el-card class="policy-card" shadow="never" v-loading="policyLoading">
          <template #header>
            <div class="card-header">
              <span>AI 自动批改策略</span>
              <el-button type="primary" :loading="policySubmitting" @click="handleSavePolicy">保存策略</el-button>
            </div>
          </template>

          <div class="policy-hint">
            这里配置的是全局 AI 批改入口和默认提示词模板。题目级是否启用 AI 批改，仍由答题卡模板里的填空题区域单独控制。
          </div>

          <el-form ref="policyFormRef" :model="policyForm" label-width="140px">
            <el-form-item label="启用 AI 自动批改">
              <el-switch v-model="policyEnabled" />
            </el-form-item>
            <el-form-item label="低置信度阈值">
              <el-input-number
                v-model="policyForm.lowConfidenceThreshold"
                :min="0"
                :max="1"
                :step="0.05"
                :precision="2"
              />
              <div class="field-tip">低于该阈值的结果建议回退到异常池或人工链路。</div>
            </el-form-item>
            <el-form-item label="失败回退策略">
              <el-select v-model="policyForm.failureStrategy">
                <el-option
                  v-for="item in failureStrategyOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="默认提示词模板">
              <el-input
                v-model="policyForm.promptTemplate"
                type="textarea"
                :rows="10"
                placeholder="请输入默认提示词模板"
              />
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="providerDialogVisible" :title="providerDialogTitle" width="720px" destroy-on-close>
      <el-form ref="providerFormRef" :model="providerForm" :rules="providerFormRules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="提供商名称" prop="providerName">
              <el-input v-model="providerForm.providerName" placeholder="请输入提供商名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="协议" prop="protocol">
              <el-select v-model="providerForm.protocol" placeholder="请选择协议">
                <el-option
                  v-for="item in protocolOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="Base URL" prop="baseUrl">
          <el-input v-model="providerForm.baseUrl" placeholder="例如 https://api.openai.com/v1" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="模型名称" prop="model">
              <el-input v-model="providerForm.model" placeholder="请输入模型名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="API Key" prop="apiKey">
              <el-input
                v-model="providerForm.apiKey"
                type="password"
                show-password
                :placeholder="providerForm.id && providerForm.hasApiKey ? '已配置密钥，如需更新请输入新值' : '请输入 API Key'"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="默认提供商">
              <el-switch v-model="providerDefault" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="启用状态">
              <el-switch v-model="providerEnabled" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="优先级">
              <el-input-number v-model="providerForm.priority" :min="0" :max="999" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="超时时间(ms)">
              <el-input-number v-model="providerForm.timeoutMs" :min="1000" :max="300000" :step="1000" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大输出Token">
              <el-input-number v-model="providerForm.maxTokens" :min="256" :max="32768" :step="256" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input v-model="providerForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="providerDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="providerSubmitting" @click="handleSubmitProvider">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import {
  createAiMarkingProvider,
  deleteAiMarkingProvider,
  getAiMarkingPolicy,
  getAiMarkingProviderPage,
  updateAiMarkingPolicy,
  updateAiMarkingProvider,
  type AiMarkingPolicy,
  type AiMarkingProvider,
} from '@/api/aiMarking'

const protocolOptions = [
  { label: 'OpenAI兼容', value: 'openai-compatible' },
  { label: 'Anthropic', value: 'anthropic' },
]

const failureStrategyOptions = [
  { label: '异常池', value: 'exception-pool' },
  { label: '人工复核', value: 'manual-review' },
  { label: '直接跳过', value: 'skip' },
]

const activeTab = ref('provider')
const providerLoading = ref(false)
const providerSubmitting = ref(false)
const policyLoading = ref(false)
const policySubmitting = ref(false)
const providerTableData = ref<AiMarkingProvider[]>([])
const providerTotal = ref(0)
const providerDialogVisible = ref(false)
const providerDialogTitle = ref('')
const providerFormRef = ref<FormInstance>()

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  providerName: '',
  protocol: '',
  enabled: undefined as number | undefined,
})

const createInitialProviderForm = (): AiMarkingProvider => ({
  id: undefined,
  providerName: '',
  protocol: 'openai-compatible',
  baseUrl: '',
  apiKey: '',
  model: '',
  enabled: 1,
  isDefault: 0,
  timeoutMs: 30000,
  maxTokens: 2048,
  priority: 0,
  remark: '',
  hasApiKey: false,
})

const providerForm = reactive<AiMarkingProvider>(createInitialProviderForm())

const createInitialPolicyForm = (): AiMarkingPolicy => ({
  id: undefined,
  enabled: 0,
  lowConfidenceThreshold: 0.75,
  failureStrategy: 'exception-pool',
  promptTemplate: '',
})

const policyForm = reactive<AiMarkingPolicy>(createInitialPolicyForm())

const providerEnabled = computed({
  get: () => providerForm.enabled === 1,
  set: (value: boolean) => {
    providerForm.enabled = value ? 1 : 0
  },
})

const providerDefault = computed({
  get: () => providerForm.isDefault === 1,
  set: (value: boolean) => {
    providerForm.isDefault = value ? 1 : 0
    if (value) {
      providerForm.enabled = 1
    }
  },
})

const policyEnabled = computed({
  get: () => policyForm.enabled === 1,
  set: (value: boolean) => {
    policyForm.enabled = value ? 1 : 0
  },
})

const providerFormRules: FormRules = {
  providerName: [{ required: true, message: '请输入提供商名称', trigger: 'blur' }],
  protocol: [{ required: true, message: '请选择协议类型', trigger: 'change' }],
  baseUrl: [{ required: true, message: '请输入 Base URL', trigger: 'blur' }],
  model: [{ required: true, message: '请输入模型名称', trigger: 'blur' }],
  apiKey: [{
    validator: (_rule, value, callback) => {
      if (!providerForm.id && !value) {
        callback(new Error('请输入 API Key'))
        return
      }
      callback()
    },
    trigger: 'blur',
  }],
}

const getProtocolLabel = (protocol?: string) => {
  return protocolOptions.find(item => item.value === protocol)?.label || protocol || '-'
}

const buildProviderPayload = (provider: AiMarkingProvider): AiMarkingProvider => ({
  id: provider.id,
  providerName: provider.providerName,
  protocol: provider.protocol,
  baseUrl: provider.baseUrl,
  apiKey: provider.apiKey,
  model: provider.model,
  enabled: provider.enabled,
  isDefault: provider.isDefault,
  timeoutMs: provider.timeoutMs,
  maxTokens: provider.maxTokens,
  priority: provider.priority,
  remark: provider.remark,
})

const fetchProviderData = async () => {
  providerLoading.value = true
  try {
    const res = await getAiMarkingProviderPage(queryParams)
    providerTableData.value = res.data.list
    providerTotal.value = res.data.total
  } finally {
    providerLoading.value = false
  }
}

const fetchPolicyData = async () => {
  policyLoading.value = true
  try {
    const res = await getAiMarkingPolicy()
    Object.assign(policyForm, createInitialPolicyForm(), res.data)
  } finally {
    policyLoading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNum = 1
  fetchProviderData()
}

const handleReset = () => {
  queryParams.providerName = ''
  queryParams.protocol = ''
  queryParams.enabled = undefined
  handleSearch()
}

const resetProviderForm = () => {
  Object.assign(providerForm, createInitialProviderForm())
}

const handleAddProvider = () => {
  providerDialogTitle.value = '新增提供商'
  resetProviderForm()
  providerDialogVisible.value = true
}

const handleEditProvider = (row: AiMarkingProvider) => {
  providerDialogTitle.value = '编辑提供商'
  Object.assign(providerForm, createInitialProviderForm(), row, {
    apiKey: '',
  })
  providerDialogVisible.value = true
}

const handleSubmitProvider = async () => {
  await providerFormRef.value?.validate()
  providerSubmitting.value = true
  try {
    if (providerForm.id) {
      await updateAiMarkingProvider(buildProviderPayload(providerForm))
      ElMessage.success('更新成功')
    } else {
      await createAiMarkingProvider(buildProviderPayload(providerForm))
      ElMessage.success('创建成功')
    }
    providerDialogVisible.value = false
    await fetchProviderData()
  } finally {
    providerSubmitting.value = false
  }
}

const handleDeleteProvider = async (row: AiMarkingProvider) => {
  await ElMessageBox.confirm(`确定要删除提供商【${row.providerName}】吗？`, '提示', {
    type: 'warning',
  })
  await deleteAiMarkingProvider(row.id!)
  ElMessage.success('删除成功')
  await fetchProviderData()
}

const handleToggleProvider = async (row: AiMarkingProvider, value: string | number | boolean) => {
  const enabled = value ? 1 : 0
  await updateAiMarkingProvider(buildProviderPayload({
    ...row,
    enabled,
    apiKey: '',
    isDefault: enabled === 0 ? 0 : row.isDefault,
  }))
  ElMessage.success(enabled === 1 ? '已启用' : '已停用')
  await fetchProviderData()
}

const handleSetDefault = async (row: AiMarkingProvider) => {
  await updateAiMarkingProvider(buildProviderPayload({
    ...row,
    isDefault: 1,
    enabled: 1,
    apiKey: '',
  }))
  ElMessage.success('已切换默认提供商')
  await fetchProviderData()
}

const handleSavePolicy = async () => {
  policySubmitting.value = true
  try {
    await updateAiMarkingPolicy(policyForm)
    ElMessage.success('策略已保存')
    await fetchPolicyData()
  } finally {
    policySubmitting.value = false
  }
}

onMounted(async () => {
  await Promise.all([
    fetchProviderData(),
    fetchPolicyData(),
  ])
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 16px;
}

.table-card :deep(.el-card__header),
.policy-card :deep(.el-card__header) {
  padding: 12px 20px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}

.policy-hint {
  margin-bottom: 16px;
  padding: 12px 14px;
  font-size: 13px;
  line-height: 1.6;
  color: #475569;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
}

.field-tip {
  margin-top: 6px;
  font-size: 12px;
  color: #64748b;
}
</style>
