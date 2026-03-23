<template>
  <div class="corner-detect-panel">
    <div class="panel-header">
      <div class="panel-title">四角定位点</div>
      <div class="panel-desc">
        系统自动检测黑色实心方块定位点，可拖拽微调位置
      </div>
    </div>

    <div class="corner-preview" ref="previewRef">
      <img
        v-if="imageUrl"
        :src="imageUrl"
        class="corner-image"
        @load="handleImageLoad"
      />

      <template v-if="imageLoaded">
        <div
          v-for="corner in cornerKeys"
          :key="corner"
          class="corner-marker"
          :class="[`corner-${corner}`, { 'is-dragging': draggingCorner === corner }]"
          :style="getCornerStyle(corner)"
          @mousedown.stop="startDrag($event, corner)"
        >
          <div class="marker-label">{{ cornerLabels[corner] }}</div>
          <div class="marker-coords">
            {{ formatCoord(corners[corner]?.x) }}, {{ formatCoord(corners[corner]?.y) }}
          </div>
        </div>

        <svg class="corner-lines" :viewBox="`0 0 ${imageWidth} ${imageHeight}`">
          <polygon
            :points="getPolygonPoints()"
            fill="none"
            stroke="#2563eb"
            stroke-width="2"
            stroke-dasharray="6 4"
          />
        </svg>
      </template>

      <div v-if="!imageUrl" class="no-image-tip">
        请先上传答题卡图片
      </div>
    </div>

    <div class="corner-info" v-if="angle !== null">
      <div class="info-item">
        <span class="info-label">检测倾斜角度</span>
        <span class="info-value" :class="{ 'is-warning': Math.abs(angle) > 1 }">
          {{ angle.toFixed(2) }}°
        </span>
      </div>
      <div class="info-item">
        <span class="info-label">图片尺寸</span>
        <span class="info-value">{{ imageWidth }} × {{ imageHeight }}</span>
      </div>
    </div>

    <div class="corner-actions">
      <el-button @click="handleAutoDetect" :loading="detecting" :disabled="!imageUrl">
        重新检测
      </el-button>
      <el-button @click="handleReset" :disabled="!hasChanges">
        重置
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, ref, watch } from 'vue'
import type { CornerConfig } from '@/api/answerSheetTemplate'

type CornerKey = 'topLeft' | 'topRight' | 'bottomLeft' | 'bottomRight'

interface CornerPoint {
  x: number
  y: number
}

const props = defineProps<{
  imageUrl?: string
  cornerConfig?: CornerConfig
}>()

const emit = defineEmits<{
  'update:cornerConfig': [config: CornerConfig]
  'detect': []
}>()

const previewRef = ref<HTMLDivElement>()
const imageWidth = ref(0)
const imageHeight = ref(0)
const imageLoaded = ref(false)
const detecting = ref(false)
const draggingCorner = ref<CornerKey | null>(null)
const initialCorners = ref<Record<CornerKey, CornerPoint> | null>(null)

const cornerKeys: CornerKey[] = ['topLeft', 'topRight', 'bottomLeft', 'bottomRight']
const cornerLabels: Record<CornerKey, string> = {
  topLeft: '左上',
  topRight: '右上',
  bottomLeft: '左下',
  bottomRight: '右下',
}

const corners = computed<Record<CornerKey, CornerPoint>>(() => ({
  topLeft: props.cornerConfig?.topLeft || { x: 5, y: 5 },
  topRight: props.cornerConfig?.topRight || { x: 95, y: 5 },
  bottomLeft: props.cornerConfig?.bottomLeft || { x: 5, y: 95 },
  bottomRight: props.cornerConfig?.bottomRight || { x: 95, y: 95 },
}))

const angle = computed(() => props.cornerConfig?.angle ?? null)

const hasChanges = computed(() => {
  if (!initialCorners.value) return false
  return cornerKeys.some((key) => {
    const initial = initialCorners.value![key]
    const current = corners.value[key]
    return Math.abs(initial.x - current.x) > 0.01 || Math.abs(initial.y - current.y) > 0.01
  })
})

const handleImageLoad = (event: Event) => {
  const img = event.target as HTMLImageElement
  imageWidth.value = img.naturalWidth
  imageHeight.value = img.naturalHeight
  imageLoaded.value = true

  // 保存初始角点位置
  initialCorners.value = {
    topLeft: { ...corners.value.topLeft },
    topRight: { ...corners.value.topRight },
    bottomLeft: { ...corners.value.bottomLeft },
    bottomRight: { ...corners.value.bottomRight },
  }
}

