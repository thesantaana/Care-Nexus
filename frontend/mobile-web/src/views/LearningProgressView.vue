<template>
  <section class="page learning-page">
    <div class="page-heading progress-heading">
      <div>
        <p class="eyebrow">
          LEARNING PROGRESS
        </p>
        <h1 tabindex="-1">
          学习进度
        </h1>
        <p>数据来自当前账号的真实学习记录。</p>
      </div>
      <button
        class="secondary-button compact-button"
        type="button"
        :disabled="loading"
        @click="loadProgress"
      >
        <AppIcon name="refresh" />
        {{ loading ? '更新中' : '更新' }}
      </button>
    </div>

    <div
      v-if="loading"
      class="progress-skeleton"
      aria-label="正在加载学习进度"
    >
      <div
        v-for="index in 3"
        :key="index"
        class="metric-card skeleton-card"
        aria-hidden="true"
      >
        <span class="skeleton skeleton-label" />
        <span class="skeleton skeleton-heading" />
        <span class="skeleton skeleton-line" />
      </div>
    </div>

    <div
      v-else-if="error"
      class="empty-card error-card"
      role="alert"
    >
      <span class="empty-icon danger-icon"><AppIcon name="alert" /></span>
      <div>
        <h2>学习进度加载失败</h2>
        <p>{{ error }}</p>
      </div>
      <button
        class="secondary-button"
        type="button"
        @click="loadProgress"
      >
        <AppIcon name="refresh" />重新加载
      </button>
    </div>

    <template v-else-if="record">
      <article
        class="progress-status-card"
        :class="{ ready: record.examAllowed }"
      >
        <div class="status-card-icon">
          <AppIcon :name="record.examAllowed ? 'check' : 'progress'" />
        </div>
        <div>
          <p class="section-kicker">
            CURRENT STATUS
          </p>
          <h2>{{ statusLabel(record.trainingStatus) }}</h2>
          <p>{{ statusDescription }}</p>
        </div>
        <span class="status-pill">{{ record.trainingScope || 'MVP' }}</span>
      </article>

      <div class="metric-grid">
        <article class="metric-card">
          <span class="metric-icon"><AppIcon name="clock" /></span>
          <p>累计学习时长</p>
          <strong>{{ formatDuration(record.totalLearningSeconds) }}</strong>
          <span>目标 {{ formatDuration(record.requiredLearningSeconds) }}</span>
        </article>
        <article class="metric-card">
          <span class="metric-icon"><AppIcon name="book" /></span>
          <p>已访问资源</p>
          <strong>{{ record.visitedResourceCount }} / {{ record.requiredResourceCount }}</strong>
          <span>仅统计当前已发布资源</span>
        </article>
      </div>

      <article
        class="requirements-card"
        aria-labelledby="requirements-title"
      >
        <div class="section-heading inline-heading">
          <div>
            <p class="section-kicker">
              REQUIREMENTS
            </p>
            <h2 id="requirements-title">
              考核前置条件
            </h2>
          </div>
          <span
            class="status-pill"
            :class="{ success: record.examAllowed }"
          >
            {{ record.examAllowed ? '已满足' : '进行中' }}
          </span>
        </div>

        <div class="progress-item">
          <div class="progress-label">
            <span>学习时长</span>
            <strong>{{ Math.round(timeProgress) }}%</strong>
          </div>
          <progress
            :value="timeProgress"
            max="100"
            aria-label="学习时长完成进度"
          >
            {{ Math.round(timeProgress) }}%
          </progress>
          <small>{{ formatDuration(record.totalLearningSeconds) }} / {{ formatDuration(record.requiredLearningSeconds) }}</small>
        </div>

        <div class="progress-item">
          <div class="progress-label">
            <span>资源访问</span>
            <strong>{{ Math.round(resourceProgress) }}%</strong>
          </div>
          <progress
            :value="resourceProgress"
            max="100"
            aria-label="已发布资源访问完成进度"
          >
            {{ Math.round(resourceProgress) }}%
          </progress>
          <small>{{ record.visitedResourceCount }} / {{ record.requiredResourceCount }} 项</small>
        </div>

        <p class="last-learning">
          <AppIcon name="clock" />
          {{ record.latestLearningTime ? `最近学习：${formatDateTime(record.latestLearningTime)}` : '还没有保存过学习时长' }}
        </p>
      </article>

      <article class="empty-card exam-honesty-card">
        <span class="empty-icon"><AppIcon name="file" /></span>
        <div>
          <h2>{{ record.examAllowed ? '已满足考核条件' : '考核入口暂未开放' }}</h2>
          <p v-if="record.examAllowed">
            后端尚未提供考核列表接口，因此不会硬编码考核 ID。接口补齐后再开放自然可达的考核入口。
          </p>
          <p v-else>
            完成学习时长和全部已发布资源访问后，系统会更新资格；考核列表接口仍待后端补齐。
          </p>
        </div>
        <RouterLink
          class="secondary-button"
          to="/training"
        >
          <AppIcon name="book" />继续学习
        </RouterLink>
      </article>
    </template>
  </section>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import AppIcon from '../components/AppIcon.vue'
import { getMyLearningRecord } from '../api/training.js'

const record = ref(null)
const loading = ref(false)
const error = ref('')
let controller = null

const timeProgress = computed(() => percentage(record.value?.totalLearningSeconds, record.value?.requiredLearningSeconds))
const resourceProgress = computed(() => percentage(record.value?.visitedResourceCount, record.value?.requiredResourceCount))
const statusDescription = computed(() => {
  if (record.value?.examAllowed) return '学习时长与已发布资源访问条件均已满足。'
  if (record.value?.trainingStatus === 'NOT_STARTED') return '打开培训资源并保存学习时长后开始记录进度。'
  return '继续完成学习时长与全部已发布资源访问。'
})

function percentage(value, total) {
  const numerator = Number(value) || 0
  const denominator = Number(total) || 0
  if (denominator <= 0) return 0
  return Math.min(100, Math.max(0, (numerator / denominator) * 100))
}

async function loadProgress() {
  controller?.abort()
  const requestController = new AbortController()
  controller = requestController
  loading.value = true
  error.value = ''
  try {
    record.value = await getMyLearningRecord(requestController.signal)
  } catch (requestError) {
    if (requestError.name !== 'AbortError') {
      error.value = requestError.message || '学习进度加载失败。'
    }
  } finally {
    if (controller === requestController) {
      loading.value = false
    }
  }
}

function statusLabel(status) {
  return {
    NOT_STARTED: '尚未开始',
    LEARNING: '学习中',
    EXAM_TAKEN: '已参加考核',
    PASSED: '培训已通过',
    NOT_PASSED: '考核未通过'
  }[status] || status || '暂无状态'
}

function formatDuration(seconds) {
  const value = Math.max(0, Number(seconds) || 0)
  if (value < 60) return `${value} 秒`
  const hours = Math.floor(value / 3600)
  const minutes = Math.floor((value % 3600) / 60)
  return hours ? `${hours} 小时 ${minutes} 分钟` : `${minutes} 分钟`
}

function formatDateTime(value) {
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '时间未知'
  return new Intl.DateTimeFormat('zh-CN', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date)
}

onMounted(loadProgress)
onBeforeUnmount(() => controller?.abort())
</script>
