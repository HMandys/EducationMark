<template>
  <div class="preview-shell">
    <div class="preview-toolbar">
      <div>
        <div class="preview-toolbar-title">模板标注预览</div>
        <div class="preview-toolbar-tip">
          {{ drawMode ? '拉框模式：在样张上拖拽创建新区域' : '点击区域选中，拖动区域框可调整位置，右下角拖点可调整尺寸。' }}
        </div>
      </div>
      <div class="preview-toolbar-right">
        <el-tag v-if="drawMode" type="warning" effect="plain">
          拉框模式
        </el-tag>
        <el-tag v-if="sampleImageVisible && sampleImageUrl" type="success" effect="plain">
          样张叠加中
        </el-tag>
        <el-tag v-if="selectedRegionIndex >= 0" type="primary" effect="plain">
          当前区域：{{ template.regions?.[selectedRegionIndex]?.regionName || '未命名区域' }}
        </el-tag>
      </div>
    </div>

    <div
      ref="pageRef"
      class="preview-panel"
      :class="{ 'is-draw-mode': drawMode }"
      :style="pageStyle"
      @click="handleBlankClick"
      @mousedown="handleDrawStart"
    >
      <div v-if="sampleImageVisible && sampleImageUrl" class="sample-image-layer" :style="{ opacity: sampleImageOpacity }">
        <img :src="sampleImageUrl" alt="答题卡样张" class="sample-image" />
      </div>

      <div class="preview-base">
        <div class="preview-header" v-if="template.headerConfig?.showTitle">
          <h2 class="preview-title">{{ template.headerConfig.title || template.name }}</h2>
          <p class="preview-subtitle" v-if="template.examName || template.subjectName">
            {{ template.examName }} - {{ template.subjectName }}
          </p>
        </div>

        <div class="student-info-area" v-if="showStudentInfo">
          <div class="info-row">
            <span v-if="template.studentInfoConfig?.showName" class="info-field">
              姓名：<span class="underline"></span>
            </span>
            <span v-if="template.studentInfoConfig?.showStudentId" class="info-field">
              学号：<span class="underline"></span>
            </span>
            <span v-if="template.studentInfoConfig?.showClass" class="info-field">
              班级：<span class="underline"></span>
            </span>
          </div>
        </div>

        <div class="regions-container">
          <template v-for="(region, index) in template.regions" :key="index">
            <div v-if="region.regionType === 1" class="region choice-region">
              <div class="region-title">
                {{ region.regionName }}
                <span v-if="region.questionStart && region.questionEnd">
                  （第{{ region.questionStart }}-{{ region.questionEnd }}题）
                </span>
              </div>
              <div class="choice-grid" :style="{ gridTemplateColumns: `repeat(${region.config?.questionsPerRow || 5}, 1fr)` }">
                <div
                  v-for="q in getQuestionRange(region)"
                  :key="q"
                  class="choice-item"
                >
                  <span class="question-no">{{ q }}.</span>
                  <span
                    v-for="opt in (region.config?.optionCount || 4)"
                    :key="opt"
                    class="option-bubble"
                    :class="{ square: region.config?.bubbleStyle === 'square' }"
                  >
                    {{ String.fromCharCode(64 + opt) }}
                  </span>
                </div>
              </div>
            </div>

            <div v-else-if="region.regionType === 2" class="region fillblank-region">
              <div class="region-title">
                {{ region.regionName }}
                <span v-if="region.questionStart && region.questionEnd">
                  （第{{ region.questionStart }}-{{ region.questionEnd }}题）
                </span>
              </div>
              <div class="fillblank-list">
                <div
                  v-for="q in getQuestionRange(region)"
                  :key="q"
                  class="fillblank-item"
                >
                  <span class="question-no">{{ q }}.</span>
                  <div class="answer-lines">
                    <div
                      v-for="line in (region.config?.linesPerQuestion || 1)"
                      :key="line"
                      class="answer-line"
                      :style="{ height: `${(region.config?.lineHeight || 30) / 3}px` }"
                    ></div>
                  </div>
                </div>
              </div>
            </div>

            <div v-else-if="region.regionType === 3" class="region answer-region">
              <div class="region-title">
                {{ region.regionName }}
                <span v-if="region.questionStart && region.questionEnd">
                  （第{{ region.questionStart }}-{{ region.questionEnd }}题）
                </span>
              </div>
              <div
                v-for="q in getQuestionRange(region)"
                :key="q"
                class="answer-item"
              >
                <div class="answer-header">
                  <span class="question-no">{{ q }}.</span>
                  <div class="score-box">得分</div>
                </div>
                <div
                  class="answer-area"
                  :style="{
                    height: `${(region.config?.height || 100) / getQuestionCount(region) / 2}px`,
                    border: region.config?.showBorder ? '1px solid #d1d5db' : 'none'
                  }"
                ></div>
              </div>
            </div>

            <div v-else-if="region.regionType === 4" class="region essay-region">
              <div class="region-title">
                {{ region.regionName }}
                <span v-if="region.questionStart && region.questionEnd">
                  （第{{ region.questionStart }}-{{ region.questionEnd }}题）
                </span>
              </div>
              <div
                class="essay-grid"
                :style="{
                  gridTemplateColumns: `repeat(${getEssayColCount(region)}, 1fr)`,
                  gridAutoRows: `${(region.config?.gridSize || 10) / 2}px`
                }"
              >
                <div
                  v-for="i in (region.config?.wordCount || 800)"
                  :key="i"
                  class="essay-cell"
                  :class="{ 'show-count': i % 100 === 0 }"
                >
                  <span v-if="i % 100 === 0" class="cell-count">{{ i }}</span>
                </div>
              </div>
              <div class="essay-footer">（本题共{{ region.config?.wordCount || 800 }}格）</div>
            </div>
          </template>

          <div v-if="!template.regions || template.regions.length === 0" class="empty-tip">
            请在左侧添加答题区域
          </div>
        </div>
      </div>

      <!-- 拉框绘制层 -->
      <div v-if="drawMode" class="draw-layer">
        <div
          v-if="drawingState.isDrawing"
          class="draw-box"
          :style="getDrawBoxStyle()"
        >
          <div class="draw-box-info">
            <div>X: {{ drawingState.displayX }}%</div>
            <div>Y: {{ drawingState.displayY }}%</div>
            <div>W: {{ drawingState.displayWidth }}%</div>
            <div>H: {{ drawingState.displayHeight }}%</div>
          </div>
        </div>
      </div>

      <div class="overlay-layer">
        <div
          v-for="(region, index) in template.regions || []"
          :key="`overlay-${index}`"
          class="overlay-region"
          :class="{
            'is-selected': selectedRegionIndex === index,
            'is-missing': !hasBounds(region),
          }"
          :style="getOverlayStyle(region, index)"
          @click.stop="handleSelect(index)"
          @mousedown.stop="startMove($event, index)"
        >
          <template v-if="hasBounds(region)">
            <div class="overlay-header">
              <span>{{ region.regionName }}</span>
              <span>
                {{ getRoleLabel(region) }}
                <template v-if="region.config?.bubbleMap?.length">
                  · {{ region.config.bubbleMap.length }} 项
                </template>
              </span>
            </div>
            <div class="overlay-footer">
              <span>第 {{ region.pageNo || 1 }} 页</span>
              <span v-if="region.questionStart && region.questionEnd">
                {{ region.questionStart }}-{{ region.questionEnd }} 题
              </span>
            </div>
            <button
              v-if="editable"
              class="resize-handle"
              type="button"
              @mousedown.stop="startResize($event, index)"
            ></button>
          </template>
          <template v-else>
            <div class="overlay-missing-text">未标注坐标</div>
          </template>
        </div>

        <template v-for="(region, regionIndex) in template.regions || []" :key="`bubble-map-${regionIndex}`">
          <div
            v-for="(bubble, bubbleIndex) in region.config?.bubbleMap || []"
            :key="`bubble-${regionIndex}-${bubbleIndex}`"
            class="bubble-box"
            :class="{ 'is-selected-region': selectedRegionIndex === regionIndex }"
            :style="getBubbleStyle(bubble)"
            @click.stop="handleSelect(regionIndex)"
          >
            {{ bubble.option }}
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, ref } from 'vue'
import type { AnswerSheetRegion, AnswerSheetTemplate, BubbleMapItem, RegionRole } from '@/api/answerSheetTemplate'

