<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="emit('update:visible', $event)"
    :title="region?.id ? '编辑区域' : '添加区域'"
    width="760px"
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="110px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="区域类型" prop="regionType">
            <el-select v-model="formData.regionType" @change="handleTypeChange">
              <el-option label="选择题" :value="1" />
              <el-option label="填空题" :value="2" />
              <el-option label="解答题" :value="3" />
              <el-option label="作文题" :value="4" />
              <el-option label="条码区" :value="5" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="区域用途">
            <el-select v-model="formData.config!.regionRole" placeholder="请选择区域用途">
              <el-option
                v-for="item in regionRoleOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="区域名称" prop="regionName">
        <el-input v-model="formData.regionName" placeholder="请输入区域名称" />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="页码">
            <el-input-number v-model="formData.pageNo" :min="1" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="裁题模式">
            <el-select v-model="formData.config!.cropMode" placeholder="请选择裁题模式" clearable>
              <el-option label="单题裁切" value="single-question" />
              <el-option label="题段裁切" value="range-question" />
              <el-option label="整块裁切" value="full-region" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="起始题号">
            <el-input-number v-model="formData.questionStart" :min="1" :disabled="!requiresQuestionRange" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="结束题号">
            <el-input-number
              v-model="formData.questionEnd"
              :min="formData.questionStart || 1"
              :disabled="!requiresQuestionRange"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-divider content-position="left">坐标标注</el-divider>

      <div class="coordinate-toolbar" :class="{ 'has-coords': hasCoordinates }">
        <div class="coordinate-tip-wrapper">
          <span class="coordinate-tip">坐标统一按当前整页百分比保存，支持在右侧预览区继续拖拽微调。</span>
          <el-tag v-if="isFromDrawMode" type="success" size="small" effect="plain">
            <el-icon><Check /></el-icon>
            已从拉框获取坐标
          </el-tag>
        </div>
        <el-button text type="primary" @click="fillDefaultBounds">填充默认框</el-button>
      </div>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="X(%)" :class="{ 'coord-filled': formData.config?.boxX !== undefined }">
            <el-input-number v-model="formData.config!.boxX" :min="0" :max="100" :precision="1" :step="0.5" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Y(%)" :class="{ 'coord-filled': formData.config?.boxY !== undefined }">
            <el-input-number v-model="formData.config!.boxY" :min="0" :max="100" :precision="1" :step="0.5" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="宽度(%)" :class="{ 'coord-filled': formData.config?.boxWidth !== undefined }">
            <el-input-number v-model="formData.config!.boxWidth" :min="1" :max="100" :precision="1" :step="0.5" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="高度(%)" :class="{ 'coord-filled': formData.config?.boxHeight !== undefined }">
            <el-input-number v-model="formData.config!.boxHeight" :min="1" :max="100" :precision="1" :step="0.5" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="锚点类型">
            <el-select v-model="formData.config!.anchorType" placeholder="请选择锚点类型">
              <el-option label="无锚点" value="none" />
              <el-option label="角标" value="corner" />
              <el-option label="定位标记" value="marker" />
              <el-option label="条码锚点" value="barcode" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="锚点标识">
            <el-input v-model="formData.config!.anchorKey" placeholder="例如 left-top-marker" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-divider content-position="left">区域配置</el-divider>

      <template v-if="formData.regionType === 1">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选项数">
              <el-input-number v-model="formData.config!.optionCount" :min="2" :max="10" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每组题数">
              <el-input-number v-model="formData.config!.questionsPerRow" :min="1" :max="20" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="布局方向">
              <el-radio-group v-model="formData.config!.layoutDirection">
                <el-radio value="column">纵向(1,2,3竖排)</el-radio>
                <el-radio value="row">横向(1,2,3横排)</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="涂卡样式">
              <el-radio-group v-model="formData.config!.bubbleStyle">
                <el-radio value="circle">圆形</el-radio>
                <el-radio value="square">方形</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="包含多选">
          <el-switch v-model="formData.config!.hasMultipleChoice" />
        </el-form-item>

        <el-divider content-position="left">选项位置检测</el-divider>
        <div class="detect-section">
          <div class="detect-tip">
            填写题号范围后，点击自动检测，系统将识别每道题的 ABCD 选项位置
          </div>
          <div class="detect-actions">
            <el-button
              type="primary"
              @click="handleDetectBubbles"
              :loading="detectingBubbles"
              :disabled="!formData.questionStart || !formData.questionEnd"
            >
              自动检测选项位置
            </el-button>
            <span v-if="formData.config?.bubbleMap?.length" class="detect-result">
              已检测到 {{ formData.config.bubbleMap.length }} 个选项
            </span>
          </div>
        </div>

        <el-divider content-position="left">分数配置</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="每题分数">
              <el-input-number v-model="formData.config!.scorePerQuestion" :min="0" :max="100" :precision="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="区域总分">
              <span class="total-score">{{ calculateRegionTotalScore() }} 分</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">正确答案配置</el-divider>
        <div class="answer-config-section">
          <div class="answer-config-tip">
            为每道题设置正确答案，多选题直接填写多个选项（如 AB、CD）
          </div>
          <div class="answer-grid">
            <div
              v-for="q in getQuestionRange()"
              :key="q"
              class="answer-item"
            >
              <span class="question-no">{{ q }}.</span>
              <div class="option-group">
                <button
                  v-for="opt in getOptionLabels()"
                  :key="opt"
                  type="button"
                  class="option-btn"
                  :class="{ 'is-selected': isOptionSelected(q, opt) }"
                  @click.prevent="toggleOption(q, opt)"
                >
                  {{ opt }}
                </button>
              </div>
              <span class="answer-display">{{ getAnswer(q) || '-' }}</span>
            </div>
          </div>
          <div class="quick-input-row">
            <el-input
              v-model="quickAnswerInput"
              placeholder="快速输入: 1-A,2-B,3-CD"
              size="small"
            />
            <el-button size="small" @click="applyQuickInput">应用</el-button>
          </div>
        </div>
      </template>

      <template v-if="formData.regionType === 2">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="行高(mm)">
              <el-input-number v-model="formData.config!.lineHeight" :min="20" :max="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每题行数">
              <el-input-number v-model="formData.config!.linesPerQuestion" :min="1" :max="5" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="线条样式">
          <el-radio-group v-model="formData.config!.lineStyle">
            <el-radio value="underline">下划线</el-radio>
            <el-radio value="box">方框</el-radio>
          </el-radio-group>
        </el-form-item>
      </template>

      <template v-if="formData.regionType === 3">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="区域总分">
              <el-input-number v-model="formData.config!.totalScore" :min="0" :max="200" :precision="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="区域高度(mm)">
              <el-input-number v-model="formData.config!.height" :min="10" :max="500" :step="10" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="显示边框">
          <el-switch v-model="formData.config!.showBorder" />
        </el-form-item>
        <el-form-item label="评分框位置">
          <el-select v-model="formData.config!.scoreBoxPosition">
            <el-option label="右上角" value="top-right" />
            <el-option label="左上角" value="top-left" />
            <el-option label="右下角" value="bottom-right" />
            <el-option label="左下角" value="bottom-left" />
          </el-select>
        </el-form-item>
      </template>

      <template v-if="formData.regionType === 4">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="作文总分">
              <el-input-number v-model="formData.config!.totalScore" :min="0" :max="200" :precision="1" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="格子类型">
          <el-radio-group v-model="formData.config!.gridType">
            <el-radio value="square">方格</el-radio>
            <el-radio value="line">横线</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="格子大小(mm)">
              <el-input-number v-model="formData.config!.gridSize" :min="6" :max="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总字数">
              <el-input-number v-model="formData.config!.wordCount" :min="100" :max="2000" :step="100" />
            </el-form-item>
          </el-col>
        </el-row>
      </template>
    </el-form>

    <template #footer>
      <el-button @click="emit('update:visible', false)">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Check } from '@element-plus/icons-vue'
