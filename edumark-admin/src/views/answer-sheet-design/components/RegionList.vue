<template>
  <div class="region-list">
    <div class="region-header">
      <div>
        <div class="region-title">区域列表</div>
        <div class="region-subtitle">先定义区域，再到右侧拖动校准坐标。</div>
      </div>
      <el-button type="primary" size="small" @click="emit('add')">
        <el-icon><Plus /></el-icon>添加区域
      </el-button>
    </div>

    <div class="region-items" v-if="modelValue && modelValue.length > 0">
      <draggable
        v-model="localRegions"
        item-key="sortOrder"
        handle=".drag-handle"
        @change="handleDragChange"
      >
        <template #item="{ element, index }">
          <div
            class="region-item"
            :class="{
              'is-selected': selectedIndex === index,
              'is-pending': !isAnnotated(element),
            }"
            @click="emit('select', index)"
          >
            <div class="region-item-main">
              <div class="region-item-top">
                <div class="region-item-meta">
                  <el-icon class="drag-handle"><Rank /></el-icon>
                  <el-tag :type="getRegionTypeTag(element.regionType)" size="small">
                    {{ getRegionTypeName(element.regionType) }}
                  </el-tag>
                  <span class="region-name">{{ element.regionName }}</span>
                </div>
                <div class="region-item-actions">
                  <el-button type="primary" link size="small" @click.stop="emit('edit', element, index)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                  <el-button type="danger" link size="small" @click.stop="emit('delete', index)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>

              <div class="region-item-info">
                <span>页码 {{ element.pageNo || 1 }}</span>
                <span v-if="element.questionStart && element.questionEnd">
                  {{ formatQuestionLabel(element) }}
                </span>
                <span>{{ getRegionRoleName(element) }}</span>
              </div>

              <div class="region-item-status">
                <el-tag size="small" :type="isAnnotated(element) ? 'success' : 'warning'">
                  {{ isAnnotated(element) ? '已标注' : '待标注' }}
                </el-tag>
                <span v-if="isAnnotated(element)" class="region-bounds">
                  {{ formatBounds(element) }}
                </span>
              </div>
            </div>
          </div>
        </template>
      </draggable>
    </div>

    <el-empty v-else description="暂无区域配置，请添加区域" :image-size="60" />
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import draggable from 'vuedraggable'
import { Plus, Edit, Delete, Rank } from '@element-plus/icons-vue'
import type { AnswerSheetRegion, RegionRole } from '@/api/answerSheetTemplate'

const props = defineProps<{
  modelValue?: AnswerSheetRegion[]
  selectedIndex?: number
}>()

const emit = defineEmits<{
  'update:modelValue': [value: AnswerSheetRegion[]]
  add: []
  edit: [region: AnswerSheetRegion, index: number]
  delete: [index: number]
  select: [index: number]
}>()

const localRegions = ref<AnswerSheetRegion[]>([])

watch(
  () => props.modelValue,
  (val) => {
    localRegions.value = val ? [...val] : []
  },
  { immediate: true },
)

const handleDragChange = () => {
  localRegions.value.forEach((region, index) => {
    region.sortOrder = index
  })
  emit('update:modelValue', localRegions.value)
}

const regionTypeMap: Record<number, string> = {
  1: '选择题',
  2: '填空题',
  3: '主观题',
}

const regionTypeTagMap: Record<number, 'primary' | 'success' | 'warning' | 'info'> = {
  1: 'primary',
  2: 'warning',
  3: 'warning',
  4: 'info',
}

const regionRoleMap: Record<RegionRole, string> = {
  choice_block: '客观题涂卡区',
  subjective_crop: '主观题裁题区',
  essay_crop: '主观题裁题区',
  score_box: '评分框',
  student_id: '学号识别区',
  student_name: '姓名识别区',
  class_name: '班级识别区',
  barcode: '条码区',
}

const getRegionTypeName = (type: number) => regionTypeMap[type] || '未知'
const getRegionTypeTag = (type: number) => regionTypeTagMap[type] || 'info'
const getRegionRoleName = (region: AnswerSheetRegion) => {
  const role = region.config?.regionRole
  if (!role) {
    return '未设置用途'
  }
  if (role === 'subjective_crop' && region.regionType === 2) {
    return '填空题裁题区'
  }
  return regionRoleMap[role] || '未设置用途'
}
const formatQuestionLabel = (region: AnswerSheetRegion) => {
  const start = region.questionStart
  const end = region.questionEnd
  if (!start || !end) {
    return ''
  }
  return start === end ? `第 ${start} 题` : `${start}-${end} 题`
}

const isAnnotated = (region: AnswerSheetRegion) => {
  const config = region.config
  return config?.boxX !== undefined
    && config?.boxY !== undefined
    && config?.boxWidth !== undefined
    && config?.boxHeight !== undefined
}

const formatBounds = (region: AnswerSheetRegion) => {
  const config = region.config
  return `X${config?.boxX}% / Y${config?.boxY}% / ${config?.boxWidth}% × ${config?.boxHeight}%`
}
</script>

<style scoped>
.region-list {
  padding: 8px 0;
}

.region-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.region-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
}

.region-subtitle {
  margin-top: 4px;
  font-size: 12px;
  color: #6b7280;
}

.region-items {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.region-item {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #fff;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.region-item:hover {
  border-color: #cbd5e1;
  box-shadow: 0 8px 16px rgba(15, 23, 42, 0.06);
}

.region-item.is-selected {
  border-color: #2563eb;
  background: #eff6ff;
}

.region-item.is-pending {
  border-style: dashed;
}

.region-item-main {
  padding: 12px 14px;
}

.region-item-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.region-item-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.region-item-actions {
  display: flex;
  align-items: center;
}

.drag-handle {
  cursor: grab;
  color: #9ca3af;
}

.region-name {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
}

.region-item-info,
.region-item-status {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 8px;
  font-size: 12px;
  color: #6b7280;
}

.region-bounds {
  color: #475569;
}
</style>