type InteractionMode = 'move' | 'resize'

interface ActiveInteraction {
  mode: InteractionMode
  index: number
  startClientX: number
  startClientY: number
  startX: number
  startY: number
  startWidth: number
  startHeight: number
}

const props = withDefaults(defineProps<{
  template: Partial<AnswerSheetTemplate>
  editable?: boolean
  selectedRegionIndex?: number
  sampleImageUrl?: string
  sampleImageVisible?: boolean
  sampleImageOpacity?: number
  drawMode?: boolean
}>(), {
  editable: false,
  selectedRegionIndex: -1,
  sampleImageUrl: '',
  sampleImageVisible: false,
  sampleImageOpacity: 0.35,
  drawMode: false,
})

const emit = defineEmits<{
  'select-region': [index: number]
  'update-region': [payload: { index: number; region: AnswerSheetRegion }]
  'create-region': [bounds: { boxX: number; boxY: number; boxWidth: number; boxHeight: number }]
}>()

const pageRef = ref<HTMLDivElement>()
const activeInteraction = ref<ActiveInteraction | null>(null)

// 拉框绘制状态
const drawingState = ref({
  isDrawing: false,
  startX: 0,      // 起始 X（百分比）
  startY: 0,      // 起始 Y（百分比）
  currentX: 0,    // 当前 X（百分比）
  currentY: 0,    // 当前 Y（百分比）
  displayX: 0,    // 显示用的左上角 X
  displayY: 0,    // 显示用的左上角 Y
  displayWidth: 0, // 显示用的宽度
  displayHeight: 0, // 显示用的高度
})

