<template>
  <view class="bind-page">
    <view class="header">
      <text class="title">绑定学生</text>
      <text class="desc">请输入学生信息进行绑定</text>
    </view>

    <view class="form-card">
      <view class="form-item">
        <text class="label">学生姓名</text>
        <input
          v-model="form.studentName"
          placeholder="请输入学生姓名"
          class="input"
        />
      </view>

      <view class="form-item">
        <text class="label">学号</text>
        <input
          v-model="form.studentNumber"
          placeholder="请输入学号"
          class="input"
        />
      </view>

      <view class="form-item">
        <text class="label">绑定码</text>
        <input
          v-model="form.bindCode"
          placeholder="请输入学校提供的绑定码"
          class="input"
        />
      </view>

      <view class="tips">
        <text class="tips-icon">!</text>
        <text class="tips-text">绑定码由学校或班主任提供，用于验证家长身份</text>
      </view>

      <view class="btn-bind" :class="{ disabled: !canSubmit }" @click="handleBind">
        确认绑定
      </view>
    </view>

    <!-- 已绑定的学生列表 -->
    <view class="bound-list" v-if="userStore.students.length > 0">
      <view class="list-header">
        <text class="list-title">已绑定学生</text>
      </view>

      <view
        v-for="student in userStore.students"
        :key="student.id"
        class="student-item"
      >
        <view class="student-info">
          <text class="student-name">{{ student.name }}</text>
          <text class="student-class">{{ student.schoolName }} · {{ student.className }}</text>
        </view>
        <text class="unbind-btn" @click="handleUnbind(student)">解绑</text>
      </view>
    </view>

    <!-- 跳过按钮（首次绑定时显示） -->
    <view class="skip-area" v-if="isFirstBind && userStore.students.length > 0">
      <text class="skip-btn" @click="handleSkip">完成，进入首页</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useUserStore, type Student } from '@/stores/user'
import { bindStudent, unbindStudent, getBindStudents } from '@/api/student'

const userStore = useUserStore()

const form = ref({
  studentName: '',
  studentNumber: '',
  bindCode: '',
})

const loading = ref(false)
const isFirstBind = ref(false)

// 是否可以提交
const canSubmit = computed(() => {
  return form.value.studentName && form.value.studentNumber && form.value.bindCode
})

// 绑定学生
const handleBind = async () => {
  if (!canSubmit.value || loading.value) return

  loading.value = true
  try {
    await bindStudent(form.value)

    // 刷新学生列表
    const res = await getBindStudents()
    userStore.setStudents(res.data)

    uni.showToast({ title: '绑定成功', icon: 'success' })

    // 清空表单
    form.value = {
      studentName: '',
      studentNumber: '',
      bindCode: '',
    }
  } catch (error) {
    console.error('绑定失败', error)
  } finally {
    loading.value = false
  }
}

// 解绑学生
const handleUnbind = async (student: Student) => {
  uni.showModal({
    title: '确认解绑',
    content: `确定要解绑学生"${student.name}"吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await unbindStudent(student.id)

          // 刷新学生列表
          const res = await getBindStudents()
          userStore.setStudents(res.data)

          uni.showToast({ title: '解绑成功', icon: 'success' })
        } catch (error) {
          console.error('解绑失败', error)
        }
      }
    },
  })
}

// 跳过/完成
const handleSkip = () => {
  if (userStore.students.length === 0) {
    uni.showToast({ title: '请先绑定至少一个学生', icon: 'none' })
    return
  }

  uni.switchTab({ url: '/pages/home/index' })
}

onLoad((options) => {
  isFirstBind.value = options?.first === '1'
})
</script>

<style lang="scss" scoped>
.bind-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 30rpx;
}

.header {
  text-align: center;
  padding: 40rpx 0;

  .title {
    display: block;
    font-size: 40rpx;
    font-weight: bold;
    color: #333;
  }

  .desc {
    display: block;
    font-size: 28rpx;
    color: #999;
    margin-top: 12rpx;
  }
}

.form-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;

  .form-item {
    margin-bottom: 30rpx;

    .label {
      display: block;
      font-size: 28rpx;
      color: #333;
      margin-bottom: 16rpx;
    }

    .input {
      background: #f5f7fa;
      border-radius: 12rpx;
      padding: 24rpx;
      font-size: 30rpx;
    }
  }

  .tips {
    display: flex;
    align-items: flex-start;
    background: #fdf6ec;
    padding: 20rpx;
    border-radius: 12rpx;
    margin-bottom: 30rpx;

    .tips-icon {
      width: 36rpx;
      height: 36rpx;
      border-radius: 50%;
      background: #E6A23C;
      color: #fff;
      font-size: 24rpx;
      font-weight: bold;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 12rpx;
      flex-shrink: 0;
    }

    .tips-text {
      font-size: 26rpx;
      color: #E6A23C;
      line-height: 1.5;
    }
  }

  .btn-bind {
    background: #409EFF;
    color: #fff;
    text-align: center;
    padding: 28rpx 0;
    border-radius: 48rpx;
    font-size: 32rpx;
    font-weight: bold;

    &.disabled {
      background: #a0cfff;
    }

    &:active {
      opacity: 0.8;
    }
  }
}

.bound-list {
  margin-top: 30rpx;
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;

  .list-header {
    margin-bottom: 20rpx;

    .list-title {
      font-size: 30rpx;
      font-weight: bold;
      color: #333;
    }
  }

  .student-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24rpx 0;
    border-bottom: 1rpx solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .student-info {
      .student-name {
        display: block;
        font-size: 30rpx;
        font-weight: bold;
        color: #333;
      }

      .student-class {
        display: block;
        font-size: 26rpx;
        color: #999;
        margin-top: 8rpx;
      }
    }

    .unbind-btn {
      font-size: 26rpx;
      color: #F56C6C;
      padding: 12rpx 24rpx;
    }
  }
}

.skip-area {
  margin-top: 40rpx;
  text-align: center;

  .skip-btn {
    font-size: 30rpx;
    color: #409EFF;
    padding: 20rpx 40rpx;
  }
}
</style>
