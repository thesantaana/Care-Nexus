<template>
  <section
    class="tn-page"
    aria-labelledby="training-resources-title"
  >
    <template v-if="canView">
      <header class="tn-page-header">
        <div>
          <p class="tn-eyebrow">
            护理培训 · 内容运营
          </p>
          <h1 id="training-resources-title">
            培训资源
          </h1>
          <p class="tn-page-description">
            管理培训文章、视频与课件，或查看已向护理人员发布的学习内容。
          </p>
        </div>
        <div
          v-if="canManage"
          class="tn-page-actions"
        >
          <RouterLink
            class="tn-button tn-button--secondary"
            :to="catalogPath"
          >
            分类与标签
          </RouterLink>
          <RouterLink
            class="tn-button tn-button--primary"
            :to="createPath"
          >
            新建资源
          </RouterLink>
        </div>
      </header>

      <div
        v-if="successMessage"
        class="tn-alert tn-alert--success"
        role="status"
        aria-live="polite"
      >
        <p>{{ successMessage }}</p>
      </div>

      <div
        v-if="optionsError"
        class="tn-alert tn-alert--warning"
        role="alert"
      >
        <p>{{ optionsError }} 分类与标签筛选暂不可用，资源列表仍可继续查看。</p>
        <div class="tn-alert-actions">
          <button
            class="tn-button tn-button--secondary"
            type="button"
            @click="loadOptions"
          >
            重试加载筛选项
          </button>
        </div>
      </div>

      <section
        class="tn-panel"
        aria-labelledby="training-filter-title"
      >
        <div class="tn-panel-header">
          <div>
            <h2 id="training-filter-title">
              筛选资源
            </h2>
            <p>可按关键字、类型、类别和标签组合查询。</p>
          </div>
        </div>
        <form
          class="tn-panel-body tn-form-grid"
          role="search"
          @submit.prevent="applyFilters"
        >
          <div class="tn-field tn-field--half">
            <label for="resource-keyword">标题或摘要</label>
            <input
              id="resource-keyword"
              v-model="filters.keyword"
              type="search"
              maxlength="200"
              placeholder="输入关键字"
              autocomplete="off"
            >
          </div>
          <div class="tn-field">
            <label for="resource-type">资源类型</label>
            <select
              id="resource-type"
              v-model="filters.resourceType"
            >
              <option value="">
                全部类型
              </option>
              <option
                v-for="type in RESOURCE_TYPES"
                :key="type.value"
                :value="type.value"
              >
                {{ type.label }}
              </option>
            </select>
          </div>
          <div class="tn-field">
            <label for="resource-category">培训类别</label>
            <select
              id="resource-category"
              v-model.number="filters.categoryId"
              :disabled="optionsLoading"
            >
              <option value="">
                全部类别
              </option>
              <option
                v-for="category in categories"
                :key="category.id"
                :value="category.id"
              >
                {{ category.categoryName }}{{ category.status === 'DISABLED' ? '（已停用）' : '' }}
              </option>
            </select>
          </div>
          <div class="tn-field">
            <label for="resource-tag">培训标签</label>
            <select
              id="resource-tag"
              v-model.number="filters.tagId"
              :disabled="optionsLoading"
            >
              <option value="">
                全部标签
              </option>
              <option
                v-for="tag in tags"
                :key="tag.id"
                :value="tag.id"
              >
                {{ tag.tagName }}{{ tag.status === 'DISABLED' ? '（已停用）' : '' }}
              </option>
            </select>
          </div>
          <div
            v-if="canManage"
            class="tn-field"
          >
            <label for="resource-status">发布状态</label>
            <select
              id="resource-status"
              v-model="filters.status"
            >
              <option value="">
                全部状态
              </option>
              <option
                v-for="status in RESOURCE_STATUSES"
                :key="status.value"
                :value="status.value"
              >
                {{ status.label }}
              </option>
            </select>
          </div>
          <div class="tn-filter-actions">
            <button
              class="tn-button tn-button--secondary"
              type="button"
              :disabled="loading"
              @click="resetFilters"
            >
              重置
            </button>
            <button
              class="tn-button tn-button--primary"
              type="submit"
              :disabled="loading"
            >
              {{ loading ? '查询中…' : '查询' }}
            </button>
          </div>
        </form>
      </section>

      <section
        class="tn-panel"
        aria-labelledby="training-list-title"
        :aria-busy="loading"
      >
        <div class="tn-panel-header">
          <div>
            <h2 id="training-list-title">
              资源列表
            </h2>
            <p v-if="!loading">
              共 {{ page.total }} 条资源
            </p>
            <p v-else>
              正在读取最新资源
            </p>
          </div>
          <div class="tn-inline-actions">
            <label
              class="tn-field-label"
              for="resource-page-size"
            >每页条数</label>
            <select
              id="resource-page-size"
              v-model.number="filters.pageSize"
              class="tn-input tn-page-size"
              :disabled="loading"
              @change="changePageSize"
            >
              <option :value="10">
                10 条
              </option>
              <option :value="20">
                20 条
              </option>
              <option :value="50">
                50 条
              </option>
            </select>
            <button
              class="tn-button tn-button--secondary"
              type="button"
              :disabled="loading"
              @click="loadResources"
            >
              刷新
            </button>
          </div>
        </div>

        <div
          v-if="loading"
          class="tn-loading"
          role="status"
          aria-live="polite"
        >
          <div
            class="tn-skeleton-list"
            aria-hidden="true"
          >
            <div
              v-for="index in 4"
              :key="index"
              class="tn-skeleton-row"
            />
          </div>
          <span class="tn-sr-only">正在加载培训资源</span>
        </div>

        <div
          v-else-if="listError"
          class="tn-empty"
          role="alert"
        >
          <div class="tn-empty-content">
            <h3>资源加载失败</h3>
            <p>{{ listError }}</p>
            <div class="tn-inline-actions">
              <button
                class="tn-button tn-button--primary"
                type="button"
                @click="loadResources"
              >
                重新加载
              </button>
            </div>
          </div>
        </div>

        <template v-else-if="page.records.length > 0">
          <div class="tn-table-wrap">
            <table class="tn-table">
              <thead>
                <tr>
                  <th scope="col">
                    资源
                  </th>
                  <th scope="col">
                    类型与来源
                  </th>
                  <th scope="col">
                    类别与标签
                  </th>
                  <th scope="col">
                    状态
                  </th>
                  <th scope="col">
                    发布时间
                  </th>
                  <th scope="col">
                    操作
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="resource in page.records"
                  :key="resource.id"
                >
                  <td>
                    <RouterLink
                      class="tn-resource-title"
                      :to="resourcePath(resource.id)"
                    >
                      {{ resource.title }}
                    </RouterLink>
                    <span class="tn-resource-summary">{{ resource.summary || '暂无摘要' }}</span>
                  </td>
                  <td>
                    <strong>{{ resourceTypeLabel(resource.resourceType) }}</strong>
                    <div class="tn-muted">
                      {{ storageModeLabel(resource.storageMode) }}
                    </div>
                    <div
                      v-if="resource.durationSeconds !== null && resource.durationSeconds !== undefined"
                      class="tn-muted"
                    >
                      建议学习 {{ resource.durationSeconds }} 秒
                    </div>
                  </td>
                  <td>
                    <strong>{{ resource.categoryName || '未分类' }}</strong>
                    <div
                      v-if="resource.tags?.length"
                      class="tn-tag-list tn-table-tags"
                    >
                      <span
                        v-for="tag in resource.tags"
                        :key="tag.id"
                        class="tn-tag"
                      >{{ tag.tagName }}</span>
                    </div>
                    <div
                      v-else
                      class="tn-muted"
                    >
                      暂无标签
                    </div>
                  </td>
                  <td>
                    <span
                      class="tn-badge"
                      :class="statusClass(resource.status)"
                    >
                      {{ resourceStatusLabel(resource.status) }}
                    </span>
                  </td>
                  <td class="tn-numeric">
                    {{ formatDateTime(resource.publishedAt) }}
                  </td>
                  <td>
                    <div class="tn-table-actions">
                      <RouterLink
                        class="tn-button tn-button--quiet"
                        :to="resourcePath(resource.id)"
                      >
                        查看
                      </RouterLink>
                      <RouterLink
                        v-if="canEdit(resource)"
                        class="tn-button tn-button--quiet"
                        :to="editPath(resource.id)"
                      >
                        编辑
                      </RouterLink>
                      <button
                        v-if="canManage && ['DRAFT', 'OFFLINE'].includes(resource.status)"
                        class="tn-button tn-button--quiet"
                        type="button"
                        :aria-label="`发布资源：${resource.title}`"
                        @click="requestAction('publish', resource)"
                      >
                        发布
                      </button>
                      <button
                        v-if="canManage && resource.status === 'PUBLISHED'"
                        class="tn-button tn-button--danger"
                        type="button"
                        :aria-label="`下架资源：${resource.title}`"
                        @click="requestAction('offline', resource)"
                      >
                        下架
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <footer class="tn-list-footer">
            <span class="tn-pagination-info">
              第 {{ page.pageNo }} / {{ Math.max(page.pages, 1) }} 页，共 {{ page.total }} 条
            </span>
            <nav
              class="tn-pagination"
              aria-label="培训资源分页"
            >
              <button
                class="tn-button tn-button--secondary"
                type="button"
                :disabled="page.pageNo <= 1"
                @click="goToPage(page.pageNo - 1)"
              >
                上一页
              </button>
              <button
                class="tn-button tn-button--secondary"
                type="button"
                :disabled="page.pageNo >= page.pages"
                @click="goToPage(page.pageNo + 1)"
              >
                下一页
              </button>
            </nav>
          </footer>
        </template>

        <div
          v-else
          class="tn-empty"
        >
          <div class="tn-empty-content">
            <h3>{{ hasActiveFilters ? '没有匹配的培训资源' : '还没有培训资源' }}</h3>
            <p>
              {{ hasActiveFilters ? '请调整筛选条件后再次查询。' : '资源创建后会以草稿状态保存在这里。' }}
            </p>
            <div class="tn-inline-actions">
              <button
                v-if="hasActiveFilters"
                class="tn-button tn-button--secondary"
                type="button"
                @click="resetFilters"
              >
                清除筛选
              </button>
              <RouterLink
                v-if="canManage"
                class="tn-button tn-button--primary"
                :to="createPath"
              >
                新建资源
              </RouterLink>
            </div>
          </div>
        </div>
      </section>

      <TrainingConfirmDialog
        :open="action.open"
        :title="action.type === 'publish' ? '发布培训资源' : '下架培训资源'"
        :description="actionDescription"
        :confirm-label="action.type === 'publish' ? '确认发布' : '确认下架'"
        :tone="action.type === 'offline' ? 'danger' : 'primary'"
        :busy="action.busy"
        :error="action.error"
        :require-reason="action.type === 'offline'"
        :reason="action.reason"
        :reason-error="action.reasonError"
        reason-label="下架原因"
        reason-placeholder="请说明下架原因，最多 500 个字符"
        @update:reason="updateActionReason"
        @cancel="closeAction"
        @confirm="confirmAction"
      />
    </template>

    <div
      v-else
      class="tn-panel tn-empty"
      role="alert"
    >
      <div class="tn-empty-content">
        <h1 id="training-resources-title">
          无法访问培训资源
        </h1>
        <p>当前账号缺少“培训资源查看”权限。如需访问，请联系管理员调整角色权限。</p>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import {
  listCategories,
  listResources,
  listTags,
  offlineResource,
  publishResource
} from '../../api/training.js'
import TrainingConfirmDialog from './TrainingConfirmDialog.vue'
import {
  TRAINING_MANAGE_PERMISSION,
  TRAINING_VIEW_PERMISSION,
  resolvePermissionCodes
} from './trainingPermissions.js'
import {
  RESOURCE_STATUSES,
  RESOURCE_TYPES,
  errorMessage,
  formatDateTime,
  resourceStatusLabel,
  resourceTypeLabel,
  storageModeLabel
} from './trainingUi.js'
import './training.css'