const MIN_BOX_SIZE = 2 // 最小框尺寸（百分比）

const roleLabelMap: Record<RegionRole, string> = {
  choice_block: '客观题涂卡区',
  subjective_crop: '主观题裁题区',
  essay_crop: '作文裁题区',
  score_box: '评分框',
  student_id: '学号识别区',
  student_name: '姓名识别区',
  class_name: '班级识别区',
  barcode: '条码区',
}

const pageSizes: Record<string, { width: number; height: number }> = {
  A4: { width: 210, height: 297 },
  A3: { width: 297, height: 420 },
  B5: { width: 176, height: 250 },
}

const pageStyle = computed(() => {
  const size = pageSizes[props.template.pageSize || 'A4']
  const isLandscape = props.template.orientation === 2
  const scale = 2

  const width = isLandscape ? size.height : size.width
  const height = isLandscape ? size.width : size.height

  return {
    width: `${width * scale}px`,
    minHeight: `${height * scale}px`,
    padding: `${((props.template.marginTop || 20) * scale) / 3}px ${((props.template.marginRight || 15) * scale) / 3}px ${((props.template.marginBottom || 20) * scale) / 3}px ${((props.template.marginLeft || 15) * scale) / 3}px`,
  }
})

const showStudentInfo = computed(() => {
  const config = props.template.studentInfoConfig
  return config && (config.showName || config.showStudentId || config.showClass)
})

const getQuestionRange = (region: AnswerSheetRegion): number[] => {
  const start = region.questionStart || 1
  const end = region.questionEnd || start
  return Array.from({ length: end - start + 1 }, (_, i) => start + i)
}

const getQuestionCount = (region: AnswerSheetRegion): number => {
  const start = region.questionStart || 1
  const end = region.questionEnd || start
  return end - start + 1
}

const getEssayColCount = (region: AnswerSheetRegion): number => {
  const pageWidth = pageSizes[props.template.pageSize || 'A4'].width
  const isLandscape = props.template.orientation === 2
  const width = isLandscape ? pageSizes[props.template.pageSize || 'A4'].height : pageWidth
  const margins = (props.template.marginLeft || 15) + (props.template.marginRight || 15)
  const gridSize = region.config?.gridSize || 10
  return Math.floor((width - margins) / gridSize)
}

