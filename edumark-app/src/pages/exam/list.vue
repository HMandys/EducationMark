<template>
  <view class="exam-page">
    <!-- 学生信息 -->
    <view class="student-bar" v-if="currentStudent">
      <text class="student-name">{{ currentStudent.name }}</text>
      <text class="student-class">{{ currentStudent.className }}</text>
    </view>

    <!-- 考试列表 -->
    <view class="exam-list">
      <view v-if="loading" class="loading">
        <text>加载中...</text>
      </view>

      <view v-else-if="examList.length === 0" class="empty">
        <text>暂无考试数据</text>
      </view>

      <view v-else>
        <view
          v-for="exam in examList"
          :key="exam.id"
          class="exam-item"
          @click="handleViewExam(exam)"
        >
          <view class="exam-info">
            <view class="exam-name">{{ exam.name }}</view>
            <view class="exam-meta">
              <text class="meta-item">{{ exam.typeName }}</text>
              <text class="meta-item">{{ exam.academicYear }}学年</text>
              <text class="meta-item">{{ exam.semester === 1 ? '上学期' : '下学期' }}</text>
            </view>
            <view class="exam-time" v-if="exam.startTime">
              {{ formatDate(exam.startTime) }}
            </view>
          </view>
          <view class="exam-status">
            <view
              class="status-tag"
              :class="{
                'status-published': exam.status === 5,
                'status-pending': exam.status < 5
              }"
            >
              {{ exam.status === 5 ? '已发布' : '进行中' }}
            </view>
            <text class="arrow">></text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'
import { getExamList, type Exam } from '@/api/exam'

const userStore = useUserStore()

const loading = ref(false)
const examList = ref<Exam[]>([])

const currentStudent = computed(() => userStore.currentStudent)

// 加载考试列表
const loadExamList = async () => {
  if (!currentStudent.value) return

  loading.value = true
  try {
    const res = await getExamList(currentStudent.value.id)
    examList.value = res.data
  } catch (error) {
    console.error('加载考试列表失败', error)
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 查看考试详情
const handleViewExam = (exam: Exam) => {
  if (exam.status !== 5) {
    uni.showToast({ title: '成绩未发布', icon: 'none' })
    return
  }

  uni.navigateTo({
    url: `/pages/score/detail?examId=${exam.id}&studentId=${currentStudent.value?.id}`,
  })
}

onShow(() => {
  if (!userStore.isLoggedIn) {
    uni.reLaunch({ url: '/pages/login/index' })
    return
  }

  loadExamList()
})
</script>

<style lang="scss" scoped>
.exam-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.student-bar {
  background: #409EFF;
  padding: 24rpx 30rpx;
  display: flex;
  align-items: center;
  color: #fff;

  .student-name {
    font-size: 32rpx;
    font-weight: bold;
    margin-right: 20rpx;
  }

  .student-class {
    font-size: 26rpx;
    opacity: 0.9;
  }
}

.exam-list {
  padding: 20rpx;
}

.loading,
.empty {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
  font-size: 28rpx;
}

.exam-item {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .exam-info {
    flex: 1;

    .exam-name {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 12rpx;
    }

    .exam-meta {
      display: flex;
      flex-wrap: wrap;
      margin-bottom: 8rpx;

      .meta-item {
        font-size: 24rpx;
        color: #666;
        background: #f5f5f5;
        padding: 6rpx 12rpx;
        border-radius: 6rpx;
        margin-right: 12rpx;
        margin-bottom: 8rpx;
      }
    }

    .exam-time {
      font-size: 24rpx;
      color: #999;
    }
  }

  .exam-status {
    display: flex;
    align-items: center;

    .status-tag {
      font-size: 24rpx;
      padding: 8rpx 16rpx;
      border-radius: 20rpx;
      margin-right: 12rpx;

      &.status-published {
        background: #e8f5e9;
        color: #67C23A;
      }

      &.status-pending {
        background: #fff3e0;
        color: #E6A23C;
      }
    }

    .arrow {
      font-size: 28rpx;
      color: #ccc;
    }
  }
}
</style>
