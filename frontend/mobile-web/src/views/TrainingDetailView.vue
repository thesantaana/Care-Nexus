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
      <CourseWorkspaceNav :resource-id="resourceId" active="chapters" />
      <article class="resource-detail">
        <div class="resource-card-topline">
          <span class="resource-type">{{ resourceTypeLabel }}</span>
          <div class="detail-actions">
            <button
              type="button"
              :class="{ active: isCompleted }"
              disabled
            >
              <AppIcon name="check" />{{ isCompleted ? '已完成' : '进度自动同步' }}
            </button>
          </div>
        </div>
        <h1 tabindex="-1">
          {{ resource.title }}
        </h1>
        <p class="detail-summary">
          {{ resource.summary || '该资源暂未填写摘要。' }}
        </p>

        <div class="detail-meta">
          <span>护理培训课程</span>
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
        class="content-card ai-training-card"
        aria-labelledby="ai-title"
      >
        <div class="section-heading inline-heading">
          <div>
            <p class="section-kicker">
              AI TRAINING
            </p><h2 id="ai-title">
              资料学习助手
            </h2>
          </div>
          <span class="storage-badge">仅依据当前资料</span>
        </div>
        <div class="ai-quick-actions">
          <button
            class="secondary-button"
            type="button"
            :disabled="aiLoading"
            @click="runAi('summary')"
          >
            总结要点
          </button>
          <button
            class="secondary-button"
            type="button"
            :disabled="aiLoading"
            @click="runAi('suggestions')"
          >
            学习建议
          </button>
        </div>
        <div class="ai-question-row">
          <textarea
            v-model="aiQuestion"
            rows="3"
            maxlength="500"
            placeholder="针对这份培训资料提问…"
          />
          <button
            class="primary-button"
            type="button"
            :disabled="aiLoading || !aiQuestion.trim()"
            @click="runAi('qa')"
          >
            {{ aiLoading ? '生成中…' : '发送问题' }}
          </button>
        </div>
        <p
          v-if="aiError"
          class="inline-feedback error"
          role="alert"
        >
          {{ aiError }}
        </p>
        <div
          v-if="aiAnswer"
          class="ai-answer"
          aria-live="polite"
        >
          <strong>{{ aiAnswerTitle }}</strong><p>{{ aiAnswer }}</p>
          <small>AI内容仅作护理培训辅助，请以已发布培训资料为准。</small>
        </div>
      </article>

      <article
        class="content-card note-editor"
        aria-labelledby="note-title"
      >
        <div class="section-heading inline-heading">
          <div>
            <p class="section-kicker">
              MY NOTES
            </p><h2 id="note-title">
              课程笔记
            </h2>
          </div>
          <span class="storage-badge">已同步到账号</span>
        </div>
        <textarea
          v-model="noteContent"
          rows="6"
          maxlength="2000"
          placeholder="记录课程重点、操作要点或需要复习的内容…"
        />
        <div class="note-editor-footer">
          <span>{{ noteContent.length }} / 2000</span><button
            class="secondary-button"
            type="button"
            @click="saveCurrentNote"
          >
            <AppIcon name="note" />保存笔记
          </button>
        </div>
        <p
          v-if="noteMessage"
          class="inline-feedback"
          role="status"
        >
          {{ noteMessage }}
        </p>
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
import CourseWorkspaceNav from '../components/CourseWorkspaceNav.vue'
import { askTrainingAi, getTrainingNote, getTrainingResource, getTrainingSuggestions, reportLearningAccess, saveTrainingNote, summarizeTrainingResource } from '../api/training.js'
import { learningLibrary, loadLearningLibrary } from '../learningStore.js'

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
const noteContent = ref('')
const noteMessage = ref('')
const aiQuestion = ref('')
const aiAnswer = ref('')
const aiAnswerTitle = ref('')
const aiError = ref('')
const aiLoading = ref(false)
let timer = null
let controller = null

const trainingBackLink = computed(() => ({ name: 'training', query: route.query }))
const isCompleted = computed(() => learningLibrary.completed.includes(Number(props.resourceId)))
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
    const serverNote = await getTrainingNote(props.resourceId, requestController.signal)
    if (serverNote) {
      noteContent.value = serverNote.content || ''
    } else {
      noteContent.value = ''
    }
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

async function saveCurrentNote() {
  try {
    await saveTrainingNote(props.resourceId, resource.value?.title || '学习笔记', noteContent.value)
    noteMessage.value = '笔记已保存到账号。'
  } catch (requestError) {
    noteMessage.value = requestError.message || '笔记保存失败，请重试。'
  }
  window.setTimeout(() => { noteMessage.value = '' }, 2200)
}

async function runAi(action) {
  if (aiLoading.value) return
  aiLoading.value = true
  aiError.value = ''
  aiAnswer.value = ''
  try {
    const handlers = {
      qa: () => askTrainingAi(props.resourceId, aiQuestion.value.trim()),
      summary: () => summarizeTrainingResource(props.resourceId),
      suggestions: () => getTrainingSuggestions(props.resourceId)
    }
    const result = await handlers[action]()
    aiAnswerTitle.value = ({ qa: '资料回答', summary: '资料要点', suggestions: '学习建议' })[action]
    aiAnswer.value = result.content
  } catch (requestError) {
    aiError.value = requestError.message || 'AI助手暂时不可用，请稍后重试。'
  } finally {
    aiLoading.value = false
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
    await loadLearningLibrary()
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

onMounted(async () => {
  await loadLearningLibrary()
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