const hasBounds = (region: AnswerSheetRegion) => {
  const config = region.config
  return config?.boxX !== undefined
    && config?.boxY !== undefined
    && config?.boxWidth !== undefined
    && config?.boxHeight !== undefined
}

const getOverlayStyle = (region: AnswerSheetRegion, index: number) => {
  if (!hasBounds(region)) {
    return {
      left: '12px',
      top: `${12 + index * 56}px`,
      width: '120px',
      height: '48px',
      position: 'absolute' as const,
    }
  }

  return {
    left: `${region.config?.boxX}%`,
    top: `${region.config?.boxY}%`,
    width: `${region.config?.boxWidth}%`,
    height: `${region.config?.boxHeight}%`,
    position: 'absolute' as const,
  }
}

const getBubbleStyle = (bubble: BubbleMapItem) => ({
  left: `${bubble.x}%`,
  top: `${bubble.y}%`,
  width: `${bubble.width}%`,
  height: `${bubble.height}%`,
  position: 'absolute' as const,
})

const cloneRegion = (region: AnswerSheetRegion): AnswerSheetRegion => ({
  ...region,
  questionIds: region.questionIds ? [...region.questionIds] : undefined,
  config: {
    ...(region.config || {}),
  },
})

const getRoleLabel = (region: AnswerSheetRegion) => {
  const role = region.config?.regionRole
  return role ? roleLabelMap[role] : '未设置用途'
}

const clamp = (value: number, min: number, max: number) => Math.min(Math.max(value, min), max)

const emitRegionUpdate = (index: number, nextBounds: Record<string, number>) => {
  const region = props.template.regions?.[index]
  if (!region) {
    return
  }

  const nextRegion = cloneRegion(region)
  nextRegion.config = {
    ...nextRegion.config,
    ...nextBounds,
  }
  emit('update-region', { index, region: nextRegion })
}

const handleSelect = (index: number) => {
  emit('select-region', index)
}

const handleBlankClick = () => {
  if (props.drawMode) {
    return
  }
  emit('select-region', -1)
}

// 拉框开始
const handleDrawStart = (event: MouseEvent) => {
  if (!props.drawMode) {
    return
  }

  const rect = pageRef.value?.getBoundingClientRect()
  if (!rect) {
    return
  }

  const x = ((event.clientX - rect.left) / rect.width) * 100
  const y = ((event.clientY - rect.top) / rect.height) * 100

  drawingState.value = {
    isDrawing: true,
    startX: x,
    startY: y,
    currentX: x,
    currentY: y,
    displayX: x,
    displayY: y,
    displayWidth: 0,
    displayHeight: 0,
  }

  window.addEventListener('mousemove', handleDrawMove)
  window.addEventListener('mouseup', handleDrawEnd)
}

// 拉框移动
const handleDrawMove = (event: MouseEvent) => {
  const rect = pageRef.value?.getBoundingClientRect()
  if (!rect) {
    return
  }

  const currentX = clamp(((event.clientX - rect.left) / rect.width) * 100, 0, 100)
  const currentY = clamp(((event.clientY - rect.top) / rect.height) * 100, 0, 100)

  const startX = drawingState.value.startX
  const startY = drawingState.value.startY

  // 计算左上角和尺寸（支持任意方向拖拽）
  const left = Math.min(startX, currentX)
  const top = Math.min(startY, currentY)
  const width = Math.abs(currentX - startX)
  const height = Math.abs(currentY - startY)

  drawingState.value.currentX = currentX
  drawingState.value.currentY = currentY
  drawingState.value.displayX = Math.round(left * 10) / 10
  drawingState.value.displayY = Math.round(top * 10) / 10
  drawingState.value.displayWidth = Math.round(width * 10) / 10
  drawingState.value.displayHeight = Math.round(height * 10) / 10
}

