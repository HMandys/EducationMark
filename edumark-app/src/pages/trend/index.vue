<template>
  <view class="trend-page">
    <view class="hero-card">
      <text class="hero-title">成绩趋势</text>
      <text class="hero-subtitle">{{ studentTitle }}</text>
    </view>

    <view v-if="loading" class="state-card">
      <text>加载中...</text>
    </view>

    <view v-else-if="scores.length === 0" class="state-card">
      <text>暂无可分析的成绩数据</text>
    </view>

    <template v-else>
      <view class="summary-grid">
        <view class="summary-item">
          <text class="summary-value">{{ latestScore?.totalScore ?? 0 }}</text>
          <text class="summary-label">最近一次总分</text>
        </view>
        <view class="summary-item">
          <text class="summary-value">{{ averageScore }}</text>
          <text class="summary-label">平均总分</text>
        </view>
        <view class="summary-item">
          <text class="summary-value">{{ bestScore?.totalScore ?? 0 }}</text>
          <text class="summary-label">最高总分</text>
        </view>
      </view>

      <view class="section-card">
        <view class="section-header">
          <text class="section-title">历次考试</text>
          <text class="section-desc">按时间倒序展示</text>
        </view>

        <view
          v-for="(score, index) in scores"
          :key="score.id"
          class="trend-item"
        >
          <view class="trend-head">
            <view>
              <text class="exam-name">{{ score.examName }}</text>
              <text class="exam-date">{{ formatDate(score.createTime) }}</text>
            </view>
            <view class="score-area">
              <text class="total-score">{{ score.totalScore }}</text>
              <text class="delta" :class="getDeltaClass(index)">
                {{ getDeltaText(index) }}
              </text>
            </view>
          </view>

          <view class="bar-track">
            <view class="bar-fill" :style="{ width: `${getBarWidth(score.totalScore)}%` }" />
          </view>

          <view class="meta-row">
            <text>班排 {{ score.classRank || '-' }}</text>
            <text>级排 {{ score.gradeRank || '-' }}</text>
            <text>{{ getSubjectScores(score).length }} 科</text>
          </view>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getRecentScores, type ExamScore } from '@/api/exam'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const loading = ref(false)
const scores = ref<ExamScore[]>([])

const latestScore = computed(() => scores.value[0] ?? null)
const bestScore = computed(() => {
  return scores.value.reduce<ExamScore | null>((best, current) => {
    if (!best || current.totalScore > best.totalScore) return current
    return best
  }, null)
})

const averageScore = computed(() => {
  if (scores.value.length === 0) return '0'
  const total = scores.value.reduce((sum, item) => sum + item.totalScore, 0)
  return (total / scores.value.length).toFixed(1)
})

const studentTitle = computed(() => {
  const student = userStore.currentStudent
  if (!student) return '未选择学生'
  return `${student.name} · ${student.schoolName} · ${student.className}`
})

const getSubjectScores = (score: ExamScore) => score.subjectScores ?? []

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`
}

const getBarWidth = (score: number) => {
  const max = Math.max(...scores.value.map(item => item.totalScore), 1)
  return Math.max(18, Math.round((score / max) * 100))
}

const getDeltaValue = (index: number) => {
  if (index >= scores.value.length - 1) return 0
  return scores.value[index].totalScore - scores.value[index + 1].totalScore
}

const getDeltaText = (index: number) => {
  const delta = getDeltaValue(index)
  if (delta === 0) return '持平'
  return delta > 0 ? `较上次 +${delta}` : `较上次 ${delta}`
}

const getDeltaClass = (index: number) => {
  const delta = getDeltaValue(index)
  if (delta > 0) return 'up'
  if (delta < 0) return 'down'
  return 'flat'
}

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
    const res = await getRecentScores(userStore.currentStudent.id, 20)
    scores.value = res.data
  } catch (error) {
    console.error('加载趋势数据失败', error)
  } finally {
    loading.value = false
  }
}

onShow(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.trend-page {
  min-height: 100vh;
  background: #f4f7fb;
  padding: 24rpx;
}

.hero-card,
.state-card,
.section-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: 0 10rpx 30rpx rgba(15, 23, 42, 0.06);
}

.hero-card {
  background: linear-gradient(135deg, #0f62fe 0%, #4f8cff 100%);
  color: #fff;
  margin-bottom: 24rpx;

  .hero-title {
    display: block;
    font-size: 40rpx;
    font-weight: 700;
    margin-bottom: 12rpx;
  }

  .hero-subtitle {
    font-size: 26rpx;
    opacity: 0.9;
    line-height: 1.6;
  }
}

.state-card {
  text-align: center;
  color: #6b7280;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20rpx;
  margin-bottom: 24rpx;
}

.summary-item {
  background: #fff;
  border-radius: 20rpx;
  padding: 26rpx 20rpx;
  text-align: center;
  box-shadow: 0 8rpx 24rpx rgba(15, 23, 42, 0.05);

  .summary-value {
    display: block;
    font-size: 42rpx;
    font-weight: 700;
    color: #111827;
  }

  .summary-label {
    display: block;
    margin-top: 10rpx;
    font-size: 24rpx;
    color: #6b7280;
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;

  .section-title {
    font-size: 32rpx;
    font-weight: 700;
    color: #111827;
  }

  .section-desc {
    font-size: 24rpx;
    color: #9ca3af;
  }
}

.trend-item {
  padding: 28rpx 0;
  border-bottom: 1rpx solid #eef2f7;

  &:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }
}

.trend-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 18rpx;
}

.exam-name,
.total-score {
  display: block;
  font-weight: 700;
}

.exam-name {
  font-size: 30rpx;
  color: #111827;
  margin-bottom: 8rpx;
}

.exam-date {
  font-size: 24rpx;
  color: #9ca3af;
}

.score-area {
  text-align: right;
}

.total-score {
  font-size: 42rpx;
  color: #111827;
}

.delta {
  font-size: 22rpx;

  &.up {
    color: #16a34a;
  }

  &.down {
    color: #dc2626;
  }

  &.flat {
    color: #6b7280;
  }
}

.bar-track {
  height: 14rpx;
  border-radius: 999rpx;
  background: #e5edf9;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 999rpx;
  background: linear-gradient(90deg, #409EFF 0%, #67C23A 100%);
}

.meta-row {
  display: flex;
  justify-content: space-between;
  margin-top: 14rpx;
  font-size: 24rpx;
  color: #6b7280;
}
</style>
