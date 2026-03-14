<template>
  <div class="student-info-panel">
    <el-form :model="modelValue" label-width="80px" size="default">
      <el-form-item label="显示姓名">
        <el-switch v-model="modelValue!.showName" @change="handleChange" />
      </el-form-item>

      <el-form-item label="显示学号">
        <el-switch v-model="modelValue!.showStudentId" @change="handleChange" />
      </el-form-item>

      <el-form-item label="显示班级">
        <el-switch v-model="modelValue!.showClass" @change="handleChange" />
      </el-form-item>

      <el-divider />

      <div class="preview-box">
        <div class="preview-title">预览效果</div>
        <div class="info-preview">
          <span v-if="modelValue?.showName" class="info-item">姓名：________</span>
          <span v-if="modelValue?.showStudentId" class="info-item">学号：________</span>
          <span v-if="modelValue?.showClass" class="info-item">班级：________</span>
        </div>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import type { StudentInfoConfig } from '@/api/answerSheetTemplate'

const props = defineProps<{
  modelValue?: StudentInfoConfig
}>()

const emit = defineEmits<{
  'update:modelValue': [value: StudentInfoConfig]
}>()

const handleChange = () => {
  if (props.modelValue) {
    emit('update:modelValue', props.modelValue)
  }
}
</script>

<style scoped>
.student-info-panel {
  padding: 8px 0;
}

.el-form-item {
  margin-bottom: 16px;
}

.preview-box {
  background: #f5f7fa;
  border-radius: 4px;
  padding: 16px;
}

.preview-title {
  font-size: 12px;
  color: #909399;
  margin-bottom: 12px;
}

.info-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 13px;
}

.info-item {
  color: #303133;
}
</style>
