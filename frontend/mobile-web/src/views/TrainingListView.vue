<template>
  <section class="page learning-library-page">
    <div class="library-title-row">
      <div>
        <p class="eyebrow">
          COURSE LIBRARY
        </p>
        <h1 tabindex="-1">
          护工培训课程
        </h1>
        <p>浏览护理培训资料，收藏常用课程并持续完成学习计划。</p>
      </div>
      <div class="library-summary">
        <strong>{{ pagination.total }}</strong><span>门课程</span>
        <strong>{{ learningLibrary.favorites.length }}</strong><span>项收藏</span>
      </div>
    </div>

    <div class="course-toolbar">
      <div
        class="course-tabs"
        role="tablist"
        aria-label="课程范围"
      >
        <button
          v-for="tab in tabs"
          :key="tab.value"
          type="button"
          role="tab"
          :aria-selected="activeTab === tab.value"
          @click="activeTab = tab.value"
        >
          {{ tab.label }}<span v-if="tab.value === 'favorite'">{{ learningLibrary.favorites.length }}</span>
        </button>
      </div>
      <form
        class="library-search"
        role="search"
        @submit.prevent="applyFilters"
      >
        <AppIcon name="search" />
        <input
          v-model.trim="filters.keyword"
          type="search"
          placeholder="搜索课程名称"
        >
        <button
          type="submit"
          aria-label="搜索"
        >
          <AppIcon name="arrow-right" />
        </button>
      </form>
    </div>

    <div
      class="catalog-strip"
      aria-label="课程筛选"
    >
      <button
        :class="{ active: !filters.categoryId }"
        type="button"
        @click="selectCategory('')"
      >
        全部分类
      </button>
      <button
        v-for="category in categories"
        :key="category.id"
        :class="{ active: filters.categoryId === String(category.id) }"
        type="button"
        @click="selectCategory(String(category.id))"
      >
        {{ category.categoryName }}
      </button>
      <select
        v-model="filters.resourceType"
        aria-label="资源类型"
        @change="applyFilters"
      >
        <option value="">
          全部类型
        </option><option value="ARTICLE">
          文章
        </option><option value="VIDEO">
          视频
        </option><option value="PPT">
          PPT
        </option>
      </select>
    </div>

    <div
      v-if="catalogWarning"
      class="notice-banner subtle-notice"
      role="status"
    >
      <AppIcon name="info" /><span>{{ catalogWarning }}</span>
    </div>

    <div class="course-section-heading">
      <div><h2>{{ activeTabLabel }}</h2><p>{{ tabDescription }}</p></div>
      <span v-if="!loading && !error">当前显示 {{ displayedResources.length }} 项</span>
    </div>

    <div
      v-if="loading"
      class="course-grid"
      aria-label="正在加载培训课程"
    >
      <article
        v-for="index in 4"
        :key="index"
        class="course-card skeleton-card"
      >
        <span class="skeleton course-cover" /><span class="skeleton skeleton-title" /><span class="skeleton skeleton-line" />
      </article>
    </div>
    <div
      v-else-if="error"
      class="empty-card error-card"
      role="alert"
    >
      <span class="empty-icon danger-icon"><AppIcon name="alert" /></span><div><h3>课程加载失败</h3><p>{{ error }}</p></div><button
        class="secondary-button"
        type="button"
        @click="loadResources"
      >
        <AppIcon name="refresh" />重新加载
      </button>
    </div>

    <div
      v-else-if="displayedResources.length"
      class="course-grid"
    >
      <article
        v-for="resource in displayedResources"
        :key="resource.id"
        class="course-card"
      >
        <RouterLink
          class="course-cover"
          :class="`cover-${resource.resourceType?.toLowerCase() || 'article'}`"
          :style="{ backgroundImage: `linear-gradient(145deg, rgba(8, 47, 45, .86), rgba(15, 118, 110, .55)), url('${resource.coverUrl || '/assets/default-course-cover.png'}')` }"
          :to="detailLink(resource)"
        >
          <span class="cover-type"><AppIcon :name="resource.resourceType === 'VIDEO' ? 'progress' : 'book'" />{{ resourceTypeLabel(resource.resourceType) }}</span>
          <span class="cover-mark">CareNexus</span>
          <strong>{{ resource.categoryName || '护理培训' }}</strong>
        </RouterLink>
        <button
          class="favorite-button"
          :class="{ active: isFavorite(resource.id) }"
          type="button"
          :aria-label="isFavorite(resource.id) ? '移出我的课程' : '加入我的课程'"
          @click="toggleFavorite(resource.id)"
        >
          <AppIcon :name="isFavorite(resource.id) ? 'check' : 'plus'" />
        </button>
        <div class="course-card-body">
          <RouterLink :to="detailLink(resource)">
            <h3>{{ resource.title }}</h3>
          </RouterLink>
          <p>{{ resource.summary || '进入课程查看完整培训内容。' }}</p>
          <div class="course-card-meta">
            <span>{{ resource.durationSeconds ? formatDuration(resource.durationSeconds) : '自主学习' }}</span><span>{{ formatDate(resource.publishedAt) }}</span>
          </div>
          <div class="course-card-actions">
            <span :class="['learning-state', { completed: isCompleted(resource.id) }]">{{ isCompleted(resource.id) ? '已完成' : isVisited(resource.id) ? '学习中' : '未开始' }}</span>
            <button
              type="button"
              @click="toggleCompleted(resource.id)"
            >
              {{ isCompleted(resource.id) ? '标记为学习中' : '标记完成' }}
            </button>
          </div>
        </div>
      </article>
    </div>
    <div
      v-else
      class="empty-card resource-empty"
    >
      <span class="empty-icon"><AppIcon :name="activeTab === 'favorite' ? 'bookmark' : 'search'" /></span><div><h3>{{ emptyTitle }}</h3><p>{{ emptyDescription }}</p></div><button
        v-if="activeTab !== 'all'"
        class="secondary-button"
        type="button"
        @click="activeTab = 'all'"
      >
        查看全部课程
      </button>
    </div>

    <nav
      v-if="activeTab === 'all' && pagination.pages > 1 && !loading"
      class="pagination"
      aria-label="培训资源分页"
    >
      <button
        type="button"
        :disabled="pagination.pageNo <= 1"
        @click="changePage(pagination.pageNo - 1)"
      >
        <AppIcon name="arrow-left" />上一页
      </button><span>第 {{ pagination.pageNo }} / {{ pagination.pages }} 页</span><button
        type="button"
        :disabled="pagination.pageNo >= pagination.pages"
        @click="changePage(pagination.pageNo + 1)"
      >
        下一页<AppIcon name="arrow-right" />
      </button>
    </nav>
  </section>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppIcon from '../components/AppIcon.vue'
