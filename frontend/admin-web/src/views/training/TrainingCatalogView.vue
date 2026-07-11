<template>
  <section
    class="tn-page"
    aria-labelledby="training-catalog-title"
  >
    <template v-if="canView">
      <header class="tn-page-header">
        <div>
          <p class="tn-eyebrow">
            护理培训 · 基础数据
          </p>
          <h1 id="training-catalog-title">
            分类与标签
          </h1>
          <p class="tn-page-description">
            维护培训资源的分类层级与检索标签。停用不会删除历史资源或学习记录。
          </p>
        </div>
        <div class="tn-page-actions">
          <RouterLink
            class="tn-button tn-button--secondary"
            :to="resourceListPath"
          >
            返回资源列表
          </RouterLink>
          <template v-if="canManage">
            <button
              class="tn-button tn-button--secondary"
              type="button"
              @click="openEditor('tag')"
            >
              新增标签
            </button>
            <button
              class="tn-button tn-button--primary"
              type="button"
              @click="openEditor('category')"
            >
              新增类别
            </button>
          </template>
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

      <section
        class="tn-panel"
        aria-labelledby="catalog-filter-title"
      >
        <div class="tn-panel-header">
          <div>
            <h2 id="catalog-filter-title">
              查看范围
            </h2>
            <p>{{ canManage ? '可查看全部、启用或停用数据。' : '当前权限仅能查看已启用数据。' }}</p>
          </div>
          <div class="tn-inline-actions">
            <label
              v-if="canManage"
              class="tn-field-label"
              for="catalog-status-filter"
            >状态</label>
            <select
              v-if="canManage"
              id="catalog-status-filter"
              v-model="statusFilter"
              class="tn-input tn-status-filter"
              :disabled="loading"
              @change="loadCatalog"
            >
              <option value="">
                全部状态
              </option>
              <option
                v-for="status in CATALOG_STATUSES"
                :key="status.value"
                :value="status.value"
              >
                {{ status.label }}
              </option>
            </select>
            <button
              class="tn-button tn-button--secondary"
              type="button"
              :disabled="loading"
              @click="loadCatalog"
            >
              刷新
            </button>
          </div>
        </div>
      </section>

      <div
        v-if="loading"
        class="tn-panel tn-loading"
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
        <span class="tn-sr-only">正在加载分类与标签</span>
      </div>

      <div
        v-else-if="loadError"
        class="tn-panel tn-empty"
        role="alert"
      >
        <div class="tn-empty-content">
          <h2>分类与标签加载失败</h2>
          <p>{{ loadError }}</p>
          <div class="tn-inline-actions">
            <button
              class="tn-button tn-button--primary"
              type="button"
              @click="loadCatalog"
            >
              重新加载
            </button>
          </div>
        </div>
      </div>

      <div
        v-else
        class="tn-catalog-grid"
      >
        <section
          class="tn-panel"
          aria-labelledby="category-list-title"
        >
          <div class="tn-panel-header">
            <div>
              <h2 id="category-list-title">
                培训类别
              </h2>
              <p>共 {{ categories.length }} 个，按排序值升序展示。</p>
            </div>
            <button
              v-if="canManage"
              class="tn-button tn-button--quiet"
              type="button"
              @click="openEditor('category')"
            >
              新增
            </button>
          </div>
          <div
            v-if="categories.length"
            class="tn-table-wrap"
          >
            <table class="tn-table">
              <thead>
                <tr>
                  <th scope="col">
                    类别名称
                  </th>
                  <th scope="col">
                    排序
                  </th>
                  <th scope="col">
                    状态
                  </th>
                  <th
                    v-if="canManage"
                    scope="col"
                  >
                    操作
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="category in categories"
                  :key="category.id"
                >
                  <td><strong>{{ category.categoryName }}</strong></td>
                  <td class="tn-numeric">
                    {{ category.sortNo }}
                  </td>
                  <td>
                    <span
                      class="tn-badge"
                      :class="catalogStatusClass(category.status)"
                    >
                      {{ catalogStatusLabel(category.status) }}
                    </span>
                  </td>
                  <td v-if="canManage">
                    <div class="tn-table-actions">
                      <button
                        class="tn-button tn-button--quiet"
                        type="button"
                        @click="openEditor('category', category)"
                      >
                        编辑
                      </button>
                      <button
                        class="tn-button"
                        :class="category.status === 'ENABLED' ? 'tn-button--danger' : 'tn-button--quiet'"
                        type="button"
                        @click="requestStatusChange('category', category)"
                      >
                        {{ category.status === 'ENABLED' ? '停用' : '启用' }}
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div
            v-else
            class="tn-catalog-empty"
          >
            <p>{{ statusFilter ? '当前状态下没有培训类别。' : '还没有培训类别。' }}</p>
            <button
              v-if="canManage"
              class="tn-button tn-button--primary"
              type="button"
              @click="openEditor('category')"
            >
              新增类别
            </button>
          </div>
        </section>

        <section
          class="tn-panel"
          aria-labelledby="tag-list-title"
        >
          <div class="tn-panel-header">
            <div>
              <h2 id="tag-list-title">
                培训标签
              </h2>
              <p>共 {{ tags.length }} 个，用于资源检索与内容聚合。</p>
            </div>
            <button
              v-if="canManage"
              class="tn-button tn-button--quiet"
              type="button"
              @click="openEditor('tag')"
            >
              新增
            </button>
          </div>
          <div
            v-if="tags.length"
            class="tn-table-wrap"
          >
            <table class="tn-table">
              <thead>
                <tr>
                  <th scope="col">
                    标签名称
                  </th>
                  <th scope="col">
                    状态
                  </th>
                  <th
                    v-if="canManage"
                    scope="col"
                  >
                    操作
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="tag in tags"
                  :key="tag.id"
                >
                  <td><strong>{{ tag.tagName }}</strong></td>
                  <td>
                    <span
                      class="tn-badge"
                      :class="catalogStatusClass(tag.status)"
                    >
                      {{ catalogStatusLabel(tag.status) }}
                    </span>
                  </td>
                  <td v-if="canManage">
                    <div class="tn-table-actions">
                      <button
                        class="tn-button tn-button--quiet"
                        type="button"
                        @click="openEditor('tag', tag)"
                      >
                        编辑
                      </button>
                      <button
                        class="tn-button"
                        :class="tag.status === 'ENABLED' ? 'tn-button--danger' : 'tn-button--quiet'"
                        type="button"
                        @click="requestStatusChange('tag', tag)"
                      >
                        {{ tag.status === 'ENABLED' ? '停用' : '启用' }}
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div
            v-else
            class="tn-catalog-empty"
          >
            <p>{{ statusFilter ? '当前状态下没有培训标签。' : '还没有培训标签。' }}</p>
            <button
              v-if="canManage"
              class="tn-button tn-button--primary"
              type="button"
              @click="openEditor('tag')"
            >
              新增标签
            </button>
          </div>
        </section>
      </div>

      <TrainingCatalogEditorDialog
        :open="editor.open"
        :kind="editor.kind"
        :item="editor.item"
        :busy="editor.busy"
        :error="editor.error"
        @cancel="closeEditor"
        @submit="saveCatalogItem"
      />

      <TrainingConfirmDialog
        :open="statusAction.open"
        :title="statusAction.targetStatus === 'DISABLED' ? `停用${statusAction.kindLabel}` : `启用${statusAction.kindLabel}`"
        :description="statusActionDescription"
        :confirm-label="statusAction.targetStatus === 'DISABLED' ? '确认停用' : '确认启用'"
        :tone="statusAction.targetStatus === 'DISABLED' ? 'danger' : 'primary'"
        :busy="statusAction.busy"
        :error="statusAction.error"
        @cancel="closeStatusAction"
        @confirm="confirmStatusChange"
      />
    </template>

    <div
      v-else
      class="tn-panel tn-empty"
      role="alert"
    >
      <div class="tn-empty-content">
        <h1 id="training-catalog-title">
          无法查看分类与标签
        </h1>
        <p>当前账号缺少培训资源查看权限。如需维护基础数据，还需要“培训资源管理”权限。</p>
        <div class="tn-inline-actions">
          <RouterLink
            class="tn-button tn-button--secondary"
            :to="resourceListPath"
          >
            返回资源列表
          </RouterLink>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import {
  createCategory,
  createTag,
  listCategories,
  listTags,
  updateCategory,
  updateCategoryStatus,
  updateTag,
  updateTagStatus
} from '../../api/training.js'
import TrainingCatalogEditorDialog from './TrainingCatalogEditorDialog.vue'
import TrainingConfirmDialog from './TrainingConfirmDialog.vue'
import {
  TRAINING_MANAGE_PERMISSION,
  TRAINING_VIEW_PERMISSION,
  resolvePermissionCodes
} from './trainingPermissions.js'
import {
  CATALOG_STATUSES,
  catalogStatusLabel,
  errorMessage
} from './trainingUi.js'
import './training.css'