const props = defineProps({
  permissionCodes: {
    type: Array,
    default: null
  },
  createPath: {
    type: String,
    default: '/training/resources/new'
  },
  catalogPath: {
    type: String,
    default: '/training/catalogs'
  },
  resourceBasePath: {
    type: String,
    default: '/training/resources'
  }
})

const permissionSet = computed(() => new Set(resolvePermissionCodes(props.permissionCodes)))
const canManage = computed(() => permissionSet.value.has(TRAINING_MANAGE_PERMISSION))
const canView = computed(() => canManage.value || permissionSet.value.has(TRAINING_VIEW_PERMISSION))
const loading = ref(false)
const optionsLoading = ref(false)
const listError = ref('')
const optionsError = ref('')
const successMessage = ref('')
const categories = ref([])
const tags = ref([])
let requestSequence = 0

const filters = reactive({
  keyword: '',
  resourceType: '',
  categoryId: '',
  tagId: '',
  status: '',
  pageNo: 1,
  pageSize: 10
})

const page = reactive({
  records: [],
  pageNo: 1,
  pageSize: 10,
  total: 0,
  pages: 0
})

const action = reactive({
  open: false,
  type: '',
  resource: null,
  reason: '',
  reasonError: '',
  error: '',
  busy: false
})

const hasActiveFilters = computed(() => Boolean(
  filters.keyword.trim() ||
  filters.resourceType ||
  filters.categoryId ||
  filters.tagId ||
  (canManage.value && filters.status)
))

