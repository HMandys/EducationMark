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
          <el-tab-pane label="模板图片" name="template-image">
            <div class="template-image-panel">
              <div class="panel-section">
                <div class="panel-title">上传答题卡图片</div>
                <div class="panel-desc">上传已设计好的答题卡图片，系统将自动检测四角定位点</div>
                <el-upload
                  class="template-upload"
                  :auto-upload="false"
                  :show-file-list="false"
                  accept="image/*"
                  :on-change="handleTemplateImageChange"
                  drag
                >
                  <div class="upload-content">
                    <el-icon class="upload-icon"><Upload /></el-icon>
                    <div class="upload-text">点击或拖拽上传答题卡图片</div>
                    <div class="upload-hint">支持 JPG、PNG 格式</div>
                  </div>
                </el-upload>
                <div v-if="templateImageUrl || templateForm.templateImageUrl" class="current-image-info">
                  <el-tag type="success">已上传模板图片</el-tag>
                  <el-button text type="primary" @click="showCornerPanel = !showCornerPanel">
                    {{ showCornerPanel ? '收起定位配置' : '查看定位配置' }}
                  </el-button>
                </div>
              </div>

              <CornerDetectPanel
                v-if="showCornerPanel && (templateImageUrl || templateForm.templateImageUrl)"
                :image-url="templateImageUrl || templateForm.templateImageUrl"
                v-model:corner-config="cornerConfig"
                @detect="handleAutoDetectCorners"
              />

              <div class="panel-section" v-if="templateImageUrl || templateForm.templateImageUrl">
                <el-button
                  type="primary"
                  @click="handleSaveCornerConfig"
                  :loading="cornerDetecting"
                  :disabled="!templateForm.id"
                >
                  保存定位配置
                </el-button>
              </div>
            </div>
          </el-tab-pane>
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
              <div class="tools-card-title">快速拉框</div>
              <div class="tools-card-desc">在样张上直接拖拽创建新区域，自动识别坐标。</div>
              <div class="tools-actions">
                <el-switch
                  v-model="drawMode"
                  :disabled="!sampleImageVisible"
                  active-text="拉框模式"
                  inactive-text="普通模式"
                  @change="handleDrawModeChange"
                />
              </div>
              <div class="tools-card-desc draw-tip">
                <template v-if="!sampleImageVisible">
                  请先上传样张并显示叠加后再使用拉框功能
                </template>
                <template v-else-if="drawMode">
                  在右侧预览区样张上按下鼠标并拖拽，释放后自动打开配置弹窗
                </template>
                <template v-else>
                  开启后可在样张上直接拖拽绘制区域
                </template>
              </div>
            </div>

            <div class="tools-card" v-if="selectedChoiceRegion">
              <div class="tools-card-title">客观题选项识别</div>
              <div class="tools-card-desc">
                基于当前样张图和客观题总框，自动识别 A/B/C/D 小气泡坐标并生成子框。
              </div>
              <div class="tools-actions">
                <el-button
                  type="primary"
                  @click="handleDetectChoiceBubbles()"
                  :disabled="!canDetectChoiceBubbles"
                  :loading="bubbleDetectLoading"
                >
                  识别选项
                </el-button>
                <el-button @click="clearDetectedChoiceBubbles" :disabled="!hasDetectedChoiceBubbles">
                  清除识别
                </el-button>
              </div>
              <div class="tools-card-desc detect-summary">
                {{ choiceBubbleSummary }}
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
            :draw-mode="drawMode"
            :image-only-mode="hasTemplateImage"
            @select-region="handleSelectRegion"
            @update-region="handlePreviewRegionUpdate"
            @create-region="handleCreateRegion"
          />
        </div>
      </div>
    </div>

    <!-- 区域配置弹窗 -->
    <RegionConfigDialog
      v-model:visible="regionDialogVisible"
      :region="currentRegion"
      @confirm="handleRegionConfirm"
      @detect-bubbles="handleDetectBubblesFromDialog"
    />

    <TemplateValidationDialog
      v-model:visible="validationDialogVisible"
      :template-name="templateForm.name"
      :result="validationResult"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type UploadFile } from 'element-plus'