// 拉框结束
const handleDrawEnd = () => {
  window.removeEventListener('mousemove', handleDrawMove)
  window.removeEventListener('mouseup', handleDrawEnd)

  const { displayX, displayY, displayWidth, displayHeight } = drawingState.value

  drawingState.value.isDrawing = false

  // 检查最小尺寸
  if (displayWidth < MIN_BOX_SIZE || displayHeight < MIN_BOX_SIZE) {
    return
  }

  // 触发创建区域事件
  emit('create-region', {
    boxX: displayX,
    boxY: displayY,
    boxWidth: displayWidth,
    boxHeight: displayHeight,
  })
}

// 获取拖拽框样式
const getDrawBoxStyle = () => {
  const { displayX, displayY, displayWidth, displayHeight } = drawingState.value
  return {
    left: `${displayX}%`,
    top: `${displayY}%`,
    width: `${displayWidth}%`,
    height: `${displayHeight}%`,
  }
}

const startInteraction = (event: MouseEvent, index: number, mode: InteractionMode) => {
  if (!props.editable) {
    handleSelect(index)
    return
  }

  const region = props.template.regions?.[index]
  const rect = pageRef.value?.getBoundingClientRect()
  if (!region || !rect || !hasBounds(region)) {
    handleSelect(index)
    return
  }

  activeInteraction.value = {
    mode,
    index,
    startClientX: event.clientX,
    startClientY: event.clientY,
    startX: region.config?.boxX || 0,
    startY: region.config?.boxY || 0,
    startWidth: region.config?.boxWidth || 0,
    startHeight: region.config?.boxHeight || 0,
  }

  emit('select-region', index)
  window.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('mouseup', stopInteraction)
}

const startMove = (event: MouseEvent, index: number) => {
  startInteraction(event, index, 'move')
}

const startResize = (event: MouseEvent, index: number) => {
  startInteraction(event, index, 'resize')
}

const handleMouseMove = (event: MouseEvent) => {
  const current = activeInteraction.value
  const rect = pageRef.value?.getBoundingClientRect()
  if (!current || !rect) {
    return
  }

  const deltaX = ((event.clientX - current.startClientX) / rect.width) * 100
  const deltaY = ((event.clientY - current.startClientY) / rect.height) * 100

  if (current.mode === 'move') {
    const nextX = clamp(current.startX + deltaX, 0, 100 - current.startWidth)
    const nextY = clamp(current.startY + deltaY, 0, 100 - current.startHeight)
    emitRegionUpdate(current.index, { boxX: nextX, boxY: nextY })
    return
  }

  const minWidth = 4
  const minHeight = 4
  const nextWidth = clamp(current.startWidth + deltaX, minWidth, 100 - current.startX)
  const nextHeight = clamp(current.startHeight + deltaY, minHeight, 100 - current.startY)
  emitRegionUpdate(current.index, { boxWidth: nextWidth, boxHeight: nextHeight })
}

const stopInteraction = () => {
  activeInteraction.value = null
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('mouseup', stopInteraction)
}

onBeforeUnmount(() => {
  stopInteraction()
  // 清理拉框事件监听
  window.removeEventListener('mousemove', handleDrawMove)
  window.removeEventListener('mouseup', handleDrawEnd)
})
</script>

<style scoped>
.preview-shell {
  width: 100%;
  max-width: 860px;
}

.preview-toolbar {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
  padding: 14px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  background: #fff;
}

.preview-toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.preview-toolbar-title {
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
}

.preview-toolbar-tip {
  margin-top: 4px;
  font-size: 12px;
  color: #64748b;
}

.preview-panel {
  position: relative;
  background: #fff;
  box-shadow: 0 18px 50px rgba(15, 23, 42, 0.08);
  font-family: 'SimSun', serif;
  user-select: none;
}

.preview-panel.is-draw-mode {
  cursor: crosshair;
}

.sample-image-layer {
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
}

.sample-image {
  width: 100%;
  height: 100%;
  object-fit: fill;
}

.preview-base {
  position: relative;
  z-index: 1;
}

.overlay-layer {
  position: absolute;
  inset: 0;
  z-index: 2;
  pointer-events: none;
}

.draw-layer {
  position: absolute;
  inset: 0;
  z-index: 3;
  pointer-events: none;
}

