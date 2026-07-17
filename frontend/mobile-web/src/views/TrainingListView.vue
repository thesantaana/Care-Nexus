<template>
  <section class="page learning-library-page">
    <div class="library-title-row">
      <div>
        <p class="eyebrow">COURSE LIBRARY</p>
        <h1 tabindex="-1">护工培训课程</h1>
        <p>浏览全部护理培训资料，完成课程学习与考核。</p>
      </div>
      <div class="library-summary">
        <strong>{{ pagination.total }}</strong><span>门课程</span>
      </div>
    </div>

    <div class="course-toolbar">
      <div class="course-tabs" role="tablist" aria-label="课程范围">
        <button
          v-for="tab in tabs"
          :key="tab.value"
          type="button"
          role="tab"
          :aria-selected="activeTab === tab.value"
          @click="activeTab = tab.value"
        >
          {{ tab.label }}
        </button>
      </div>
      <form class="library-search" role="search" @submit.prevent="applyFilters">
        <AppIcon name="search" />
        <input v-model.trim="keyword" type="search" placeholder="搜索课程名称">
        <button type="submit" aria-label="搜索"><AppIcon name="arrow-right" /></button>
      </form>
    </div>

    <div class="course-section-heading">
      <div>
        <h2>{{ activeTab === 'completed' ? '已完成课程' : '全部课程' }}</h2>
        <p>{{ activeTab === 'completed' ? '回顾已经完成学习的培训内容' : '查看当前已发布的全部护理培训课程' }}</p>
      </div>
      <span v-if="!loading && !error">当前显示 {{ displayedResources.length }} 项</span>
    </div>

    <div v-if="loading" class="course-grid" aria-label="正在加载培训课程">
      <article v-for="index in 3" :key="index" class="course-card skeleton-card">
        <span class="skeleton course-cover" /><span class="skeleton skeleton-title" /><span class="skeleton skeleton-line" />
      </article>
    </div>

    <div v-else-if="error" class="empty-card error-card" role="alert">
      <span class="empty-icon danger-icon"><AppIcon name="alert" /></span>
      <div><h3>课程加载失败</h3><p>{{ error }}</p></div>
      <button class="secondary-button" type="button" @click="loadResources"><AppIcon name="refresh" />重新加载</button>
    </div>

    <div v-else-if="displayedResources.length" class="course-grid">
      <article v-for="resource in displayedResources" :key="resource.id" class="course-card">
        <RouterLink
          class="course-cover"
          :class="`cover-${resource.resourceType?.toLowerCase() || 'article'}`"
          :style="coverStyle(resource)"
          :to="detailLink(resource)"
        >
          <span class="cover-type"><AppIcon name="book" />文章课程</span>
          <strong>护理培训</strong>
        </RouterLink>
        <div class="course-card-body">
          <RouterLink :to="detailLink(resource)"><h3>{{ resource.title }}</h3></RouterLink>
          <p>{{ resource.summary || '进入课程查看完整培训内容。' }}</p>
          <div class="course-card-meta">
            <span>{{ resource.durationSeconds ? formatDuration(resource.durationSeconds) : '自主学习' }}</span>
            <span>{{ formatDate(resource.publishedAt) }}</span>
          </div>
          <div class="course-card-actions">
            <span :class="['learning-state', { completed: isCompleted(resource.id) }]">
              {{ isCompleted(resource.id) ? '已完成' : isVisited(resource.id) ? '学习中' : '未开始' }}
            </span>
            <RouterLink :to="detailLink(resource)">进入课程 <AppIcon name="arrow-right" /></RouterLink>
          </div>
        </div>
      </article>
    </div>

    <div v-else class="empty-card resource-empty">
      <span class="empty-icon"><AppIcon name="book" /></span>
      <div><h3>{{ activeTab === 'completed' ? '尚无已完成课程' : '没有匹配的课程' }}</h3><p>可以返回全部课程继续学习。</p></div>
      <button v-if="activeTab === 'completed'" class="secondary-button" type="button" @click="activeTab = 'all'">查看全部课程</button>
    </div>

    <nav v-if="activeTab === 'all' && pagination.pages > 1 && !loading" class="pagination" aria-label="课程分页">
      <button type="button" :disabled="pagination.pageNo <= 1" @click="changePage(pagination.pageNo - 1)"><AppIcon name="arrow-left" />上一页</button>
      <span>第 {{ pagination.pageNo }} / {{ pagination.pages }} 页</span>
      <button type="button" :disabled="pagination.pageNo >= pagination.pages" @click="changePage(pagination.pageNo + 1)">下一页<AppIcon name="arrow-right" /></button>
    </nav>
  </section>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppIcon from '../components/AppIcon.vue'
import { learningLibrary, loadLearningLibrary } from '../learningStore.js'
import { getTrainingResources } from '../api/training.js'

const route = useRoute()
const router = useRouter()
const tabs = [{ value: 'all', label: '全部课程' }, { value: 'completed', label: '已完成' }]
const activeTab = ref('all')
const keyword = ref(typeof route.query.keyword === 'string' ? route.query.keyword : '')
const resources = ref([])
const loading = ref(false)
const error = ref('')
const pagination = reactive({ pageNo: positiveInteger(route.query.pageNo, 1), pageSize: 8, total: 0, pages: 0 })
let activeController = null

const displayedResources = computed(() => activeTab.value === 'completed'
  ? resources.value.filter((resource) => isCompleted(resource.id))
  : resources.value)

function positiveInteger(value, fallback) { const parsed = Number.parseInt(value, 10); return Number.isInteger(parsed) && parsed > 0 ? parsed : fallback }
function isVisited(id) { return learningLibrary.visited.includes(Number(id)) }
function isCompleted(id) { return learningLibrary.completed.includes(Number(id)) }
function detailLink(resource) { return { name: 'training-detail', params: { resourceId: resource.id } } }
function coverStyle(resource) {
  const cover = resource.coverUrl || '/assets/default-course-cover.png'
  return { backgroundImage: `linear-gradient(145deg, rgba(8, 47, 45, .30), rgba(15, 118, 110, .06)), url('${cover}')` }
}
function formatDuration(seconds) { return `${Math.max(1, Math.ceil(Number(seconds) / 60))} 分钟` }
function formatDate(value) { if (!value) return '近期发布'; const date = new Date(value); return Number.isNaN(date.getTime()) ? '' : new Intl.DateTimeFormat('zh-CN', { month: 'short', day: 'numeric' }).format(date) }

async function loadResources() {
  activeController?.abort()
  const controller = new AbortController()
  activeController = controller
  loading.value = true
  error.value = ''
  try {
    const data = await getTrainingResources({ keyword: keyword.value, pageNo: pagination.pageNo, pageSize: pagination.pageSize }, controller.signal)
    resources.value = data?.records || []
    pagination.pageNo = Number(data?.pageNo || pagination.pageNo)
    pagination.total = Number(data?.total || 0)
    pagination.pages = Number(data?.pages || 0)
  } catch (requestError) {
    if (requestError.name !== 'AbortError') error.value = requestError.message || '培训课程加载失败。'
  } finally {
    if (activeController === controller) loading.value = false
  }
}
async function applyFilters() { pagination.pageNo = 1; await router.replace({ name: 'training', query: keyword.value ? { keyword: keyword.value } : {} }); await loadResources() }
async function changePage(pageNo) { pagination.pageNo = pageNo; await loadResources(); window.scrollTo({ top: 0, behavior: 'smooth' }) }

onMounted(async () => { await loadLearningLibrary(); await loadResources() })
onBeforeUnmount(() => activeController?.abort())
</script>