import type {
  AnswerSheetRegion,
  RegionConfig,
  RegionRole,
} from '@/api/answerSheetTemplate'

const props = defineProps<{
  visible: boolean
  region: AnswerSheetRegion | null
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  confirm: [region: AnswerSheetRegion]
  'detect-bubbles': [region: AnswerSheetRegion, callback: (result: BubbleDetectResult | null) => void]
}>()

interface BubbleDetectResult {
  bubbleMap: Array<{
    questionNo: number
    option: string
    x: number
    y: number
    width: number
    height: number
  }>
  detectedCount: number
  expectedCount: number
}

const detectingBubbles = ref(false)

// 检查坐标是否已填充（用于视觉提示）
const hasCoordinates = computed(() => {
  const config = formData.config
  return config?.boxX !== undefined
    && config?.boxY !== undefined
    && config?.boxWidth !== undefined
    && config?.boxHeight !== undefined
})

// 检查是否来自拉框创建（坐标已填充且是新区域）
const isFromDrawMode = computed(() => {
  return hasCoordinates.value && !props.region?.id
})

const formRef = ref<FormInstance>()
const quickAnswerInput = ref('')

const regionRoleOptions: Array<{ label: string; value: RegionRole }> = [
  { label: '客观题涂卡区', value: 'choice_block' },
  { label: '主观题裁题区', value: 'subjective_crop' },
  { label: '作文裁题区', value: 'essay_crop' },
  { label: '评分框', value: 'score_box' },
  { label: '学号识别区', value: 'student_id' },
  { label: '姓名识别区', value: 'student_name' },
  { label: '班级识别区', value: 'class_name' },
  { label: '条码区', value: 'barcode' },
]

