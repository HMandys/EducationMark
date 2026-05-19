<template>
  <div class="page-container ai-marking-page">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="考试进度" name="progress">
        <el-card class="search-card" shadow="never">
          <el-form :model="progressQueryParams" inline>
            <el-form-item label="考试名称">
              <el-input v-model="progressQueryParams.examName" placeholder="请输入考试名称" clearable />
            </el-form-item>
            <el-form-item label="科目">
              <el-input v-model="progressQueryParams.subjectName" placeholder="请输入科目名称" clearable />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="progressQueryParams.status" placeholder="请选择状态" clearable>
                <el-option label="未开始" :value="0" />
                <el-option label="进行中" :value="1" />
                <el-option label="已完成" :value="2" />
                <el-option label="异常待处理" :value="3" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleProgressSearch">
                <el-icon><Search /></el-icon>搜索
              </el-button>
              <el-button @click="handleProgressReset">
                <el-icon><Refresh /></el-icon>重置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="table-card" shadow="never">
          <template #header>
            <div class="card-header">
              <div>
                <div class="header-title">启用 AI 的考试列表</div>
                <div class="header-desc">按考试科目展示 AI 填空题批改进度，便于快速定位未开始、处理中和异常待修正的链路。</div>
              </div>
            </div>
          </template>

          <el-table v-loading="progressLoading" :data="progressTableData" row-key="examSubjectId">
            <el-table-column prop="examName" label="考试名称" min-width="220" />
            <el-table-column prop="subjectName" label="科目" width="120" />
            <el-table-column prop="templateName" label="模板" min-width="180" show-overflow-tooltip />
            <el-table-column label="AI题数" width="90" align="center">
              <template #default="{ row }">
                <el-tag effect="plain">{{ row.aiQuestionCount }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="答题卡" width="100" align="center">
              <template #default="{ row }">
                {{ row.answerSheetCount }}
              </template>
            </el-table-column>
            <el-table-column label="进度" min-width="220">
              <template #default="{ row }">
                <el-progress
                  :percentage="Number(row.progress || 0)"
                  :status="getProgressBarStatus(row)"
                  :stroke-width="12"
                />
                <div class="progress-line">
                  <span>已完成 {{ row.completedCount }} / {{ row.totalTaskCount }}</span>
                  <span v-if="row.pendingCount > 0">待处理 {{ row.pendingCount }}</span>
                  <span v-if="row.anomalyCount > 0" class="danger-text">异常 {{ row.anomalyCount }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="getProgressStatusType(row.status)">{{ row.statusName }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="updateTime" label="模板更新时间" width="170" />
            <el-table-column label="操作" width="170" fixed="right">
              <template #default="{ row }">
                <el-button
                  type="success"
                  link
                  :loading="runningSubjectId === row.examSubjectId"
                  :disabled="row.totalTaskCount === 0"
                  @click="handleRunProgress(row)"
                >
                  启动批改
                </el-button>
                <el-button type="primary" link @click="handleViewProgress(row)">查看进度</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="progressQueryParams.pageNum"
            v-model:page-size="progressQueryParams.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="progressTotal"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="fetchProgressData"
            @current-change="fetchProgressData"
          />
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="提供商配置" name="provider">
        <el-card class="search-card" shadow="never">
          <el-form :model="queryParams" inline>
            <el-form-item label="提供商">
              <el-input v-model="queryParams.providerName" placeholder="请输入提供商名称" clearable />
            </el-form-item>
            <el-form-item label="协议">
              <el-select v-model="queryParams.protocol" placeholder="请选择协议" clearable>
                <el-option
                  v-for="item in protocolOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="queryParams.enabled" placeholder="请选择状态" clearable>
                <el-option label="启用" :value="1" />
                <el-option label="停用" :value="0" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>搜索
              </el-button>
              <el-button @click="handleReset">
                <el-icon><Refresh /></el-icon>重置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="table-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>多模态模型提供商</span>
              <el-button type="primary" @click="handleAddProvider">
                <el-icon><Plus /></el-icon>新增提供商
              </el-button>
            </div>
          </template>

          <el-table v-loading="providerLoading" :data="providerTableData" row-key="id">
            <el-table-column prop="providerName" label="提供商" min-width="140" />
            <el-table-column label="协议" width="140">
              <template #default="{ row }">
                <el-tag effect="plain">{{ getProtocolLabel(row.protocol) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="baseUrl" label="Base URL" min-width="220" show-overflow-tooltip />
            <el-table-column prop="model" label="模型" min-width="160" />
            <el-table-column label="API Key" min-width="140">
              <template #default="{ row }">
                <span>{{ row.apiKeyMasked || '未配置' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="默认" width="100" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.isDefault === 1" type="success">默认</el-tag>
                <el-button v-else type="primary" link @click="handleSetDefault(row)">设为默认</el-button>
              </template>
            </el-table-column>
            <el-table-column label="启用" width="100" align="center">
              <template #default="{ row }">
                <el-switch
                  :model-value="row.enabled === 1"
                  inline-prompt
                  active-text="启"
                  inactive-text="停"
                  @change="handleToggleProvider(row, $event)"
                />
              </template>
            </el-table-column>
            <el-table-column prop="priority" label="优先级" width="90" align="center" />
            <el-table-column prop="updateTime" label="更新时间" width="170" />
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditProvider(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteProvider(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="queryParams.pageNum"
            v-model:page-size="queryParams.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="providerTotal"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="fetchProviderData"
            @current-change="fetchProviderData"
          />
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="批改策略" name="policy">
        <el-card class="policy-card" shadow="never" v-loading="policyLoading">
          <template #header>
            <div class="card-header">
              <span>AI 自动批改策略</span>
              <el-button type="primary" :loading="policySubmitting" @click="handleSavePolicy">保存策略</el-button>
            </div>
          </template>

          <div class="policy-hint">
            这里配置的是全局 AI 批改入口和默认提示词模板。题目级是否启用 AI 批改，仍由答题卡模板里的填空题区域单独控制。
          </div>

          <el-form ref="policyFormRef" :model="policyForm" label-width="140px">
            <el-form-item label="启用 AI 自动批改">
              <el-switch v-model="policyEnabled" />
            </el-form-item>
            <el-form-item label="低置信度阈值">
              <el-input-number
                v-model="policyForm.lowConfidenceThreshold"
                :min="0"
                :max="1"
                :step="0.05"
                :precision="2"
              />
              <div class="field-tip">低于该阈值的结果建议回退到异常池或人工链路。</div>
            </el-form-item>
            <el-form-item label="失败回退策略">
              <el-select v-model="policyForm.failureStrategy">
                <el-option
                  v-for="item in failureStrategyOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="默认提示词模板">
              <el-input
                v-model="policyForm.promptTemplate"
                type="textarea"
                :rows="10"
                placeholder="请输入默认提示词模板"
              />
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="批改审计" name="record">
        <el-card class="search-card" shadow="never">
          <el-form :model="recordQueryParams" inline>
            <el-form-item label="答题卡ID">
              <el-input-number v-model="recordQueryParams.answerSheetId" :min="1" :controls="false" placeholder="答题卡ID" />
            </el-form-item>
            <el-form-item label="题号">
              <el-input-number v-model="recordQueryParams.questionNo" :min="1" :controls="false" placeholder="题号" />
            </el-form-item>
            <el-form-item label="协议">
              <el-select v-model="recordQueryParams.protocol" placeholder="请选择协议" clearable>
                <el-option
                  v-for="item in protocolOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="recordQueryParams.status" placeholder="请选择状态" clearable>
                <el-option label="成功" :value="1" />
                <el-option label="失败" :value="0" />
              </el-select>
            </el-form-item>
            <el-form-item label="提供商">
              <el-input v-model="recordQueryParams.providerName" placeholder="请输入提供商名称" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleRecordSearch">
                <el-icon><Search /></el-icon>搜索
              </el-button>
              <el-button @click="handleRecordReset">
                <el-icon><Refresh /></el-icon>重置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="table-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>AI 批改审计记录</span>
            </div>
          </template>

          <el-table v-loading="recordLoading" :data="recordTableData" row-key="id">
            <el-table-column prop="answerSheetId" label="答题卡ID" width="110" />
            <el-table-column prop="questionNo" label="题号" width="80" />
            <el-table-column prop="providerName" label="提供商" min-width="120" />
            <el-table-column label="协议" width="160">
              <template #default="{ row }">
                <el-tag effect="plain">{{ getProtocolLabel(row.protocol) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="model" label="模型" min-width="150" />
            <el-table-column label="状态" width="90" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '成功' : '失败' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="suggestedScore" label="分数" width="80" align="center" />
            <el-table-column label="置信度" width="100" align="center">
              <template #default="{ row }">
                {{ row.confidence !== undefined && row.confidence !== null ? row.confidence.toFixed(2) : '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="recognizedText" label="识别文本" min-width="180" show-overflow-tooltip />
            <el-table-column prop="errorMessage" label="错误信息" min-width="180" show-overflow-tooltip />
            <el-table-column prop="createTime" label="时间" width="170" />
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleViewRecord(row)">查看详情</el-button>
                <el-button
                  v-if="row.status === 0"
                  type="warning"
                  link
                  @click="handleRecordException(row)"
                >
                  处理异常
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="recordQueryParams.pageNum"
            v-model:page-size="recordQueryParams.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="recordTotal"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="fetchRecordData"
            @current-change="fetchRecordData"
          />
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <el-drawer
      v-model="progressDrawerVisible"
      title="AI 批改进度详情"
      size="1120px"
      destroy-on-close
      class="progress-drawer"
    >
      <div v-loading="progressDetailLoading" class="progress-detail">
        <template v-if="progressSummary">
          <div class="progress-heading">
            <div>
              <div class="progress-heading__title">{{ progressSummary.examName }} / {{ progressSummary.subjectName }}</div>
              <div class="progress-heading__desc">
                模板：{{ progressSummary.templateName || '未命名模板' }}
              </div>
            </div>
            <el-tag :type="getProgressStatusType(progressSummary.status)" size="large">
              {{ progressSummary.statusName }}
            </el-tag>
          </div>

          <div class="overview-grid">
            <div class="overview-card">
              <div class="overview-label">AI题目</div>
              <div class="overview-value">{{ progressSummary.aiQuestionCount }}</div>
              <div class="overview-sub">{{ currentProgressDetail?.aiQuestionNos?.join('、') || '-' }}</div>
            </div>
            <div class="overview-card">
              <div class="overview-label">答题卡</div>
              <div class="overview-value">{{ progressSummary.answerSheetCount }}</div>
              <div class="overview-sub">已进入 AI 批改链路</div>
            </div>
            <div class="overview-card">
              <div class="overview-label">已完成</div>
              <div class="overview-value success-text">{{ progressSummary.completedCount }}</div>
              <div class="overview-sub">共 {{ progressSummary.totalTaskCount }} 个任务单元</div>
            </div>
            <div class="overview-card">
              <div class="overview-label">待处理 / 异常</div>
              <div class="overview-value warning-text">{{ progressSummary.pendingCount }} / {{ progressSummary.anomalyCount }}</div>
              <div class="overview-sub">异常需人工处理后再重跑</div>
            </div>
          </div>

          <el-card class="detail-block" shadow="never">
            <template #header>
              <div class="card-header">
                <span>题目进度</span>
              </div>
            </template>
            <el-table :data="currentProgressDetail?.questionProgressList || []" row-key="questionNo" max-height="280">
              <el-table-column prop="questionNo" label="题号" width="80" align="center" />
              <el-table-column label="进度" min-width="220">
                <template #default="{ row }">
                  <el-progress
                    :percentage="Number(row.progress || 0)"
                    :status="getProgressBarStatus(row)"
                    :stroke-width="10"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="completedCount" label="已完成" width="90" align="center" />
              <el-table-column prop="pendingCount" label="待处理" width="90" align="center" />
              <el-table-column prop="anomalyCount" label="异常" width="90" align="center">
                <template #default="{ row }">
                  <span :class="{ 'danger-text': row.anomalyCount > 0 }">{{ row.anomalyCount }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="totalCount" label="总数" width="90" align="center" />
            </el-table>
          </el-card>

          <el-card class="detail-block" shadow="never">
            <template #header>
              <div class="card-header">
                <span>答题卡进度</span>
              </div>
            </template>
            <el-table :data="currentProgressDetail?.sheetProgressList || []" row-key="answerSheetId" max-height="420">
              <el-table-column prop="studentName" label="学生" width="120">
                <template #default="{ row }">
                  {{ row.studentName || '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="studentNumber" label="学号" width="140">
                <template #default="{ row }">
                  {{ row.studentNumber || '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="className" label="班级" width="120">
                <template #default="{ row }">
                  {{ row.className || '-' }}
                </template>
              </el-table-column>
              <el-table-column label="答题卡状态" width="110" align="center">
                <template #default="{ row }">
                  <el-tag effect="plain">{{ row.answerSheetStatusName || '-' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="AI进度" min-width="220">
                <template #default="{ row }">
                  <el-progress
                    :percentage="Number(row.progress || 0)"
                    :status="getProgressBarStatus(row)"
                    :stroke-width="10"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="completedCount" label="已完成" width="80" align="center" />
              <el-table-column prop="pendingCount" label="待处理" width="80" align="center" />
              <el-table-column prop="anomalyCount" label="异常" width="80" align="center">
                <template #default="{ row }">
                  <span :class="{ 'danger-text': row.anomalyCount > 0 }">{{ row.anomalyCount }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="totalQuestionCount" label="题数" width="80" align="center" />
              <el-table-column label="处理" width="90" align="center" fixed="right">
                <template #default="{ row }">
                  <el-button
                    type="primary"
                    link
                    :disabled="row.totalQuestionCount === 0"
                    @click="handleSheetException(row)"
                  >
                    处理
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </template>

        <el-empty v-else description="暂无进度详情" />
      </div>
    </el-drawer>

    <el-drawer
      v-model="exceptionDrawerVisible"
      title="AI 异常处理"
      size="1180px"
      destroy-on-close
      class="exception-drawer"
    >
      <div v-loading="exceptionLoading" class="exception-workspace">
        <template v-if="exceptionContext">
          <div class="exception-heading">
            <div>
              <div class="exception-title">
                {{ exceptionContext.studentName || '未识别学生' }}
                <span>{{ exceptionContext.studentNumber || '-' }}</span>
              </div>
              <div class="exception-subtitle">
                {{ exceptionContext.examName || '-' }} / {{ exceptionContext.subjectName || '-' }}
                <span v-if="exceptionContext.className"> / {{ exceptionContext.className }}</span>
                <span> / 答题卡 {{ exceptionContext.answerSheetId }}</span>
              </div>
            </div>
            <div class="exception-actions">
              <el-switch
                v-model="onlyShowExceptionQuestions"
                inline-prompt
                active-text="异常"
                inactive-text="全部"
              />
              <el-button
                :loading="exceptionRerunLoading"
                @click="handleExceptionRerun"
              >
                重跑本卡 AI
              </el-button>
            </div>
          </div>

          <div class="exception-stats">
            <div class="exception-stat">
              <span>AI题目</span>
              <strong>{{ exceptionBaseQuestions.length }}</strong>
            </div>
            <div class="exception-stat">
              <span>待处理</span>
              <strong class="warning-text">{{ exceptionPendingCount }}</strong>
            </div>
            <div class="exception-stat">
              <span>已处理</span>
              <strong class="success-text">{{ exceptionResolvedCount }}</strong>
            </div>
            <div class="exception-stat">
              <span>失败记录</span>
              <strong class="danger-text">{{ exceptionFailedRecordCount }}</strong>
            </div>
          </div>

          <div class="exception-grid">
            <section class="exception-panel question-list-panel">
              <div class="exception-panel__header">
                <span>题目</span>
                <el-tag effect="plain">{{ exceptionDisplayQuestions.length }}</el-tag>
              </div>
              <el-empty v-if="exceptionDisplayQuestions.length === 0" description="暂无待处理题目" />
              <div v-else class="exception-question-list">
                <button
                  v-for="item in exceptionDisplayQuestions"
                  :key="String(item.questionId)"
                  type="button"
                  class="exception-question"
                  :class="{
                    'is-active': currentExceptionQuestion?.questionId === item.questionId,
                    'is-blocking': isBlockingExceptionQuestion(item)
                  }"
                  @click="handleSelectExceptionQuestion(item)"
                >
                  <div class="exception-question__top">
                    <span>第 {{ item.questionNo || item.questionId }} 题</span>
                    <el-tag size="small" :type="getQuestionStatusType(item)">
                      {{ item.statusName || '待处理' }}
                    </el-tag>
                  </div>
                  <div class="exception-question__meta">
                    <span>{{ item.score ?? 0 }} / {{ item.fullScore || 0 }} 分</span>
                    <span>{{ item.regionRoleName || '-' }}</span>
                  </div>
                  <div
                    v-if="getQuestionRecord(item)?.errorMessage || item.anomalyReason"
                    class="exception-question__reason"
                  >
                    {{ getQuestionRecord(item)?.errorMessage || item.anomalyReason }}
                  </div>
                </button>
              </div>
            </section>

            <section class="exception-panel preview-panel">
              <div class="exception-panel__header">
                <span>{{ currentExceptionQuestion ? `题号 ${currentExceptionQuestion.questionNo}` : '题图预览' }}</span>
                <el-tag v-if="currentExceptionRecord" effect="plain" :type="currentExceptionRecord.status === 1 ? 'success' : 'danger'">
                  {{ currentExceptionRecord.status === 1 ? 'AI成功' : 'AI失败' }}
                </el-tag>
              </div>
              <div v-loading="exceptionPreviewLoading" class="exception-preview-body">
                <el-empty
                  v-if="!exceptionPreviewUrl && !exceptionPreviewLoading"
                  :description="currentExceptionQuestion?.anomalyReason || '暂无题图'"
                />
                <el-image
                  v-else
                  :src="exceptionPreviewUrl"
                  fit="contain"
                  class="exception-preview-image"
                  :preview-src-list="exceptionPreviewUrl ? [exceptionPreviewUrl] : []"
                />
              </div>
            </section>

            <section class="exception-panel decision-panel">
              <div class="exception-panel__header">
                <span>处理</span>
                <el-tag v-if="currentExceptionQuestion" :type="getQuestionStatusType(currentExceptionQuestion)">
                  {{ currentExceptionQuestion.statusName || '待处理' }}
                </el-tag>
              </div>

              <el-empty v-if="!currentExceptionQuestion" description="请选择题目" />

              <template v-else>
                <div class="decision-block">
                  <div class="decision-label">标准答案</div>
                  <div class="decision-value">{{ currentExceptionQuestion.correctAnswer || currentExceptionRecord?.referenceAnswer || '-' }}</div>
                </div>
                <div class="decision-block">
                  <div class="decision-label">学生答案 / AI识别</div>
                  <el-input
                    v-model="exceptionReviewForm.studentAnswer"
                    type="textarea"
                    :rows="3"
                    placeholder="请输入人工确认的学生答案"
                  />
                </div>
                <div class="decision-grid">
                  <div class="decision-block">
                    <div class="decision-label">当前得分</div>
                    <el-input-number
                      v-model="exceptionReviewForm.score"
                      :min="0"
                      :max="currentExceptionQuestion.fullScore || 0"
                      :step="1"
                      :controls="false"
                    />
                    <span class="score-suffix">/ {{ currentExceptionQuestion.fullScore || 0 }}</span>
                  </div>
                  <div class="decision-block">
                    <div class="decision-label">置信度</div>
                    <div class="decision-value">{{ formatConfidence(currentExceptionRecord?.confidence) }}</div>
                  </div>
                </div>
                <div class="decision-block">
                  <div class="decision-label">失败原因</div>
                  <div class="decision-value danger-text">
                    {{ currentExceptionRecord?.errorMessage || currentExceptionQuestion.anomalyReason || '-' }}
                  </div>
                </div>
                <div class="decision-block">
                  <div class="decision-label">AI说明</div>
                  <div class="decision-value">{{ currentExceptionRecord?.judgeReason || '-' }}</div>
                </div>

                <div class="decision-actions">
                  <el-button
                    type="primary"
                    :loading="exceptionReviewLoading"
                    @click="handleSaveExceptionResult"
                  >
                    保存并完成本题
                  </el-button>
                  <el-button
                    type="success"
                    :loading="exceptionReviewLoading"
                    @click="handleExceptionStatus(SUBJECTIVE_REVIEW_STATUS_VERIFIED)"
                  >
                    仅确认题图正常
                  </el-button>
                  <el-button
                    type="danger"
                    plain
                    :loading="exceptionReviewLoading"
                    @click="handleExceptionStatus(SUBJECTIVE_REVIEW_STATUS_ANOMALY)"
                  >
                    标记异常
                  </el-button>
                  <el-button
                    :loading="exceptionReviewLoading"
                    @click="handleExceptionStatus(SUBJECTIVE_REVIEW_STATUS_PENDING)"
                  >
                    重置待处理
                  </el-button>
                  <el-button :loading="exceptionPreviewLoading" @click="reloadCurrentExceptionPreview">
                    刷新题图
                  </el-button>
                </div>
              </template>
            </section>
          </div>
        </template>

        <el-empty v-else description="暂无异常处理上下文" />
      </div>
    </el-drawer>

    <el-dialog v-model="providerDialogVisible" :title="providerDialogTitle" width="720px" destroy-on-close>
      <el-form ref="providerFormRef" :model="providerForm" :rules="providerFormRules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="提供商名称" prop="providerName">
              <el-input v-model="providerForm.providerName" placeholder="请输入提供商名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="协议" prop="protocol">
              <el-select v-model="providerForm.protocol" placeholder="请选择协议">
                <el-option
                  v-for="item in protocolOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="Base URL" prop="baseUrl">
          <el-input v-model="providerForm.baseUrl" placeholder="例如 https://api.openai.com/v1" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="模型名称" prop="model">
              <el-input v-model="providerForm.model" placeholder="请输入模型名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="API Key" prop="apiKey">
              <el-input
                v-model="providerForm.apiKey"
                type="password"
                show-password
                :placeholder="providerForm.id && providerForm.hasApiKey ? '已配置密钥，如需更新请输入新值' : '请输入 API Key'"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="默认提供商">
              <el-switch v-model="providerDefault" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="启用状态">
              <el-switch v-model="providerEnabled" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="优先级">
              <el-input-number v-model="providerForm.priority" :min="0" :max="999" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="超时时间(ms)">
              <el-input-number v-model="providerForm.timeoutMs" :min="1000" :max="300000" :step="1000" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大输出Token">
              <el-input-number v-model="providerForm.maxTokens" :min="256" :max="32768" :step="256" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input v-model="providerForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="providerDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="providerSubmitting" @click="handleSubmitProvider">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="recordDialogVisible" title="批改审计详情" width="860px" destroy-on-close>
      <el-descriptions v-if="currentRecord" :column="2" border>
        <el-descriptions-item label="答题卡ID">{{ currentRecord.answerSheetId }}</el-descriptions-item>
        <el-descriptions-item label="题号">{{ currentRecord.questionNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="提供商">{{ currentRecord.providerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="协议">{{ getProtocolLabel(currentRecord.protocol) }}</el-descriptions-item>
        <el-descriptions-item label="模型">{{ currentRecord.model || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentRecord.status === 1 ? 'success' : 'danger'">
            {{ currentRecord.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="标准答案" :span="2">{{ currentRecord.referenceAnswer || '-' }}</el-descriptions-item>
        <el-descriptions-item label="识别文本" :span="2">{{ currentRecord.recognizedText || '-' }}</el-descriptions-item>
        <el-descriptions-item label="建议得分">{{ currentRecord.suggestedScore ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="置信度">
          {{ currentRecord.confidence !== undefined && currentRecord.confidence !== null ? currentRecord.confidence.toFixed(2) : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="判分理由" :span="2">{{ currentRecord.judgeReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2">{{ currentRecord.errorMessage || '-' }}</el-descriptions-item>
        <el-descriptions-item label="时间" :span="2">{{ currentRecord.createTime || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div v-if="currentRecord?.rawResponse" class="raw-response-section">
        <div class="raw-response-title">原始响应</div>
        <el-input :model-value="currentRecord.rawResponse" type="textarea" :rows="12" readonly />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import {
  getAnswerSheetDetail,
  getAnswerSheetQuestionDetails,
  getAnswerSheetQuestionPreview,
  rerunAiMarking,
  updateAiReviewResult,
  updateSubjectiveReviewStatus,
  type AnswerSheet,
  type AnswerSheetQuestionDetail,
} from '@/api/answerSheet'
import type { Id } from '@/api/types'
import {
  createAiMarkingProvider,
  deleteAiMarkingProvider,
  getAiMarkingPolicy,
  getAiMarkingProgressDetail,
  getAiMarkingProgressPage,
  getAiMarkingRecordPage,
  getAiMarkingProviderPage,
  updateAiMarkingPolicy,
  updateAiMarkingProvider,
  runAiMarkingProgress,
  type AiMarkingExamProgress,
  type AiMarkingExamProgressDetail,
  type AiMarkingPolicy,
  type AiMarkingRecord,
  type AiMarkingProvider,
} from '@/api/aiMarking'

const SUBJECTIVE_REVIEW_STATUS_PENDING = 0
const SUBJECTIVE_REVIEW_STATUS_COMPLETED = 1
const SUBJECTIVE_REVIEW_STATUS_VERIFIED = 2
const SUBJECTIVE_REVIEW_STATUS_ANOMALY = 3

const protocolOptions = [
  { label: 'OpenAI兼容', value: 'openai-compatible' },
  { label: 'OpenAI Responses / Codex', value: 'openai-responses' },
  { label: 'Anthropic', value: 'anthropic' },
]

const failureStrategyOptions = [
  { label: '异常池', value: 'exception-pool' },
  { label: '人工复核', value: 'manual-review' },
  { label: '直接跳过', value: 'skip' },
]

const activeTab = ref('progress')
const loadedTabs = new Set<string>()
const progressLoading = ref(false)
const providerLoading = ref(false)
const providerSubmitting = ref(false)
const policyLoading = ref(false)
const policySubmitting = ref(false)
const recordLoading = ref(false)
const progressDetailLoading = ref(false)
const runningSubjectId = ref<number>()
const exceptionLoading = ref(false)
const exceptionPreviewLoading = ref(false)
const exceptionReviewLoading = ref(false)
const exceptionRerunLoading = ref(false)
const providerTableData = ref<AiMarkingProvider[]>([])
const providerTotal = ref(0)
const progressTableData = ref<AiMarkingExamProgress[]>([])
const progressTotal = ref(0)
const recordTableData = ref<AiMarkingRecord[]>([])
const recordTotal = ref(0)
const exceptionRecordData = ref<AiMarkingRecord[]>([])
const providerDialogVisible = ref(false)
const providerDialogTitle = ref('')
const providerFormRef = ref<FormInstance>()
const recordDialogVisible = ref(false)
const currentRecord = ref<AiMarkingRecord | null>(null)
const progressDrawerVisible = ref(false)
const exceptionDrawerVisible = ref(false)
const currentProgressDetail = ref<AiMarkingExamProgressDetail | null>(null)
const exceptionAnswerSheet = ref<AnswerSheet | null>(null)
const exceptionQuestions = ref<AnswerSheetQuestionDetail[]>([])
const currentExceptionQuestionId = ref<string | number>()
const exceptionPreviewUrl = ref('')
const onlyShowExceptionQuestions = ref(true)
const exceptionReviewForm = reactive({
  studentAnswer: '',
  score: 0,
})
const policyFormRef = ref<FormInstance>()

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  providerName: '',
  protocol: '',
  enabled: undefined as number | undefined,
})

const progressQueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  examName: '',
  subjectName: '',
  status: undefined as number | undefined,
})

const recordQueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  answerSheetId: undefined as number | undefined,
  questionNo: undefined as number | undefined,
  status: undefined as number | undefined,
  providerName: '',
  protocol: '',
})

const createInitialProviderForm = (): AiMarkingProvider => ({
  id: undefined,
  providerName: '',
  protocol: 'openai-compatible',
  baseUrl: '',
  apiKey: '',
  model: '',
  enabled: 1,
  isDefault: 0,
  timeoutMs: 30000,
  maxTokens: 2048,
  priority: 0,
  remark: '',
  hasApiKey: false,
})

const providerForm = reactive<AiMarkingProvider>(createInitialProviderForm())

const createInitialPolicyForm = (): AiMarkingPolicy => ({
  id: undefined,
  enabled: 0,
  lowConfidenceThreshold: 0.75,
  failureStrategy: 'exception-pool',
  promptTemplate: '',
})

const policyForm = reactive<AiMarkingPolicy>(createInitialPolicyForm())

const progressSummary = computed(() => currentProgressDetail.value?.summary || null)
const exceptionContext = computed(() => {
  const sheet = exceptionAnswerSheet.value
  if (!sheet) return null
  return {
    answerSheetId: sheet.id,
    examName: sheet.examName,
    subjectName: sheet.subjectName,
    studentName: sheet.studentName,
    studentNumber: sheet.studentNumber,
    className: sheet.className,
  }
})
const exceptionRecordMap = computed(() => {
  const map = new Map<string, AiMarkingRecord>()
  for (const record of exceptionRecordData.value) {
    if (record.questionNo === undefined || record.questionNo === null) {
      continue
    }
    const key = String(record.questionNo)
    const current = map.get(key)
    if (!current || String(record.createTime || '') > String(current.createTime || '')) {
      map.set(key, record)
    }
  }
  return map
})
const exceptionBaseQuestions = computed(() =>
  exceptionQuestions.value.filter((item) => item.isObjective !== 1)
)
const exceptionDisplayQuestions = computed(() =>
  onlyShowExceptionQuestions.value
    ? exceptionBaseQuestions.value.filter(isBlockingExceptionQuestion)
    : exceptionBaseQuestions.value
)
const currentExceptionQuestion = computed(() =>
  exceptionQuestions.value.find((item) => item.questionId === currentExceptionQuestionId.value) || null
)
const currentExceptionRecord = computed(() =>
  currentExceptionQuestion.value ? getQuestionRecord(currentExceptionQuestion.value) : undefined
)
const exceptionPendingCount = computed(() =>
  exceptionBaseQuestions.value.filter(isBlockingExceptionQuestion).length
)
const exceptionResolvedCount = computed(() =>
  exceptionBaseQuestions.value.filter((item) =>
    item.status === SUBJECTIVE_REVIEW_STATUS_COMPLETED || item.status === SUBJECTIVE_REVIEW_STATUS_VERIFIED
  ).length
)
const exceptionFailedRecordCount = computed(() =>
  exceptionRecordData.value.filter((item) => item.status === 0).length
)

const providerEnabled = computed({
  get: () => providerForm.enabled === 1,
  set: (value: boolean) => {
    providerForm.enabled = value ? 1 : 0
  },
})

const providerDefault = computed({
  get: () => providerForm.isDefault === 1,
  set: (value: boolean) => {
    providerForm.isDefault = value ? 1 : 0
    if (value) {
      providerForm.enabled = 1
    }
  },
})

const policyEnabled = computed({
  get: () => policyForm.enabled === 1,
  set: (value: boolean) => {
    policyForm.enabled = value ? 1 : 0
  },
})

const providerFormRules: FormRules = {
  providerName: [{ required: true, message: '请输入提供商名称', trigger: 'blur' }],
  protocol: [{ required: true, message: '请选择协议类型', trigger: 'change' }],
  baseUrl: [{ required: true, message: '请输入 Base URL', trigger: 'blur' }],
  model: [{ required: true, message: '请输入模型名称', trigger: 'blur' }],
  apiKey: [{
    validator: (_rule, value, callback) => {
      if (!providerForm.id && !value) {
        callback(new Error('请输入 API Key'))
        return
      }
      callback()
    },
    trigger: 'blur',
  }],
}

const getProtocolLabel = (protocol?: string) => {
  return protocolOptions.find(item => item.value === protocol)?.label || protocol || '-'
}

const getProgressStatusType = (status?: number): 'info' | 'warning' | 'success' | 'danger' => {
  if (status === 2) return 'success'
  if (status === 3) return 'danger'
  if (status === 1) return 'warning'
  return 'info'
}

const getProgressBarStatus = (row: { progress?: number; anomalyCount?: number }) => {
  if ((row.anomalyCount || 0) > 0) {
    return 'exception'
  }
  if ((row.progress || 0) >= 100) {
    return 'success'
  }
  return undefined
}

const formatConfidence = (confidence?: number) => {
  return confidence !== undefined && confidence !== null ? confidence.toFixed(2) : '-'
}

const getQuestionNoKey = (question?: AnswerSheetQuestionDetail) => {
  if (!question?.questionNo) {
    return ''
  }
  return String(question.questionNo)
}

const getQuestionRecord = (question?: AnswerSheetQuestionDetail) => {
  const key = getQuestionNoKey(question)
  return key ? exceptionRecordMap.value.get(key) : undefined
}

const isBlockingExceptionQuestion = (question: AnswerSheetQuestionDetail) => {
  const record = getQuestionRecord(question)
  if (record?.status === 0) {
    return true
  }
  if (question.status === SUBJECTIVE_REVIEW_STATUS_ANOMALY) {
    return true
  }
  if (question.status === SUBJECTIVE_REVIEW_STATUS_COMPLETED || question.status === SUBJECTIVE_REVIEW_STATUS_VERIFIED) {
    return false
  }
  return question.anomalyFlag === true
}

const getQuestionStatusType = (question: AnswerSheetQuestionDetail): 'info' | 'warning' | 'success' | 'danger' => {
  if (question.status === SUBJECTIVE_REVIEW_STATUS_COMPLETED || question.status === SUBJECTIVE_REVIEW_STATUS_VERIFIED) {
    return 'success'
  }
  if (question.status === SUBJECTIVE_REVIEW_STATUS_ANOMALY || getQuestionRecord(question)?.status === 0) {
    return 'danger'
  }
  if (question.anomalyFlag) {
    return 'warning'
  }
  return 'info'
}

const buildProviderPayload = (provider: AiMarkingProvider): AiMarkingProvider => ({
  id: provider.id,
  providerName: provider.providerName,
  protocol: provider.protocol,
  baseUrl: provider.baseUrl,
  apiKey: provider.apiKey,
  model: provider.model,
  enabled: provider.enabled,
  isDefault: provider.isDefault,
  timeoutMs: provider.timeoutMs,
  maxTokens: provider.maxTokens,
  priority: provider.priority,
  remark: provider.remark,
})

const fetchProgressData = async () => {
  progressLoading.value = true
  try {
    const res = await getAiMarkingProgressPage(progressQueryParams)
    progressTableData.value = res.data.list
    progressTotal.value = res.data.total
  } finally {
    progressLoading.value = false
  }
}

const fetchProviderData = async () => {
  providerLoading.value = true
  try {
    const res = await getAiMarkingProviderPage(queryParams)
    providerTableData.value = res.data.list
    providerTotal.value = res.data.total
  } finally {
    providerLoading.value = false
  }
}

const fetchPolicyData = async () => {
  policyLoading.value = true
  try {
    const res = await getAiMarkingPolicy()
    Object.assign(policyForm, createInitialPolicyForm(), res.data)
  } finally {
    policyLoading.value = false
  }
}

const fetchRecordData = async () => {
  recordLoading.value = true
  try {
    const res = await getAiMarkingRecordPage(recordQueryParams)
    recordTableData.value = res.data.list
    recordTotal.value = res.data.total
  } finally {
    recordLoading.value = false
  }
}

const fetchExceptionRecords = async (answerSheetId: Id) => {
  const res = await getAiMarkingRecordPage({
    pageNum: 1,
    pageSize: 200,
    answerSheetId: Number(answerSheetId),
  })
  exceptionRecordData.value = res.data.list || []
}

const reloadExceptionDetails = async (answerSheetId: Id) => {
  const [sheetRes, detailRes] = await Promise.all([
    getAnswerSheetDetail(answerSheetId),
    getAnswerSheetQuestionDetails(answerSheetId),
  ])
  exceptionAnswerSheet.value = sheetRes.data
  exceptionQuestions.value = detailRes.data || []
  await fetchExceptionRecords(answerSheetId)
}

const handleProgressSearch = () => {
  progressQueryParams.pageNum = 1
  fetchProgressData()
}

const handleProgressReset = () => {
  progressQueryParams.examName = ''
  progressQueryParams.subjectName = ''
  progressQueryParams.status = undefined
  handleProgressSearch()
}

const handleRunProgress = async (row: AiMarkingExamProgress) => {
  runningSubjectId.value = row.examSubjectId
  try {
    await runAiMarkingProgress(row.examSubjectId)
    ElMessage.success('已加入 AI 自动批改队列')
    await fetchProgressData()
  } catch (error: any) {
    ElMessage.error(error?.message || 'AI 自动批改启动失败')
  } finally {
    runningSubjectId.value = undefined
  }
}

const handleViewProgress = async (row: AiMarkingExamProgress) => {
  progressDrawerVisible.value = true
  progressDetailLoading.value = true
  currentProgressDetail.value = null
  try {
    const res = await getAiMarkingProgressDetail(row.examSubjectId)
    currentProgressDetail.value = res.data
  } catch (error: any) {
    progressDrawerVisible.value = false
    ElMessage.error(error?.message || '加载 AI 进度详情失败')
  } finally {
    progressDetailLoading.value = false
  }
}

const openExceptionDrawer = async (answerSheetId: Id, questionNo?: number) => {
  exceptionDrawerVisible.value = true
  exceptionLoading.value = true
  exceptionPreviewUrl.value = ''
  currentExceptionQuestionId.value = undefined
  try {
    await reloadExceptionDetails(answerSheetId)
    const target = findExceptionQuestion(questionNo)
    if (target) {
      await handleSelectExceptionQuestion(target)
    }
  } catch (error: any) {
    exceptionDrawerVisible.value = false
    ElMessage.error(error?.message || '加载 AI 异常处理失败')
  } finally {
    exceptionLoading.value = false
  }
}

const findExceptionQuestion = (questionNo?: number) => {
  if (questionNo !== undefined && questionNo !== null) {
    const matched = exceptionQuestions.value.find((item) => String(item.questionNo) === String(questionNo))
    if (matched) {
      return matched
    }
  }
  return exceptionDisplayQuestions.value[0] || exceptionBaseQuestions.value[0]
}

const handleSheetException = async (row: { answerSheetId: Id }) => {
  await openExceptionDrawer(row.answerSheetId)
}

const handleRecordException = async (row: AiMarkingRecord) => {
  recordDialogVisible.value = false
  await openExceptionDrawer(row.answerSheetId, row.questionNo)
}

const handleSelectExceptionQuestion = async (question: AnswerSheetQuestionDetail) => {
  currentExceptionQuestionId.value = question.questionId
  syncExceptionReviewForm(question)
  await loadExceptionPreview(question)
}

const syncExceptionReviewForm = (question?: AnswerSheetQuestionDetail) => {
  const record = getQuestionRecord(question)
  exceptionReviewForm.studentAnswer = question?.studentAnswer || record?.recognizedText || ''
  exceptionReviewForm.score = question?.score ?? record?.suggestedScore ?? 0
}

const loadExceptionPreview = async (question: AnswerSheetQuestionDetail) => {
  if (!exceptionContext.value || !question.previewAvailable) {
    exceptionPreviewUrl.value = ''
    return
  }

  exceptionPreviewLoading.value = true
  try {
    const res = await getAnswerSheetQuestionPreview(exceptionContext.value.answerSheetId, question.questionId)
    exceptionPreviewUrl.value = res.data || ''
  } catch (error: any) {
    exceptionPreviewUrl.value = ''
    ElMessage.error(error?.message || '加载题图失败')
  } finally {
    exceptionPreviewLoading.value = false
  }
}

const reloadCurrentExceptionPreview = async () => {
  if (!currentExceptionQuestion.value) {
    return
  }
  await loadExceptionPreview(currentExceptionQuestion.value)
}

const handleSaveExceptionResult = async () => {
  if (!exceptionContext.value || !currentExceptionQuestion.value) {
    return
  }
  const fullScore = currentExceptionQuestion.value.fullScore || 0
  if (exceptionReviewForm.score < 0 || exceptionReviewForm.score > fullScore) {
    ElMessage.error('得分不能超过题目满分')
    return
  }

  exceptionReviewLoading.value = true
  try {
    const res = await updateAiReviewResult(
      exceptionContext.value.answerSheetId,
      currentExceptionQuestion.value.questionId,
      {
        studentAnswer: exceptionReviewForm.studentAnswer,
        score: exceptionReviewForm.score,
      }
    )
    exceptionQuestions.value = exceptionQuestions.value.map((item) =>
      item.questionId === res.data.questionId ? res.data : item
    )
    await fetchExceptionRecords(exceptionContext.value.answerSheetId)
    ElMessage.success('AI 复核结果已保存')
    const target = findExceptionQuestion()
    if (target) {
      await handleSelectExceptionQuestion(target)
    }
    await refreshProgressAfterExceptionChange()
  } catch (error: any) {
    ElMessage.error(error?.message || '保存失败')
  } finally {
    exceptionReviewLoading.value = false
  }
}

const handleExceptionStatus = async (status: number) => {
  if (!exceptionContext.value || !currentExceptionQuestion.value) {
    return
  }
  if (status === SUBJECTIVE_REVIEW_STATUS_ANOMALY) {
    try {
      await ElMessageBox.confirm('标记异常后，该题会继续停留在异常池。', '确认标记异常', {
        type: 'warning',
        confirmButtonText: '确认',
        cancelButtonText: '取消',
      })
    } catch {
      return
    }
  }

  exceptionReviewLoading.value = true
  try {
    const res = await updateSubjectiveReviewStatus(
      exceptionContext.value.answerSheetId,
      currentExceptionQuestion.value.questionId,
      status
    )
    exceptionQuestions.value = exceptionQuestions.value.map((item) =>
      item.questionId === res.data.questionId ? res.data : item
    )
    await fetchExceptionRecords(exceptionContext.value.answerSheetId)
    if (status === SUBJECTIVE_REVIEW_STATUS_VERIFIED) {
      ElMessage.success('已确认正常')
    } else if (status === SUBJECTIVE_REVIEW_STATUS_ANOMALY) {
      ElMessage.success('已标记异常')
    } else {
      ElMessage.success('已重置为待处理')
    }
    const target = findExceptionQuestion()
    if (target) {
      await handleSelectExceptionQuestion(target)
    }
    await refreshProgressAfterExceptionChange()
  } catch (error: any) {
    ElMessage.error(error?.message || '处理失败')
  } finally {
    exceptionReviewLoading.value = false
  }
}

const handleExceptionRerun = async () => {
  if (!exceptionContext.value) {
    return
  }
  exceptionRerunLoading.value = true
  try {
    await rerunAiMarking(exceptionContext.value.answerSheetId)
    ElMessage.success('已加入 AI 自动批改队列')
    await refreshProgressAfterExceptionChange()
  } catch (error: any) {
    ElMessage.error(error?.message || 'AI 自动批改重跑失败')
  } finally {
    exceptionRerunLoading.value = false
  }
}

const refreshProgressAfterExceptionChange = async () => {
  if (activeTab.value === 'record') {
    await fetchRecordData()
  }
  await fetchProgressData()
  if (progressDrawerVisible.value && progressSummary.value?.examSubjectId) {
    const res = await getAiMarkingProgressDetail(progressSummary.value.examSubjectId)
    currentProgressDetail.value = res.data
  }
}

const handleSearch = () => {
  queryParams.pageNum = 1
  fetchProviderData()
}

const handleReset = () => {
  queryParams.providerName = ''
  queryParams.protocol = ''
  queryParams.enabled = undefined
  handleSearch()
}

const handleRecordSearch = () => {
  recordQueryParams.pageNum = 1
  fetchRecordData()
}

const handleRecordReset = () => {
  recordQueryParams.answerSheetId = undefined
  recordQueryParams.questionNo = undefined
  recordQueryParams.status = undefined
  recordQueryParams.providerName = ''
  recordQueryParams.protocol = ''
  handleRecordSearch()
}

const resetProviderForm = () => {
  Object.assign(providerForm, createInitialProviderForm())
}

const handleAddProvider = () => {
  providerDialogTitle.value = '新增提供商'
  resetProviderForm()
  providerDialogVisible.value = true
}

const handleEditProvider = (row: AiMarkingProvider) => {
  providerDialogTitle.value = '编辑提供商'
  Object.assign(providerForm, createInitialProviderForm(), row, {
    apiKey: '',
  })
  providerDialogVisible.value = true
}

const handleSubmitProvider = async () => {
  await providerFormRef.value?.validate()
  providerSubmitting.value = true
  try {
    if (providerForm.id) {
      await updateAiMarkingProvider(buildProviderPayload(providerForm))
      ElMessage.success('更新成功')
    } else {
      await createAiMarkingProvider(buildProviderPayload(providerForm))
      ElMessage.success('创建成功')
    }
    providerDialogVisible.value = false
    await fetchProviderData()
  } finally {
    providerSubmitting.value = false
  }
}

const handleDeleteProvider = async (row: AiMarkingProvider) => {
  await ElMessageBox.confirm(`确定要删除提供商【${row.providerName}】吗？`, '提示', {
    type: 'warning',
  })
  await deleteAiMarkingProvider(row.id!)
  ElMessage.success('删除成功')
  await fetchProviderData()
}

const handleToggleProvider = async (row: AiMarkingProvider, value: string | number | boolean) => {
  const enabled = value ? 1 : 0
  await updateAiMarkingProvider(buildProviderPayload({
    ...row,
    enabled,
    apiKey: '',
    isDefault: enabled === 0 ? 0 : row.isDefault,
  }))
  ElMessage.success(enabled === 1 ? '已启用' : '已停用')
  await fetchProviderData()
}

const handleSetDefault = async (row: AiMarkingProvider) => {
  await updateAiMarkingProvider(buildProviderPayload({
    ...row,
    isDefault: 1,
    enabled: 1,
    apiKey: '',
  }))
  ElMessage.success('已切换默认提供商')
  await fetchProviderData()
}

const handleSavePolicy = async () => {
  policySubmitting.value = true
  try {
    await updateAiMarkingPolicy(policyForm)
    ElMessage.success('策略已保存')
    loadedTabs.delete('policy')
    await fetchPolicyData()
    loadedTabs.add('policy')
  } finally {
    policySubmitting.value = false
  }
}

const handleViewRecord = (row: AiMarkingRecord) => {
  currentRecord.value = row
  recordDialogVisible.value = true
}

const loadTabData = async (tab: string) => {
  if (loadedTabs.has(tab)) return
  loadedTabs.add(tab)
  switch (tab) {
    case 'progress':
      await fetchProgressData()
      break
    case 'provider':
      await fetchProviderData()
      break
    case 'policy':
      await fetchPolicyData()
      break
    case 'record':
      await fetchRecordData()
      break
  }
}

watch(activeTab, (tab) => {
  loadTabData(tab)
})

onMounted(async () => {
  await loadTabData('progress')
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 16px;
}

.table-card :deep(.el-card__header),
.policy-card :deep(.el-card__header),
.detail-block :deep(.el-card__header) {
  padding: 12px 20px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
}

.header-desc {
  margin-top: 4px;
  font-size: 13px;
  color: #64748b;
}

.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}

.policy-hint {
  margin-bottom: 16px;
  padding: 12px 14px;
  font-size: 13px;
  line-height: 1.6;
  color: #475569;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
}

.field-tip {
  margin-top: 6px;
  font-size: 12px;
  color: #64748b;
}

.progress-line {
  display: flex;
  gap: 12px;
  margin-top: 6px;
  font-size: 12px;
  color: #64748b;
  flex-wrap: wrap;
}

.progress-detail,
.exception-workspace {
  padding-right: 8px;
}

.progress-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.progress-heading__title {
  font-size: 20px;
  font-weight: 600;
  color: #0f172a;
}

.progress-heading__desc {
  margin-top: 6px;
  font-size: 13px;
  color: #64748b;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 16px;
}

.overview-card {
  padding: 16px 18px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
}

.overview-label {
  font-size: 13px;
  color: #64748b;
}

.overview-value {
  margin-top: 10px;
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
  line-height: 1;
}

.overview-sub {
  margin-top: 10px;
  font-size: 12px;
  line-height: 1.5;
  color: #64748b;
  word-break: break-all;
}

.detail-block + .detail-block {
  margin-top: 16px;
}

.exception-heading {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 14px;
}

.exception-title {
  display: flex;
  align-items: baseline;
  gap: 10px;
  font-size: 20px;
  font-weight: 700;
  color: #111827;
}

.exception-title span {
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
}

.exception-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: #64748b;
}

.exception-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.exception-stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 14px;
}

.exception-stat {
  padding: 12px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: #fff;
}

.exception-stat span {
  display: block;
  font-size: 12px;
  color: #64748b;
}

.exception-stat strong {
  display: block;
  margin-top: 6px;
  font-size: 22px;
  line-height: 1;
  color: #111827;
}

.exception-grid {
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr) 320px;
  gap: 14px;
  min-height: 620px;
}

.exception-panel {
  min-width: 0;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: #fff;
  overflow: hidden;
}

.exception-panel__header {
  height: 48px;
  padding: 0 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  border-bottom: 1px solid #e2e8f0;
  font-weight: 600;
  color: #111827;
}

.question-list-panel {
  display: flex;
  flex-direction: column;
}

.exception-question-list {
  flex: 1;
  overflow: auto;
  padding: 10px;
}

.exception-question {
  width: 100%;
  margin: 0 0 8px;
  padding: 11px 12px;
  text-align: left;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  transition: border-color .15s ease, background-color .15s ease, box-shadow .15s ease;
}

.exception-question:hover,
.exception-question.is-active {
  border-color: #2563eb;
  background: #eff6ff;
}

.exception-question.is-active {
  box-shadow: 0 0 0 2px rgba(37, 99, 235, .12);
}

.exception-question.is-blocking {
  border-left: 3px solid #dc2626;
}

.exception-question__top,
.exception-question__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.exception-question__top {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
}

.exception-question__meta {
  margin-top: 8px;
  font-size: 12px;
  color: #64748b;
}

.exception-question__reason {
  margin-top: 8px;
  font-size: 12px;
  line-height: 1.5;
  color: #b91c1c;
  word-break: break-word;
}

.preview-panel {
  display: flex;
  flex-direction: column;
}

.exception-preview-body {
  flex: 1;
  min-height: 560px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
}

.exception-preview-image {
  width: 100%;
  height: 560px;
  padding: 14px;
}

.decision-panel {
  padding-bottom: 14px;
}

.decision-block {
  margin: 14px 14px 0;
  padding-bottom: 12px;
  border-bottom: 1px solid #edf2f7;
}

.decision-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0;
}

.decision-label {
  margin-bottom: 6px;
  font-size: 12px;
  color: #64748b;
}

.decision-value {
  min-height: 20px;
  font-size: 14px;
  line-height: 1.55;
  color: #111827;
  word-break: break-word;
}

.score-suffix {
  margin-left: 8px;
  font-size: 13px;
  color: #64748b;
}

.decision-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin: 16px 14px 0;
}

.raw-response-section {
  margin-top: 16px;
}

.raw-response-title {
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.success-text {
  color: #059669;
}

.warning-text {
  color: #d97706;
}

.danger-text {
  color: #dc2626;
}

@media (max-width: 1200px) {
  .overview-grid,
  .exception-stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .exception-grid {
    grid-template-columns: 260px minmax(0, 1fr);
  }

  .decision-panel {
    grid-column: 1 / -1;
  }
}

@media (max-width: 768px) {
  .overview-grid,
  .exception-stats,
  .exception-grid {
    grid-template-columns: 1fr;
  }

  .progress-heading,
  .exception-heading {
    flex-direction: column;
  }
}
</style>
