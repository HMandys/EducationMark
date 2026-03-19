<template>
  <div class="editor-container">
    <!-- 顶部工具栏 -->
    <div class="editor-header">
      <div class="header-left">
        <el-button @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>返回
        </el-button>
        <el-input
          v-model="templateForm.name"
          placeholder="输入模板名称"
          style="width: 300px; margin-left: 16px;"
        />
      </div>
      <div class="header-right">
        <el-button @click="handleSave" :loading="saveLoading">
          <el-icon><DocumentChecked /></el-icon>保存
        </el-button>
        <el-button type="primary" @click="handlePublish" :loading="publishLoading">
          <el-icon><Upload /></el-icon>发布
        </el-button>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="editor-body">
      <!-- 左侧配置面板 -->
      <div class="editor-sidebar">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="页面设置" name="page">
            <PageSettingPanel v-model="templateForm" />
          </el-tab-pane>
          <el-tab-pane label="学生信息区" name="student">
            <StudentInfoPanel v-model="templateForm.studentInfoConfig" />
          </el-tab-pane>
          <el-tab-pane label="答题区域" name="regions">
            <RegionList
              v-model="templateForm.regions"
              :selected-index="selectedRegionIndex"
              @add="handleAddRegion"
              @edit="handleEditRegion"
              @delete="handleDeleteRegion"
              @select="handleSelectRegion"
            />
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 右侧预览面板 -->
      <div class="editor-preview">
        <PreviewPanel
          :template="templateForm"
          :editable="true"
          :selected-region-index="selectedRegionIndex"
          @select-region="handleSelectRegion"
          @update-region="handlePreviewRegionUpdate"
        />
      </div>
    </div>

    <!-- 区域配置弹窗 -->
    <RegionConfigDialog
      v-model:visible="regionDialogVisible"
      :region="currentRegion"
      @confirm="handleRegionConfirm"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, DocumentChecked, Upload } from '@element-plus/icons-vue'
import PageSettingPanel from './components/PageSettingPanel.vue'
import StudentInfoPanel from './components/StudentInfoPanel.vue'
import RegionList from './components/RegionList.vue'
import RegionConfigDialog from './components/RegionConfigDialog.vue'
import PreviewPanel from './components/PreviewPanel.vue'
import {
  getTemplateDetail,
  createTemplate,
  updateTemplate,
  publishTemplate,
  validateTemplate,
  type AnswerSheetTemplate,
  type AnswerSheetRegion,
} from '@/api/answerSheetTemplate'

const route = useRoute()
const router = useRouter()

const activeTab = ref('page')
const saveLoading = ref(false)
const publishLoading = ref(false)

// 模板表单数据
const templateForm = reactive<Partial<AnswerSheetTemplate>>({
  id: undefined,
  paperId: undefined,
  name: '答题卡模板',
  pageSize: 'A4',
  orientation: 1,
  columns: 1,
  marginTop: 20,
  marginBottom: 20,
  marginLeft: 15,
  marginRight: 15,
  headerConfig: {
    title: '',
    showTitle: true,
  },
  studentInfoConfig: {
    showStudentId: true,
    showName: true,
    showClass: true,
  },
  regions: [],
})

// 区域编辑
const regionDialogVisible = ref(false)
const currentRegion = ref<AnswerSheetRegion | null>(null)
const editingRegionIndex = ref(-1)
const selectedRegionIndex = ref(-1)

const cloneRegion = (region: AnswerSheetRegion): AnswerSheetRegion => ({
  ...region,
  questionIds: region.questionIds ? [...region.questionIds] : undefined,
  config: {
    ...(region.config || {}),
  },
})

// 获取模板详情
const fetchTemplateDetail = async (id: number) => {
  const res = await getTemplateDetail(id)
  Object.assign(templateForm, res.data)
}

// 返回
const handleBack = () => {
  router.push('/answer-sheet-design/list')
}

