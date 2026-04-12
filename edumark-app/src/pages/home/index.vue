<template>
  <view class="home-page">
    <!-- 顶部学生信息卡片 -->
    <view class="student-card">
      <view class="student-info" @click="handleSwitchStudent">
        <view class="avatar">
          <text class="avatar-text">{{ currentStudent?.name?.charAt(0) || '?' }}</text>
        </view>
        <view class="info">
          <view class="name-row">
            <text class="name">{{ currentStudent?.name || '未选择学生' }}</text>
            <text class="switch-icon" v-if="userStore.isParent">&#xe604;</text>
          </view>
          <text class="class" v-if="currentStudent">
            {{ currentStudentSummary }}
          </text>
        </view>
      </view>
      <view class="actions" v-if="userStore.isParent">
        <view class="action-btn" @click="handleBindStudent">
          <text class="action-icon">+</text>
          <text class="action-text">绑定学生</text>
        </view>
      </view>
    </view>

    <!-- 最新成绩 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">最新成绩</text>
        <text class="section-more" @click="handleViewAllExams">查看全部 ></text>
      </view>

      <view v-if="loading" class="loading">
        <text>加载中...</text>
      </view>

      <view v-else-if="recentScores.length === 0" class="empty">
        <text>暂无成绩数据</text>
      </view>

      <view v-else class="score-list">
        <view
          v-for="score in recentScores"
          :key="score.id"
          class="score-item"
          @click="handleViewScore(score)"
        >
          <view class="score-header">
            <text class="exam-name">{{ score.examName }}</text>
            <text class="exam-time">{{ formatDate(score.createTime) }}</text>
          </view>
          <view class="score-body">
            <view class="total-score">
              <text class="score-value">{{ score.totalScore }}</text>
              <text class="score-label">总分</text>
            </view>
            <view class="divider" />
            <view class="rank-info">
              <view class="rank-item">
                <text class="rank-value">{{ score.classRank || '-' }}</text>
                <text class="rank-label">班级排名</text>
              </view>
              <view class="rank-item">
                <text class="rank-value">{{ score.gradeRank || '-' }}</text>
                <text class="rank-label">年级排名</text>
              </view>
            </view>
          </view>
          <view class="score-subjects" v-if="getSubjectScores(score).length">
            <view
              v-for="subject in getSubjectScores(score).slice(0, 4)"
              :key="subject.id"
              class="subject-item"
            >
              <text class="subject-name">{{ subject.subjectName }}</text>
              <text class="subject-score">{{ subject.score }}/{{ subject.fullScore }}</text>
            </view>
            <view v-if="getSubjectScores(score).length > 4" class="subject-item more">
              <text>...</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 功能入口 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">快捷功能</text>
      </view>
      <view class="function-grid">
        <view class="function-item" @click="handleViewAllExams">
          <view class="function-icon" style="background: #409EFF;">
            <text>考</text>
          </view>
          <text class="function-text">考试列表</text>
        </view>
        <view class="function-item" @click="handleViewAnswerSheet">
          <view class="function-icon" style="background: #67C23A;">
            <text>卡</text>
          </view>
          <text class="function-text">答题卡</text>
        </view>
        <view class="function-item" @click="handleViewAIReport">
          <view class="function-icon" style="background: #E6A23C;">
            <text>AI</text>
          </view>
          <text class="function-text">学情分析</text>
        </view>
        <view class="function-item" @click="handleViewTrend">
          <view class="function-icon" style="background: #F56C6C;">
            <text>趋</text>
          </view>
          <text class="function-text">成绩趋势</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, watch } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'
import { getRecentScores, type ExamScore } from '@/api/exam'

const userStore = useUserStore()

const loading = ref(false)
const recentScores = ref<ExamScore[]>([])

// 当前学生
const currentStudent = ref(userStore.currentStudent)
const currentStudentSummary = computed(() => {
  if (!currentStudent.value) return ''
  return `${currentStudent.value.schoolName} · ${currentStudent.value.gradeName} · ${currentStudent.value.className}`
})

const getSubjectScores = (score: ExamScore) => score.subjectScores ?? []

