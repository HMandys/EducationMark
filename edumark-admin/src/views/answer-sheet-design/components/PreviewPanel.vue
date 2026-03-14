<template>
  <div class="preview-panel" :style="pageStyle">
    <!-- 页眉 -->
    <div class="preview-header" v-if="template.headerConfig?.showTitle">
      <h2 class="preview-title">{{ template.headerConfig.title || template.name }}</h2>
      <p class="preview-subtitle" v-if="template.examName || template.subjectName">
        {{ template.examName }} - {{ template.subjectName }}
      </p>
    </div>

    <!-- 学生信息区 -->
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

    <!-- 答题区域 -->
    <div class="regions-container">
      <template v-for="(region, index) in template.regions" :key="index">
        <!-- 选择题区域 -->
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

        <!-- 填空题区域 -->
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

        <!-- 解答题区域 -->
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
                border: region.config?.showBorder ? '1px solid #ddd' : 'none'
              }"
            ></div>
          </div>
        </div>

        <!-- 作文题区域 -->
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
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { AnswerSheetTemplate, AnswerSheetRegion } from '@/api/answerSheetTemplate'

const props = defineProps<{
  template: Partial<AnswerSheetTemplate>
}>()

const pageSizes: Record<string, { width: number; height: number }> = {
  A4: { width: 210, height: 297 },
  A3: { width: 297, height: 420 },
  B5: { width: 176, height: 250 },
}

const pageStyle = computed(() => {
  const size = pageSizes[props.template.pageSize || 'A4']
  const isLandscape = props.template.orientation === 2
  const scale = 2 // 预览缩放比例

  const width = isLandscape ? size.height : size.width
  const height = isLandscape ? size.width : size.height

  return {
    width: `${width * scale}px`,
    minHeight: `${height * scale}px`,
    padding: `${(props.template.marginTop || 20) * scale / 3}px ${(props.template.marginRight || 15) * scale / 3}px ${(props.template.marginBottom || 20) * scale / 3}px ${(props.template.marginLeft || 15) * scale / 3}px`,
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
  // 根据纸张宽度和格子大小计算每行字数
  const pageWidth = pageSizes[props.template.pageSize || 'A4'].width
  const isLandscape = props.template.orientation === 2
  const width = isLandscape ? pageSizes[props.template.pageSize || 'A4'].height : pageWidth
  const margins = (props.template.marginLeft || 15) + (props.template.marginRight || 15)
  const gridSize = region.config?.gridSize || 10
  return Math.floor((width - margins) / gridSize)
}
</script>

<style scoped>
.preview-panel {
  background: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  font-family: 'SimSun', serif;
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
  border: 1px solid #333;
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
  border-bottom: 1px solid #333;
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
  font-size: 12px;
  font-weight: bold;
  margin-bottom: 8px;
}

/* 选择题样式 */
.choice-grid {
  display: grid;
  gap: 4px;
  border: 1px solid #ddd;
  padding: 8px;
}

.choice-item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px;
  border: 1px solid #eee;
}

.question-no {
  font-size: 10px;
  min-width: 20px;
}

.option-bubble {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 14px;
  height: 14px;
  border: 1px solid #333;
  border-radius: 50%;
  font-size: 8px;
}

.option-bubble.square {
  border-radius: 2px;
}

/* 填空题样式 */
.fillblank-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.fillblank-item {
  display: flex;
  gap: 8px;
}

.answer-lines {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.answer-line {
  border-bottom: 1px solid #333;
}

/* 解答题样式 */
.answer-item {
  margin-bottom: 12px;
}

.answer-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 4px;
}

.score-box {
  width: 40px;
  height: 20px;
  border: 1px solid #333;
  font-size: 8px;
  text-align: center;
  line-height: 20px;
}

.answer-area {
  background: #fafafa;
}

/* 作文题样式 */
.essay-grid {
  display: grid;
  border: 1px solid #ddd;
}

.essay-cell {
  border: 1px solid #ddd;
  position: relative;
}

.essay-cell.show-count {
  background: #f5f5f5;
}

.cell-count {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 6px;
  color: #999;
}

.essay-footer {
  text-align: right;
  font-size: 8px;
  color: #999;
  margin-top: 4px;
}

.empty-tip {
  text-align: center;
  padding: 40px;
  color: #999;
  font-size: 14px;
}
</style>