const regionTypeNames: Record<number, string> = {
  1: '选择题',
  2: '填空题',
  3: '解答题',
  4: '作文题',
  5: '条码区',
}

const getDefaultBounds = (type: number) => {
  switch (type) {
    case 1:
      return { boxX: 8, boxY: 22, boxWidth: 84, boxHeight: 20 }
    case 2:
      return { boxX: 8, boxY: 44, boxWidth: 84, boxHeight: 14 }
    case 3:
      return { boxX: 8, boxY: 60, boxWidth: 84, boxHeight: 18 }
    case 4:
      return { boxX: 8, boxY: 22, boxWidth: 84, boxHeight: 58 }
    case 5:
      return { boxX: 70, boxY: 5, boxWidth: 25, boxHeight: 8 } // 条码区默认在右上角
    default:
      return { boxX: 8, boxY: 22, boxWidth: 84, boxHeight: 16 }
  }
}

const getDefaultRole = (type: number): RegionRole => {
  switch (type) {
    case 1:
      return 'choice_block'
    case 4:
      return 'essay_crop'
    case 5:
      return 'barcode'
    default:
      return 'subjective_crop'
  }
}

const getDefaultConfig = (type: number): RegionConfig => {
  const bounds = getDefaultBounds(type)
  const base: RegionConfig = {
    ...bounds,
    regionRole: getDefaultRole(type),
    anchorType: 'none',
    cropMode: type === 1 ? 'range-question' : type === 4 ? 'full-region' : 'single-question',
  }

  switch (type) {
    case 1:
      return {
        ...base,
        optionCount: 4,
        questionsPerRow: 5,
        layoutDirection: 'column', // 默认纵向布局（更常见）
        bubbleStyle: 'square',
        hasMultipleChoice: false,
        scorePerQuestion: 2, // 客观题默认每题2分
      }
    case 2:
      return {
        ...base,
        lineHeight: 30,
        linesPerQuestion: 1,
        lineStyle: 'underline',
        totalScore: 10, // 填空题默认10分
      }
    case 3:
      return {
        ...base,
        height: 100,
        showBorder: true,
        scoreBoxPosition: 'top-right',
        totalScore: 10, // 主观题默认10分
      }
    case 4:
      return {
        ...base,
        gridType: 'square',
        gridSize: 10,
        wordCount: 800,
        totalScore: 60, // 作文默认60分
      }
    case 5:
      return {
        ...base,
        regionRole: 'barcode',
        cropMode: 'full-region',
      }
    default:
      return base
  }
}

const cloneRegion = (region: AnswerSheetRegion): AnswerSheetRegion => ({
  ...region,
  questionIds: region.questionIds ? [...region.questionIds] : undefined,
  config: {
    ...getDefaultConfig(region.regionType),
    ...(region.config || {}),
  },
})

const formData = reactive<AnswerSheetRegion>({
  regionType: 1,
  regionName: '选择题',
  pageNo: 1,
  sortOrder: 0,
  questionStart: 1,
  questionEnd: 10,
  config: getDefaultConfig(1),
})

