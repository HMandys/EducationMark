<template>
  <view class="mine-page">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="avatar">
        <text class="avatar-text">{{ userStore.userInfo?.nickname?.charAt(0) || '?' }}</text>
      </view>
      <view class="user-info">
        <text class="nickname">{{ userStore.userInfo?.nickname || '未登录' }}</text>
        <text class="phone">{{ maskPhone(userStore.userInfo?.phone) }}</text>
      </view>
    </view>

    <!-- 绑定学生（家长端） -->
    <view class="section" v-if="userStore.isParent">
      <view class="section-header">
        <text class="section-title">已绑定学生</text>
        <text class="section-action" @click="handleBindStudent">+ 绑定</text>
      </view>

      <view class="student-list">
        <view
          v-for="student in userStore.students"
          :key="student.id"
          class="student-item"
          :class="{ active: userStore.currentStudent?.id === student.id }"
          @click="handleSelectStudent(student)"
        >
          <view class="student-avatar">
            <text>{{ student.name.charAt(0) }}</text>
          </view>
          <view class="student-info">
            <text class="student-name">{{ student.name }}</text>
            <text class="student-class">{{ student.schoolName }} · {{ student.className }}</text>
          </view>
          <view class="check-icon" v-if="userStore.currentStudent?.id === student.id">✓</view>
        </view>
      </view>
    </view>

    <!-- 学生信息（学生端） -->
    <view class="section" v-if="userStore.isStudent && userStore.currentStudent">
      <view class="section-header">
        <text class="section-title">学生信息</text>
      </view>

      <view class="info-list">
        <view class="info-item">
          <text class="info-label">姓名</text>
          <text class="info-value">{{ userStore.currentStudent.name }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">学号</text>
          <text class="info-value">{{ userStore.currentStudent.studentNumber }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">学校</text>
          <text class="info-value">{{ userStore.currentStudent.schoolName }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">班级</text>
          <text class="info-value">{{ userStore.currentStudent.className }}</text>
        </view>
      </view>
    </view>

    <!-- 功能列表 -->
    <view class="menu-list">
      <view class="menu-item" @click="handleChangePassword">
        <text class="menu-icon">&#xe607;</text>
        <text class="menu-text">修改密码</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="handleAbout">
        <text class="menu-icon">&#xe608;</text>
        <text class="menu-text">关于我们</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="handleFeedback">
        <text class="menu-icon">&#xe609;</text>
        <text class="menu-text">意见反馈</text>
        <text class="menu-arrow">></text>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-btn" @click="handleLogout">退出登录</view>

    <!-- 版本信息 -->
    <view class="version">
      <text>EduMark v1.0.0</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { useUserStore, type Student } from '@/stores/user'

const userStore = useUserStore()

// 手机号脱敏
const maskPhone = (phone?: string) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 选择学生
const handleSelectStudent = (student: Student) => {
  userStore.setCurrentStudentValue(student)
  uni.showToast({ title: `已切换到 ${student.name}`, icon: 'success' })
}

// 绑定学生
const handleBindStudent = () => {
  uni.navigateTo({ url: '/pages/bind/index' })
}

// 修改密码
const handleChangePassword = () => {
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

// 关于我们
const handleAbout = () => {
  uni.showToast({ title: 'EduMark 智能阅卷与查分平台', icon: 'none' })
}

// 意见反馈
const handleFeedback = () => {
  uni.showToast({ title: '请联系学校管理员反馈', icon: 'none' })
}

// 退出登录
const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
        uni.reLaunch({ url: '/pages/login/index' })
      }
    },
  })
}
</script>

<style lang="scss" scoped>
.mine-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.user-card {
  background: linear-gradient(135deg, #409EFF 0%, #79bbff 100%);
  padding: 60rpx 30rpx;
  display: flex;
  align-items: center;
  color: #fff;

  .avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 30rpx;

    .avatar-text {
      font-size: 48rpx;
      font-weight: bold;
    }
  }

  .user-info {
    .nickname {
      display: block;
      font-size: 36rpx;
      font-weight: bold;
      margin-bottom: 8rpx;
    }

    .phone {
      display: block;
      font-size: 28rpx;
      opacity: 0.9;
    }
  }
}

.section {
  margin: 20rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;

    .section-title {
      font-size: 30rpx;
      font-weight: bold;
      color: #333;
    }

    .section-action {
      font-size: 28rpx;
      color: #409EFF;
    }
  }
}

.student-list {
  .student-item {
    display: flex;
    align-items: center;
    padding: 20rpx;
    border-radius: 12rpx;
    margin-bottom: 16rpx;
    background: #f8f9fa;

    &.active {
      background: #e8f4ff;
      border: 2rpx solid #409EFF;
    }

    &:last-child {
      margin-bottom: 0;
    }

    .student-avatar {
      width: 72rpx;
      height: 72rpx;
      border-radius: 50%;
      background: #409EFF;
      color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 32rpx;
      font-weight: bold;
      margin-right: 20rpx;
    }

    .student-info {
      flex: 1;

      .student-name {
        display: block;
        font-size: 30rpx;
        font-weight: bold;
        color: #333;
      }

      .student-class {
        display: block;
        font-size: 24rpx;
        color: #999;
        margin-top: 6rpx;
      }
    }

    .check-icon {
      font-size: 32rpx;
      color: #409EFF;
      font-weight: bold;
    }
  }
}

.info-list {
  .info-item {
    display: flex;
    justify-content: space-between;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .info-label {
      font-size: 28rpx;
      color: #999;
    }

    .info-value {
      font-size: 28rpx;
      color: #333;
    }
  }
}

.menu-list {
  margin: 20rpx;
  background: #fff;
  border-radius: 16rpx;

  .menu-item {
    display: flex;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .menu-icon {
      font-size: 40rpx;
      color: #409EFF;
      margin-right: 20rpx;
    }

    .menu-text {
      flex: 1;
      font-size: 30rpx;
      color: #333;
    }

    .menu-arrow {
      font-size: 28rpx;
      color: #ccc;
    }
  }
}

.logout-btn {
  margin: 40rpx 20rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
  text-align: center;
  font-size: 32rpx;
  color: #F56C6C;

  &:active {
    background: #f5f5f5;
  }
}

.version {
  text-align: center;
  font-size: 24rpx;
  color: #ccc;
  margin-top: 40rpx;
}
</style>
