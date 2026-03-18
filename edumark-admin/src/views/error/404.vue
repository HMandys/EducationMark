<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

function goHome() {
  router.push('/')
}

function goBack() {
  router.back()
}
</script>

<template>
  <div class="error-page">
    <div class="error-panel">
      <div class="error-panel__code">404</div>
      <div class="error-panel__title">当前页面不存在</div>
      <p class="error-panel__desc">
        可能是菜单权限未开放、地址输入错误，或者该页面已经调整到新的业务入口。
      </p>

      <div class="error-panel__tips">
        <div class="tips-item">
          <span class="tips-item__label">建议一</span>
          <span class="tips-item__value">先回到工作台确认当前账号可见菜单</span>
        </div>
        <div class="tips-item">
          <span class="tips-item__label">建议二</span>
          <span class="tips-item__value">如仍无法访问，检查对应角色是否已分配权限</span>
        </div>
      </div>

      <div class="error-actions">
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button @click="goBack">返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.error-page {
  width: 100%;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: #eef2f6;
}

.error-panel {
  width: min(640px, 100%);
  padding: 36px;
  border: 1px solid var(--app-border);
  border-radius: 28px;
  background: #fff;
  box-shadow: var(--app-shadow);
}

.error-panel__code {
  font-size: 92px;
  font-weight: 800;
  line-height: 1;
  letter-spacing: -0.06em;
  color: #2563eb;
}

.error-panel__title {
  margin-top: 18px;
  font-size: 28px;
  font-weight: 700;
  color: var(--app-text);
}

.error-panel__desc {
  margin-top: 12px;
  line-height: 1.8;
  color: var(--app-text-secondary);
}

.error-panel__tips {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 22px;
}

.tips-item {
  display: grid;
  grid-template-columns: 70px 1fr;
  gap: 14px;
  padding: 14px 16px;
  border: 1px solid var(--app-border);
  border-radius: 14px;
  background: var(--app-surface-muted);
}

.tips-item__label {
  color: var(--app-text-tertiary);
}

.tips-item__value {
  color: var(--app-text);
}

.error-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .error-panel {
    padding: 28px 22px;
  }

  .error-panel__code {
    font-size: 72px;
  }

  .error-actions {
    flex-direction: column;
  }
}
</style>
