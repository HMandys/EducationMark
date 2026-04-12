<template>
  <view class="password-page">
    <view class="notice-card">
      <text class="notice-title">账号安全</text>
      <text class="notice-desc">建议定期更新密码，密码长度 6-20 位，尽量包含数字和字母。</text>
    </view>

    <view class="form-card">
      <view class="form-item">
        <text class="label">原密码</text>
        <input
          v-model="form.oldPassword"
          class="input"
          :password="!showOldPassword"
          placeholder="请输入原密码"
        />
        <text class="toggle" @click="showOldPassword = !showOldPassword">
          {{ showOldPassword ? '隐藏' : '显示' }}
        </text>
      </view>

      <view class="form-item">
        <text class="label">新密码</text>
        <input
          v-model="form.newPassword"
          class="input"
          :password="!showNewPassword"
          placeholder="请输入新密码"
        />
        <text class="toggle" @click="showNewPassword = !showNewPassword">
          {{ showNewPassword ? '隐藏' : '显示' }}
        </text>
      </view>

      <view class="form-item">
        <text class="label">确认新密码</text>
        <input
          v-model="form.confirmPassword"
          class="input"
          :password="!showConfirmPassword"
          placeholder="请再次输入新密码"
        />
        <text class="toggle" @click="showConfirmPassword = !showConfirmPassword">
          {{ showConfirmPassword ? '隐藏' : '显示' }}
        </text>
      </view>

      <view class="strength">
        <text class="strength-label">密码强度</text>
        <view class="strength-bars">
          <view
            v-for="level in 3"
            :key="level"
            class="strength-bar"
            :class="{ active: passwordStrength >= level }"
          />
        </view>
        <text class="strength-text">{{ strengthText }}</text>
      </view>

      <view class="btn-submit" :class="{ disabled: !canSubmit || loading }" @click="handleSubmit">
        {{ loading ? '提交中...' : '确认修改' }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { changePassword } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const form = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const loading = ref(false)
const showOldPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

const passwordStrength = computed(() => {
  const value = form.value.newPassword
  if (value.length < 6) return 0

  let score = 1
  if (/[A-Za-z]/.test(value) && /\d/.test(value)) score += 1
  if (/[^A-Za-z\d]/.test(value) || value.length >= 10) score += 1
  return Math.min(score, 3)
})

const strengthText = computed(() => {
  if (passwordStrength.value <= 1) return '弱'
  if (passwordStrength.value === 2) return '中'
  return '强'
})

const canSubmit = computed(() => {
  return (
    form.value.oldPassword.length >= 6
    && form.value.newPassword.length >= 6
    && form.value.confirmPassword.length >= 6
  )
})

const resetForm = () => {
  form.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
  }
}

const handleSubmit = async () => {
  if (!canSubmit.value || loading.value) return

  if (form.value.newPassword !== form.value.confirmPassword) {
    uni.showToast({ title: '两次输入的新密码不一致', icon: 'none' })
    return
  }

  if (form.value.oldPassword === form.value.newPassword) {
    uni.showToast({ title: '新密码不能与原密码相同', icon: 'none' })
    return
  }

  loading.value = true
  try {
    await changePassword({
      oldPassword: form.value.oldPassword,
      newPassword: form.value.newPassword,
    })

    uni.showModal({
      title: '修改成功',
      content: '密码已更新，请重新登录。',
      showCancel: false,
      success: () => {
        userStore.logout()
        resetForm()
        uni.reLaunch({ url: '/pages/login/index' })
      },
    })
  } catch (error) {
    console.error('修改密码失败', error)
  } finally {
    loading.value = false
  }
}

onShow(() => {
  if (!userStore.isLoggedIn) {
    uni.reLaunch({ url: '/pages/login/index' })
  }
})
</script>

<style lang="scss" scoped>
.password-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 24rpx;
}

.notice-card,
.form-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 28rpx;
  box-shadow: 0 8rpx 24rpx rgba(15, 23, 42, 0.05);
}

.notice-card {
  margin-bottom: 24rpx;

  .notice-title {
    display: block;
    font-size: 32rpx;
    font-weight: 700;
    color: #1f2937;
    margin-bottom: 12rpx;
  }

  .notice-desc {
    font-size: 26rpx;
    color: #6b7280;
    line-height: 1.6;
  }
}

.form-item {
  margin-bottom: 24rpx;

  .label {
    display: block;
    font-size: 28rpx;
    color: #374151;
    margin-bottom: 14rpx;
  }

  .input {
    background: #f8fafc;
    border: 2rpx solid #e5e7eb;
    border-radius: 14rpx;
    padding: 22rpx 24rpx;
    font-size: 30rpx;
  }

  .toggle {
    display: inline-block;
    margin-top: 12rpx;
    font-size: 24rpx;
    color: #409EFF;
  }
}

.strength {
  margin: 12rpx 0 36rpx;

  .strength-label {
    font-size: 26rpx;
    color: #6b7280;
  }

  .strength-bars {
    display: flex;
    gap: 12rpx;
    margin: 14rpx 0 12rpx;
  }

  .strength-bar {
    flex: 1;
    height: 12rpx;
    border-radius: 999rpx;
    background: #e5e7eb;

    &.active {
      background: linear-gradient(90deg, #409EFF 0%, #67C23A 100%);
    }
  }

  .strength-text {
    font-size: 24rpx;
    color: #6b7280;
  }
}

.btn-submit {
  background: linear-gradient(135deg, #409EFF 0%, #2563eb 100%);
  color: #fff;
  text-align: center;
  padding: 28rpx 0;
  border-radius: 999rpx;
  font-size: 32rpx;
  font-weight: 700;

  &.disabled {
    opacity: 0.55;
  }
}
</style>
