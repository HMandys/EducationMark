<template>
  <div class="answer-config-panel">
    <div class="panel-header">
      <div class="panel-title">正确答案配置</div>
      <div class="panel-desc">
        为客观题区域设置每道题的正确答案，支持多选（如 AB、CD）
      </div>
    </div>

    <div v-if="!region" class="no-region-tip">
      请先选择一个客观题区域
    </div>

    <template v-else>
      <div class="region-info">
        <el-tag>{{ region.regionName }}</el-tag>
        <span class="question-range">
          第 {{ region.questionStart }} - {{ region.questionEnd }} 题
          （共 {{ questionCount }} 题）
        </span>
      </div>

      <div class="answer-grid">
        <div
          v-for="q in questionRange"
          :key="q"
          class="answer-item"
        >
          <div class="question-no">{{ q }}.</div>
          <div class="option-group">
            <button
              v-for="opt in optionLabels"
              :key="opt"
              type="button"
              class="option-btn"
              :class="{ 'is-selected': isOptionSelected(q, opt) }"
              @click="toggleOption(q, opt)"
            >
              {{ opt }}
            </button>
          </div>
          <div class="answer-display">
            {{ getAnswer(q) || '-' }}
          </div>
        </div>
      </div>

      <div class="panel-actions">
        <el-button type="primary" @click="handleSave" :loading="saving">
          保存答案
        </el-button>
        <el-button @click="handleClear">
          清空全部
        </el-button>
      </div>

      <div class="quick-input">
        <div class="quick-input-title">快速输入</div>
        <div class="quick-input-desc">
          格式：题号-答案，用逗号或空格分隔，如：1-A,2-B,3-CD,4-A
        </div>
        <el-input
          v-model="quickInputText"
          type="textarea"
          :rows="3"
          placeholder="1-A,2-B,3-CD,4-A"
        />
        <el-button @click="applyQuickInput" style="margin-top: 8px;">
          应用
        </el-button>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { AnswerSheetRegion } from '@/api/answerSheetTemplate'

const props = defineProps<{
  region?: AnswerSheetRegion | null
  correctAnswers?: Record<string, string>
}>()

const emit = defineEmits<{
  'update:correctAnswers': [answers: Record<string, string>]
  save: [answers: Record<string, string>]
}>()

const saving = ref(false)
const quickInputText = ref('')
const localAnswers = ref<Record<string, string>>({})

const questionCount = computed(() => {
  if (!props.region) return 0
  return (props.region.questionEnd || 0) - (props.region.questionStart || 0) + 1
})

const questionRange = computed(() => {
  if (!props.region) return []
  const start = props.region.questionStart || 1
  const end = props.region.questionEnd || start
  return Array.from({ length: end - start + 1 }, (_, i) => start + i)
})

const optionLabels = computed(() => {
  const count = props.region?.config?.optionCount || 4
  return Array.from({ length: count }, (_, i) => String.fromCharCode(65 + i))
})

const getAnswer = (questionNo: number) => {
  return localAnswers.value[String(questionNo)] || ''
}

const isOptionSelected = (questionNo: number, option: string) => {
  const answer = getAnswer(questionNo)
  return answer.includes(option)
}

const toggleOption = (questionNo: number, option: string) => {
  const key = String(questionNo)
  const current = localAnswers.value[key] || ''

  let newAnswer: string
  if (current.includes(option)) {
    // 取消选中
    newAnswer = current.replace(option, '')
  } else {
    // 选中，保持字母顺序
    const chars = (current + option).split('').sort()
    newAnswer = chars.join('')
  }

  localAnswers.value = {
    ...localAnswers.value,
    [key]: newAnswer,
  }
}

const handleSave = async () => {
  saving.value = true
  try {
    emit('save', { ...localAnswers.value })
    ElMessage.success('答案已保存')
  } finally {
    saving.value = false
  }
}

const handleClear = () => {
  localAnswers.value = {}
}

const applyQuickInput = () => {
  const text = quickInputText.value.trim()
  if (!text) return

  const newAnswers: Record<string, string> = { ...localAnswers.value }

  // 支持多种分隔符
  const parts = text.split(/[,，\s]+/)

  for (const part of parts) {
    const match = part.match(/^(\d+)\s*[-:：]\s*([A-Za-z]+)$/)
    if (match) {
      const questionNo = match[1]
      const answer = match[2].toUpperCase().split('').sort().join('')
      newAnswers[questionNo] = answer
    }
  }

  localAnswers.value = newAnswers
  quickInputText.value = ''
  ElMessage.success('已应用快速输入')
}

// 同步外部传入的答案
watch(() => props.correctAnswers, (newVal) => {
  localAnswers.value = newVal ? { ...newVal } : {}
}, { immediate: true })

// 区域变化时重置
watch(() => props.region, () => {
  if (props.region?.config?.correctAnswers) {
    localAnswers.value = { ...props.region.config.correctAnswers }
  } else {
    localAnswers.value = {}
  }
})
</script>

<style scoped>
.answer-config-panel {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px;
}

.panel-header {
  margin-bottom: 16px;
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

.no-region-tip {
  padding: 40px 0;
  text-align: center;
  color: #94a3b8;
  font-size: 14px;
}

.region-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.question-range {
  font-size: 13px;
  color: #64748b;
}

.answer-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
  max-height: 400px;
  overflow-y: auto;
  padding-right: 8px;
}

.answer-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f8fafc;
  border-radius: 8px;
}

.question-no {
  min-width: 32px;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.option-group {
  display: flex;
  gap: 4px;
}

.option-btn {
  width: 28px;
  height: 28px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  background: #fff;
  font-size: 12px;
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
  min-width: 40px;
  text-align: center;
  font-size: 14px;
  font-weight: 600;
  color: #059669;
}

.panel-actions {
  display: flex;
  gap: 8px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.quick-input {
  margin-top: 16px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
}

.quick-input-title {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
}

.quick-input-desc {
  margin-top: 4px;
  margin-bottom: 8px;
  font-size: 12px;
  color: #64748b;
}
</style>