import { learningLibrary, loadLearningLibrary, toggleCompleted, toggleFavorite } from '../learningStore.js'
import { getTrainingCategories, getTrainingResources } from '../api/training.js'

const route = useRoute()
const router = useRouter()
const tabs = [{ value: 'mine', label: '我的课程' }, { value: 'all', label: '全部课程' }, { value: 'completed', label: '已完成' }, { value: 'favorite', label: '我的收藏' }]
const activeTab = ref('all')
const filters = reactive({ keyword: typeof route.query.keyword === 'string' ? route.query.keyword : '', resourceType: typeof route.query.resourceType === 'string' ? route.query.resourceType : '', categoryId: typeof route.query.categoryId === 'string' ? route.query.categoryId : '' })
const categories = ref([])
const resources = ref([])
const loading = ref(false)
const error = ref('')
const catalogWarning = ref('')
const pagination = reactive({ pageNo: positiveInteger(route.query.pageNo, 1), pageSize: 8, total: 0, pages: 0 })
let activeController = null

const displayedResources = computed(() => resources.value.filter((resource) => {
  if (activeTab.value === 'mine') return isVisited(resource.id) || isFavorite(resource.id)
  if (activeTab.value === 'completed') return isCompleted(resource.id)
  if (activeTab.value === 'favorite') return isFavorite(resource.id)
  return true
}))
const activeTabLabel = computed(() => tabs.find((tab) => tab.value === activeTab.value)?.label || '全部课程')
const tabDescription = computed(() => ({ mine: '继续最近学习或已收藏的课程', all: '查看当前已发布的全部护理培训课程', completed: '回顾已经完成的培训内容', favorite: '快速访问你收藏的重点课程' })[activeTab.value])
const emptyTitle = computed(() => ({ mine: '还没有开始学习', completed: '还没有完成的课程', favorite: '还没有收藏课程' })[activeTab.value] || '没有匹配的课程')
const emptyDescription = computed(() => activeTab.value === 'all' ? '调整搜索词或课程分类后重试。' : '从全部课程中选择内容开始学习。')

