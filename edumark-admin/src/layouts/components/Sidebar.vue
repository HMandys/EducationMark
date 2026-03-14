<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/store/app'
import { authRoutes } from '@/router'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 过滤出可见的菜单
const menuList = computed(() => {
  return authRoutes.filter((item) => !item.meta?.hidden)
})

// 跳转路由
function handleMenuClick(path: string) {
  router.push(path)
}
</script>

<template>
  <div class="sidebar-container">
    <!-- Logo -->
    <div class="logo">
      <img src="@/assets/logo.svg" alt="logo" class="logo-img" />
      <span v-show="!appStore.sidebarCollapsed" class="logo-text">EduMark</span>
    </div>

    <!-- 菜单 -->
    <el-scrollbar>
      <el-menu
        :default-active="activeMenu"
        :collapse="appStore.sidebarCollapsed"
        :collapse-transition="false"
        background-color="#001529"
        text-color="#ffffffa6"
        active-text-color="#fff"
        unique-opened
        router
      >
        <template v-for="menu in menuList" :key="menu.path">
          <!-- 单级菜单 -->
          <el-menu-item
            v-if="!menu.children || menu.children.length === 1"
            :index="menu.redirect as string || menu.path"
            @click="handleMenuClick(menu.redirect as string || menu.path)"
          >
            <el-icon v-if="menu.meta?.icon || menu.children?.[0]?.meta?.icon">
              <component :is="menu.meta?.icon || menu.children?.[0]?.meta?.icon" />
            </el-icon>
            <template #title>
              {{ menu.meta?.title || menu.children?.[0]?.meta?.title }}
            </template>
          </el-menu-item>

          <!-- 多级菜单 -->
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
.sidebar-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  background-color: #002140;

  .logo-img {
    width: 32px;
    height: 32px;
  }

  .logo-text {
    margin-left: 12px;
    font-size: 18px;
    font-weight: 600;
    color: #fff;
    white-space: nowrap;
  }
}

:deep(.el-menu) {
  border-right: none;

  .el-menu-item {
    &:hover {
      background-color: #000c17;
    }

    &.is-active {
      background-color: #1890ff;
    }
  }

  .el-sub-menu__title:hover {
    background-color: #000c17;
  }
}
</style>
