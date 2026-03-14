<script setup lang="ts">
// 角色管理页面 - 待实现
</script>

<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" icon="Plus">新增角色</el-button>
        </div>
      </template>

      <el-alert
        title="角色管理模块将在后续开发中完善"
        type="info"
        show-icon
        :closable="false"
      />
    </el-card>
  </div>
</template>

<style lang="scss" scoped>
.page-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
