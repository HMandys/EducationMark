<template>
  <view class="login-page">
    <!-- Logo区域 -->
    <view class="logo-area">
      <image class="logo" src="/static/logo.png" mode="aspectFit" />
      <text class="app-name">EduMark 查分</text>
      <text class="app-desc">智能阅卷与家长查分平台</text>
    </view>

    <!-- 登录表单 -->
    <view class="form-area">
      <!-- 用户类型切换 -->
      <view class="user-type-tabs">
        <view
          class="tab-item"
          :class="{ active: userType === 'parent' }"
          @click="userType = 'parent'"
        >
          家长登录
        </view>
        <view
          class="tab-item"
          :class="{ active: userType === 'student' }"
          @click="userType = 'student'"
        >
          学生登录
        </view>
      </view>

      <!-- 表单 -->
      <view class="form">
        <view class="form-item">
          <text class="form-label">手机号</text>
          <view class="input-wrap">
            <input
              v-model="form.phone"
              type="number"
              maxlength="11"
              placeholder="请输入手机号"
              class="input"
            />
          </view>
        </view>

        <view class="form-item">
          <text class="form-label">密码</text>
          <view class="input-wrap password-wrap">
            <input
              v-model="form.password"
              :password="!showPassword"
              placeholder="请输入密码"
              class="input"
            />
            <text
              class="password-toggle"
              @click="showPassword = !showPassword"
            >
              {{ showPassword ? '隐藏' : '显示' }}
            </text>
          </view>
        </view>

        <view class="btn-login" :class="{ disabled: !canSubmit }" @click="handleLogin">
          登 录
        </view>
      </view>

      <!-- 其他操作 -->
      <view class="other-actions">
        <text class="link" @click="handleForgetPassword">忘记密码?</text>
        <text class="link" @click="handleRegister">注册账号</text>
      </view>
    </view>

    <!-- 底部协议 -->
    <view class="agreement">
      <checkbox-group @change="handleAgreementChange">
        <label class="agreement-label">
          <checkbox :checked="agreed" color="#409EFF" />
          <text>我已阅读并同意</text>
          <text class="link" @click="handleViewAgreement($event, 'user')">《用户协议》</text>
          <text>和</text>
          <text class="link" @click="handleViewAgreement($event, 'privacy')">《隐私政策》</text>
        </label>
      </checkbox-group>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/auth'
import { loadStudentContext } from '@/services/session'

const userStore = useUserStore()

// 表单数据
const form = ref({
  phone: '',
  password: '',
})
const userType = ref<'parent' | 'student'>('parent')
const showPassword = ref(false)
const agreed = ref(false)
const loading = ref(false)

// 是否可以提交
const canSubmit = computed(() => {
  return form.value.phone.length === 11 && form.value.password.length >= 6 && agreed.value
})

// 处理协议勾选
const handleAgreementChange = () => {
  agreed.value = !agreed.value
}

// 登录
const handleLogin = async () => {
  if (!canSubmit.value || loading.value) return

  if (!agreed.value) {
    uni.showToast({ title: '请先同意用户协议', icon: 'none' })
    return
  }

  loading.value = true
  try {
    const res = await login({
      phone: form.value.phone,
      password: form.value.password,
      userType: userType.value,
    })

    // 保存登录信息
    userStore.setTokenValue(res.data.token)
    userStore.setUserInfoValue(res.data.userInfo)

    await loadStudentContext()

    if (userType.value === 'parent' && userStore.students.length === 0) {
      uni.reLaunch({ url: '/pages/bind/index?first=1' })
      return
    }

    uni.showToast({ title: '登录成功', icon: 'success' })
    uni.switchTab({ url: '/pages/home/index' })
  } catch (error) {
    console.error('登录失败', error)
  } finally {
    loading.value = false
  }
}

// 忘记密码
const handleForgetPassword = () => {
  uni.showToast({ title: '请联系学校管理员重置密码', icon: 'none' })
}

