<template>
  <section class="page detail-page">
    <RouterLink
      class="back-link"
      :to="trainingBackLink"
    >
      <AppIcon name="arrow-left" />
      返回培训资源
    </RouterLink>

    <div
      v-if="loading"
      class="detail-loading"
      aria-label="正在加载资源详情"
    >
      <span class="skeleton skeleton-label" />
      <span class="skeleton skeleton-heading" />
      <span class="skeleton skeleton-line" />
      <span class="skeleton skeleton-line short" />
      <div class="skeleton detail-surface" />
    </div>

    <div
      v-else-if="error"
      class="empty-card error-card"
      role="alert"
    >
      <span class="empty-icon danger-icon"><AppIcon name="alert" /></span>
      <div>
        <h1 tabindex="-1">
          资源详情加载失败
        </h1>
        <p>{{ error }}</p>
      </div>
      <button
        class="secondary-button"
        type="button"
        @click="loadResource"
      >
        <AppIcon name="refresh" />重新加载
      </button>
    </div>

    <template v-else-if="resource">
      <article class="resource-detail">
        <div class="resource-card-topline">
          <span class="resource-type">{{ resourceTypeLabel }}</span>
          <span
            v-if="resource.durationSeconds"
            class="resource-duration"
          >
            <AppIcon name="clock" />{{ formatDuration(resource.durationSeconds) }}
          </span>
        </div>
        <h1 tabindex="-1">
          {{ resource.title }}
        </h1>
        <p class="detail-summary">
          {{ resource.summary || '该资源暂未填写摘要。' }}
        </p>

        <div class="detail-meta">
          <span>{{ resource.category?.categoryName || '未分类' }}</span>
          <span v-if="resource.publishedAt">{{ formatDateTime(resource.publishedAt) }}发布</span>
        </div>
        <ul
          v-if="resource.tags?.length"
          class="tag-list"
          aria-label="资源标签"
        >
          <li
            v-for="tag in resource.tags"
            :key="tag.id"
          >
            {{ tag.tagName }}
          </li>
        </ul>
      </article>

      <article
        class="content-card"
        aria-labelledby="content-title"
      >
        <div class="section-heading inline-heading">
          <div>
            <p class="section-kicker">
              RESOURCE CONTENT
            </p>
            <h2 id="content-title">
              学习内容
            </h2>
          </div>
          <span class="storage-badge">{{ storageModeLabel }}</span>
        </div>

        <div
          v-if="resource.storageMode === 'TEXT'"
          class="article-content"
        >
          {{ resource.content || '该文本资源暂未填写正文。' }}
        </div>

        <div
          v-else-if="resource.storageMode === 'EXTERNAL_LINK'"
          class="external-resource"
        >
          <span class="empty-icon"><AppIcon name="external" /></span>
          <div>
            <h3>外部学习资料</h3>
            <p>将在新窗口打开资源提供方页面，请留意外部站点的隐私提示。</p>
          </div>
          <a
            v-if="safeExternalUrl"
            class="primary-button"
            :href="safeExternalUrl"
            target="_blank"
            rel="noopener noreferrer"
          >
            打开学习资料<AppIcon name="external" />
          </a>
          <p
            v-else
            class="form-error"
            role="alert"
          >
            当前外部链接无效，暂时无法打开。
          </p>
        </div>

        <div
          v-else
          class="empty-card inline-empty"
        >
          <span class="empty-icon"><AppIcon name="file" /></span>
          <div>
            <h3>附件资料</h3>
            <p>该资料需要下载或使用外部链接查看，请根据页面提供的资源方式继续学习。</p>
          </div>
        </div>
      </article>

      <article
        class="learning-timer-card"
        aria-labelledby="timer-title"
      >
        <div class="timer-heading">
          <span class="timer-icon"><AppIcon name="clock" /></span>
          <div>
            <p class="section-kicker">
              LEARNING SESSION
            </p>
            <h2 id="timer-title">
              记录本次学习
            </h2>
          </div>
        </div>
        <p>页面可见时会累计本次时长，确认后才会提交到真实学习记录。</p>
        <div
          class="timer-value"
          aria-live="polite"
        >
          <strong>{{ formatTimer(elapsedSeconds) }}</strong>
          <span>本次未保存时长</span>
        </div>
        <button
          class="primary-button save-learning-button"
          type="button"
          :disabled="elapsedSeconds < 1 || reporting"
          @click="saveLearningTime"
        >
          <span
            v-if="reporting"
            class="button-spinner"
            aria-hidden="true"
          />
          <AppIcon
            v-else
            name="check"
          />
          {{ reporting ? '正在保存…' : '保存本次学习时长' }}
        </button>
        <p
          v-if="learningMessage"
          class="inline-feedback"
          :class="{ error: learningFailed }"
          role="status"
        >
          {{ learningMessage }}
        </p>
        <RouterLink
          class="text-link"
          to="/learning"
        >
          查看完整学习进度<AppIcon name="arrow-right" />
        </RouterLink>
      </article>
    </template>
  </section>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import AppIcon from '../components/AppIcon.vue'
