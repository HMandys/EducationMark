<template>
  <div class="editor-container">
    <!-- 顶部工具栏 -->
    <div class="editor-header">
      <div class="header-left">
        <el-button @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>返回
        </el-button>
        <el-input
          v-model="templateForm.name"
          placeholder="输入模板名称"
          style="width: 300px; margin-left: 16px;"
        />
      </div>
      <div class="header-right">
        <el-button @click="handleSave" :loading="saveLoading">
          <el-icon><DocumentChecked /></el-icon>保存
        </el-button>
        <el-button @click="handleValidateCurrent" :disabled="!templateForm.id">
          校验
        </el-button>
        <el-button type="primary" @click="handlePublish" :loading="publishLoading">
          <el-icon><Upload /></el-icon>发布
        </el-button>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="editor-body">
      <!-- 左侧配置面板 -->
      <div class="editor-sidebar">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="页面设置" name="page">
            <PageSettingPanel v-model="templateForm" />
          </el-tab-pane>
          <el-tab-pane label="学生信息区" name="student">
            <StudentInfoPanel v-model="templateForm.studentInfoConfig" />
          </el-tab-pane>
          <el-tab-pane label="答题区域" name="regions">
            <RegionList
              v-model="templateForm.regions"
              :selected-index="selectedRegionIndex"
              @add="handleAddRegion"
              @edit="handleEditRegion"
              @delete="handleDeleteRegion"
              @select="handleSelectRegion"
            />
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 右侧预览面板 -->
      <div class="editor-preview">
        <div class="preview-workspace">
          <div class="preview-tools">
            <div class="tools-card">
              <div class="tools-card-title">样张叠加</div>
              <div class="tools-card-desc">上传扫描样张后可半透明叠加到模板上，直接对照图片拖框。</div>

              <div class="tools-actions">
                <el-upload
                  :auto-upload="false"
                  :show-file-list="false"
                  accept="image/*"
                  :on-change="handleSampleImageChange"
                >
                  <el-button type="primary" plain>上传样张</el-button>
                </el-upload>
                <el-button @click="clearSampleImage" :disabled="!sampleImageUrl">清除样张</el-button>
              </div>

              <div class="tools-switches">
                <el-switch
                  v-model="sampleImageVisible"
                  :disabled="!sampleImageUrl"
                  active-text="显示叠加"
                  inactive-text="隐藏叠加"
                />
                <span class="sample-file-name" v-if="sampleImageName">{{ sampleImageName }}</span>
              </div>

              <div class="opacity-row">
                <span>透明度</span>
                <el-slider v-model="sampleImageOpacity" :min="10" :max="90" :disabled="!sampleImageUrl" />
                <span>{{ sampleImageOpacity }}%</span>
              </div>
            </div>

            <div class="tools-card">
              <div class="tools-card-title">当前校验状态</div>
              <div class="tools-card-desc">
                {{ validationSummaryText }}
              </div>
              <div class="tools-actions">
                <el-button @click="handleValidateCurrent" :disabled="!templateForm.id">重新校验</el-button>
                <el-button @click="validationDialogVisible = true" :disabled="!validationResult">查看详情</el-button>
              </div>
            </div>
          </div>

          <PreviewPanel
            :template="templateForm"
            :editable="true"
            :selected-region-index="selectedRegionIndex"
            :sample-image-url="sampleImageUrl"
            :sample-image-visible="sampleImageVisible"
            :sample-image-opacity="sampleImageOpacity / 100"
            @select-region="handleSelectRegion"
            @update-region="handlePreviewRegionUpdate"
          />
        </div>
      </div>
    </div>

    <!-- 区域配置弹窗 -->
    <RegionConfigDialog
      v-model:visible="regionDialogVisible"
      :region="currentRegion"
      @confirm="handleRegionConfirm"
    />

    <TemplateValidationDialog
      v-model:visible="validationDialogVisible"
      :template-name="templateForm.name"
      :result="validationResult"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, reactive, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type UploadFile } from 'element-plus'
