<template>
  <view class="register-page">
    <view class="hero">
      <text class="title">注册家长账号</text>
      <text class="desc">注册后使用学生姓名、学号和绑定码完成学生绑定</text>
    </view>

    <view class="form-card">
      <view class="form-item">
        <text class="label">家长姓名</text>
        <input
          v-model="form.nickname"
          class="input"
          placeholder="请输入家长姓名"
          maxlength="20"
        />
      </view>

      <view class="form-item">
        <text class="label">手机号</text>
        <input
          v-model="form.phone"
          class="input"
          type="number"
          maxlength="11"
          placeholder="请输入手机号"
        />
      </view>

      <view class="form-item">
        <text class="label">登录密码</text>
        <view class="password-input">
          <input
            v-model="form.password"
            class="input"
            :password="!showPassword"
            maxlength="32"
            placeholder="请设置6位以上密码"
          />
          <text class="toggle" @click="showPassword = !showPassword">
            {{ showPassword ? '隐藏' : '显示' }}
          </text>
        </view>
      </view>

      <view class="form-item">
        <text class="label">确认密码</text>
        <input
          v-model="form.confirmPassword"
          class="input"
          :password="!showPassword"
          maxlength="32"
          placeholder="请再次输入密码"
        />
      </view>

      <view class="tips">
        <text>学生账号由学校开通。家长注册后，需要输入学校提供的绑定码才能查看学生成绩。</text>
      </view>

      <view class="btn-register" :class="{ disabled: !canSubmit }" @click="handleRegister">
        注册并绑定学生
      </view>

      <view class="login-link" @click="handleBackLogin">
        已有账号，返回登录
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { register } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import { loadStudentContext } from '@/services/session'

const userStore = useUserStore()

const form = ref({
  nickname: '',
  phone: '',
  password: '',
  confirmPassword: '',
})

const showPassword = ref(false)
const loading = ref(false)

const canSubmit = computed(() => {
  return form.value.nickname.trim().length > 0
    && /^1[3-9]\d{9}$/.test(form.value.phone)
    && form.value.password.length >= 6
    && form.value.confirmPassword.length >= 6
})

const handleRegister = async () => {
  if (!canSubmit.value || loading.value) return

  if (form.value.password !== form.value.confirmPassword) {
    uni.showToast({ title: '两次输入的密码不一致', icon: 'none' })
    return
  }

  loading.value = true
  try {
    const res = await register({
      nickname: form.value.nickname.trim(),
      phone: form.value.phone,
      password: form.value.password,
      userType: 'parent',
    })

    userStore.setTokenValue(res.data.token)
    userStore.setUserInfoValue(res.data.userInfo)
    await loadStudentContext()

    uni.showToast({ title: '注册成功，请绑定学生', icon: 'success' })
    uni.reLaunch({ url: '/pages/bind/index?first=1' })
  } catch (error) {
    console.error('注册失败', error)
  } finally {
    loading.value = false
  }
}

const handleBackLogin = () => {
  uni.navigateBack({
    fail: () => {
      uni.reLaunch({ url: '/pages/login/index' })
    },
  })
}
</script>

<style lang="scss" scoped>
.register-page {
  width: 100%;
  min-height: 100vh;
  background: #f5f7fa;
  padding: 36rpx 24rpx 60rpx;
  box-sizing: border-box;
  overflow-x: hidden;
}

.hero {
  padding: 28rpx 6rpx 34rpx;

  .title {
    display: block;
    font-size: 44rpx;
    font-weight: 700;
    color: #1f2d3d;
    line-height: 1.3;
  }

  .desc {
    display: block;
    margin-top: 14rpx;
    font-size: 28rpx;
    line-height: 1.6;
    color: #6b7280;
  }
}

.form-card {
  width: 100%;
  background: #fff;
  border-radius: 20rpx;
  padding: 34rpx 24rpx;
  box-sizing: border-box;
  box-shadow: 0 8rpx 28rpx rgba(31, 45, 61, 0.08);
}

.form-item {
  margin-bottom: 28rpx;

  .label {
    display: block;
    margin-bottom: 14rpx;
    font-size: 28rpx;
    color: #303133;
    line-height: 1.4;
  }
}

.input {
  width: 100%;
  height: 92rpx;
  line-height: 92rpx;
  background: #f5f7fa;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 30rpx;
  color: #303133;
  text-align: left;
  box-sizing: border-box;
}

.password-input {
  position: relative;

  .input {
    padding-right: 104rpx;
  }

  .toggle {
    position: absolute;
    right: 24rpx;
    top: 50%;
    transform: translateY(-50%);
    font-size: 26rpx;
    color: #409EFF;
    line-height: 1;
  }
}

.tips {
  background: #ecf5ff;
  border-radius: 12rpx;
  padding: 20rpx 22rpx;
  margin: 4rpx 0 30rpx;
  font-size: 26rpx;
  line-height: 1.6;
  color: #337ecc;
}

.btn-register {
  width: 100%;
  background: #409EFF;
  color: #fff;
  text-align: center;
  padding: 28rpx 0;
  border-radius: 48rpx;
  font-size: 32rpx;
  font-weight: 700;
  line-height: 1.2;
  box-sizing: border-box;

  &.disabled {
    background: #a0cfff;
  }

  &:active {
    opacity: 0.82;
  }
}

.login-link {
  margin-top: 30rpx;
  text-align: center;
  font-size: 28rpx;
  color: #409EFF;
}
</style>