// 保存
const handleSave = async () => {
  if (!templateForm.name) {
    ElMessage.warning('请输入模板名称')
    return
  }
  if (!templateForm.paperId) {
    ElMessage.warning('请关联试卷')
    return
  }

  saveLoading.value = true
  try {
    if (templateForm.id) {
      await updateTemplate(templateForm)
      ElMessage.success('保存成功')
    } else {
      const res = await createTemplate(templateForm)
      templateForm.id = res.data
      // 更新URL
      router.replace(`/answer-sheet-design/edit/${res.data}`)
      ElMessage.success('创建成功')
    }
  } finally {
    saveLoading.value = false
  }
}

// 发布
const handlePublish = async () => {
  if (!templateForm.id) {
    ElMessage.warning('请先保存模板')
    return
  }
  if (!templateForm.regions || templateForm.regions.length === 0) {
    ElMessage.warning('请先添加答题区域')
    return
  }

  const validation = await validateTemplate(templateForm.id)
  if (!validation.data.passed) {
    await ElMessageBox.alert(
      validation.data.issues.map((item, index) => `${index + 1}. ${item.message}`).join('<br>'),
      '模板校验未通过',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '我知道了',
      },
    )
    return
  }

  await ElMessageBox.confirm('确定要发布模板吗？发布后将生成PDF文件。', '提示', {
    type: 'warning',
  })

  publishLoading.value = true
  try {
    // 先保存
    await updateTemplate(templateForm)
    // 再发布
    await publishTemplate(templateForm.id)
    ElMessage.success('发布成功')
    router.push('/answer-sheet-design/list')
  } finally {
    publishLoading.value = false
  }
}

// 添加区域
const handleAddRegion = () => {
  currentRegion.value = {
    regionType: 1,
    regionName: '选择题',
    pageNo: 1,
    sortOrder: templateForm.regions?.length || 0,
    questionStart: 1,
    questionEnd: 10,
    config: {
      boxX: 8,
      boxY: 22,
      boxWidth: 84,
      boxHeight: 20,
      regionRole: 'choice_block',
      anchorType: 'none',
      cropMode: 'range-question',
      optionCount: 4,
      questionsPerRow: 5,
    },
  }
  editingRegionIndex.value = -1
  regionDialogVisible.value = true
}

// 编辑区域
const handleEditRegion = (region: AnswerSheetRegion, index: number) => {
  currentRegion.value = cloneRegion(region)
  editingRegionIndex.value = index
  selectedRegionIndex.value = index
  regionDialogVisible.value = true
}

// 删除区域
const handleDeleteRegion = async (index: number) => {
  await ElMessageBox.confirm('确定要删除该区域吗？', '提示', {
    type: 'warning',
  })
  templateForm.regions?.splice(index, 1)
  if (selectedRegionIndex.value === index) {
    selectedRegionIndex.value = -1
  } else if (selectedRegionIndex.value > index) {
    selectedRegionIndex.value -= 1
  }
}

// 区域配置确认
const handleRegionConfirm = (region: AnswerSheetRegion) => {
  if (editingRegionIndex.value >= 0) {
    // 编辑
    templateForm.regions![editingRegionIndex.value] = cloneRegion(region)
    selectedRegionIndex.value = editingRegionIndex.value
  } else {
    // 新增
    if (!templateForm.regions) {
      templateForm.regions = []
    }
    templateForm.regions.push(cloneRegion(region))
    selectedRegionIndex.value = templateForm.regions.length - 1
  }
  regionDialogVisible.value = false
}

const handleSelectRegion = (index: number) => {
  selectedRegionIndex.value = index
}

const handlePreviewRegionUpdate = ({ index, region }: { index: number; region: AnswerSheetRegion }) => {
  if (!templateForm.regions || !templateForm.regions[index]) {
    return
  }
  templateForm.regions.splice(index, 1, cloneRegion(region))
}

onMounted(async () => {
  const id = route.params.id as string
  const paperId = route.query.paperId as string

  if (id) {
    // 编辑模式
    await fetchTemplateDetail(Number(id))
  } else if (paperId) {
    // 新建模式，关联试卷
    templateForm.paperId = Number(paperId)
  }
})
</script>

<style scoped>
.editor-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f7fa;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.editor-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.editor-sidebar {
  width: 360px;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  overflow-y: auto;
  padding: 16px;
}

.editor-preview {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  justify-content: center;
}
</style>