// 注册
const handleRegister = () => {
  uni.navigateTo({ url: '/pages/register/index' })
}

// 查看协议
const handleViewAgreement = (event: any, type: string) => {
  event?.stopPropagation?.()
  uni.showToast({ title: `查看${type === 'user' ? '用户协议' : '隐私政策'}`, icon: 'none' })
}
</script>

<style lang="scss" scoped>
.login-page {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(180deg, #409EFF 0%, #79bbff 100%);
  display: flex;
  flex-direction: column;
  padding: 0;
  box-sizing: border-box;
  overflow-x: hidden;
}

.logo-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 120rpx;
  margin-bottom: 60rpx;

  .logo {
    width: 160rpx;
    height: 160rpx;
    margin-bottom: 20rpx;
  }

  .app-name {
    font-size: 48rpx;
    font-weight: bold;
    color: #fff;
    margin-bottom: 12rpx;
  }

  .app-desc {
    font-size: 28rpx;
    color: rgba(255, 255, 255, 0.8);
  }
}

.form-area {
  align-self: center;
  width: auto;
  margin: 0 36rpx;
  max-width: 640rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
  box-sizing: border-box;
}

.user-type-tabs {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  width: 100%;
  margin-bottom: 40rpx;

  .tab-item {
    min-width: 0;
    text-align: center;
    padding: 20rpx 0;
    font-size: 32rpx;
    color: #999;
    border-bottom: 4rpx solid transparent;
    transition: all 0.3s;

    &.active {
      color: #409EFF;
      border-bottom-color: #409EFF;
      font-weight: bold;
    }
  }
}

.form {
  display: flex;
  flex-direction: column;
  gap: 28rpx;

  .form-item {
    width: 100%;
    margin-bottom: 0;
    box-sizing: border-box;
  }

  .form-label {
    display: block;
    margin-bottom: 12rpx;
    font-size: 26rpx;
    line-height: 1.4;
    color: #606266;
  }

  .input-wrap {
    width: 100%;
    display: flex;
    align-items: center;
    position: relative;
    background: #f5f7fa;
    border-radius: 12rpx;
    padding: 0 24rpx;
    min-height: 96rpx;
    box-sizing: border-box;

    .password-toggle {
      position: absolute;
      right: 24rpx;
      top: 50%;
      transform: translateY(-50%);
      font-size: 26rpx;
      line-height: 1;
      color: #409EFF;
    }

    .input {
      flex: 1;
      min-width: 0;
      width: 100%;
      height: 96rpx;
      line-height: 96rpx;
      font-size: 30rpx;
      text-align: left;
      box-sizing: border-box;
    }
  }

  .password-wrap {
    .input {
      padding-right: 90rpx;
    }
  }
}

.btn-login {
  width: 100%;
  background: #409EFF;
  color: #fff;
  text-align: center;
  padding: 28rpx 0;
  border-radius: 48rpx;
  font-size: 34rpx;
  font-weight: bold;
  margin-top: 10rpx;
  line-height: 1.2;
  box-sizing: border-box;

  &.disabled {
    background: #a0cfff;
  }

  &:active {
    opacity: 0.8;
  }
}

.other-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 34rpx;
  gap: 24rpx;

  .link {
    font-size: 26rpx;
    color: #409EFF;
  }
}

.agreement {
  align-self: center;
  width: auto;
  margin-left: 36rpx;
  margin-right: 36rpx;
  max-width: 640rpx;
  margin-top: auto;
  padding: 40rpx 0;
  box-sizing: border-box;

  .agreement-label {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-wrap: wrap;
    row-gap: 8rpx;
    font-size: 24rpx;
    line-height: 1.6;
    color: rgba(255, 255, 255, 0.9);

    checkbox {
      transform: scale(0.7);
    }

    .link {
      color: #fff;
      text-decoration: underline;
    }
  }
}
</style>