.draw-box {
  position: absolute;
  border: 2px dashed #f59e0b;
  background: rgba(245, 158, 11, 0.12);
  pointer-events: none;
}

.draw-box-info {
  position: absolute;
  top: 4px;
  left: 4px;
  padding: 6px 8px;
  background: rgba(245, 158, 11, 0.95);
  border-radius: 6px;
  font-size: 11px;
  font-weight: 600;
  color: #fff;
  font-family: ui-monospace, monospace;
  white-space: nowrap;
  line-height: 1.4;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.overlay-region {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  border: 2px solid #2563eb;
  background: rgba(37, 99, 235, 0.08);
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.35);
  pointer-events: auto;
  cursor: move;
  overflow: hidden;
}

.bubble-box {
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(16, 185, 129, 0.95);
  background: rgba(16, 185, 129, 0.18);
  color: #047857;
  font-size: 10px;
  font-weight: 700;
  pointer-events: auto;
}

.bubble-box.is-selected-region {
  border-color: rgba(220, 38, 38, 0.95);
  background: rgba(220, 38, 38, 0.14);
  color: #b91c1c;
}

.overlay-region.is-selected {
  border-color: #dc2626;
  background: rgba(220, 38, 38, 0.1);
}

.overlay-region.is-missing {
  border-style: dashed;
  border-color: #f59e0b;
  background: rgba(245, 158, 11, 0.12);
  cursor: pointer;
}

.overlay-header,
.overlay-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 4px 6px;
  font-size: 11px;
  color: #0f172a;
  background: rgba(255, 255, 255, 0.82);
}

.overlay-missing-text {
  margin: auto;
  font-size: 12px;
  color: #92400e;
  font-weight: 600;
}

.resize-handle {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 12px;
  height: 12px;
  border: none;
  background: #2563eb;
  cursor: nwse-resize;
}

.overlay-region.is-selected .resize-handle {
  background: #dc2626;
}

.preview-header {
  text-align: center;
  margin-bottom: 16px;
}

.preview-title {
  font-size: 18px;
  font-weight: bold;
  margin: 0 0 8px 0;
}

.preview-subtitle {
  font-size: 12px;
  color: #666;
  margin: 0;
}

.student-info-area {
  border: 1px solid #374151;
  padding: 10px;
  margin-bottom: 16px;
}

.info-row {
  display: flex;
  gap: 24px;
}

.info-field {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.underline {
  display: inline-block;
  width: 80px;
  border-bottom: 1px solid #374151;
}

.regions-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.region {
  margin-bottom: 12px;
}

.region-title {
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #111827;
}

.choice-grid {
  display: grid;
  gap: 8px 12px;
}

.choice-item,
.fillblank-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.question-no {
  min-width: 20px;
  font-size: 12px;
  color: #374151;
}

.option-bubble {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  border: 1px solid #6b7280;
  border-radius: 999px;
  font-size: 10px;
}

.option-bubble.square {
  border-radius: 3px;
}

.fillblank-list,
.answer-region {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.answer-lines {
  flex: 1;
}

.answer-line {
  width: 100%;
  border-bottom: 1px solid #6b7280;
  margin-bottom: 6px;
}

.answer-item {
  margin-bottom: 8px;
}

.answer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
}

.score-box {
  min-width: 44px;
  padding: 2px 6px;
  border: 1px solid #9ca3af;
  font-size: 11px;
  text-align: center;
}

.answer-area {
  background: rgba(243, 244, 246, 0.45);
}

.essay-grid {
  display: grid;
  border: 1px solid #d1d5db;
}

.essay-cell {
  position: relative;
  border-right: 1px solid #e5e7eb;
  border-bottom: 1px solid #e5e7eb;
}

.cell-count {
  position: absolute;
  right: 2px;
  bottom: 0;
  font-size: 9px;
  color: #9ca3af;
}

.essay-footer {
  margin-top: 8px;
  font-size: 12px;
  color: #6b7280;
  text-align: right;
}

.empty-tip {
  padding: 40px 0;
  text-align: center;
  color: #9ca3af;
}
</style>
