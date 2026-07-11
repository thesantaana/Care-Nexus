<template>
  <section class="page training-page">
    <div class="page-heading">
      <p class="eyebrow">
        LEARNING LIBRARY
      </p>
      <h1 tabindex="-1">
        培训资源
      </h1>
      <p>浏览后端已发布的护理培训内容，筛选条件会保留在当前链接中。</p>
    </div>

    <form
      class="filter-card"
      role="search"
      @submit.prevent="applyFilters"
    >
      <label for="training-keyword">搜索培训内容</label>
      <div class="search-row">
        <div class="search-input">
          <AppIcon name="search" />
          <input
            id="training-keyword"
            v-model.trim="filters.keyword"
            type="search"
            inputmode="search"
            placeholder="输入标题关键词"
          >
        </div>
        <button
          class="primary-button search-button"
          type="submit"
          :disabled="loading"
        >
          搜索
        </button>
      </div>

      <details class="advanced-filters">
        <summary>
          <span><AppIcon name="filter" />筛选条件</span>
          <span
            v-if="activeFilterCount"
            class="filter-count"
          >{{ activeFilterCount }}</span>
        </summary>
        <div class="filter-grid">
          <div class="field-group compact-field">
            <label for="resource-type">资源类型</label>
            <select
              id="resource-type"
              v-model="filters.resourceType"
            >
              <option value="">
                全部类型
              </option>
              <option value="ARTICLE">
                文章
              </option>
              <option value="VIDEO">
                视频
              </option>
              <option value="PPT">
                PPT
              </option>
            </select>
          </div>
          <div class="field-group compact-field">
            <label for="resource-category">培训类别</label>
            <select
              id="resource-category"
              v-model="filters.categoryId"
            >
              <option value="">
                全部类别
              </option>
              <option
                v-for="category in categories"
                :key="category.id"
                :value="String(category.id)"
              >
                {{ category.categoryName }}
              </option>
            </select>
          </div>
          <div class="field-group compact-field">
            <label for="resource-tag">培训标签</label>
            <select
              id="resource-tag"
              v-model="filters.tagId"
            >
              <option value="">
                全部标签
              </option>
              <option
                v-for="tag in tags"
                :key="tag.id"
                :value="String(tag.id)"
              >
                {{ tag.tagName }}
              </option>
            </select>
          </div>
        </div>
        <div class="filter-actions">
          <button
            class="text-button"
            type="button"
            @click="resetFilters"
          >
            清除筛选
          </button>
          <button
            class="secondary-button"
            type="submit"
          >
            应用筛选
          </button>
        </div>
      </details>
    </form>

    <div
      v-if="catalogWarning"
      class="notice-banner subtle-notice"
      role="status"
    >
      <AppIcon name="info" />
      <span>{{ catalogWarning }}</span>
    </div>

    <div
      class="results-heading"
      aria-live="polite"
    >
      <div>
        <p class="section-kicker">
          PUBLISHED RESOURCES
        </p>
        <h2>已发布资源</h2>
      </div>
      <span v-if="!loading && !error">共 {{ pagination.total }} 项</span>
    </div>

    <div
      v-if="loading"
      class="resource-list"
      aria-label="正在加载培训资源"
    >
      <article
        v-for="index in 3"
        :key="index"
        class="resource-card skeleton-card"
        aria-hidden="true"
      >
        <span class="skeleton skeleton-label" />
        <span class="skeleton skeleton-title" />
        <span class="skeleton skeleton-line" />
        <span class="skeleton skeleton-line short" />
      </article>
    </div>

    <div
      v-else-if="error"
      class="empty-card error-card"
      role="alert"
    >
      <span class="empty-icon danger-icon"><AppIcon name="alert" /></span>
      <div>
        <h3>资源加载失败</h3>
        <p>{{ error }}</p>
      </div>
      <button
        class="secondary-button"
        type="button"
        @click="loadResources"
      >
        <AppIcon name="refresh" />重新加载
      </button>
    </div>

    <div
      v-else-if="resources.length"
      class="resource-list"
    >
      <RouterLink
        v-for="resource in resources"
        :key="resource.id"
        class="resource-card"
        :to="{
          name: 'training-detail',
          params: { resourceId: resource.id },
          query: queryFromState(pagination.pageNo)
        }"
      >
        <div class="resource-card-topline">
          <span class="resource-type">{{ resourceTypeLabel(resource.resourceType) }}</span>
          <span
            v-if="resource.durationSeconds"
            class="resource-duration"
          >
            <AppIcon name="clock" />{{ formatDuration(resource.durationSeconds) }}
          </span>
        </div>
        <h3>{{ resource.title }}</h3>
        <p>{{ resource.summary || '该资源暂未填写摘要，进入详情查看完整内容。' }}</p>
        <div class="resource-meta">
          <span>{{ resource.categoryName || '未分类' }}</span>
          <span v-if="resource.publishedAt">{{ formatDate(resource.publishedAt) }}发布</span>
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
        <span class="card-link-copy">开始学习 <AppIcon name="arrow-right" /></span>
      </RouterLink>
    </div>

    <div
      v-else
      class="empty-card resource-empty"
    >
      <span class="empty-icon"><AppIcon name="search" /></span>
      <div>
        <h3>没有匹配的已发布资源</h3>
        <p>可以调整关键词或筛选条件后重新查询。</p>
      </div>
      <button
        v-if="activeFilterCount"
        class="secondary-button"
        type="button"
        @click="resetFilters"
      >
        清除筛选
      </button>
    </div>

    <nav
      v-if="pagination.pages > 1 && !loading"
      class="pagination"
      aria-label="培训资源分页"
    >
      <button
        type="button"
        :disabled="pagination.pageNo <= 1"
        aria-label="上一页"
        @click="changePage(pagination.pageNo - 1)"
      >
        <AppIcon name="arrow-left" />
        上一页
      </button>
      <span>第 {{ pagination.pageNo }} / {{ pagination.pages }} 页</span>
      <button
        type="button"
        :disabled="pagination.pageNo >= pagination.pages"
        aria-label="下一页"
        @click="changePage(pagination.pageNo + 1)"
      >
        下一页
        <AppIcon name="arrow-right" />
      </button>
    </nav>
  </section>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppIcon from '../components/AppIcon.vue'
