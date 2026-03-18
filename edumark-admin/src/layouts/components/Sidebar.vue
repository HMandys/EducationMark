<script setup lang="ts">
import { computed } from 'vue'
import type { RouteRecordRaw } from 'vue-router'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/store/app'
import { useUserStore } from '@/store/user'
import { authRoutes } from '@/router'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const filterRoute = (routeItem: RouteRecordRaw): RouteRecordRaw | null => {
  if (routeItem.meta?.hidden) {
    return null
  }

  const permission = routeItem.meta?.permission as string | undefined
  if (permission && !userStore.hasPermission(permission)) {
    return null
  }

  const children = routeItem.children
    ?.map(child => filterRoute(child))
    .filter((child): child is RouteRecordRaw => child !== null)

  const nextRoute = { ...routeItem } as RouteRecordRaw

  if (children) {
    nextRoute.children = children
  } else {
    delete (nextRoute as { children?: RouteRecordRaw[] }).children
  }

  return nextRoute
}

const menuList = computed(() => {
  return authRoutes
    .map(item => filterRoute(item))
    .filter((item): item is RouteRecordRaw => item !== null)
    .filter(item => !item.meta?.hidden)
})

function handleMenuClick(path: string) {
  router.push(path)
}
</script>

<template>
  <div class="sidebar-shell">
    <div class="brand-panel">
      <div class="brand-mark">
        <img src="@/assets/logo.svg" alt="logo" class="logo-img" />
      </div>
      <div v-show="!appStore.sidebarCollapsed" class="brand-copy">
        <span class="brand-copy__title">EduMark</span>
        <span class="brand-copy__desc">阅卷管理后台</span>
      </div>
    </div>

    <el-scrollbar class="menu-scroll">
      <el-menu
        :default-active="activeMenu"
        :collapse="appStore.sidebarCollapsed"
        :collapse-transition="false"
        unique-opened
        router
      >
        <template v-for="menu in menuList" :key="menu.path">
          <el-menu-item
            v-if="!menu.children || menu.children.length === 1"
            :index="(menu.redirect as string) || menu.path"
            @click="handleMenuClick((menu.redirect as string) || menu.path)"
          >
            <el-icon v-if="menu.meta?.icon || menu.children?.[0]?.meta?.icon">
              <component :is="menu.meta?.icon || menu.children?.[0]?.meta?.icon" />
            </el-icon>
            <template #title>
              {{ menu.meta?.title || menu.children?.[0]?.meta?.title }}
            </template>
          </el-menu-item>

          <el-sub-menu v-else :index="menu.path">
            <template #title>
              <el-icon v-if="menu.meta?.icon">
                <component :is="menu.meta.icon" />
              </el-icon>
              <span>{{ menu.meta?.title }}</span>
            </template>

            <el-menu-item
              v-for="child in menu.children"
              :key="child.path"
              :index="`${menu.path}/${child.path}`"
            >
              <el-icon v-if="child.meta?.icon">
                <component :is="child.meta.icon" />
              </el-icon>
              <template #title>{{ child.meta?.title }}</template>
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<style lang="scss" scoped>
.sidebar-shell {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 18px 14px 16px;
  background: #0f172a;
}

.brand-panel {
  display: flex;
  align-items: center;
  gap: 12px;
  min-height: 64px;
  padding: 0 8px 18px;
}

.brand-mark {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.08);
}

.logo-img {
  width: 28px;
  height: 28px;
}

.brand-copy {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.brand-copy__title {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.01em;
}

.brand-copy__desc {
  margin-top: 2px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.58);
}

.menu-scroll {
  flex: 1;
}

:deep(.el-menu) {
  border-right: none;
  background: transparent;
}

:deep(.el-menu--collapse) {
  width: auto;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 44px;
  margin-bottom: 6px;
  border-radius: 12px;
  color: rgba(255, 255, 255, 0.72);
  background: transparent;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
}

:deep(.el-menu-item.is-active) {
  background: #2563eb;
  color: #fff;
}

:deep(.el-sub-menu .el-menu-item) {
  min-width: auto;
  margin-left: 6px;
}

:deep(.el-sub-menu .el-menu) {
  background: transparent;
}
</style>