import { ArrowLeft, DocumentChecked, Upload } from '@element-plus/icons-vue'
import PageSettingPanel from './components/PageSettingPanel.vue'
import StudentInfoPanel from './components/StudentInfoPanel.vue'
import RegionList from './components/RegionList.vue'
import RegionConfigDialog from './components/RegionConfigDialog.vue'
import PreviewPanel from './components/PreviewPanel.vue'
import TemplateValidationDialog from './components/TemplateValidationDialog.vue'
import {
  getTemplateDetail,
  createTemplate,
  updateTemplate,
  publishTemplate,
  validateTemplate,
  type AnswerSheetTemplate,
  type AnswerSheetRegion,
  type TemplateValidationResult,
} from '@/api/answerSheetTemplate'

const route = useRoute()
const router = useRouter()

const activeTab = ref('page')
const saveLoading = ref(false)
const publishLoading = ref(false)

// 模板表单数据
const templateForm = reactive<Partial<AnswerSheetTemplate>>({
  id: undefined,
  paperId: undefined,
  name: '答题卡模板',
  pageSize: 'A4',
  orientation: 1,
  columns: 1,
  marginTop: 20,
  marginBottom: 20,
  marginLeft: 15,
  marginRight: 15,
  headerConfig: {
    title: '',
    showTitle: true,
  },
  studentInfoConfig: {
    showStudentId: true,
    showName: true,
    showClass: true,
  },
  regions: [],
})

// 区域编辑
const regionDialogVisible = ref(false)
const currentRegion = ref<AnswerSheetRegion | null>(null)
const editingRegionIndex = ref(-1)
const selectedRegionIndex = ref(-1)
const validationDialogVisible = ref(false)
const validationResult = ref<TemplateValidationResult | null>(null)
const sampleImageUrl = ref('')
const sampleImageName = ref('')
const sampleImageVisible = ref(false)
const sampleImageOpacity = ref(35)

const cloneRegion = (region: AnswerSheetRegion): AnswerSheetRegion => ({
  ...region,
  questionIds: region.questionIds ? [...region.questionIds] : undefined,
  config: {
    ...(region.config || {}),
  },
})

const validationSummaryText = computed(() => {
  if (!validationResult.value) {
    return '还没有执行模板校验，保存后建议先校验一次再发布。'
  }

  if (validationResult.value.passed) {
    return `校验通过，已标注 ${validationResult.value.annotatedRegionCount}/${validationResult.value.totalRegionCount} 个区域，可以直接发布。`
  }

  return `校验未通过，当前还有 ${validationResult.value.issueCount} 个问题待处理。`
})

const revokeSampleImage = () => {
  if (sampleImageUrl.value) {
    URL.revokeObjectURL(sampleImageUrl.value)
  }
}

// 获取模板详情
const fetchTemplateDetail = async (id: number) => {
  const res = await getTemplateDetail(id)
  Object.assign(templateForm, res.data)
}

// 返回
const handleBack = () => {
  router.push('/answer-sheet-design/list')
}

// 保存
const handleSave = async () => {
  if (!templateForm.name) {
    ElMessage.warning('请输入模板名称')
    return
  }
  if (!templateForm.paperId) {
    ElMessage.warning('请关联试卷')
    return
  }

  saveLoading.value = true
  try {
    if (templateForm.id) {
      await updateTemplate(templateForm)
      ElMessage.success('保存成功')
    } else {
      const res = await createTemplate(templateForm)
      templateForm.id = res.data
      // 更新URL
      router.replace(`/answer-sheet-design/edit/${res.data}`)
      ElMessage.success('创建成功')
    }
  } finally {
    saveLoading.value = false
  }
}

// 发布
const handlePublish = async () => {
  if (!templateForm.id) {
    ElMessage.warning('请先保存模板')
    return
  }
  if (!templateForm.regions || templateForm.regions.length === 0) {
    ElMessage.warning('请先添加答题区域')
    return
  }

  const passed = await runValidationAndMaybeOpen()
  if (!passed) {
    return
  }

  await ElMessageBox.confirm('确定要发布模板吗？发布后将生成PDF文件。', '提示', {
    type: 'warning',
  })

  publishLoading.value = true
  try {
    // 先保存
    await updateTemplate(templateForm)
    // 再发布
    await publishTemplate(templateForm.id)
    ElMessage.success('发布成功')
    router.push('/answer-sheet-design/list')
  } finally {
    publishLoading.value = false
  }
}