const getCornerStyle = (corner: CornerKey) => {
  const point = corners.value[corner]
  return {
    left: `${point.x}%`,
    top: `${point.y}%`,
  }
}

const getPolygonPoints = () => {
  const c = corners.value
  const w = imageWidth.value
  const h = imageHeight.value
  return [
    `${(c.topLeft.x / 100) * w},${(c.topLeft.y / 100) * h}`,
    `${(c.topRight.x / 100) * w},${(c.topRight.y / 100) * h}`,
    `${(c.bottomRight.x / 100) * w},${(c.bottomRight.y / 100) * h}`,
    `${(c.bottomLeft.x / 100) * w},${(c.bottomLeft.y / 100) * h}`,
  ].join(' ')
}

const formatCoord = (value?: number) => {
  return value !== undefined ? value.toFixed(1) : '-'
}

const startDrag = (event: MouseEvent, corner: CornerKey) => {
  draggingCorner.value = corner
  window.addEventListener('mousemove', handleDrag)
  window.addEventListener('mouseup', stopDrag)
}

const handleDrag = (event: MouseEvent) => {
  if (!draggingCorner.value || !previewRef.value) return

  const rect = previewRef.value.getBoundingClientRect()
  const x = Math.max(0, Math.min(100, ((event.clientX - rect.left) / rect.width) * 100))
  const y = Math.max(0, Math.min(100, ((event.clientY - rect.top) / rect.height) * 100))

  const newConfig: CornerConfig = {
    ...props.cornerConfig,
    [draggingCorner.value]: { x, y },
  }

  // 重新计算角度
  const topLeft = newConfig.topLeft || corners.value.topLeft
  const topRight = newConfig.topRight || corners.value.topRight
  const deltaY = topRight.y - topLeft.y
  const deltaX = topRight.x - topLeft.x
  newConfig.angle = Math.atan2(deltaY, deltaX) * (180 / Math.PI)

  emit('update:cornerConfig', newConfig)
}

const stopDrag = () => {
  draggingCorner.value = null
  window.removeEventListener('mousemove', handleDrag)
  window.removeEventListener('mouseup', stopDrag)
}

const handleAutoDetect = () => {
  detecting.value = true
  emit('detect')
  // 检测完成后由父组件更新 cornerConfig
  setTimeout(() => {
    detecting.value = false
  }, 100)
}

const handleReset = () => {
  if (initialCorners.value) {
    emit('update:cornerConfig', {
      ...props.cornerConfig,
      ...initialCorners.value,
    })
  }
}

watch(() => props.imageUrl, () => {
  imageLoaded.value = false
})

onBeforeUnmount(() => {
  window.removeEventListener('mousemove', handleDrag)
  window.removeEventListener('mouseup', stopDrag)
})
</script>

<style scoped>
.corner-detect-panel {
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

.corner-preview {
  position: relative;
  width: 100%;
  background: #f8fafc;
  border-radius: 8px;
  overflow: hidden;
  min-height: 200px;
}

.corner-image {
  display: block;
  width: 100%;
  height: auto;
}

.no-image-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #94a3b8;
  font-size: 14px;
}

.corner-marker {
  position: absolute;
  width: 32px;
  height: 32px;
  margin-left: -16px;
  margin-top: -16px;
  cursor: grab;
  z-index: 10;
}

.corner-marker::before {
  content: '';
  position: absolute;
  inset: 8px;
  background: #2563eb;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(37, 99, 235, 0.4);
}

.corner-marker.is-dragging {
  cursor: grabbing;
}

.corner-marker.is-dragging::before {
  background: #dc2626;
}

.marker-label {
  position: absolute;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  padding: 2px 6px;
  background: rgba(37, 99, 235, 0.9);
  border-radius: 4px;
  font-size: 10px;
  color: #fff;
  white-space: nowrap;
}

.marker-coords {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  margin-top: 4px;
  padding: 2px 6px;
  background: rgba(0, 0, 0, 0.7);
  border-radius: 4px;
  font-size: 10px;
  color: #fff;
  white-space: nowrap;
  font-family: ui-monospace, monospace;
}

.corner-lines {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.corner-info {
  display: flex;
  gap: 24px;
  margin-top: 12px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 12px;
  color: #64748b;
}

.info-value {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
}

.info-value.is-warning {
  color: #d97706;
}

.corner-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}
</style>
