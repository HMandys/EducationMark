<template>
  <view class="report-page">
    <view class="hero-card">
      <text class="hero-title">学习概览</text>
      <text class="hero-subtitle">{{ studentTitle }}</text>
    </view>

    <view v-if="loading" class="state-card">
      <text>加载中...</text>
    </view>

    <view v-else-if="!report" class="state-card">
      <text>暂无成绩数据，无法生成学习概览</text>
    </view>

    <template v-else>
      <view class="insight-card">
        <text class="section-title">综合判断</text>
        <text class="insight-text">{{ reportView.summary }}</text>
      </view>

      <view class="metrics-grid">
        <view class="metric-card">
          <text class="metric-value">{{ reportView.averageTotalScore }}</text>
          <text class="metric-label">平均总分</text>
        </view>
        <view class="metric-card">
          <text class="metric-value">{{ reportView.trend }}</text>
          <text class="metric-label">成绩趋势</text>
        </view>
        <view class="metric-card">
          <text class="metric-value">{{ reportView.providerName || '-' }}</text>
          <text class="metric-label">分析提供商</text>
        </view>
        <view class="metric-card">
          <text class="metric-value">{{ reportView.model || '-' }}</text>
          <text class="metric-label">使用模型</text>
        </view>
      </view>

      <view class="section-card">
        <view class="section-title">优势项</view>
        <view v-for="item in reportView.strengths" :key="item" class="advice-item">
          <text class="advice-dot" />
          <text class="advice-text">{{ item }}</text>
        </view>
      </view>

      <view class="section-card">
        <view class="section-title">待提升项</view>
        <view class="advice-item" v-for="item in reportView.weaknesses" :key="item">
          <text class="advice-dot" />
          <text class="advice-text">{{ item }}</text>
        </view>
      </view>

      <view class="section-card">
        <view class="section-title">建议行动</view>
        <view class="advice-item" v-for="advice in reportView.suggestions" :key="advice">
          <text class="advice-dot" />
          <text class="advice-text">{{ advice }}</text>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getAiReport, type AiReport } from '@/api/ai'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const loading = ref(false)
const report = ref<AiReport | null>(null)

const studentTitle = computed(() => {
  const student = userStore.currentStudent
  if (!student) return '未选择学生'
  return `${student.name} · ${student.gradeName} · ${student.className}`
})

const reportView = computed(() => report.value ?? {
  summary: '',
  strengths: [],
  weaknesses: [],
  suggestions: [],
  latestTotalScore: 0,
  averageTotalScore: 0,
  trend: '',
  providerName: '',
  model: '',
  generatedAt: '',
})

const loadData = async () => {
  if (!userStore.isLoggedIn) {
    uni.reLaunch({ url: '/pages/login/index' })
    return
  }

  if (!userStore.currentStudent) {
    uni.showToast({ title: '请先选择学生', icon: 'none' })
    return
  }

  loading.value = true
  try {
    const res = await getAiReport(userStore.currentStudent.id, 10)
    report.value = res.data
  } catch (error) {
    console.error('加载学习概览失败', error)
  } finally {
    loading.value = false
  }
}

onShow(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.report-page {
  min-height: 100vh;
  background: #f4f7fb;
  padding: 24rpx;
}

.hero-card,
.insight-card,
.state-card,
.section-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: 0 10rpx 30rpx rgba(15, 23, 42, 0.06);
  margin-bottom: 24rpx;
}

.hero-card {
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  color: #fff;

  .hero-title {
    display: block;
    font-size: 40rpx;
    font-weight: 700;
    margin-bottom: 12rpx;
  }

  .hero-subtitle {
    font-size: 26rpx;
    opacity: 0.9;
  }
}

.state-card {
  text-align: center;
  color: #6b7280;
}

.section-title {
  display: block;
  font-size: 32rpx;
  font-weight: 700;
  color: #111827;
  margin-bottom: 18rpx;
}

.insight-text {
  font-size: 28rpx;
  line-height: 1.8;
  color: #374151;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
  margin-bottom: 24rpx;
}

.metric-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 28rpx 24rpx;
  box-shadow: 0 8rpx 24rpx rgba(15, 23, 42, 0.05);

  .metric-value {
    display: block;
    font-size: 38rpx;
    font-weight: 700;
    color: #111827;
  }

  .metric-label {
    display: block;
    margin-top: 10rpx;
    font-size: 24rpx;
    color: #6b7280;
  }
}

.subject-row {
  padding: 18rpx 0;
  border-bottom: 1rpx solid #eef2f7;

  &:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }
}

.subject-head {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12rpx;
}

.subject-name,
.subject-score {
  font-size: 28rpx;
  color: #374151;
}

.subject-bar {
  height: 14rpx;
  border-radius: 999rpx;
  background: #e5edf9;
  overflow: hidden;
}

.subject-fill {
  height: 100%;
  border-radius: 999rpx;
  background: linear-gradient(90deg, #409EFF 0%, #22c55e 100%);
}

.advice-item {
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
  margin-bottom: 18rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.advice-dot {
  width: 12rpx;
  height: 12rpx;
  margin-top: 12rpx;
  border-radius: 50%;
  background: #409EFF;
  flex-shrink: 0;
}

.advice-text {
  font-size: 27rpx;
  color: #374151;
  line-height: 1.7;
}
</style>
