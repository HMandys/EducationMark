<template>
  <div class="region-list">
    <div class="region-header">
      <span>区域列表</span>
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
          <div class="region-item">
            <div class="region-item-left">
              <el-icon class="drag-handle"><Rank /></el-icon>
              <el-tag :type="getRegionTypeTag(element.regionType)" size="small">
                {{ getRegionTypeName(element.regionType) }}
              </el-tag>
              <span class="region-name">{{ element.regionName }}</span>
              <span class="region-range" v-if="element.questionStart && element.questionEnd">
                ({{ element.questionStart }}-{{ element.questionEnd }}题)
              </span>
            </div>
            <div class="region-item-right">
              <el-button type="primary" link size="small" @click="emit('edit', element, index)">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button type="danger" link size="small" @click="emit('delete', index)">
                <el-icon><Delete /></el-icon>
              </el-button>
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
import type { AnswerSheetRegion } from '@/api/answerSheetTemplate'

const props = defineProps<{
  modelValue?: AnswerSheetRegion[]
}>()

const emit = defineEmits<{
  'update:modelValue': [value: AnswerSheetRegion[]]
  add: []
  edit: [region: AnswerSheetRegion, index: number]
  delete: [index: number]
}>()

const localRegions = ref<AnswerSheetRegion[]>([])

watch(
  () => props.modelValue,
  (val) => {
    localRegions.value = val ? [...val] : []
  },
  { immediate: true }
)

const handleDragChange = () => {
  // 更新排序号
  localRegions.value.forEach((region, index) => {
    region.sortOrder = index
  })
  emit('update:modelValue', localRegions.value)
}

const regionTypeMap: Record<number, string> = {
  1: '选择题',
  2: '填空题',
  3: '解答题',
  4: '作文题',
}

const regionTypeTagMap: Record<number, 'primary' | 'success' | 'warning' | 'info'> = {
  1: 'primary',
  2: 'success',
  3: 'warning',
  4: 'info',
}

const getRegionTypeName = (type: number) => regionTypeMap[type] || '未知'
const getRegionTypeTag = (type: number) => regionTypeTagMap[type] || 'info'
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
  font-weight: 500;
}

.region-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.region-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
  cursor: move;
}

.region-item-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.drag-handle {
  cursor: grab;
  color: #909399;
}

.region-name {
  font-size: 14px;
  color: #303133;
}

.region-range {
  font-size: 12px;
  color: #909399;
}

.region-item-right {
  display: flex;
  gap: 4px;
}
</style>