import { getTrainingResource, reportLearningAccess } from '../api/training.js'

const props = defineProps({
  resourceId: { type: String, required: true }
})
const route = useRoute()
const resource = ref(null)
const loading = ref(false)
const error = ref('')
const elapsedSeconds = ref(0)
const reporting = ref(false)
const learningMessage = ref('')
const learningFailed = ref(false)
let timer = null
let controller = null

const trainingBackLink = computed(() => ({ name: 'training', query: route.query }))
const resourceTypeLabel = computed(() =>
  ({ ARTICLE: '文章', VIDEO: '视频', PPT: 'PPT' })[resource.value?.resourceType] || '培训资料'
)
const storageModeLabel = computed(() =>
  ({ TEXT: '文本', EXTERNAL_LINK: '外部链接', LOCAL_FILE: '本地文件' })[resource.value?.storageMode] || '资源'
)
const safeExternalUrl = computed(() => {
  if (!resource.value?.externalUrl) return ''
  try {
    const url = new URL(resource.value.externalUrl)
    return ['http:', 'https:'].includes(url.protocol) ? url.toString() : ''
  } catch {
    return ''
  }
})

async function loadResource() {
  controller?.abort()
  const requestController = new AbortController()
  controller = requestController
  loading.value = true
  error.value = ''
  try {
    resource.value = await getTrainingResource(props.resourceId, requestController.signal)
  } catch (requestError) {
    if (requestError.name !== 'AbortError') {
      error.value = requestError.message || '资源详情加载失败。'
    }
  } finally {
    if (controller === requestController) {
      loading.value = false
    }
  }
}

async function saveLearningTime() {
  if (elapsedSeconds.value < 1 || reporting.value) return
  const secondsToSave = Math.min(elapsedSeconds.value, 86400)
  reporting.value = true
  learningMessage.value = ''
  learningFailed.value = false
  try {
    const result = await reportLearningAccess(Number(props.resourceId), secondsToSave)
    elapsedSeconds.value = Math.max(0, elapsedSeconds.value - secondsToSave)
    learningMessage.value = `已保存 ${formatTimer(result.accessSeconds)}，累计学习 ${formatDuration(result.totalLearningSeconds)}。`
  } catch (requestError) {
    learningFailed.value = true
    learningMessage.value = requestError.message || '学习时长保存失败，请重试。'
  } finally {
    reporting.value = false
  }
}

function formatTimer(seconds) {
  const value = Math.max(0, Number(seconds) || 0)
  const hours = Math.floor(value / 3600)
  const minutes = Math.floor((value % 3600) / 60)
  const remainingSeconds = value % 60
  return [hours, minutes, remainingSeconds].map((part) => String(part).padStart(2, '0')).join(':')
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
  if (Number.isNaN(date.getTime())) return ''
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  }).format(date)
}

onMounted(() => {
  loadResource()
  timer = window.setInterval(() => {
    if (resource.value && document.visibilityState === 'visible') {
      elapsedSeconds.value += 1
    }
  }, 1000)
})

onBeforeUnmount(() => {
  controller?.abort()
  window.clearInterval(timer)
})
</script>
