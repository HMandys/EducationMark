<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="emit('update:visible', $event)"
    :title="region?.id ? '编辑区域' : '添加区域'"
    width="600px"
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
    >
      <el-form-item label="区域类型" prop="regionType">
        <el-select v-model="formData.regionType" @change="handleTypeChange">
          <el-option label="选择题" :value="1" />
          <el-option label="填空题" :value="2" />
          <el-option label="解答题" :value="3" />
          <el-option label="作文题" :value="4" />
        </el-select>
      </el-form-item>

      <el-form-item label="区域名称" prop="regionName">
        <el-input v-model="formData.regionName" placeholder="请输入区域名称" />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="起始题号" prop="questionStart">
            <el-input-number v-model="formData.questionStart" :min="1" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="结束题号" prop="questionEnd">
            <el-input-number v-model="formData.questionEnd" :min="formData.questionStart || 1" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="页码">
        <el-input-number v-model="formData.pageNo" :min="1" />
      </el-form-item>

      <el-divider content-position="left">区域配置</el-divider>

      <!-- 选择题配置 -->
      <template v-if="formData.regionType === 1">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选项数">
              <el-input-number v-model="formData.config!.optionCount" :min="2" :max="10" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每行题数">
              <el-input-number v-model="formData.config!.questionsPerRow" :min="1" :max="10" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="涂卡样式">
          <el-radio-group v-model="formData.config!.bubbleStyle">
            <el-radio value="circle">圆形</el-radio>
            <el-radio value="square">方形</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="包含多选">
          <el-switch v-model="formData.config!.hasMultipleChoice" />
        </el-form-item>
      </template>

      <!-- 填空题配置 -->
      <template v-if="formData.regionType === 2">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="行高(mm)">
              <el-input-number v-model="formData.config!.lineHeight" :min="20" :max="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每题行数">
              <el-input-number v-model="formData.config!.linesPerQuestion" :min="1" :max="5" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="线条样式">
          <el-radio-group v-model="formData.config!.lineStyle">
            <el-radio value="underline">下划线</el-radio>
            <el-radio value="box">方框</el-radio>
          </el-radio-group>
        </el-form-item>
      </template>

      <!-- 解答题配置 -->
      <template v-if="formData.regionType === 3">
        <el-form-item label="区域高度(mm)">
          <el-input-number v-model="formData.config!.height" :min="50" :max="500" :step="10" />
        </el-form-item>
        <el-form-item label="显示边框">
          <el-switch v-model="formData.config!.showBorder" />
        </el-form-item>
        <el-form-item label="评分框位置">
          <el-select v-model="formData.config!.scoreBoxPosition">
            <el-option label="右上角" value="top-right" />
            <el-option label="左上角" value="top-left" />
            <el-option label="右下角" value="bottom-right" />
            <el-option label="左下角" value="bottom-left" />
          </el-select>
        </el-form-item>
      </template>

      <!-- 作文题配置 -->
      <template v-if="formData.regionType === 4">
        <el-form-item label="格子类型">
          <el-radio-group v-model="formData.config!.gridType">
            <el-radio value="square">方格</el-radio>
            <el-radio value="line">横线</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="格子大小(mm)">
              <el-input-number v-model="formData.config!.gridSize" :min="6" :max="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总字数">
              <el-input-number v-model="formData.config!.wordCount" :min="100" :max="2000" :step="100" />
            </el-form-item>
          </el-col>
        </el-row>
      </template>
    </el-form>

    <template #footer>
      <el-button @click="emit('update:visible', false)">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { AnswerSheetRegion, RegionConfig } from '@/api/answerSheetTemplate'

const props = defineProps<{
  visible: boolean
  region: AnswerSheetRegion | null
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  confirm: [region: AnswerSheetRegion]
}>()

const formRef = ref<FormInstance>()

const getDefaultConfig = (type: number): RegionConfig => {
  switch (type) {
    case 1:
      return { optionCount: 4, questionsPerRow: 5, bubbleStyle: 'circle', hasMultipleChoice: false }
    case 2:
      return { lineHeight: 30, linesPerQuestion: 1, lineStyle: 'underline' }
    case 3:
      return { height: 100, showBorder: true, scoreBoxPosition: 'top-right' }
    case 4:
      return { gridType: 'square', gridSize: 10, wordCount: 800 }
    default:
      return {}
  }
}

const regionTypeNames: Record<number, string> = {
  1: '选择题',
  2: '填空题',
  3: '解答题',
  4: '作文题',
}

const formData = reactive<AnswerSheetRegion>({
  regionType: 1,
  regionName: '选择题',
  pageNo: 1,
  sortOrder: 0,
  questionStart: 1,
  questionEnd: 10,
  config: getDefaultConfig(1),
})

const formRules: FormRules = {
  regionType: [{ required: true, message: '请选择区域类型', trigger: 'change' }],
  regionName: [{ required: true, message: '请输入区域名称', trigger: 'blur' }],
  questionStart: [{ required: true, message: '请输入起始题号', trigger: 'blur' }],
  questionEnd: [{ required: true, message: '请输入结束题号', trigger: 'blur' }],
}

watch(
  () => props.visible,
  (val) => {
    if (val && props.region) {
      Object.assign(formData, props.region)
      if (!formData.config) {
        formData.config = getDefaultConfig(formData.regionType)
      }
    }
  }
)

const handleTypeChange = (type: number) => {
  formData.regionName = regionTypeNames[type] || '未知'
  formData.config = getDefaultConfig(type)
}

const handleConfirm = async () => {
  await formRef.value?.validate()
  emit('confirm', { ...formData })
}
</script>

<style scoped>
.el-input-number {
  width: 100%;
}

.el-select {
  width: 100%;
}
</style>
