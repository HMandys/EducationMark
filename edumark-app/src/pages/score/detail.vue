<template>
  <view class="score-page">
    <!-- 总分卡片 -->
    <view class="score-card" v-if="scoreData">
      <view class="card-header">
        <text class="exam-name">{{ scoreView.examName }}</text>
      </view>

      <view class="score-main">
        <view class="total-score">
          <text class="score-num">{{ scoreView.totalScore }}</text>
          <text class="score-label">总分</text>
        </view>
        <view class="rank-list">
          <view class="rank-item">
            <text class="rank-num">{{ scoreView.classRank || '-' }}</text>
            <text class="rank-label">班级排名</text>
          </view>
          <view class="rank-item">
            <text class="rank-num">{{ scoreView.gradeRank || '-' }}</text>
            <text class="rank-label">年级排名</text>
          </view>
        </view>
      </view>

      <view class="student-info">
        <text>{{ scoreView.studentName }}</text>
        <text class="divider">|</text>
        <text>{{ scoreView.className }}</text>
        <text class="divider">|</text>
        <text>{{ scoreView.studentNumber }}</text>
      </view>
    </view>

    <!-- 各科成绩 -->
    <view class="section" v-if="subjectScores.length">
      <view class="section-header">
        <text class="section-title">各科成绩</text>
      </view>

      <view class="subject-list">
        <view
          v-for="subject in subjectScores"
          :key="subject.id"
          class="subject-item"
        >
          <view class="subject-left">
            <text class="subject-name">{{ subject.subjectName }}</text>
            <view class="subject-detail">
              <text class="detail-item">客观题: {{ subject.objectiveScore || 0 }}</text>
              <text class="detail-item">主观题: {{ subject.subjectiveScore || 0 }}</text>
            </view>
          </view>
          <view class="subject-right">
            <view class="subject-score">
              <text class="score-num">{{ subject.score }}</text>
              <text class="score-full">/{{ subject.fullScore }}</text>
            </view>
            <view class="subject-rank">
              <text>班{{ subject.classRank || '-' }}</text>
              <text class="divider">/</text>
              <text>级{{ subject.gradeRank || '-' }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 操作按钮 -->
    <view class="action-bar">
      <view class="action-btn" @click="handleViewAnswerSheet">
        <text class="btn-icon">&#xe605;</text>
        <text class="btn-text">查看答题卡</text>
      </view>
      <view class="action-btn" @click="handleViewAIReport">
        <text class="btn-icon">&#xe606;</text>
        <text class="btn-text">AI学情分析</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getStudentExamScore, type ExamScore } from '@/api/exam'

const scoreData = ref<ExamScore | null>(null)
const loading = ref(false)

const scoreView = computed(() => scoreData.value ?? {
  examName: '',
  totalScore: 0,
  classRank: undefined,
  gradeRank: undefined,
  studentName: '',
  className: '',
  studentNumber: '',
})

const subjectScores = computed(() => scoreData.value?.subjectScores ?? [])

let examId: number
let studentId: number

// 加载成绩数据
const loadScoreData = async () => {
  loading.value = true
  try {
    const res = await getStudentExamScore(examId, studentId)
    scoreData.value = res.data
  } catch (error) {
    console.error('加载成绩失败', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

// 查看答题卡
const handleViewAnswerSheet = () => {
  uni.navigateTo({
    url: `/pages/answer-sheet/view?examId=${examId}&studentId=${studentId}`,
  })
}

// 查看AI报告
const handleViewAIReport = () => {
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

onLoad((options) => {
  examId = Number(options?.examId)
  studentId = Number(options?.studentId)

  if (examId && studentId) {
    loadScoreData()
  }
})
</script>

<style lang="scss" scoped>
.score-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 180rpx;
}

.score-card {
  background: linear-gradient(135deg, #409EFF 0%, #79bbff 100%);
  margin: 20rpx;
  border-radius: 20rpx;
  padding: 30rpx;
  color: #fff;

  .card-header {
    margin-bottom: 30rpx;

    .exam-name {
      font-size: 34rpx;
      font-weight: bold;
    }
  }

  .score-main {
    display: flex;
    align-items: center;
    margin-bottom: 30rpx;

    .total-score {
      text-align: center;
      padding-right: 40rpx;
      border-right: 2rpx solid rgba(255, 255, 255, 0.3);

      .score-num {
        font-size: 80rpx;
        font-weight: bold;
        line-height: 1;
      }

      .score-label {
        display: block;
        font-size: 26rpx;
        opacity: 0.9;
        margin-top: 8rpx;
      }
    }

    .rank-list {
      flex: 1;
      display: flex;
      padding-left: 40rpx;

      .rank-item {
        flex: 1;
        text-align: center;

        .rank-num {
          font-size: 48rpx;
          font-weight: bold;
        }

        .rank-label {
          display: block;
          font-size: 24rpx;
          opacity: 0.9;
          margin-top: 8rpx;
        }
      }
    }
  }

  .student-info {
    font-size: 26rpx;
    opacity: 0.9;
    text-align: center;
    padding-top: 20rpx;
    border-top: 2rpx solid rgba(255, 255, 255, 0.2);

    .divider {
      margin: 0 16rpx;
      opacity: 0.5;
    }
  }
}

.section {
  margin: 20rpx;
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;

  .section-header {
    margin-bottom: 20rpx;

    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
  }
}

.subject-list {
  .subject-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24rpx 0;
    border-bottom: 1rpx solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .subject-left {
      .subject-name {
        font-size: 30rpx;
        font-weight: bold;
        color: #333;
      }

      .subject-detail {
        margin-top: 8rpx;

        .detail-item {
          font-size: 24rpx;
          color: #999;
          margin-right: 20rpx;
        }
      }
    }

    .subject-right {
      text-align: right;

      .subject-score {
        .score-num {
          font-size: 40rpx;
          font-weight: bold;
          color: #409EFF;
        }

        .score-full {
          font-size: 26rpx;
          color: #999;
        }
      }

      .subject-rank {
        font-size: 24rpx;
        color: #666;
        margin-top: 8rpx;

        .divider {
          margin: 0 8rpx;
          color: #ccc;
        }
      }
    }
  }
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  display: flex;
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.05);

  .action-btn {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f7fa;
    padding: 24rpx 0;
    border-radius: 12rpx;
    margin: 0 10rpx;

    .btn-icon {
      font-size: 36rpx;
      margin-right: 12rpx;
      color: #409EFF;
    }

    .btn-text {
      font-size: 28rpx;
      color: #333;
    }

    &:active {
      background: #e8e8e8;
    }
  }
}
</style>
