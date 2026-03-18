<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore, useUserStore } from '@/store'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const appStore = useAppStore()
const userStore = useUserStore()

const currentPageTitle = computed(() => (route.meta.title as string) || '工作台')
const currentDateText = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'short',
  })
})

function toggleSidebar() {
  appStore.toggleSidebar()
}

function handleLogout() {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    userStore.logout()
  })
}
</script>

<template>
  <div class="navbar-shell">
    <div class="navbar-left">
      <button class="nav-trigger" type="button" @click="toggleSidebar">
        <el-icon :size="18">
          <Fold v-if="!appStore.sidebarCollapsed" />
          <Expand v-else />
        </el-icon>
      </button>

      <div class="navbar-meta">
        <div class="navbar-title">{{ currentPageTitle }}</div>
        <div class="navbar-subtitle">{{ currentDateText }}</div>
      </div>
    </div>

    <div class="navbar-right">
      <div class="role-chip">
        <span class="role-chip__label">当前角色</span>
        <strong>{{ userStore.roleName || '管理员' }}</strong>
      </div>

      <el-dropdown trigger="click">
        <div class="user-info">
          <el-avatar :size="34" icon="UserFilled" />
          <div class="user-info__text">
            <span class="username">{{ userStore.realName || userStore.userInfo?.username }}</span>
            <span class="user-role">{{ userStore.userInfo?.username }}</span>
          </div>
          <el-icon><ArrowDown /></el-icon>
        </div>

        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </el-dropdown-item>
            <el-dropdown-item>
              <el-icon><Setting /></el-icon>
              <span>账号设置</span>
            </el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.navbar-shell {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.navbar-left,
.navbar-right {
  display: flex;
  align-items: center;
  gap: 14px;
}

.nav-trigger {
  width: 40px;
  height: 40px;
  border: 1px solid var(--app-border);
  border-radius: 12px;
  background: #fff;
  color: var(--app-text);
  cursor: pointer;
  transition: all 0.2s ease;
}

.nav-trigger:hover {
  border-color: var(--app-primary);
  color: var(--app-primary);
}

.navbar-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.navbar-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--app-text);
}

.navbar-subtitle {
  font-size: 12px;
  color: var(--app-text-tertiary);
}

.role-chip {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border: 1px solid var(--app-border);
  border-radius: 999px;
  background: var(--app-surface-muted);
  color: var(--app-text-secondary);
}

.role-chip__label {
  font-size: 12px;
}

.role-chip strong {
  color: var(--app-text);
  font-size: 13px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
  padding: 8px 10px;
  border-radius: 14px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.user-info:hover {
  background: #f6f8fb;
}

.user-info__text {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.username {
  font-size: 14px;
  font-weight: 600;
  color: var(--app-text);
}

.user-role {
  font-size: 12px;
  color: var(--app-text-tertiary);
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
}

@media (max-width: 768px) {
  .role-chip {
    display: none;
  }

  .navbar-subtitle,
  .user-role {
    display: none;
  }
}
</style>