function positiveInteger(value, fallback) { const parsed = Number.parseInt(value, 10); return Number.isInteger(parsed) && parsed > 0 ? parsed : fallback }
function isFavorite(id) { return learningLibrary.favorites.includes(Number(id)) }
function isVisited(id) { return learningLibrary.visited.includes(Number(id)) }
function isCompleted(id) { return learningLibrary.completed.includes(Number(id)) }
function queryFromState(pageNo = 1) { return Object.fromEntries(Object.entries({ keyword: filters.keyword, resourceType: filters.resourceType, categoryId: filters.categoryId, pageNo: pageNo > 1 ? pageNo : '' }).filter(([, value]) => value !== '')) }
function detailLink(resource) { return { name: 'training-detail', params: { resourceId: resource.id }, query: queryFromState(pagination.pageNo) } }

async function loadCatalogs() { try { categories.value = await getTrainingCategories() || [] } catch { catalogWarning.value = '课程分类暂时未加载，可继续浏览全部课程。' } }
async function loadResources() {
  activeController?.abort(); const requestController = new AbortController(); activeController = requestController; loading.value = true; error.value = ''
  try {
    const data = await getTrainingResources({ ...filters, pageNo: pagination.pageNo, pageSize: pagination.pageSize }, requestController.signal)
    resources.value = data?.records || []; pagination.pageNo = Number(data?.pageNo || pagination.pageNo); pagination.total = Number(data?.total || 0); pagination.pages = Number(data?.pages || 0)
  } catch (requestError) { if (requestError.name !== 'AbortError') { error.value = requestError.message || '培训课程加载失败。'; resources.value = [] } }
  finally { if (activeController === requestController) loading.value = false }
}
async function applyFilters() { pagination.pageNo = 1; await router.replace({ name: 'training', query: queryFromState(1) }); await loadResources() }
async function selectCategory(categoryId) { filters.categoryId = categoryId; await applyFilters() }
async function changePage(pageNo) { pagination.pageNo = pageNo; await router.replace({ name: 'training', query: queryFromState(pageNo) }); await loadResources(); window.scrollTo({ top: 0, behavior: 'smooth' }) }
function resourceTypeLabel(type) { return { ARTICLE: '文章', VIDEO: '视频', PPT: 'PPT' }[type] || '培训资料' }
function formatDuration(seconds) { return `${Math.max(1, Math.ceil(Number(seconds) / 60))} 分钟` }
function formatDate(value) { if (!value) return '近期发布'; const date = new Date(value); return Number.isNaN(date.getTime()) ? '' : new Intl.DateTimeFormat('zh-CN', { month: 'short', day: 'numeric' }).format(date) }

onMounted(() => { loadLearningLibrary(); loadCatalogs(); loadResources() })
onBeforeUnmount(() => activeController?.abort())
</script>
