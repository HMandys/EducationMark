<template>
  <div class="marking-workspace">
    <!-- 任务选择 -->
    <el-card class="task-card" shadow="never" v-if="!selectedTask">
      <template #header>
        <span>我的阅卷任务</span>
      </template>
      <el-empty v-if="myTasks.length === 0" description="暂无阅卷任务" />
      <div class="task-list" v-else>
        <div
          class="task-item"
          v-for="item in myTasks"
          :key="item.id"
          @click="selectTask(item)"
        >
          <div class="task-info">
            <h3>{{ item.examName }} - {{ item.subjectName }}</h3>
            <p>题号：{{ item.questionNo }} | 角色：{{ getRoleName(item.markingRole) }}</p>
          </div>
          <div class="task-progress">
            <el-progress
              :percentage="item.assignCount > 0 ? Math.round(item.completedCount / item.assignCount * 100) : 0"
              :stroke-width="10"
              :status="item.completedCount === item.assignCount ? 'success' : undefined"
            />
            <span>{{ item.completedCount }} / {{ item.assignCount }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 阅卷工作区 -->
    <template v-if="selectedTask">
      <el-page-header @back="goBack" class="page-header">
        <template #content>
          <span>{{ selectedTask.examName }} - {{ selectedTask.subjectName }} - {{ selectedTask.questionNo }}</span>
        </template>
        <template #extra>
          <el-tag>{{ getRoleName(selectedTask.markingRole) }}</el-tag>
          <el-tag type="success" class="ml-2">已完成：{{ selectedTask.completedCount }} / {{ selectedTask.assignCount }}</el-tag>
        </template>
      </el-page-header>

      <div class="workspace-content">
        <!-- 答题卡显示区 -->
        <el-card class="answer-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>答题卡</span>
              <div v-if="currentRecord">
                <el-tag size="small">学生：{{ currentRecord.studentName }}</el-tag>
                <el-tag size="small" type="info" class="ml-2">学号：{{ currentRecord.studentNumber }}</el-tag>
              </div>
            </div>
          </template>
          <div class="answer-image" v-if="currentRecord?.answerImageUrl">
            <el-image
              :src="currentRecord.answerImageUrl"
              fit="contain"
              style="width: 100%; max-height: 600px"
              :preview-src-list="[currentRecord.answerImageUrl]"
            />
          </div>
          <el-empty v-else description="暂无答题卡图片" />
        </el-card>

        <!-- 评分面板 -->
        <el-card class="scoring-card" shadow="never">
          <template #header>
            <span>评分</span>
          </template>
          <div v-if="currentRecord" class="scoring-form">
            <el-form label-width="80px">
              <el-form-item label="满分">
                <span class="full-score">{{ currentRecord.fullScore }} 分</span>
              </el-form-item>
              <el-form-item label="得分">
                <el-input-number
                  v-model="scoreForm.score"
                  :min="0"
                  :max="currentRecord.fullScore"
                  :precision="0"
                  size="large"
                  style="width: 150px"
                />
              </el-form-item>
              <el-form-item label="快捷评分">
                <el-button-group>
                  <el-button @click="scoreForm.score = 0">0分</el-button>
                  <el-button @click="scoreForm.score = Math.floor(currentRecord.fullScore / 2)">
                    {{ Math.floor(currentRecord.fullScore / 2) }}分
                  </el-button>
                  <el-button @click="scoreForm.score = currentRecord.fullScore">满分</el-button>
                </el-button-group>
              </el-form-item>
              <el-form-item label="评语">
                <el-input
                  v-model="scoreForm.comment"
                  type="textarea"
                  :rows="3"
                  placeholder="可选填写评语"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" size="large" @click="submitScore" :loading="submitting">
                  提交并下一份
                </el-button>
                <el-button size="large" @click="skipToNext">
                  跳过
                </el-button>
              </el-form-item>
            </el-form>
          </div>
          <el-empty v-else description="暂无待阅记录">
            <el-button type="primary" @click="loadNextRecord">刷新</el-button>
          </el-empty>
        </el-card>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getMyAssigns,
  getNextPendingRecord,
  submitMarkingScore,
  type MarkingTaskAssignVO,
  type MarkingRecordVO
} from '@/api/marking'

const myTasks = ref<MarkingTaskAssignVO[]>([])
const selectedTask = ref<MarkingTaskAssignVO | null>(null)
const currentRecord = ref<MarkingRecordVO | null>(null)
const submitting = ref(false)

const scoreForm = reactive({
  score: 0,
  comment: ''
})

onMounted(() => {
  loadMyTasks()
})

async function loadMyTasks() {
  try {
    const res = await getMyAssigns()
    myTasks.value = res.data || []
  } catch (e: any) {
    ElMessage.error(e.message || '加载失败')
  }
}

function selectTask(task: MarkingTaskAssignVO) {
  selectedTask.value = task
  loadNextRecord()
}

function goBack() {
  selectedTask.value = null
  currentRecord.value = null
  loadMyTasks()
}

async function loadNextRecord() {
  if (!selectedTask.value) return

  try {
    const res = await getNextPendingRecord(selectedTask.value.taskId)
    currentRecord.value = res.data
    if (currentRecord.value) {
      scoreForm.score = 0
      scoreForm.comment = ''
    }
  } catch (e: any) {
    currentRecord.value = null
  }
}

async function submitScore() {
  if (!currentRecord.value) return

  submitting.value = true
  try {
    await submitMarkingScore({
      recordId: currentRecord.value.id,
      score: scoreForm.score,
      comment: scoreForm.comment
    })
    ElMessage.success('提交成功')

    // 更新本地计数
    if (selectedTask.value) {
      selectedTask.value.completedCount++
    }

    // 加载下一条
    await loadNextRecord()
  } catch (e: any) {
    ElMessage.error(e.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

function skipToNext() {
  loadNextRecord()
}

function getRoleName(role: number) {
  switch (role) {
    case 1: return '一评'
    case 2: return '二评'
    case 3: return '仲裁'
    default: return '未知'
  }
}
</script>

<style scoped>
.marking-workspace {
  padding: 20px;
}

.task-card {
  max-width: 800px;
  margin: 0 auto;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.task-item:hover {
  border-color: #409eff;
  background: #ecf5ff;
}

.task-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
}

.task-info p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.task-progress {
  text-align: center;
  width: 150px;
}

.task-progress span {
  font-size: 12px;
  color: #909399;
}

.page-header {
  margin-bottom: 20px;
}

.workspace-content {
  display: flex;
  gap: 20px;
}

.answer-card {
  flex: 2;
}

.scoring-card {
  flex: 1;
  min-width: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.answer-image {
  text-align: center;
}

.full-score {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.scoring-form {
  padding: 10px;
}

.ml-2 {
  margin-left: 8px;
}
</style>
