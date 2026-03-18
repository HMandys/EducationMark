<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  remember: false,
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const platformHighlights = [
  { label: '系统管理', value: '用户、角色、菜单一体化维护' },
  { label: '组织管理', value: '学校、年级、班级、教师、学生全链路管理' },
  { label: '阅卷与成绩', value: '任务流转、成绩查询、发布操作集中处理' },
]

const platformStats = [
  { label: '管理模块', value: '12+' },
  { label: '核心流程', value: '8' },
  { label: '运行状态', value: 'Online' },
]

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login({
      username: loginForm.username,
      password: loginForm.password,
    })
    ElMessage.success('登录成功')
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch (error: any) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <section class="login-showcase">
      <div class="showcase-header">
        <div class="brand-box">
          <img src="@/assets/logo.svg" alt="logo" class="brand-box__logo" />
        </div>
        <div>
          <div class="showcase-kicker">EduMark Admin</div>
          <h1 class="showcase-title">智能阅卷管理后台</h1>
          <p class="showcase-desc">
            用更稳定的系统台面管理组织、考试、阅卷与成绩，不再依赖临时脚本和分散入口。
          </p>
        </div>
      </div>

      <div class="showcase-metrics">
        <div v-for="item in platformStats" :key="item.label" class="metric-card">
          <span class="metric-card__label">{{ item.label }}</span>
          <strong class="metric-card__value">{{ item.value }}</strong>
        </div>
      </div>

      <div class="showcase-panel">
        <div class="panel-title">当前后台能力</div>
        <div v-for="item in platformHighlights" :key="item.label" class="capability-row">
          <span class="capability-row__label">{{ item.label }}</span>
          <span class="capability-row__value">{{ item.value }}</span>
        </div>
      </div>
    </section>

    <section class="login-form-section">
      <div class="login-card">
        <div class="login-card__header">
          <span class="login-card__eyebrow">后台登录</span>
          <h2>进入管理控制台</h2>
          <p>使用管理员账号登录后即可访问组织、考试、阅卷和系统配置页面。</p>
        </div>

        <el-form ref="formRef" :model="loginForm" :rules="rules" size="large">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <div class="login-hint">
            <span>开发环境默认账号</span>
            <strong>admin / admin123</strong>
          </div>

          <el-form-item>
            <div class="login-options">
              <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
              <el-link type="primary" :underline="false">忘记密码？</el-link>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              class="login-btn"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <span>EduMark</span>
          <span>管理端</span>
          <span>稳定运行</span>
        </div>
      </div>
    </section>
  </div>
</template>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(360px, 1.15fr) minmax(360px, 0.85fr);
  background: #e9eef4;
}

.login-showcase {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 56px;
  background: #f4f7fb;
  border-right: 1px solid rgba(15, 23, 42, 0.08);
}

.showcase-header {
  display: flex;
  gap: 18px;
  align-items: flex-start;
}

.brand-box {
  width: 72px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 20px;
  background: #0f172a;
}

.brand-box__logo {
  width: 38px;
  height: 38px;
}

.showcase-kicker {
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #2563eb;
}

.showcase-title {
  margin-top: 12px;
  font-size: 42px;
  line-height: 1.05;
  letter-spacing: -0.03em;
  color: #14213d;
}

.showcase-desc {
  max-width: 560px;
  margin-top: 18px;
  font-size: 16px;
  line-height: 1.8;
  color: #526277;
}

.showcase-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-top: 40px;
}

.metric-card {
  padding: 18px 20px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.06);
}

.metric-card__label {
  display: block;
  color: #7b8794;
  font-size: 13px;
}

.metric-card__value {
  display: block;
  margin-top: 10px;
  font-size: 26px;
  font-weight: 700;
  color: #14213d;
}

.showcase-panel {
  margin-top: auto;
  padding: 24px 26px;
  border-radius: 24px;
  background: #0f172a;
  color: rgba(255, 255, 255, 0.92);
}

.panel-title {
  margin-bottom: 18px;
  font-size: 15px;
  font-weight: 700;
}

.capability-row {
  display: grid;
  grid-template-columns: 120px 1fr;
  gap: 12px;
  padding: 14px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.capability-row:first-of-type {
  border-top: none;
  padding-top: 0;
}

.capability-row__label {
  color: rgba(255, 255, 255, 0.58);
}

.capability-row__value {
  line-height: 1.6;
}

.login-form-section {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 28px;
}

.login-card {
  width: min(460px, 100%);
  padding: 34px 34px 28px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 26px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 24px 44px rgba(15, 23, 42, 0.08);
}

.login-card__header h2 {
  margin-top: 10px;
  font-size: 30px;
  color: #14213d;
}

.login-card__header p {
  margin-top: 10px;
  color: #6b7a90;
  line-height: 1.7;
}

.login-card__eyebrow {
  font-size: 12px;
  font-weight: 700;
  color: #2563eb;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.login-hint {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 4px 0 18px;
  padding: 12px 14px;
  border: 1px dashed #bfd2ee;
  border-radius: 12px;
  background: #f7fbff;
  color: #526277;
}

.login-hint strong {
  color: #14213d;
  font-size: 13px;
}

.login-options {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.login-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
}

.login-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 18px;
  padding-top: 18px;
  border-top: 1px solid rgba(15, 23, 42, 0.08);
  color: #8895a7;
  font-size: 12px;
}

@media (max-width: 1024px) {
  .login-page {
    grid-template-columns: 1fr;
  }

  .login-showcase {
    padding: 32px 24px;
    border-right: none;
    border-bottom: 1px solid rgba(15, 23, 42, 0.08);
  }
}

@media (max-width: 768px) {
  .login-showcase {
    display: none;
  }

  .login-form-section {
    padding: 18px;
  }

  .login-card {
    padding: 28px 22px 24px;
  }

  .login-footer {
    gap: 10px;
    flex-wrap: wrap;
  }
}
</style>