const props = defineProps({
  permissionCodes: {
    type: Array,
    default: null
  },
  resourceListPath: {
    type: String,
    default: '/training/resources'
  }
})

const permissionSet = computed(() => new Set(resolvePermissionCodes(props.permissionCodes)))
const canManage = computed(() => permissionSet.value.has(TRAINING_MANAGE_PERMISSION))
const canView = computed(() => canManage.value || permissionSet.value.has(TRAINING_VIEW_PERMISSION))
const statusFilter = ref('')
const loading = ref(false)
const loadError = ref('')
const successMessage = ref('')
const categories = ref([])
const tags = ref([])
let loadSequence = 0

const editor = reactive({
  open: false,
  kind: 'category',
  item: null,
  busy: false,
  error: ''
})

const statusAction = reactive({
  open: false,
  kind: '',
  kindLabel: '',
  item: null,
  targetStatus: '',
  busy: false,
  error: ''
})

const statusActionDescription = computed(() => {
  if (!statusAction.item) {
    return ''
  }
  const name = statusAction.kind === 'category'
    ? statusAction.item.categoryName
    : statusAction.item.tagName
  return statusAction.targetStatus === 'DISABLED'
    ? `停用“${name}”后，新建或编辑资源时不能再选用；历史资源和学习记录不会被删除。`
    : `启用“${name}”后，新建或编辑资源时可以再次选用。`
})