import { ArrowLeft, DocumentChecked, Upload } from '@element-plus/icons-vue'
import { useDebounceFn } from '@vueuse/core'
import PageSettingPanel from './components/PageSettingPanel.vue'
import StudentInfoPanel from './components/StudentInfoPanel.vue'
import RegionList from './components/RegionList.vue'
import RegionConfigDialog from './components/RegionConfigDialog.vue'
import PreviewPanel from './components/PreviewPanel.vue'
import TemplateValidationDialog from './components/TemplateValidationDialog.vue'
import CornerDetectPanel from './components/CornerDetectPanel.vue'
import {
  getTemplateDetail,
  createTemplate,
  updateTemplate,
  publishTemplate,
  validateTemplate,
  uploadTemplateImage,
  saveCornerConfig,
  detectCorners,
  detectBubbles,
  type AnswerSheetTemplate,
  type AnswerSheetRegion,
  type BubbleMapItem,
  type TemplateValidationResult,
  type CornerConfig,
} from '@/api/answerSheetTemplate'
import { request } from '@/utils/request'
import type { Id } from '@/api/types'

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
const bubbleDetectLoading = ref(false)
const autoDetecting = ref(false)
const lastAutoDetectSignature = ref('')
const drawMode = ref(false)

// 模板图片模式
const templateImageFile = ref<File | null>(null)
const templateImageUrl = ref('')
const templateImageUploading = ref(false)
const cornerDetecting = ref(false)
const cornerConfig = ref<CornerConfig>({})
const showCornerPanel = ref(false)

// 是否有模板图片（本地上传的或已保存的）
const hasTemplateImage = computed(() => {
  return !!(templateImageUrl.value || templateForm.templateImageUrl)
})

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

const selectedChoiceRegion = computed(() => {
  if (selectedRegionIndex.value < 0 || !templateForm.regions?.[selectedRegionIndex.value]) {
    return null
  }
  const region = templateForm.regions[selectedRegionIndex.value]
  return region.config?.regionRole === 'choice_block' ? region : null
})

const canDetectChoiceBubbles = computed(() => {
  const region = selectedChoiceRegion.value
  return Boolean(
    region
    && sampleImageUrl.value
    && region.config?.boxX !== undefined
    && region.config?.boxY !== undefined
    && region.config?.boxWidth
    && region.config?.boxHeight
    && region.questionStart
    && region.questionEnd
    && region.config?.optionCount
    && region.config?.questionsPerRow,
  )
})

const hasDetectedChoiceBubbles = computed(() => {
  const region = selectedChoiceRegion.value
  return Boolean(region?.config?.bubbleMap?.length)
})

const choiceBubbleSummary = computed(() => {
  const region = selectedChoiceRegion.value
  if (!region) {
    return '请选择一个客观题涂卡区域后再做选项识别。'
  }

  const expectedCount = calculateExpectedBubbleCount(region)
  const detectedCount = region.config?.bubbleMap?.length || 0

  if (!sampleImageUrl.value) {
    return '请先上传样张图片，再基于图像识别选项。'
  }

  if (autoDetecting.value || bubbleDetectLoading.value) {
    return '系统正在自动识别当前客观题选项，请稍候。'
  }

  if (!detectedCount) {
    return `当前预期识别 ${expectedCount} 个选项气泡，系统会自动生成 bubbleMap 子坐标。`
  }

  return `已识别 ${detectedCount}/${expectedCount} 个选项气泡，并生成子框。`
})

const autoDetectSignature = computed(() => {
  const region = selectedChoiceRegion.value
  if (!region || !sampleImageUrl.value) {
    return ''
  }

  return JSON.stringify({
    sampleImageName: sampleImageName.value,
    sampleImageVisible: sampleImageVisible.value,
    boxX: region.config?.boxX,
    boxY: region.config?.boxY,
    boxWidth: region.config?.boxWidth,
    boxHeight: region.config?.boxHeight,
    questionStart: region.questionStart,
    questionEnd: region.questionEnd,
    optionCount: region.config?.optionCount,
    questionsPerRow: region.config?.questionsPerRow,
  })
})