import {
  getTrainingCategories,
  getTrainingResources,
  getTrainingTags
} from '../api/training.js'

const route = useRoute()
const router = useRouter()
const filters = reactive({
  keyword: typeof route.query.keyword === 'string' ? route.query.keyword : '',
  resourceType: typeof route.query.resourceType === 'string' ? route.query.resourceType : '',
  categoryId: typeof route.query.categoryId === 'string' ? route.query.categoryId : '',
  tagId: typeof route.query.tagId === 'string' ? route.query.tagId : ''
})
const categories = ref([])
const tags = ref([])
const resources = ref([])
const loading = ref(false)
const error = ref('')
const catalogWarning = ref('')
const pagination = reactive({
  pageNo: positiveInteger(route.query.pageNo, 1),
  pageSize: 8,
  total: 0,
  pages: 0
})
let activeController = null

const activeFilterCount = computed(() =>
  [filters.keyword, filters.resourceType, filters.categoryId, filters.tagId].filter(Boolean).length
)

function positiveInteger(value, fallback) {
  const parsed = Number.parseInt(value, 10)
  return Number.isInteger(parsed) && parsed > 0 ? parsed : fallback
}

function queryFromState(pageNo = 1) {
  return Object.fromEntries(
    Object.entries({
      keyword: filters.keyword,
      resourceType: filters.resourceType,
      categoryId: filters.categoryId,
      tagId: filters.tagId,
      pageNo: pageNo > 1 ? pageNo : ''
    }).filter(([, value]) => value !== '')
  )
}

async function loadCatalogs() {
  const [categoryResult, tagResult] = await Promise.allSettled([
    getTrainingCategories(),
    getTrainingTags()
  ])
  if (categoryResult.status === 'fulfilled') categories.value = categoryResult.value || []
  if (tagResult.status === 'fulfilled') tags.value = tagResult.value || []
  if (categoryResult.status === 'rejected' || tagResult.status === 'rejected') {
    catalogWarning.value = '类别或标签暂时未加载，可继续按关键词和资源类型查询。'
  }
}

async function loadResources() {
  activeController?.abort()
  const requestController = new AbortController()
  activeController = requestController
  loading.value = true
  error.value = ''
  try {
    const data = await getTrainingResources(
      {
        keyword: filters.keyword,
        resourceType: filters.resourceType,
        categoryId: filters.categoryId,
        tagId: filters.tagId,
        pageNo: pagination.pageNo,
        pageSize: pagination.pageSize
      },
      requestController.signal
    )
    resources.value = data?.records || []
    pagination.pageNo = Number(data?.pageNo || pagination.pageNo)
    pagination.total = Number(data?.total || 0)
    pagination.pages = Number(data?.pages || 0)
  } catch (requestError) {
    if (requestError.name !== 'AbortError') {
      error.value = requestError.message || '培训资源加载失败。'
      resources.value = []
    }
  } finally {
    if (activeController === requestController) {
      loading.value = false
    }
  }
}

async function applyFilters() {
  pagination.pageNo = 1
  await router.replace({ name: 'training', query: queryFromState(1) })
  await loadResources()
}

async function resetFilters() {
  Object.assign(filters, { keyword: '', resourceType: '', categoryId: '', tagId: '' })
  await applyFilters()
}

async function changePage(pageNo) {
  pagination.pageNo = pageNo
  await router.replace({ name: 'training', query: queryFromState(pageNo) })
  await loadResources()
  const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches
  window.scrollTo({ top: 0, behavior: reduceMotion ? 'auto' : 'smooth' })
}

function resourceTypeLabel(type) {
  return { ARTICLE: '文章', VIDEO: '视频', PPT: 'PPT' }[type] || type || '培训资料'
}

function formatDuration(seconds) {
  const minutes = Math.max(1, Math.ceil(Number(seconds) / 60))
  return `${minutes} 分钟`
}

function formatDate(value) {
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return ''
  return new Intl.DateTimeFormat('zh-CN', { month: 'short', day: 'numeric' }).format(date)
}

onMounted(() => {
  loadCatalogs()
  loadResources()
})

onBeforeUnmount(() => activeController?.abort())
</script>