function catalogStatusClass(status) {
  return status === 'ENABLED' ? 'tn-badge--enabled' : 'tn-badge--disabled'
}

async function loadCatalog() {
  if (!canView.value) {
    return
  }

  const sequence = ++loadSequence
  loading.value = true
  loadError.value = ''
  try {
    const status = canManage.value ? statusFilter.value : 'ENABLED'
    const [categoryData, tagData] = await Promise.all([
      listCategories(status),
      listTags(status)
    ])
    if (sequence !== loadSequence) {
      return
    }
    categories.value = Array.isArray(categoryData) ? categoryData : []
    tags.value = Array.isArray(tagData) ? tagData : []
  } catch (error) {
    if (sequence === loadSequence) {
      loadError.value = errorMessage(error, '分类与标签加载失败。')
    }
  } finally {
    if (sequence === loadSequence) {
      loading.value = false
    }
  }
}

function openEditor(kind, item = null) {
  if (!canManage.value) {
    return
  }
  Object.assign(editor, {
    open: true,
    kind,
    item,
    busy: false,
    error: ''
  })
}

function closeEditor() {
  if (!editor.busy) {
    editor.open = false
    editor.item = null
  }
}

async function saveCatalogItem(payload) {
  if (!canManage.value) {
    return
  }
  editor.busy = true
  editor.error = ''
  try {
    const isEdit = Boolean(editor.item)
    if (editor.kind === 'category') {
      if (isEdit) {
        await updateCategory(editor.item.id, payload)
      } else {
        await createCategory(payload)
      }
    } else if (isEdit) {
      await updateTag(editor.item.id, payload)
    } else {
      await createTag(payload)
    }
    const kindLabel = editor.kind === 'category' ? '培训类别' : '培训标签'
    successMessage.value = `${kindLabel}${isEdit ? '已更新' : '已新增'}。`
    editor.busy = false
    closeEditor()
    await loadCatalog()
  } catch (error) {
    editor.error = errorMessage(error)
  } finally {
    editor.busy = false
  }
}

function requestStatusChange(kind, item) {
  if (!canManage.value) {
    return
  }
  Object.assign(statusAction, {
    open: true,
    kind,
    kindLabel: kind === 'category' ? '培训类别' : '培训标签',
    item,
    targetStatus: item.status === 'ENABLED' ? 'DISABLED' : 'ENABLED',
    busy: false,
    error: ''
  })
}

function closeStatusAction() {
  if (!statusAction.busy) {
    statusAction.open = false
    statusAction.item = null
  }
}

async function confirmStatusChange() {
  if (!canManage.value || !statusAction.item) {
    return
  }
  statusAction.busy = true
  statusAction.error = ''
  try {
    if (statusAction.kind === 'category') {
      await updateCategoryStatus(statusAction.item.id, statusAction.targetStatus)
    } else {
      await updateTagStatus(statusAction.item.id, statusAction.targetStatus)
    }
    successMessage.value = `${statusAction.kindLabel}已${statusAction.targetStatus === 'ENABLED' ? '启用' : '停用'}。`
    statusAction.busy = false
    closeStatusAction()
    await loadCatalog()
  } catch (error) {
    statusAction.error = errorMessage(error)
  } finally {
    statusAction.busy = false
  }
}

watch([canView, canManage], ([hasAccess, hasManage], previous) => {
  if (!hasAccess) {
    loadSequence += 1
    loading.value = false
    return
  }
  if (!hasManage) {
    statusFilter.value = ''
  }
  if (!previous || !previous[0] || previous[1] !== hasManage) {
    loadCatalog()
  }
}, { immediate: true })
</script>

<style scoped>
.tn-status-filter {
  width: auto;
  min-width: 130px;
}

.tn-panel-header .tn-field-label {
  margin: 0;
}

.tn-catalog-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(0, 0.85fr);
  gap: 20px;
}

.tn-catalog-grid .tn-panel {
  align-self: start;
}

.tn-catalog-empty {
  display: grid;
  min-height: 180px;
  place-items: center;
  padding: 28px;
  text-align: center;
}

.tn-catalog-empty p {
  margin: 0 0 16px;
  color: var(--tn-text-muted);
}

.tn-numeric {
  font-variant-numeric: tabular-nums;
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

@media (max-width: 980px) {
  .tn-catalog-grid {
    grid-template-columns: 1fr;
  }
}
</style>
