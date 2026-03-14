<script setup lang="ts">
import { computed } from 'vue'
import { useAppStore } from '@/store/app'
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Navbar.vue'

const appStore = useAppStore()

const sidebarWidth = computed(() => (appStore.sidebarCollapsed ? '64px' : '220px'))
</script>

<template>
  <div class="layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :style="{ width: sidebarWidth }">
      <Sidebar />
    </aside>

    <!-- 主内容区 -->
    <div class="main-container" :style="{ marginLeft: sidebarWidth }">
      <!-- 顶部导航 -->
      <header class="navbar">
        <Navbar />
      </header>

      <!-- 页面内容 -->
      <main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <keep-alive>
              <component :is="Component" />
            </keep-alive>
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.layout {
  width: 100%;
  height: 100vh;
  display: flex;
}

.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  height: 100%;
  background-color: #001529;
  transition: width 0.3s;
  z-index: 1001;
  overflow: hidden;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  transition: margin-left 0.3s;
}

.navbar {
  height: 60px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.main-content {
  flex: 1;
  padding: 20px;
  background-color: #f0f2f5;
  overflow: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