// 加载最新成绩
const loadRecentScores = async () => {
  if (!currentStudent.value) return

  loading.value = true
  try {
    const res = await getRecentScores(currentStudent.value.id, 5)
    recentScores.value = res.data
  } catch (error) {
    console.error('加载成绩失败', error)
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}/${date.getDate()}`
}

// 切换学生
const handleSwitchStudent = () => {
  if (!userStore.isParent || userStore.students.length <= 1) return

  uni.showActionSheet({
    itemList: userStore.students.map(s => s.name),
    success: (res) => {
      const student = userStore.students[res.tapIndex]
      userStore.setCurrentStudentValue(student)
      currentStudent.value = student
      loadRecentScores()
    },
  })
}

// 绑定学生
const handleBindStudent = () => {
  uni.navigateTo({ url: '/pages/bind/index' })
}

// 查看所有考试
const handleViewAllExams = () => {
  uni.switchTab({ url: '/pages/exam/list' })
}

// 查看成绩详情
const handleViewScore = (score: ExamScore) => {
  uni.navigateTo({ url: `/pages/score/detail?examId=${score.examId}&studentId=${score.studentId}` })
}

// 查看答题卡
const handleViewAnswerSheet = () => {
  if (recentScores.value.length > 0) {
    const score = recentScores.value[0]
    uni.navigateTo({ url: `/pages/answer-sheet/view?examId=${score.examId}&studentId=${score.studentId}` })
  } else {
    uni.showToast({ title: '暂无考试数据', icon: 'none' })
  }
}

// 查看AI报告
const handleViewAIReport = () => {
  if (!currentStudent.value) {
    uni.showToast({ title: '请先选择学生', icon: 'none' })
    return
  }

  uni.navigateTo({ url: '/pages/report/index' })
}

// 查看成绩趋势
const handleViewTrend = () => {
  if (!currentStudent.value) {
    uni.showToast({ title: '请先选择学生', icon: 'none' })
    return
  }

  uni.navigateTo({ url: '/pages/trend/index' })
}

// 监听学生变化
watch(() => userStore.currentStudent, (newVal) => {
  currentStudent.value = newVal
  if (newVal) {
    loadRecentScores()
  }
})

onShow(() => {
  // 检查登录状态
  if (!userStore.isLoggedIn) {
    uni.reLaunch({ url: '/pages/login/index' })
    return
  }

  // 家长未绑定学生
  if (userStore.isParent && !userStore.currentStudent) {
    uni.reLaunch({ url: '/pages/bind/index' })
    return
  }

  currentStudent.value = userStore.currentStudent
  loadRecentScores()
})

onMounted(() => {
  loadRecentScores()
})
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.student-card {
  background: linear-gradient(135deg, #409EFF 0%, #79bbff 100%);
  padding: 40rpx 30rpx;
  margin: 20rpx;
  border-radius: 20rpx;
  color: #fff;

  .student-info {
    display: flex;
    align-items: center;

    .avatar {
      width: 100rpx;
      height: 100rpx;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.3);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 24rpx;

      .avatar-text {
        font-size: 48rpx;
        font-weight: bold;
      }
    }

    .info {
      flex: 1;

      .name-row {
        display: flex;
        align-items: center;

        .name {
          font-size: 36rpx;
          font-weight: bold;
        }

        .switch-icon {
          margin-left: 12rpx;
          font-size: 28rpx;
        }
      }

      .class {
        font-size: 26rpx;
        margin-top: 8rpx;
        opacity: 0.9;
      }
    }
  }

  .actions {
    margin-top: 24rpx;
    display: flex;
    justify-content: flex-end;

    .action-btn {
      display: flex;
      align-items: center;
      background: rgba(255, 255, 255, 0.2);
      padding: 12rpx 24rpx;
      border-radius: 30rpx;

      .action-icon {
        font-size: 32rpx;
        margin-right: 8rpx;
      }

      .action-text {
        font-size: 26rpx;
      }
    }
  }
}

.section {
  margin: 20rpx;
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24rpx;

    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }

    .section-more {
      font-size: 26rpx;
      color: #999;
    }
  }
}

.loading,
.empty {
  text-align: center;
  padding: 60rpx 0;
  color: #999;
  font-size: 28rpx;
}

.score-list {
  .score-item {
    background: #f8f9fa;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 20rpx;

    &:last-child {
      margin-bottom: 0;
    }

    .score-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20rpx;

      .exam-name {
        font-size: 30rpx;
        font-weight: bold;
        color: #333;
      }

      .exam-time {
        font-size: 24rpx;
        color: #999;
      }
    }

    .score-body {
      display: flex;
      align-items: center;

      .total-score {
        text-align: center;
        padding-right: 30rpx;

        .score-value {
          font-size: 56rpx;
          font-weight: bold;
          color: #409EFF;
        }

        .score-label {
          display: block;
          font-size: 24rpx;
          color: #999;
          margin-top: 4rpx;
        }
      }

      .divider {
        width: 2rpx;
        height: 60rpx;
        background: #e0e0e0;
        margin: 0 30rpx;
      }

      .rank-info {
        flex: 1;
        display: flex;

        .rank-item {
          flex: 1;
          text-align: center;

          .rank-value {
            font-size: 36rpx;
            font-weight: bold;
            color: #333;
          }

          .rank-label {
            display: block;
            font-size: 22rpx;
            color: #999;
            margin-top: 4rpx;
          }
        }
      }
    }

    .score-subjects {
      display: flex;
      flex-wrap: wrap;
      margin-top: 20rpx;
      padding-top: 20rpx;
      border-top: 1rpx solid #e0e0e0;

      .subject-item {
        width: 25%;
        text-align: center;
        margin-bottom: 12rpx;

        .subject-name {
          display: block;
          font-size: 24rpx;
          color: #666;
        }

        .subject-score {
          display: block;
          font-size: 26rpx;
          color: #333;
          font-weight: bold;
          margin-top: 4rpx;
        }

        &.more {
          display: flex;
          align-items: center;
          justify-content: center;
          color: #999;
        }
      }
    }
  }
}

.function-grid {
  display: flex;
  flex-wrap: wrap;

  .function-item {
    width: 25%;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20rpx 0;

    .function-icon {
      width: 88rpx;
      height: 88rpx;
      border-radius: 20rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 32rpx;
      font-weight: bold;
    }

    .function-text {
      font-size: 26rpx;
      color: #666;
      margin-top: 12rpx;
    }
  }
}
</style>