const currentExamId = computed(() => {
  const examId = route.query.examId
  if (typeof examId === 'string' && examId) {
    return examId
  }
  if (Array.isArray(examId) && examId.length > 0) {
    return String(examId[0])
  }
  return ''
})

const revokeSampleImage = () => {
  if (sampleImageUrl.value) {
    URL.revokeObjectURL(sampleImageUrl.value)
  }
}

// 获取模板详情
const fetchTemplateDetail = async (id: Id) => {
  const res = await getTemplateDetail(id)
  Object.assign(templateForm, res.data)
}

// 返回
const handleBack = () => {
  router.push({
    path: '/answer-sheet-design/list',
    query: currentExamId.value ? { examId: currentExamId.value } : undefined,
  })
}

// 保存
const handleSave = async () => {
  if (!templateForm.name) {
    ElMessage.warning('请输入模板名称')
    return
  }
  // paperId 非必填，允许独立设计答题卡模板

  saveLoading.value = true
  try {
    if (templateForm.id) {
      await updateTemplate(templateForm)
      ElMessage.success('保存成功')
    } else {
      const res = await createTemplate(templateForm)
      templateForm.id = res.data
      // 更新URL
      router.replace({
        path: `/answer-sheet-design/edit/${res.data}`,
        query: currentExamId.value ? { examId: currentExamId.value } : undefined,
      })
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
    router.push({
      path: '/answer-sheet-design/list',
      query: currentExamId.value ? { examId: currentExamId.value } : undefined,
    })
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

// 处理拉框创建区域
const handleCreateRegion = (bounds: { boxX: number; boxY: number; boxWidth: number; boxHeight: number }) => {
  // 创建新区域，让用户填写题号范围
  currentRegion.value = {
    regionType: 1,
    regionName: `选择题区域`,
    pageNo: 1,
    sortOrder: templateForm.regions?.length || 0,
    questionStart: undefined,
    questionEnd: undefined,
    config: {
      ...bounds,
      regionRole: 'choice_block',
      anchorType: 'none',
      cropMode: 'range-question',
      optionCount: 4,
      questionsPerRow: 5,
      bubbleStyle: 'square',
    },
  }

  editingRegionIndex.value = -1
  regionDialogVisible.value = true
  drawMode.value = false
  selectedRegionIndex.value = -1
}

// 自动检测气泡（供 RegionConfigDialog 调用）
const handleAutoDetectBubbles = async (region: AnswerSheetRegion) => {
  if (!templateImageFile.value) {
    ElMessage.warning('请先上传答题卡图片')
    return null
  }

  if (!region.questionStart || !region.questionEnd) {
    ElMessage.warning('请先填写起始题号和结束题号')
    return null
  }

  const config = region.config
  if (!config?.boxX || !config?.boxY || !config?.boxWidth || !config?.boxHeight) {
    ElMessage.warning('区域坐标不完整')
    return null
  }

  ElMessage.info('正在自动检测选项位置...')

  try {
    const result = await detectBubbles(templateImageFile.value, {
      boxX: config.boxX,
      boxY: config.boxY,
      boxWidth: config.boxWidth,
      boxHeight: config.boxHeight,
      questionStart: region.questionStart,
      questionEnd: region.questionEnd,
      optionCount: config.optionCount || 4,
      questionsPerRow: config.questionsPerRow || 5,
      layoutDirection: config.layoutDirection || 'column',
    })

    if (result.data.success && result.data.bubbleMap) {
      ElMessage.success(`检测到 ${result.data.detectedCount} 个选项位置`)
      return result.data
    } else {
      ElMessage.warning(result.data.errorMessage || '气泡检测失败')
      return null
    }
  } catch (error) {
    console.error('气泡检测失败', error)
    ElMessage.error('气泡检测失败')
    return null
  }
}

// 处理弹窗中的气泡检测请求
const handleDetectBubblesFromDialog = async (
  region: AnswerSheetRegion,
  callback: (result: { bubbleMap: BubbleMapItem[]; detectedCount: number; expectedCount: number } | null) => void
) => {
  const result = await handleAutoDetectBubbles(region)
  callback(result)
}

// 暴露给子组件
defineExpose({ handleAutoDetectBubbles })

// 拉框模式切换
const handleDrawModeChange = (enabled: string | number | boolean) => {
  if (Boolean(enabled)) {
    selectedRegionIndex.value = -1 // 取消当前选中
  }
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
  lastAutoDetectSignature.value = ''
  sampleImageUrl.value = URL.createObjectURL(file.raw)
  sampleImageName.value = file.name
  sampleImageVisible.value = true
}

const clearSampleImage = () => {
  revokeSampleImage()
  lastAutoDetectSignature.value = ''
  sampleImageUrl.value = ''
  sampleImageName.value = ''
  sampleImageVisible.value = false
}

// 模板图片处理
const handleTemplateImageChange = async (file: UploadFile) => {
  if (!file.raw) return

  templateImageFile.value = file.raw

  // 创建本地预览URL
  if (templateImageUrl.value) {
    URL.revokeObjectURL(templateImageUrl.value)
  }
  templateImageUrl.value = URL.createObjectURL(file.raw)

  // 同时设置为样张，方便拉框
  revokeSampleImage()
  sampleImageUrl.value = URL.createObjectURL(file.raw)
  sampleImageName.value = file.name
  sampleImageVisible.value = true

  // 自动检测四角
  await handleAutoDetectCorners()

  // 如果模板已保存，自动上传图片
  if (templateForm.id) {
    await uploadTemplateImageToServer()
  }
}

const uploadTemplateImageToServer = async () => {
  if (!templateImageFile.value || !templateForm.id) return

  templateImageUploading.value = true
  try {
    // 先上传到文件服务
    const formData = new FormData()
    formData.append('file', templateImageFile.value)
    formData.append('directory', 'answer-sheet-template')

    const uploadRes = await request.post<{ objectName: string; url: string }>('/file/upload', formData)
    const imagePath = uploadRes.data.objectName

    // 关联到模板
    const imageUrl = await uploadTemplateImage(templateForm.id, imagePath)
    templateForm.templateImagePath = imagePath
    templateForm.templateImageUrl = imageUrl.data

    ElMessage.success('模板图片已上传')
  } catch (error) {
    console.error('上传模板图片失败', error)
    ElMessage.error('上传模板图片失败')
  } finally {
    templateImageUploading.value = false
  }
}

const handleAutoDetectCorners = async () => {
  if (!templateImageFile.value) return

  cornerDetecting.value = true
  try {
    const result = await detectCorners(templateImageFile.value)
    if (result.data.success) {
      cornerConfig.value = {
        topLeft: { x: result.data.topLeftX || 5, y: result.data.topLeftY || 5 },
        topRight: { x: result.data.topRightX || 95, y: result.data.topRightY || 5 },
        bottomLeft: { x: result.data.bottomLeftX || 5, y: result.data.bottomLeftY || 95 },
        bottomRight: { x: result.data.bottomRightX || 95, y: result.data.bottomRightY || 95 },
        angle: result.data.angle || 0,
      }
      showCornerPanel.value = true
      ElMessage.success('四角定位点检测完成')
    } else {
      ElMessage.warning(result.data.errorMessage || '四角检测失败，请手动调整')
      // 使用默认值
      cornerConfig.value = {
        topLeft: { x: 5, y: 5 },
        topRight: { x: 95, y: 5 },
        bottomLeft: { x: 5, y: 95 },
        bottomRight: { x: 95, y: 95 },
        angle: 0,
      }
    }
  } catch (error) {
    console.error('四角检测失败', error)
    ElMessage.warning('四角检测失败，请手动设置定位点')
  } finally {
    cornerDetecting.value = false
  }
}

const handleSaveCornerConfig = async () => {
  if (!templateForm.id) {
    ElMessage.warning('请先保存模板')
    return
  }

  cornerDetecting.value = true
  try {
    await saveCornerConfig(templateForm.id, cornerConfig.value)
    templateForm.cornerConfig = { ...cornerConfig.value }
    ElMessage.success('定位配置已保存')
  } catch (error) {
    console.error('保存定位配置失败', error)
    ElMessage.error('保存定位配置失败')
  } finally {
    cornerDetecting.value = false
  }
}

const calculateExpectedBubbleCount = (region: AnswerSheetRegion) => {
  const questionStart = region.questionStart || 0
  const questionEnd = region.questionEnd || 0
  const optionCount = region.config?.optionCount || 0
  return Math.max(questionEnd - questionStart + 1, 0) * optionCount
}

interface DetectedComponent {
  x: number
  y: number
  width: number
  height: number
  area: number
  fillRatio: number
  centerX: number
  centerY: number
}

const loadImageElement = (url: string) => new Promise<HTMLImageElement>((resolve, reject) => {
  const image = new Image()
  image.onload = () => resolve(image)
  image.onerror = () => reject(new Error('样张图片加载失败'))
  image.src = url
})

const median = (values: number[]) => {
  if (values.length === 0) {
    return 0
  }
  const sorted = [...values].sort((a, b) => a - b)
  const middle = Math.floor(sorted.length / 2)
  return sorted.length % 2 === 0
    ? (sorted[middle - 1] + sorted[middle]) / 2
    : sorted[middle]
}

const computeOtsuThreshold = (grayValues: Uint8ClampedArray) => {
  const histogram = new Array<number>(256).fill(0)
  grayValues.forEach((value) => {
    histogram[value] += 1
  })

  const total = grayValues.length
  let sum = 0
  for (let i = 0; i < 256; i += 1) {
    sum += i * histogram[i]
  }

  let sumB = 0
  let weightB = 0
  let maxVariance = 0
  let threshold = 127

  for (let i = 0; i < 256; i += 1) {
    weightB += histogram[i]
    if (weightB === 0) {
      continue
    }

    const weightF = total - weightB
    if (weightF === 0) {
      break
    }

    sumB += i * histogram[i]
    const meanB = sumB / weightB
    const meanF = (sum - sumB) / weightF
    const betweenClassVariance = weightB * weightF * (meanB - meanF) ** 2

    if (betweenClassVariance > maxVariance) {
      maxVariance = betweenClassVariance
      threshold = i
    }
  }

  return threshold
}

const detectConnectedComponents = (
  grayValues: Uint8ClampedArray,
  width: number,
  height: number,
  threshold: number,
) => {
  const visited = new Uint8Array(width * height)
  const components: DetectedComponent[] = []
  const queueX = new Int32Array(width * height)
  const queueY = new Int32Array(width * height)

  const getIndex = (x: number, y: number) => y * width + x

  for (let y = 0; y < height; y += 1) {
    for (let x = 0; x < width; x += 1) {
      const index = getIndex(x, y)
      if (visited[index] || grayValues[index] > threshold) {
        continue
      }

      let head = 0
      let tail = 0
      queueX[tail] = x
      queueY[tail] = y
      tail += 1
      visited[index] = 1

      let minX = x
      let minY = y
      let maxX = x
      let maxY = y
      let area = 0

      while (head < tail) {
        const currentX = queueX[head]
        const currentY = queueY[head]
        head += 1
        area += 1

        minX = Math.min(minX, currentX)
        minY = Math.min(minY, currentY)
        maxX = Math.max(maxX, currentX)
        maxY = Math.max(maxY, currentY)

        const neighbors = [
          [currentX - 1, currentY],
          [currentX + 1, currentY],
          [currentX, currentY - 1],
          [currentX, currentY + 1],
        ]

        neighbors.forEach(([nextX, nextY]) => {
          if (nextX < 0 || nextX >= width || nextY < 0 || nextY >= height) {
            return
          }

          const nextIndex = getIndex(nextX, nextY)
          if (visited[nextIndex] || grayValues[nextIndex] > threshold) {
            return
          }

          visited[nextIndex] = 1
          queueX[tail] = nextX
          queueY[tail] = nextY
          tail += 1
        })
      }

      const componentWidth = maxX - minX + 1
      const componentHeight = maxY - minY + 1
      const boxArea = componentWidth * componentHeight

      components.push({
        x: minX,
        y: minY,
        width: componentWidth,
        height: componentHeight,
        area,
        fillRatio: boxArea > 0 ? area / boxArea : 0,
        centerX: minX + componentWidth / 2,
        centerY: minY + componentHeight / 2,
      })
    }
  }

  return components
}

const clusterRows = (components: DetectedComponent[], rowTolerance: number) => {
  const rows: DetectedComponent[][] = []
  components
    .slice()
    .sort((a, b) => a.centerY - b.centerY)
    .forEach((component) => {
      const lastRow = rows[rows.length - 1]
      if (!lastRow) {
        rows.push([component])
        return
      }

      const averageY = lastRow.reduce((sum, item) => sum + item.centerY, 0) / lastRow.length
      if (Math.abs(component.centerY - averageY) <= rowTolerance) {
        lastRow.push(component)
      } else {
        rows.push([component])
      }
    })

  return rows.map(row => row.sort((a, b) => a.centerX - b.centerX))
}

const pickBestWindow = (components: DetectedComponent[], expectedCount: number) => {
  if (components.length <= expectedCount) {
    return components
  }

  let bestWindow = components.slice(0, expectedCount)
  let bestScore = Number.NEGATIVE_INFINITY

  for (let start = 0; start <= components.length - expectedCount; start += 1) {
    const window = components.slice(start, start + expectedCount)
    const gapValues = window.slice(1).map((item, index) => item.centerX - window[index].centerX)
    const gapMedian = median(gapValues)
    const gapVariance = gapValues.reduce((sum, value) => sum + Math.abs(value - gapMedian), 0)
    const areaScore = window.reduce((sum, item) => sum + item.area, 0)
    const score = areaScore - gapVariance * 4

    if (score > bestScore) {
      bestScore = score
      bestWindow = window
    }
  }

  return bestWindow
}

const handleDetectChoiceBubbles = async (silent: boolean = false) => {
  const region = selectedChoiceRegion.value
  if (!region || !sampleImageUrl.value) {
    if (!silent) {
      ElMessage.warning('请先选择客观题区域并上传样张')
    }
    return false
  }

  bubbleDetectLoading.value = true
  try {
    const image = await loadImageElement(sampleImageUrl.value)
    const cropX = Math.max(Math.floor(((region.config?.boxX || 0) / 100) * image.width), 0)
    const cropY = Math.max(Math.floor(((region.config?.boxY || 0) / 100) * image.height), 0)
    const cropWidth = Math.max(Math.floor(((region.config?.boxWidth || 0) / 100) * image.width), 1)
    const cropHeight = Math.max(Math.floor(((region.config?.boxHeight || 0) / 100) * image.height), 1)

    const canvas = document.createElement('canvas')
    const maxDetectWidth = 1200
    const scale = cropWidth > maxDetectWidth ? maxDetectWidth / cropWidth : 1
    const detectWidth = Math.max(Math.floor(cropWidth * scale), 1)
    const detectHeight = Math.max(Math.floor(cropHeight * scale), 1)
    canvas.width = detectWidth
    canvas.height = detectHeight

    const context = canvas.getContext('2d')
    if (!context) {
      throw new Error('无法初始化图像识别画布')
    }

    context.drawImage(image, cropX, cropY, cropWidth, cropHeight, 0, 0, detectWidth, detectHeight)
    const imageData = context.getImageData(0, 0, detectWidth, detectHeight)
    const grayValues = new Uint8ClampedArray(detectWidth * detectHeight)
    for (let i = 0; i < imageData.data.length; i += 4) {
      const pixelIndex = i / 4
      grayValues[pixelIndex] = Math.round(
        imageData.data[i] * 0.299
        + imageData.data[i + 1] * 0.587
        + imageData.data[i + 2] * 0.114,
      )
    }

    const otsuThreshold = computeOtsuThreshold(grayValues)
    const threshold = Math.min(220, Math.round(otsuThreshold * 0.92))
    const allComponents = detectConnectedComponents(grayValues, detectWidth, detectHeight, threshold)
    const expectedBubbleCount = calculateExpectedBubbleCount(region)

    const roughCandidates = allComponents.filter((component) => {
      const aspectRatio = component.width / component.height
      return component.area >= 10
        && component.width >= 4
        && component.height >= 4
        && component.width <= detectWidth * 0.12
        && component.height <= detectHeight * 0.12
        && aspectRatio >= 0.45
        && aspectRatio <= 1.9
        && component.fillRatio >= 0.12
        && component.fillRatio <= 0.85
    })

    if (roughCandidates.length === 0) {
      throw new Error('没有检测到可用的选项气泡候选，请调整总框或更换更清晰的样张')
    }

    const sampleCandidates = roughCandidates
      .slice()
      .sort((a, b) => b.area - a.area)
      .slice(0, Math.max(expectedBubbleCount * 2, 20))

    const medianWidth = median(sampleCandidates.map(item => item.width))
    const medianHeight = median(sampleCandidates.map(item => item.height))
    const candidates = roughCandidates
      .filter((component) => (
        component.width >= medianWidth * 0.55
        && component.width <= medianWidth * 1.8
        && component.height >= medianHeight * 0.55
        && component.height <= medianHeight * 1.8
      ))
      .sort((a, b) => a.centerY - b.centerY)

    const questionCount = Math.max((region.questionEnd || 0) - (region.questionStart || 0) + 1, 0)
    const optionCount = region.config?.optionCount || 4
    const questionsPerRow = region.config?.questionsPerRow || 5
    const expectedRowCount = Math.ceil(questionCount / questionsPerRow)

    let rows = clusterRows(candidates, Math.max(medianHeight * 1.2, 8))
    while (rows.length > expectedRowCount && rows.length > 1) {
      let mergeIndex = 0
      let minDistance = Number.POSITIVE_INFINITY
      for (let i = 0; i < rows.length - 1; i += 1) {
        const currentY = rows[i].reduce((sum, item) => sum + item.centerY, 0) / rows[i].length
        const nextY = rows[i + 1].reduce((sum, item) => sum + item.centerY, 0) / rows[i + 1].length
        const distance = Math.abs(nextY - currentY)
        if (distance < minDistance) {
          minDistance = distance
          mergeIndex = i
        }
      }
      rows.splice(mergeIndex, 2, [...rows[mergeIndex], ...rows[mergeIndex + 1]].sort((a, b) => a.centerX - b.centerX))
    }

    const bubbleMap: BubbleMapItem[] = []
    for (let rowIndex = 0; rowIndex < rows.length; rowIndex += 1) {
      const questionsInRow = Math.min(questionsPerRow, questionCount - rowIndex * questionsPerRow)
      if (questionsInRow <= 0) {
        break
      }

      const expectedRowBubbleCount = questionsInRow * optionCount
      const rowComponents = pickBestWindow(rows[rowIndex], expectedRowBubbleCount)
      if (rowComponents.length < expectedRowBubbleCount) {
        continue
      }

      for (let questionOffset = 0; questionOffset < questionsInRow; questionOffset += 1) {
        const questionNo = (region.questionStart || 1) + rowIndex * questionsPerRow + questionOffset
        const optionComponents = rowComponents.slice(questionOffset * optionCount, (questionOffset + 1) * optionCount)
        optionComponents.forEach((component, optionIndex) => {
          bubbleMap.push({
            questionNo,
            option: String.fromCharCode(65 + optionIndex),
            x: (region.config?.boxX || 0) + (component.x / detectWidth) * (region.config?.boxWidth || 0),
            y: (region.config?.boxY || 0) + (component.y / detectHeight) * (region.config?.boxHeight || 0),
            width: (component.width / detectWidth) * (region.config?.boxWidth || 0),
            height: (component.height / detectHeight) * (region.config?.boxHeight || 0),
            confidence: Number((component.area / Math.max(medianWidth * medianHeight, 1)).toFixed(2)),
          })
        })
      }
    }

    if (bubbleMap.length === 0) {
      throw new Error('识别结果为空，请检查客观题总框是否覆盖到真实涂卡区')
    }

    const nextRegion = cloneRegion(region)
    nextRegion.config = {
      ...nextRegion.config,
      bubbleMap,
      detectedBubbleCount: bubbleMap.length,
      expectedBubbleCount,
    }

    handlePreviewRegionUpdate({
      index: selectedRegionIndex.value,
      region: nextRegion,
    })

    if (bubbleMap.length < expectedBubbleCount) {
      if (!silent) {
        ElMessage.warning(`仅识别到 ${bubbleMap.length}/${expectedBubbleCount} 个选项，请检查样张清晰度或适当缩小总框`)
      }
      return false
    }

    if (!silent) {
      ElMessage.success(`已识别 ${bubbleMap.length} 个选项气泡`)
    }
    return true
  } catch (error) {
    if (!silent && error instanceof Error) {
      ElMessage.warning(error.message)
    }
    return false
  } finally {
    bubbleDetectLoading.value = false
  }
}

const clearDetectedChoiceBubbles = () => {
  const region = selectedChoiceRegion.value
  if (!region) {
    return
  }

  const nextRegion = cloneRegion(region)
  nextRegion.config = {
    ...nextRegion.config,
    bubbleMap: [],
    detectedBubbleCount: 0,
  }
  lastAutoDetectSignature.value = ''
  handlePreviewRegionUpdate({
    index: selectedRegionIndex.value,
    region: nextRegion,
  })
}

const runAutoDetectChoiceBubbles = useDebounceFn(async (signature: string) => {
  if (!signature || !canDetectChoiceBubbles.value || signature === lastAutoDetectSignature.value) {
    return
  }

  // 如果区域已经有 bubbleMap（可能是从后端检测得到的），就不再自动检测
  const region = selectedChoiceRegion.value
  if (region?.config?.bubbleMap?.length) {
    lastAutoDetectSignature.value = signature
    return
  }

  autoDetecting.value = true
  try {
    const success = await handleDetectChoiceBubbles(true)
    if (success) {
      lastAutoDetectSignature.value = signature
    }
  } finally {
    autoDetecting.value = false
  }
}, 500)

watch(
  autoDetectSignature,
  async (signature) => {
    if (!signature) {
      return
    }
    await runAutoDetectChoiceBubbles(signature)
  },
  { flush: 'post' },
)

onMounted(async () => {
  const id = route.params.id as string
  const paperId = route.query.paperId as string

  if (id) {
    // 编辑模式
    await fetchTemplateDetail(id)

    // 加载四角配置
    if (templateForm.cornerConfig) {
      cornerConfig.value = templateForm.cornerConfig as CornerConfig
    }

    // 如果有模板图片，同时设置为样张
    if (templateForm.templateImageUrl) {
      sampleImageUrl.value = templateForm.templateImageUrl
      sampleImageName.value = '模板图片'
      sampleImageVisible.value = true
    }
  } else if (paperId) {
    // 新建模式，关联试卷
    templateForm.paperId = paperId
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

.tools-card-desc.draw-tip {
  margin-top: 12px;
  padding: 8px 10px;
  background: #fef3c7;
  border-radius: 8px;
  color: #92400e;
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

/* 模板图片面板样式 */
.template-image-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.panel-section {
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
}

.panel-desc {
  margin-top: 4px;
  font-size: 12px;
  color: #64748b;
}

.template-upload {
  margin-top: 12px;
}

.template-upload :deep(.el-upload-dragger) {
  padding: 32px 20px;
  border-radius: 12px;
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.upload-icon {
  font-size: 40px;
  color: #94a3b8;
}

.upload-text {
  font-size: 14px;
  color: #475569;
}

.upload-hint {
  font-size: 12px;
  color: #94a3b8;
}

.current-image-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12px;
  padding: 12px;
  background: #f0fdf4;
  border-radius: 8px;
}
</style>
