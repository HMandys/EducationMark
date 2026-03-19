<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="emit('update:visible', $event)"
    :title="dialogTitle"
    width="720px"
    destroy-on-close
  >
    <div v-if="result" class="validation-dialog">
      <div class="validation-summary" :class="{ 'is-passed': result.passed, 'is-failed': !result.passed }">
        <div class="validation-summary-title">
          {{ result.passed ? '模板校验通过' : '模板校验未通过' }}
        </div>
        <div class="validation-summary-desc">
          {{ templateName || '当前模板' }} 已标注 {{ result.annotatedRegionCount }}/{{ result.totalRegionCount }} 个区域
        </div>
      </div>

      <div class="validation-stats">
        <div class="validation-stat-card">
          <div class="stat-label">区域总数</div>
          <div class="stat-value">{{ result.totalRegionCount }}</div>
        </div>
        <div class="validation-stat-card">
          <div class="stat-label">已标注</div>
          <div class="stat-value">{{ result.annotatedRegionCount }}</div>
        </div>
        <div class="validation-stat-card">
          <div class="stat-label">问题数</div>
          <div class="stat-value">{{ result.issueCount }}</div>
        </div>
      </div>

      <div class="validation-progress">
        <div class="progress-label">
          标注完成度
          <span>{{ progressText }}</span>
        </div>
        <el-progress :percentage="progressPercent" :status="result.passed ? 'success' : undefined" :stroke-width="10" />
      </div>

      <div v-if="result.issues.length > 0" class="validation-issues">
        <div class="issues-title">待处理项</div>
        <div class="issues-list">
          <div v-for="(issue, index) in result.issues" :key="`${issue.field}-${index}`" class="issue-item">
            <div class="issue-index">{{ index + 1 }}</div>
            <div class="issue-content">
              <div class="issue-message">{{ issue.message }}</div>
              <div v-if="issue.regionName || issue.field" class="issue-meta">
                <span v-if="issue.regionName">{{ issue.regionName }}</span>
                <span v-if="issue.field">字段：{{ issue.field }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-else description="当前模板已经满足发布前校验要求" :image-size="88" />
    </div>

    <template #footer>
      <el-button @click="emit('update:visible', false)">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { TemplateValidationResult } from '@/api/answerSheetTemplate'

const props = defineProps<{
  visible: boolean
  templateName?: string
  result: TemplateValidationResult | null
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
}>()

const dialogTitle = computed(() => props.templateName ? `模板校验 - ${props.templateName}` : '模板校验结果')

const progressPercent = computed(() => {
  const result = props.result
  if (!result || result.totalRegionCount === 0) {
    return 0
  }
  return Math.round((result.annotatedRegionCount / result.totalRegionCount) * 100)
})

const progressText = computed(() => `${progressPercent.value}%`)
</script>

<style scoped>
.validation-dialog {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.validation-summary {
  padding: 16px 18px;
  border-radius: 14px;
  border: 1px solid #e5e7eb;
}

.validation-summary.is-passed {
  background: #ecfdf5;
  border-color: #a7f3d0;
}

.validation-summary.is-failed {
  background: #fff7ed;
  border-color: #fdba74;
}

.validation-summary-title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.validation-summary-desc {
  margin-top: 6px;
  font-size: 13px;
  color: #4b5563;
}

.validation-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.validation-stat-card {
  padding: 14px 16px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.stat-label {
  font-size: 12px;
  color: #64748b;
}

.stat-value {
  margin-top: 8px;
  font-size: 26px;
  font-weight: 700;
  color: #0f172a;
}

.validation-progress {
  padding: 14px 16px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid #e5e7eb;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 13px;
  color: #475569;
}

.issues-title {
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
}

.issues-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.issue-item {
  display: flex;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 12px;
  border: 1px solid #fed7aa;
  background: #fffaf0;
}

.issue-index {
  width: 24px;
  height: 24px;
  border-radius: 999px;
  background: #f97316;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}

.issue-content {
  min-width: 0;
}

.issue-message {
  font-size: 13px;
  color: #1f2937;
  line-height: 1.5;
}

.issue-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 6px;
  font-size: 12px;
  color: #6b7280;
}
</style>
