<script setup lang="ts">
import { computed } from 'vue'
import { useAppStore } from '@/store/app'
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Navbar.vue'

const appStore = useAppStore()

const sidebarWidth = computed(() => (appStore.sidebarCollapsed ? '92px' : '248px'))
</script>

<template>
  <div class="layout-shell">
    <aside class="sidebar" :style="{ width: sidebarWidth }">
      <Sidebar />
    </aside>

    <div class="main-container" :style="{ marginLeft: sidebarWidth }">
      <header class="navbar">
        <Navbar />
      </header>

      <main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
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
.layout-shell {
  width: 100%;
  min-height: 100vh;
  background: var(--app-bg);
}

.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  height: 100%;
  transition: width 0.28s ease;
  z-index: 1001;
  overflow: hidden;
  box-shadow: 18px 0 40px rgba(15, 23, 42, 0.16);
}

.main-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  transition: margin-left 0.28s ease;
}

.navbar {
  position: sticky;
  top: 0;
  z-index: 1000;
  height: 76px;
  margin: 16px 20px 0;
  padding: 0 18px;
  border: 1px solid var(--app-border);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(14px);
  box-shadow: var(--app-shadow);
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow: auto;
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(6px);
}

@media (max-width: 768px) {
  .navbar {
    margin: 12px 12px 0;
  }

  .main-content {
    padding: 12px;
  }
}
</style>
