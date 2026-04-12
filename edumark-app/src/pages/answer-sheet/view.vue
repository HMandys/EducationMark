<template>
  <view class="answer-sheet-page">
    <!-- 科目选择 -->
    <scroll-view class="subject-tabs" scroll-x>
      <view
        v-for="sheet in answerSheets"
        :key="sheet.id"
        class="tab-item"
        :class="{ active: currentSheet?.id === sheet.id }"
        @click="handleSelectSheet(sheet)"
      >
        {{ sheet.subjectName }}
      </view>
    </scroll-view>

    <!-- 图片列表 -->
    <view class="image-container" v-if="currentSheet">
      <view class="sheet-info">
        <text class="info-item">科目: {{ currentSheetView.subjectName }}</text>
        <text class="info-item" v-if="currentSheetView.totalScore !== undefined">
          得分: {{ currentSheetView.totalScore }}
        </text>
      </view>

      <view class="image-list">
        <view
          v-for="(image, index) in currentSheetImages"
          :key="image.id"
          class="image-item"
          @click="handlePreviewImage(index)"
        >
          <image
            :src="image.imageUrl"
            mode="widthFix"
            class="sheet-image"
            :lazy-load="true"
          />
          <view class="page-label">第{{ image.pageNo }}页</view>
        </view>
      </view>
    </view>

    <view v-else class="empty">
      <text>暂无答题卡数据</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getAnswerSheetList, type AnswerSheet } from '@/api/answerSheet'

const answerSheets = ref<AnswerSheet[]>([])
const currentSheet = ref<AnswerSheet | null>(null)
const loading = ref(false)

const currentSheetView = computed(() => currentSheet.value ?? {
  subjectName: '',
  totalScore: undefined,
})

const currentSheetImages = computed(() => currentSheet.value?.images ?? [])

let examId: number
let studentId: number

// 加载答题卡列表
const loadAnswerSheets = async () => {
  loading.value = true
  try {
    const res = await getAnswerSheetList(examId, studentId)
    answerSheets.value = res.data

    // 默认选中第一个
    if (res.data.length > 0) {
      currentSheet.value = res.data[0]
    }
  } catch (error) {
    console.error('加载答题卡失败', error)
  } finally {
    loading.value = false
  }
}

// 选择科目
const handleSelectSheet = (sheet: AnswerSheet) => {
  currentSheet.value = sheet
}

// 预览图片
const handlePreviewImage = (index: number) => {
  if (!currentSheet.value) return

  const urls = currentSheetImages.value.map(img => img.imageUrl)
  uni.previewImage({
    urls,
    current: index,
  })
}

onLoad((options) => {
  examId = Number(options?.examId)
  studentId = Number(options?.studentId)

  if (examId && studentId) {
    loadAnswerSheets()
  }
})
</script>

<style lang="scss" scoped>
.answer-sheet-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.subject-tabs {
  white-space: nowrap;
  background: #fff;
  padding: 20rpx;
  border-bottom: 1rpx solid #eee;

  .tab-item {
    display: inline-block;
    padding: 16rpx 32rpx;
    margin-right: 20rpx;
    font-size: 28rpx;
    color: #666;
    background: #f5f5f5;
    border-radius: 30rpx;

    &.active {
      background: #409EFF;
      color: #fff;
    }
  }
}

.image-container {
  padding: 20rpx;
}

.sheet-info {
  background: #fff;
  padding: 24rpx;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  display: flex;

  .info-item {
    font-size: 28rpx;
    color: #333;
    margin-right: 40rpx;
  }
}

.image-list {
  .image-item {
    background: #fff;
    border-radius: 12rpx;
    overflow: hidden;
    margin-bottom: 20rpx;

    .sheet-image {
      width: 100%;
    }

    .page-label {
      text-align: center;
      padding: 16rpx;
      font-size: 26rpx;
      color: #999;
      background: #f9f9f9;
    }
  }
}

.empty {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
  font-size: 28rpx;
}
</style>