const formRules: FormRules = {
  regionType: [{ required: true, message: '请选择区域类型', trigger: 'change' }],
  regionName: [{ required: true, message: '请输入区域名称', trigger: 'blur' }],
}

const requiresQuestionRange = computed(() => {
  // 条码区不需要题号范围
  if (formData.regionType === 5) return false
  const role = formData.config?.regionRole
  return role === 'choice_block' || role === 'subjective_crop' || role === 'essay_crop' || !role
})

// 分数计算
const calculateRegionTotalScore = () => {
  const start = formData.questionStart || 0
  const end = formData.questionEnd || 0
  const questionCount = Math.max(end - start + 1, 0)
  const scorePerQuestion = formData.config?.scorePerQuestion || 0
  return questionCount * scorePerQuestion
}

// 正确答案配置相关方法
const getQuestionRange = () => {
  const start = formData.questionStart || 1
  const end = formData.questionEnd || start
  return Array.from({ length: Math.max(end - start + 1, 0) }, (_, i) => start + i)
}

const getOptionLabels = () => {
  const count = formData.config?.optionCount || 4
  return Array.from({ length: count }, (_, i) => String.fromCharCode(65 + i))
}

const getAnswer = (questionNo: number) => {
  return formData.config?.correctAnswers?.[String(questionNo)] || ''
}

const isOptionSelected = (questionNo: number, option: string) => {
  const answer = getAnswer(questionNo)
  return answer.includes(option)
}

const toggleOption = (questionNo: number, option: string) => {
  const key = String(questionNo)
  const current = formData.config?.correctAnswers?.[key] || ''

  let newAnswer: string
  if (current.includes(option)) {
    newAnswer = current.replace(option, '')
  } else {
    const chars = (current + option).split('').sort()
    newAnswer = chars.join('')
  }

  if (!formData.config) {
    formData.config = {}
  }
  if (!formData.config.correctAnswers) {
    formData.config.correctAnswers = {}
  }
  formData.config.correctAnswers[key] = newAnswer
}

const applyQuickInput = () => {
  const text = quickAnswerInput.value.trim()
  if (!text) return

  if (!formData.config) {
    formData.config = {}
  }
  if (!formData.config.correctAnswers) {
    formData.config.correctAnswers = {}
  }

  const parts = text.split(/[,，\s]+/)
  for (const part of parts) {
    const match = part.match(/^(\d+)\s*[-:：]\s*([A-Za-z]+)$/)
    if (match) {
      const questionNo = match[1]
      const answer = match[2].toUpperCase().split('').sort().join('')
      formData.config.correctAnswers[questionNo] = answer
    }
  }

  quickAnswerInput.value = ''
  ElMessage.success('已应用快速输入')
}

watch(
  () => props.visible,
  (val) => {
    if (!val) {
      return
    }

    const nextRegion = props.region
      ? cloneRegion(props.region)
      : {
          regionType: 1,
          regionName: '选择题',
          pageNo: 1,
          sortOrder: 0,
          questionStart: 1,
          questionEnd: 10,
          config: getDefaultConfig(1),
        }

    Object.assign(formData, nextRegion)
  },
)

const fillDefaultBounds = () => {
  Object.assign(formData.config!, getDefaultBounds(formData.regionType))
}

const handleTypeChange = (type: number) => {
  const commonConfig: RegionConfig = {
    boxX: formData.config?.boxX,
    boxY: formData.config?.boxY,
    boxWidth: formData.config?.boxWidth,
    boxHeight: formData.config?.boxHeight,
    anchorType: formData.config?.anchorType,
    anchorKey: formData.config?.anchorKey,
  }

  formData.regionName = regionTypeNames[type] || '未知区域'
  formData.config = {
    ...getDefaultConfig(type),
    ...commonConfig,
  }

  if (
    commonConfig.boxX === undefined
    || commonConfig.boxY === undefined
    || commonConfig.boxWidth === undefined
    || commonConfig.boxHeight === undefined
  ) {
    fillDefaultBounds()
  }
}