// 添加区域
const handleAddRegion = () => {
  currentRegion.value = {
    regionType: 1,
    regionName: '选择题',
    pageNo: 1,
    sortOrder: templateForm.regions?.length || 0,
    questionStart: 1,
    questionEnd: 10,
    config: {
      boxX: 8,
      boxY: 22,
      boxWidth: 84,
      boxHeight: 20,
      regionRole: 'choice_block',
      anchorType: 'none',
      cropMode: 'range-question',
      optionCount: 4,
      questionsPerRow: 5,
    },
  }
  editingRegionIndex.value = -1
  regionDialogVisible.value = true
}

// 编辑区域
const handleEditRegion = (region: AnswerSheetRegion, index: number) => {
  currentRegion.value = cloneRegion(region)
  editingRegionIndex.value = index
  selectedRegionIndex.value = index
  regionDialogVisible.value = true
}

// 删除区域
const handleDeleteRegion = async (index: number) => {
  await ElMessageBox.confirm('确定要删除该区域吗？', '提示', {
    type: 'warning',
  })
  templateForm.regions?.splice(index, 1)
  if (selectedRegionIndex.value === index) {
    selectedRegionIndex.value = -1
  } else if (selectedRegionIndex.value > index) {
    selectedRegionIndex.value -= 1
  }
}

// 区域配置确认
const handleRegionConfirm = (region: AnswerSheetRegion) => {
  if (editingRegionIndex.value >= 0) {
    // 编辑
    templateForm.regions![editingRegionIndex.value] = cloneRegion(region)
    selectedRegionIndex.value = editingRegionIndex.value
  } else {
    // 新增
    if (!templateForm.regions) {
      templateForm.regions = []
    }
    templateForm.regions.push(cloneRegion(region))
    selectedRegionIndex.value = templateForm.regions.length - 1
  }
  regionDialogVisible.value = false
}

const handleSelectRegion = (index: number) => {
  selectedRegionIndex.value = index
}

const handlePreviewRegionUpdate = ({ index, region }: { index: number; region: AnswerSheetRegion }) => {
  if (!templateForm.regions || !templateForm.regions[index]) {
    return
  }
  templateForm.regions.splice(index, 1, cloneRegion(region))
}

const runValidationAndMaybeOpen = async () => {
  if (!templateForm.id) {
    ElMessage.warning('请先保存模板后再校验')
    return false
  }

  const validation = await validateTemplate(templateForm.id)
  validationResult.value = validation.data
  if (!validation.data.passed) {
    validationDialogVisible.value = true
    return false
  }
  return true
}

const handleValidateCurrent = async () => {
  if (!templateForm.id) {
    ElMessage.warning('请先保存模板后再校验')
    return
  }

  await runValidationAndMaybeOpen()
  validationDialogVisible.value = true
}

const handleSampleImageChange = (file: UploadFile) => {
  if (!file.raw) {
    return
  }

  revokeSampleImage()
  sampleImageUrl.value = URL.createObjectURL(file.raw)
  sampleImageName.value = file.name
  sampleImageVisible.value = true
}

const clearSampleImage = () => {
  revokeSampleImage()
  sampleImageUrl.value = ''
  sampleImageName.value = ''
  sampleImageVisible.value = false
}

onMounted(async () => {
  const id = route.params.id as string
  const paperId = route.query.paperId as string

  if (id) {
    // 编辑模式
    await fetchTemplateDetail(Number(id))
  } else if (paperId) {
    // 新建模式，关联试卷
    templateForm.paperId = Number(paperId)
  }
})

onBeforeUnmount(() => {
  revokeSampleImage()
})
</script>

<style scoped>
.editor-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f7fa;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.editor-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.editor-sidebar {
  width: 360px;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  overflow-y: auto;
  padding: 16px;
}

.editor-preview {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  justify-content: center;
}

.preview-workspace {
  width: 100%;
  max-width: 980px;
}

.preview-tools {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.tools-card {
  padding: 16px 18px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
}

.tools-card-title {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
}

.tools-card-desc {
  margin-top: 6px;
  font-size: 12px;
  line-height: 1.6;
  color: #6b7280;
}

.tools-actions {
  display: flex;
  gap: 10px;
  margin-top: 14px;
}

.tools-switches {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin-top: 14px;
}

.sample-file-name {
  font-size: 12px;
  color: #475569;
}

.opacity-row {
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 12px;
  align-items: center;
  margin-top: 14px;
  font-size: 12px;
  color: #475569;
}

@media (max-width: 1200px) {
  .preview-tools {
    grid-template-columns: 1fr;
  }
}
</style>
