<template>
  <div class="marking-workspace">
    <el-card v-if="!selectedTask" class="task-card" shadow="never">
      <template #header>
        <div class="section-header">
          <span>我的阅卷任务</span>
          <el-tag effect="plain">{{ myTasks.length }} 个分配</el-tag>
        </div>
      </template>

      <el-empty v-if="myTasks.length === 0" description="暂无阅卷任务" />

      <div v-else class="task-list">
        <button
          v-for="item in myTasks"
          :key="item.id"
          type="button"
          class="task-item"
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
        </button>
      </div>
    </el-card>

    <template v-else>
      <el-page-header @back="goBack" class="page-header">
        <template #content>
          <span>{{ selectedTask.examName }} - {{ selectedTask.subjectName }} - {{ selectedTask.questionNo }}</span>
        </template>
        <template #extra>
          <div class="page-extra">
            <el-tag>{{ getRoleName(selectedTask.markingRole) }}</el-tag>
            <el-tag type="success">已完成：{{ selectedTask.completedCount }} / {{ selectedTask.assignCount }}</el-tag>
          </div>
        </template>
      </el-page-header>

      <div v-if="selectedTask.markingRole === 3" class="workspace-content arbitration-layout">
        <el-card class="answer-card" shadow="never">
          <template #header>
            <div class="section-header">
              <span>仲裁题图</span>
              <div v-if="currentArbitration">
                <el-tag size="small">学生：{{ currentArbitration.studentName }}</el-tag>
                <el-tag size="small" type="warning">分差：{{ currentArbitration.scoreDiff }}</el-tag>
              </div>
            </div>
          </template>

          <div class="answer-image" v-if="currentArbitration?.answerImageUrl">
            <el-image
              :src="currentArbitration.answerImageUrl"
              fit="contain"
              style="width: 100%; max-height: 600px"
              :preview-src-list="[currentArbitration.answerImageUrl]"
            />
          </div>
          <el-empty v-else description="暂无仲裁题图">
            <el-button type="primary" @click="loadNextArbitrationRecord">刷新</el-button>
          </el-empty>
        </el-card>

        <el-card class="scoring-card" shadow="never">
          <template #header>
            <span>仲裁裁决</span>
          </template>

          <div v-if="currentArbitration" class="scoring-form">
            <div class="arbitration-summary">
              <div class="score-pill first-score">
                <span>一评</span>
                <strong>{{ currentArbitration.firstScore }}</strong>
                <small>{{ currentArbitration.firstTeacherName || '-' }}</small>
              </div>
              <div class="score-pill second-score">
                <span>二评</span>
                <strong>{{ currentArbitration.secondScore }}</strong>
                <small>{{ currentArbitration.secondTeacherName || '-' }}</small>
              </div>
            </div>

            <el-form label-width="80px">
              <el-form-item label="仲裁分">
                <el-input-number
                  v-model="arbitrationForm.score"
                  :min="0"
                  :max="fullScoreForArbitration"
                  :precision="0"
                  size="large"
                  style="width: 160px"
                />
              </el-form-item>
              <el-form-item label="快捷评分">
                <el-button-group>
                  <el-button @click="arbitrationForm.score = currentArbitration.firstScore">取一评</el-button>
                  <el-button @click="arbitrationForm.score = currentArbitration.secondScore">取二评</el-button>
                  <el-button @click="arbitrationForm.score = middleScore">取中间值</el-button>
                </el-button-group>
              </el-form-item>
              <el-form-item label="说明">
                <el-input
                  v-model="arbitrationForm.comment"
                  type="textarea"
                  :rows="4"
                  placeholder="可填写仲裁理由"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" size="large" :loading="submitting" @click="submitCurrentArbitration">
                  提交并下一份
                </el-button>
                <el-button size="large" @click="skipToNext">跳过</el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-empty v-else description="暂无待仲裁记录">
            <el-button type="primary" @click="loadNextArbitrationRecord">刷新</el-button>
          </el-empty>
        </el-card>
      </div>

      <div v-else class="workspace-content">
        <el-card class="answer-card" shadow="never">
          <template #header>
            <div class="section-header">
              <span>答题卡</span>
              <div v-if="currentRecord">
                <el-tag size="small">学生：{{ currentRecord.studentName }}</el-tag>
                <el-tag size="small" type="info">学号：{{ currentRecord.studentNumber }}</el-tag>
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
          <el-empty v-else description="暂无答题卡图片">
            <el-button type="primary" @click="loadNextRecord">刷新</el-button>
          </el-empty>
        </el-card>

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
                <el-button type="primary" size="large" :loading="submitting" @click="submitScore">
                  提交并下一份
                </el-button>
                <el-button size="large" @click="skipToNext">跳过</el-button>
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
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getMyAssigns,
  getNextArbitration,
  getNextPendingRecord,
  submitArbitration,
  submitMarkingScore,
  type MarkingArbitrationVO,
  type MarkingRecordVO,
  type MarkingTaskAssignVO,
} from '@/api/marking'

const myTasks = ref<MarkingTaskAssignVO[]>([])
const selectedTask = ref<MarkingTaskAssignVO | null>(null)
const currentRecord = ref<MarkingRecordVO | null>(null)
const currentArbitration = ref<MarkingArbitrationVO | null>(null)
const submitting = ref(false)

const scoreForm = reactive({
  score: 0,
  comment: '',
})

const arbitrationForm = reactive({
  score: 0,
  comment: '',
})