const actionDescription = computed(() => {
  if (!action.resource) {
    return ''
  }
  return action.type === 'publish'
    ? `“${action.resource.title}”发布后，具有培训资源查看权限的护理人员即可访问。`
    : `“${action.resource.title}”下架后将不再向普通学习用户展示，历史学习记录不会被删除。`
})

function statusClass(status) {
  return {
    DRAFT: 'tn-badge--draft',
    PUBLISHED: 'tn-badge--published',
    OFFLINE: 'tn-badge--offline'
  }[status] || 'tn-badge--offline'
}

function basePath() {
  return props.resourceBasePath.replace(/\/$/, '')
}

function resourcePath(id) {
  return `${basePath()}/${id}`
}

function editPath(id) {
  return `${resourcePath(id)}/edit`
}

function canEdit(resource) {
  return canManage.value &&
    ['DRAFT', 'OFFLINE'].includes(resource.status) &&
    resource.storageMode !== 'LOCAL_FILE'
}

async function loadOptions() {
  if (!canView.value) {
    return
  }

  optionsLoading.value = true
  optionsError.value = ''
  try {
    const status = canManage.value ? '' : 'ENABLED'
    const [categoryData, tagData] = await Promise.all([
      listCategories(status),
      listTags(status)
    ])
    categories.value = Array.isArray(categoryData) ? categoryData : []
    tags.value = Array.isArray(tagData) ? tagData : []
  } catch (error) {
    optionsError.value = errorMessage(error, '分类与标签加载失败。')
  } finally {
    optionsLoading.value = false
  }
}