const validateBounds = () => {
  const config = formData.config || {}
  const values = [
    { key: 'boxX', label: 'X', value: config.boxX },
    { key: 'boxY', label: 'Y', value: config.boxY },
    { key: 'boxWidth', label: '宽度', value: config.boxWidth },
    { key: 'boxHeight', label: '高度', value: config.boxHeight },
  ]

  const hasAny = values.some(item => item.value !== undefined && item.value !== null)
  if (!hasAny) {
    return
  }

  const missing = values.find(item => item.value === undefined || item.value === null)
  if (missing) {
    throw new Error(`请补全${missing.label}坐标`)
  }

  values.forEach((item) => {
    if (typeof item.value !== 'number' || Number.isNaN(item.value)) {
      throw new Error(`${item.label}坐标格式不正确`)
    }
    if (item.value < 0 || item.value > 100) {
      throw new Error(`${item.label}坐标必须在 0 到 100 之间`)
    }
  })

  if ((config.boxWidth || 0) <= 0 || (config.boxHeight || 0) <= 0) {
    throw new Error('区域宽高必须大于 0')
  }

  if (
    (config.boxX || 0) + (config.boxWidth || 0) > 100
    || (config.boxY || 0) + (config.boxHeight || 0) > 100
  ) {
    throw new Error('区域坐标超出页面范围，请调整坐标或尺寸')
  }
}

const validateQuestionRange = () => {
  if (!requiresQuestionRange.value) {
    return
  }
  if (!formData.questionStart || !formData.questionEnd) {
    throw new Error('当前区域用途需要填写起止题号')
  }
  if (formData.questionStart > formData.questionEnd) {
    throw new Error('结束题号不能小于起始题号')
  }
}

const handleDetectBubbles = () => {
  detectingBubbles.value = true

  emit('detect-bubbles', cloneRegion(formData), (result) => {
    detectingBubbles.value = false

    if (result && result.bubbleMap) {
      if (!formData.config) {
        formData.config = {}
      }
      formData.config.bubbleMap = result.bubbleMap
      formData.config.detectedBubbleCount = result.detectedCount
      formData.config.expectedBubbleCount = result.expectedCount
    }
  })
}

const handleConfirm = async () => {
  try {
    await formRef.value?.validate()
    validateQuestionRange()
    validateBounds()
    emit('confirm', cloneRegion(formData))
  } catch (error) {
    if (error instanceof Error) {
      ElMessage.warning(error.message)
    }
  }
}
</script>

<style scoped>
.el-input-number,
.el-select {
  width: 100%;
}

.coordinate-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  padding: 10px 12px;
  background: #f6f8fb;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
}

.coordinate-toolbar.has-coords {
  background: #f0fdf4;
  border-color: #86efac;
}

.coordinate-tip-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.coordinate-tip {
  font-size: 12px;
  color: #606266;
}

/* 坐标已填充时的视觉提示 */
:deep(.coord-filled .el-input-number) {
  border-color: #86efac;
}

:deep(.coord-filled .el-input-number__wrapper) {
  box-shadow: 0 0 0 1px #86efac inset;
}

/* 分数显示样式 */
.total-score {
  display: inline-block;
  padding: 4px 12px;
  font-size: 16px;
  font-weight: 600;
  color: #059669;
  background: #d1fae5;
  border-radius: 6px;
}

/* 正确答案配置样式 */
.answer-config-section {
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
}

.answer-config-tip {
  margin-bottom: 12px;
  font-size: 12px;
  color: #64748b;
}

.answer-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 8px;
  max-height: 240px;
  overflow-y: auto;
  padding-right: 4px;
}

.answer-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  background: #fff;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
}

.answer-item .question-no {
  min-width: 28px;
  font-size: 13px;
  font-weight: 600;
  color: #374151;
}

.option-group {
  display: flex;
  gap: 3px;
}

.option-btn {
  width: 24px;
  height: 24px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  background: #fff;
  font-size: 11px;
  font-weight: 600;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.15s;
}

.option-btn:hover {
  border-color: #2563eb;
  color: #2563eb;
}

.option-btn.is-selected {
  background: #2563eb;
  border-color: #2563eb;
  color: #fff;
}

.answer-display {
  margin-left: auto;
  min-width: 32px;
  text-align: center;
  font-size: 13px;
  font-weight: 600;
  color: #059669;
}

.quick-input-row {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.quick-input-row .el-input {
  flex: 1;
}
</style>