const fullScoreForArbitration = computed(() => {
  if (!currentArbitration.value) {
    return 100
  }
  return Math.max(currentArbitration.value.firstScore || 0, currentArbitration.value.secondScore || 0, 100)
})

const middleScore = computed(() => {
  if (!currentArbitration.value) {
    return 0
  }
  return Math.round(((currentArbitration.value.firstScore || 0) + (currentArbitration.value.secondScore || 0)) / 2)
})

onMounted(() => {
  loadMyTasks()
})

async function loadMyTasks() {
  try {
    const res = await getMyAssigns()
    myTasks.value = res.data || []
  } catch (error: any) {
    ElMessage.error(error.message || '加载失败')
  }
}

async function selectTask(task: MarkingTaskAssignVO) {
  selectedTask.value = task
  currentRecord.value = null
  currentArbitration.value = null
  if (task.markingRole === 3) {
    await loadNextArbitrationRecord()
    return
  }
  await loadNextRecord()
}

function goBack() {
  selectedTask.value = null
  currentRecord.value = null
  currentArbitration.value = null
  loadMyTasks()
}

async function loadNextRecord() {
  if (!selectedTask.value) {
    return
  }

  try {
    const res = await getNextPendingRecord(selectedTask.value.taskId)
    currentRecord.value = res.data
    if (currentRecord.value) {
      scoreForm.score = 0
      scoreForm.comment = ''
    }
  } catch (_error) {
    currentRecord.value = null
  }
}

async function loadNextArbitrationRecord() {
  if (!selectedTask.value) {
    return
  }

  try {
    const res = await getNextArbitration(selectedTask.value.taskId)
    currentArbitration.value = res.data
    if (currentArbitration.value) {
      arbitrationForm.score = middleScore.value
      arbitrationForm.comment = ''
    }
  } catch (_error) {
    currentArbitration.value = null
  }
}

async function submitScore() {
  if (!currentRecord.value) {
    return
  }

  submitting.value = true
  try {
    await submitMarkingScore({
      recordId: currentRecord.value.id,
      score: scoreForm.score,
      comment: scoreForm.comment,
    })
    ElMessage.success('提交成功')
    increaseCompletedCount()
    await loadNextRecord()
  } catch (error: any) {
    ElMessage.error(error.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

async function submitCurrentArbitration() {
  if (!currentArbitration.value) {
    return
  }

  submitting.value = true
  try {
    await submitArbitration({
      arbitrationId: currentArbitration.value.id,
      score: arbitrationForm.score,
      comment: arbitrationForm.comment,
    })
    ElMessage.success('仲裁已提交')
    increaseCompletedCount()
    await loadNextArbitrationRecord()
  } catch (error: any) {
    ElMessage.error(error.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

async function skipToNext() {
  if (selectedTask.value?.markingRole === 3) {
    await loadNextArbitrationRecord()
    return
  }
  await loadNextRecord()
}

function increaseCompletedCount() {
  if (selectedTask.value) {
    selectedTask.value.completedCount += 1
  }
}

function getRoleName(role: number) {
  switch (role) {
    case 1:
      return '一评'
    case 2:
      return '二评'
    case 3:
      return '仲裁'
    default:
      return '未知'
  }
}
</script>

<style scoped>
.marking-workspace {
  padding: 20px;
}

.task-card,
.answer-card,
.scoring-card {
  border-radius: 14px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.task-item {
  width: 100%;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 18px;
  border: 1px solid #dce3ea;
  border-radius: 12px;
  background: #fff;
  cursor: pointer;
  transition: border-color 0.18s ease, box-shadow 0.18s ease, transform 0.18s ease;
}

.task-item:hover {
  border-color: #1f6feb;
  box-shadow: 0 10px 24px rgba(20, 45, 90, 0.08);
  transform: translateY(-1px);
}

.task-info h3 {
  margin: 0 0 6px;
  font-size: 16px;
  color: #18222c;
}

.task-info p {
  margin: 0;
  color: #66788a;
  font-size: 13px;
}

.task-progress {
  min-width: 180px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
  color: #66788a;
  font-size: 13px;
}

.page-header {
  margin-bottom: 16px;
}

.page-extra {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.workspace-content {
  display: grid;
  grid-template-columns: minmax(0, 1.3fr) minmax(340px, 420px);
  gap: 16px;
}

.arbitration-layout {
  grid-template-columns: minmax(0, 1.2fr) minmax(360px, 460px);
}

.answer-image {
  min-height: 620px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f7f9fb;
  border-radius: 12px;
}

.scoring-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.full-score {
  font-size: 18px;
  font-weight: 700;
  color: #18222c;
}

.arbitration-summary {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.score-pill {
  padding: 16px;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.score-pill span,
.score-pill small {
  color: #66788a;
}

.score-pill strong {
  font-size: 30px;
  line-height: 1;
}

.first-score {
  background: #eef5ff;
  border: 1px solid #cfe0ff;
  color: #205ecf;
}

.second-score {
  background: #fff5ec;
  border: 1px solid #ffd9b0;
  color: #c46a11;
}

@media (max-width: 1080px) {
  .workspace-content,
  .arbitration-layout {
    grid-template-columns: 1fr;
  }

  .task-item {
    flex-direction: column;
  }

  .task-progress {
    min-width: 0;
  }
}

@media (max-width: 768px) {
  .marking-workspace {
    padding: 14px;
  }

  .arbitration-summary {
    grid-template-columns: 1fr;
  }
}
</style>