async function loadResources() {
  if (!canView.value) {
    return
  }

  const sequence = ++requestSequence
  loading.value = true
  listError.value = ''
  try {
    const data = await listResources({
      keyword: filters.keyword.trim(),
      resourceType: filters.resourceType,
      categoryId: filters.categoryId,
      tagId: filters.tagId,
      status: canManage.value ? filters.status : '',
      pageNo: filters.pageNo,
      pageSize: filters.pageSize
    })
    if (sequence !== requestSequence) {
      return
    }
    const records = Array.isArray(data?.records) ? data.records : []
    const pages = Number(data?.pages) || 0
    if (records.length === 0 && pages > 0 && filters.pageNo > pages) {
      filters.pageNo = pages
      await loadResources()
      return
    }
    page.records = records
    page.pageNo = Number(data?.pageNo) || filters.pageNo
    page.pageSize = Number(data?.pageSize) || filters.pageSize
    page.total = Number(data?.total) || 0
    page.pages = pages
  } catch (error) {
    if (sequence === requestSequence) {
      listError.value = errorMessage(error, '培训资源加载失败。')
    }
  } finally {
    if (sequence === requestSequence) {
      loading.value = false
    }
  }
}

function applyFilters() {
  filters.pageNo = 1
  successMessage.value = ''
  loadResources()
}

function resetFilters() {
  Object.assign(filters, {
    keyword: '',
    resourceType: '',
    categoryId: '',
    tagId: '',
    status: '',
    pageNo: 1
  })
  successMessage.value = ''
  loadResources()
}

function changePageSize() {
  filters.pageNo = 1
  loadResources()
}

function goToPage(pageNo) {
  if (pageNo < 1 || pageNo > page.pages || loading.value) {
    return
  }
  filters.pageNo = pageNo
  loadResources()
}

function requestAction(type, resource) {
  if (!canManage.value) {
    return
  }
  Object.assign(action, {
    open: true,
    type,
    resource,
    reason: '',
    reasonError: '',
    error: '',
    busy: false
  })
}

function updateActionReason(reason) {
  action.reason = reason
  if (reason.trim()) {
    action.reasonError = ''
  }
}

function closeAction() {
  if (!action.busy) {
    action.open = false
    action.resource = null
  }
}

async function confirmAction() {
  if (!action.resource || !canManage.value) {
    return
  }
  if (action.type === 'offline' && !action.reason.trim()) {
    action.reasonError = '请输入下架原因。'
    return
  }

  action.busy = true
  action.error = ''
  try {
    if (action.type === 'publish') {
      await publishResource(action.resource.id)
      successMessage.value = '培训资源已发布。'
    } else {
      await offlineResource(action.resource.id, action.reason.trim())
      successMessage.value = '培训资源已下架。'
    }
    action.busy = false
    closeAction()
    await loadResources()
  } catch (error) {
    action.error = errorMessage(error)
  } finally {
    action.busy = false
  }
}

watch([canView, canManage], ([hasAccess, hasManage], previous) => {
  if (!hasAccess) {
    requestSequence += 1
    loading.value = false
    return
  }
  if (!previous || !previous[0] || previous[1] !== hasManage) {
    Promise.all([loadOptions(), loadResources()])
  }
}, { immediate: true })
</script>

<style scoped>
.tn-page-size {
  width: auto;
  min-width: 92px;
}

.tn-panel-header .tn-field-label {
  margin: 0;
}

.tn-table-tags {
  margin-top: 8px;
}

.tn-numeric {
  font-variant-numeric: tabular-nums;
  white-space: nowrap;
}

.tn-sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}
</style>
