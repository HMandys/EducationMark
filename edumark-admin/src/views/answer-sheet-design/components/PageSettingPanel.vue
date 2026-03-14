<template>
  <div class="page-setting-panel">
    <el-form :model="modelValue" label-width="80px" size="default">
      <el-form-item label="纸张大小">
        <el-select v-model="modelValue.pageSize" @change="handleChange">
          <el-option label="A4 (210 x 297mm)" value="A4" />
          <el-option label="A3 (297 x 420mm)" value="A3" />
          <el-option label="B5 (176 x 250mm)" value="B5" />
        </el-select>
      </el-form-item>

      <el-form-item label="页面方向">
        <el-radio-group v-model="modelValue.orientation" @change="handleChange">
          <el-radio :value="1">纵向</el-radio>
          <el-radio :value="2">横向</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="页面列数">
        <el-radio-group v-model="modelValue.columns" @change="handleChange">
          <el-radio :value="1">单列</el-radio>
          <el-radio :value="2">双列</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-divider content-position="left">页边距</el-divider>

      <el-row :gutter="12">
        <el-col :span="12">
          <el-form-item label="上边距">
            <el-input-number
              v-model="modelValue.marginTop"
              :min="5"
              :max="50"
              @change="handleChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="下边距">
            <el-input-number
              v-model="modelValue.marginBottom"
              :min="5"
              :max="50"
              @change="handleChange"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="12">
        <el-col :span="12">
          <el-form-item label="左边距">
            <el-input-number
              v-model="modelValue.marginLeft"
              :min="5"
              :max="50"
              @change="handleChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="右边距">
            <el-input-number
              v-model="modelValue.marginRight"
              :min="5"
              :max="50"
              @change="handleChange"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-divider content-position="left">页眉设置</el-divider>

      <el-form-item label="显示标题">
        <el-switch
          v-model="modelValue.headerConfig!.showTitle"
          @change="handleChange"
        />
      </el-form-item>

      <el-form-item label="标题文字" v-if="modelValue.headerConfig?.showTitle">
        <el-input
          v-model="modelValue.headerConfig!.title"
          placeholder="请输入标题"
          @change="handleChange"
        />
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import type { AnswerSheetTemplate } from '@/api/answerSheetTemplate'

const props = defineProps<{
  modelValue: Partial<AnswerSheetTemplate>
}>()

const emit = defineEmits<{
  'update:modelValue': [value: Partial<AnswerSheetTemplate>]
}>()

const handleChange = () => {
  emit('update:modelValue', props.modelValue)
}
</script>

<style scoped>
.page-setting-panel {
  padding: 8px 0;
}

.el-form-item {
  margin-bottom: 16px;
}

.el-input-number {
  width: 100%;
}

.el-select {
  width: 100%;
}
</style>
