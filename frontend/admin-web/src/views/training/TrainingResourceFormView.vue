<template>
  <section
    class="tn-page"
    aria-labelledby="resource-form-title"
  >
    <template v-if="canManage">
      <header class="tn-page-header">
        <div>
          <p class="tn-eyebrow">
            护理培训 · 资源维护
          </p>
          <h1 id="resource-form-title">
            {{ isEdit ? '编辑培训资源' : '新建培训资源' }}
          </h1>
          <p class="tn-page-description">
            当前支持文本内容和外部链接。本地文件上传不在本次前端接入范围内。
          </p>
        </div>
        <div class="tn-page-actions">
          <RouterLink
            class="tn-button tn-button--secondary"
            :to="listPath"
          >
            返回列表
          </RouterLink>
          <RouterLink
            v-if="isEdit"
            class="tn-button tn-button--secondary"
            :to="detailPath(currentResourceId)"
          >
            查看详情
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
        <span class="tn-sr-only">正在加载培训资源表单</span>
      </div>

      <div
        v-else-if="loadError"
        class="tn-panel tn-empty"
        role="alert"
      >
        <div class="tn-empty-content">
          <h2>表单加载失败</h2>
          <p>{{ loadError }}</p>
          <div class="tn-inline-actions">
            <button
              class="tn-button tn-button--primary"
              type="button"
              @click="loadForm"
            >
              重新加载
            </button>
            <RouterLink
              class="tn-button tn-button--secondary"
              :to="listPath"
            >
              返回列表
            </RouterLink>
          </div>
        </div>
      </div>

      <form
        v-else
        novalidate
        @submit.prevent="submitForm"
      >
        <div
          v-if="isPublished"
          class="tn-alert tn-alert--warning"
          role="alert"
        >
          <p>已发布资源不能直接修改。请先在资源详情页下架，再编辑核心内容。</p>
        </div>
        <div
          v-if="isLocalFile"
          class="tn-alert tn-alert--warning"
          role="alert"
        >
          <p>该资源使用本地文件存储。当前页面可展示其基础信息，但不会修改或伪造文件资源 ID。</p>
        </div>
        <div
          v-if="isEditable && hasDisabledSelection"
          class="tn-alert tn-alert--warning"
          role="alert"
        >
          <p>资源关联了已停用的类别或标签。保存前请改用启用类别，并取消已停用标签。</p>
        </div>

        <div
          v-if="Object.keys(errors).length"
          class="tn-alert tn-alert--error"
          role="alert"
          tabindex="-1"
        >
          <p><strong>请检查以下内容：</strong></p>
          <ul class="tn-error-summary">
            <li
              v-for="(message, field) in errors"
              :key="field"
            >
              <a
                href="#"
                @click.prevent="focusField(field)"
              >{{ message }}</a>
            </li>
          </ul>
        </div>

        <section
          class="tn-panel"
          aria-labelledby="resource-basic-title"
        >
          <div class="tn-panel-header">
            <div>
              <h2 id="resource-basic-title">
                基础信息
              </h2>
              <p>标题、类别和摘要用于资源检索与学习者识别。</p>
            </div>
            <span
              v-if="isEdit"
              class="tn-badge"
              :class="statusClass(resourceStatus)"
            >
              {{ resourceStatusLabel(resourceStatus) }}
            </span>
          </div>
          <div class="tn-panel-body tn-form-grid">
            <div class="tn-field tn-field--wide">
              <label for="training-resource-title">
                资源标题 <span
                  class="tn-required"
                  aria-hidden="true"
                >*</span>
              </label>
              <input
                id="training-resource-title"
                v-model="form.title"
                type="text"
                maxlength="200"
                autocomplete="off"
                placeholder="例如：老年人跌倒预防与安全陪护"
                :readonly="!isEditable"
                :aria-invalid="Boolean(errors.title)"
                :aria-describedby="errors.title ? 'training-resource-title-error' : 'training-resource-title-count'"
              >
              <p
                v-if="errors.title"
                id="training-resource-title-error"
                class="tn-field-error"
              >
                {{ errors.title }}
              </p>
              <p
                id="training-resource-title-count"
                class="tn-character-count"
              >
                {{ form.title.length }}/200
              </p>
            </div>

            <div class="tn-field">
              <label for="training-resource-type">
                资源类型 <span
                  class="tn-required"
                  aria-hidden="true"
                >*</span>
              </label>
              <select
                id="training-resource-type"
                v-model="form.resourceType"
                :disabled="!isEditable"
                :aria-invalid="Boolean(errors.resourceType)"
                :aria-describedby="errors.resourceType ? 'training-resource-type-error' : undefined"
              >
                <option value="">
                  请选择
                </option>
                <option
                  v-for="type in RESOURCE_TYPES"
                  :key="type.value"
                  :value="type.value"
                >
                  {{ type.label }}
                </option>
              </select>
              <p
                v-if="errors.resourceType"
                id="training-resource-type-error"
                class="tn-field-error"
              >
                {{ errors.resourceType }}
              </p>
            </div>

            <div class="tn-field">
              <label for="training-resource-category">
                培训类别 <span
                  class="tn-required"
                  aria-hidden="true"
                >*</span>
              </label>
              <select
                id="training-resource-category"
                v-model.number="form.categoryId"
                :disabled="!isEditable"
                :aria-invalid="Boolean(errors.categoryId)"
                :aria-describedby="errors.categoryId ? 'training-resource-category-error' : undefined"
              >
                <option value="">
                  请选择
                </option>
                <option
                  v-for="category in categories"
                  :key="category.id"
                  :value="category.id"
                  :disabled="category.status === 'DISABLED' && category.id !== form.categoryId"
                >
                  {{ category.categoryName }}{{ category.status === 'DISABLED' ? '（已停用）' : '' }}
                </option>
              </select>
              <p
                v-if="errors.categoryId"
                id="training-resource-category-error"
                class="tn-field-error"
              >
                {{ errors.categoryId }}
              </p>
            </div>

            <div class="tn-field">
              <label for="training-resource-duration">建议学习时长（秒）</label>
              <input
                id="training-resource-duration"
                v-model="form.durationSeconds"
                type="number"
                min="0"
                max="86400"
                step="1"
                inputmode="numeric"
                placeholder="可选，例如 900"
                :readonly="!isEditable"
                :aria-invalid="Boolean(errors.durationSeconds)"
                :aria-describedby="errors.durationSeconds ? 'training-resource-duration-error' : 'training-resource-duration-help'"
              >
              <p
                id="training-resource-duration-help"
                class="tn-field-help"
              >
                范围 0–86400，不填写表示未设置。
              </p>
              <p
                v-if="errors.durationSeconds"
                id="training-resource-duration-error"
                class="tn-field-error"
              >
                {{ errors.durationSeconds }}
              </p>
            </div>

            <div class="tn-field tn-field--wide">
              <label for="training-resource-summary">资源摘要</label>
              <textarea
                id="training-resource-summary"
                v-model="form.summary"
                rows="4"
                maxlength="500"
                placeholder="简要说明资源内容，最多 500 个字符"
                :readonly="!isEditable"
                :aria-invalid="Boolean(errors.summary)"
                :aria-describedby="errors.summary ? 'training-resource-summary-error' : 'training-resource-summary-count'"
              />
              <p
                v-if="errors.summary"
                id="training-resource-summary-error"
                class="tn-field-error"
              >
                {{ errors.summary }}
              </p>
              <p
                id="training-resource-summary-count"
                class="tn-character-count"
              >
                {{ form.summary.length }}/500
              </p>
            </div>
          </div>
        </section>

        <section
          class="tn-panel"
          aria-labelledby="resource-content-title"
        >
          <div class="tn-panel-header">
            <div>
              <h2 id="resource-content-title">
                内容来源
              </h2>
              <p>文本直接保存到资源正文；外部链接仅接受 http 或 https 地址。</p>
            </div>
          </div>
          <div class="tn-panel-body">
            <template v-if="isLocalFile">
              <dl class="tn-meta-list">
                <div>
                  <dt>存储方式</dt>
                  <dd>本地文件</dd>
                </div>
                <div>
                  <dt>文件资源 ID</dt>
                  <dd>{{ loadedResource?.fileResourceId || '未提供' }}</dd>
                </div>
              </dl>
            </template>
            <div
              v-else
              class="tn-form-grid"
            >
              <div class="tn-field tn-field--half">
                <label for="training-resource-storage">
                  存储方式 <span
                    class="tn-required"
                    aria-hidden="true"
                  >*</span>
                </label>
                <select
                  id="training-resource-storage"
                  v-model="form.storageMode"
                  :disabled="!isEditable"
                  :aria-invalid="Boolean(errors.storageMode)"
                  :aria-describedby="errors.storageMode ? 'training-resource-storage-error' : undefined"
                >
                  <option
                    v-for="mode in STORAGE_MODES"
                    :key="mode.value"
                    :value="mode.value"
                  >
                    {{ mode.label }}
                  </option>
                </select>
                <p
                  v-if="errors.storageMode"
                  id="training-resource-storage-error"
                  class="tn-field-error"
                >
                  {{ errors.storageMode }}
                </p>
              </div>

              <div
                v-if="form.storageMode === 'TEXT'"
                class="tn-field tn-field--wide"
              >
                <label for="training-resource-content">
                  资源正文 <span
                    class="tn-required"
                    aria-hidden="true"
                  >*</span>
                </label>
                <textarea
                  id="training-resource-content"
                  v-model="form.content"
                  rows="14"
                  placeholder="输入培训正文；内容将按纯文本安全展示"
                  :readonly="!isEditable"
                  :aria-invalid="Boolean(errors.content)"
                  :aria-describedby="errors.content ? 'training-resource-content-error' : undefined"
                />
                <p
                  v-if="errors.content"
                  id="training-resource-content-error"
                  class="tn-field-error"
                >
                  {{ errors.content }}
                </p>
              </div>

              <div
                v-else
                class="tn-field tn-field--wide"
              >
                <label for="training-resource-url">
                  外部链接 <span
                    class="tn-required"
                    aria-hidden="true"
                  >*</span>
                </label>
                <input
                  id="training-resource-url"
                  v-model="form.externalUrl"
                  type="url"
                  maxlength="500"
                  placeholder="https://example.com/training"
                  :readonly="!isEditable"
                  :aria-invalid="Boolean(errors.externalUrl)"
                  :aria-describedby="errors.externalUrl ? 'training-resource-url-error' : 'training-resource-url-help'"
                >
                <p
                  id="training-resource-url-help"
                  class="tn-field-help"
                >
                  链接必须包含有效主机名。
                </p>
                <p
                  v-if="errors.externalUrl"
                  id="training-resource-url-error"
                  class="tn-field-error"
                >
                  {{ errors.externalUrl }}
                </p>
                <p
                  v-if="!isEditable && form.externalUrl"
                  class="tn-field-help"
                >
                  <a
                    :href="form.externalUrl"
                    target="_blank"
                    rel="noopener noreferrer"
                  >在新窗口打开链接</a>
                </p>
              </div>
            </div>
          </div>
        </section>

        <section
          class="tn-panel"
          aria-labelledby="resource-tags-title"
        >
          <div class="tn-panel-header">
            <div>
              <h2
                id="resource-tags-title"
                tabindex="-1"
              >
                资源标签
              </h2>
              <p>标签可多选；已停用标签需取消后才能保存。</p>
            </div>
          </div>
          <div class="tn-panel-body">
            <fieldset class="tn-tag-fieldset">
              <legend class="tn-sr-only">
                选择培训标签
              </legend>
              <div
                v-if="tags.length"
                class="tn-checkbox-grid"
              >
                <label
                  v-for="tag in tags"
                  :key="tag.id"
                  class="tn-checkbox"
                >
                  <input
                    v-model="form.tagIds"
                    type="checkbox"
                    :value="tag.id"
                    :disabled="!isEditable || (tag.status === 'DISABLED' && !form.tagIds.includes(tag.id))"
                  >
                  <span>{{ tag.tagName }}{{ tag.status === 'DISABLED' ? '（已停用）' : '' }}</span>
                </label>
              </div>
              <p
                v-else
                class="tn-muted"
              >
                暂无培训标签。可以先在“分类与标签”页面新增。
              </p>
              <p
                v-if="errors.tagIds"
                class="tn-field-error"
                role="alert"
              >
                {{ errors.tagIds }}
              </p>
            </fieldset>
          </div>
        </section>

        <div
          v-if="submitError"
          class="tn-alert tn-alert--error"
          role="alert"
        >
          <p>{{ submitError }}</p>
        </div>

        <footer class="tn-form-actions">
          <div class="tn-status-row">
            <span
              v-if="isEditable && isDirty"
              class="tn-unsaved-indicator"
            >有未保存的更改</span>
            <span
              v-else-if="isEdit"
              class="tn-muted"
            >当前内容已保存</span>
          </div>
          <div class="tn-inline-actions">
            <button
              v-if="isEditable && isDirty"
              class="tn-button tn-button--secondary"
              type="button"
              :disabled="saving"
              @click="resetForm"
            >
              撤销更改
            </button>
            <RouterLink
              class="tn-button tn-button--secondary"
              :to="isEdit ? detailPath(currentResourceId) : listPath"
            >
              {{ isEdit ? '取消并返回详情' : '取消' }}
            </RouterLink>
            <button
              v-if="isEditable"
              class="tn-button tn-button--primary"
              type="submit"
              :disabled="saving || !isDirty"
            >
              {{ saving ? '保存中…' : isEdit ? '保存修改' : '保存为草稿' }}
            </button>
          </div>
        </footer>
      </form>
    </template>

    <div
      v-else
      class="tn-panel tn-empty"
      role="alert"
    >
      <div class="tn-empty-content">
        <h1 id="resource-form-title">
          无法维护培训资源
        </h1>
        <p>当前账号缺少“培训资源管理”权限，不能新增或编辑资源。</p>
        <div class="tn-inline-actions">
          <RouterLink
            class="tn-button tn-button--secondary"
            :to="listPath"
          >
            返回资源列表
          </RouterLink>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, reactive, ref, watch } from 'vue'
import { onBeforeRouteLeave, useRoute, useRouter } from 'vue-router'
import {
  createResource,
  getResource,
  listCategories,
  listTags,
  updateResource
} from '../../api/training.js'
import {
  TRAINING_MANAGE_PERMISSION,
  resolvePermissionCodes
} from './trainingPermissions.js'
import {
  RESOURCE_TYPES,
  STORAGE_MODES,
  errorMessage,
  resourceStatusLabel
} from './trainingUi.js'
import './training.css'

const props = defineProps({
  resourceId: {
    type: [String, Number],
    default: null
  },
  permissionCodes: {
    type: Array,
    default: null
  },
  listPath: {
    type: String,
    default: '/training/resources'
  },
  resourceBasePath: {
    type: String,
    default: '/training/resources'
  }
})

const emit = defineEmits(['saved'])
const route = useRoute()
const router = useRouter()
const permissionSet = computed(() => new Set(resolvePermissionCodes(props.permissionCodes)))
const canManage = computed(() => permissionSet.value.has(TRAINING_MANAGE_PERMISSION))
const currentResourceId = computed(() => props.resourceId ?? route.params.id ?? null)
const isEdit = computed(() => currentResourceId.value !== null && currentResourceId.value !== undefined)
const isPublished = computed(() => resourceStatus.value === 'PUBLISHED')
const isLocalFile = computed(() => loadedResource.value?.storageMode === 'LOCAL_FILE')
const isEditable = computed(() => canManage.value && !isPublished.value && !isLocalFile.value)
const loading = ref(false)
const saving = ref(false)
const loadError = ref('')
const submitError = ref('')
const successMessage = ref('')
const categories = ref([])
const tags = ref([])
const resourceStatus = ref('DRAFT')
const loadedResource = ref(null)
const initialSnapshot = ref('')
let loadSequence = 0

const form = reactive({
  resourceType: 'ARTICLE',
  storageMode: 'TEXT',
  categoryId: '',
  title: '',
  summary: '',
  content: '',
  externalUrl: '',
  durationSeconds: '',
  tagIds: []
})

const errors = reactive({})

const normalizedForm = computed(() => ({
  resourceType: form.resourceType,
  storageMode: form.storageMode,
  categoryId: form.categoryId,
  title: form.title,
  summary: form.summary,
  content: form.content,
  externalUrl: form.externalUrl,
  durationSeconds: form.durationSeconds,
  tagIds: [...form.tagIds].map(Number).sort((left, right) => left - right)
}))

const isDirty = computed(() => Boolean(initialSnapshot.value) &&
  initialSnapshot.value !== JSON.stringify(normalizedForm.value))

const hasDisabledSelection = computed(() => {
  const selectedCategory = categories.value.find((category) => category.id === form.categoryId)
  const disabledTagIds = new Set(tags.value.filter((tag) => tag.status === 'DISABLED').map((tag) => tag.id))
  return selectedCategory?.status === 'DISABLED' || form.tagIds.some((id) => disabledTagIds.has(id))
})

function statusClass(status) {
  return {
    DRAFT: 'tn-badge--draft',
    PUBLISHED: 'tn-badge--published',
    OFFLINE: 'tn-badge--offline'
  }[status] || 'tn-badge--offline'
}

function resourcePath(id) {
  return `${props.resourceBasePath.replace(/\/$/, '')}/${id}`
}

function detailPath(id) {
  return resourcePath(id)
}

function editPath(id) {
  return `${resourcePath(id)}/edit`
}

function clearErrors() {
  Object.keys(errors).forEach((key) => delete errors[key])
}

function defaultForm() {
  return {
    resourceType: 'ARTICLE',
    storageMode: 'TEXT',
    categoryId: '',
    title: '',
    summary: '',
    content: '',
    externalUrl: '',
    durationSeconds: '',
    tagIds: []
  }
}

function updateSnapshot() {
  initialSnapshot.value = JSON.stringify(normalizedForm.value)
}

function hydrate(resource) {
  loadedResource.value = resource
  resourceStatus.value = resource.status || 'DRAFT'
  Object.assign(form, {
    resourceType: resource.resourceType || 'ARTICLE',
    storageMode: resource.storageMode || 'TEXT',
    categoryId: resource.categoryId ?? '',
    title: resource.title || '',
    summary: resource.summary || '',
    content: resource.content || '',
    externalUrl: resource.externalUrl || '',
    durationSeconds: resource.durationSeconds ?? '',
    tagIds: (resource.tags || []).map((tag) => tag.id)
  })
  clearErrors()
  updateSnapshot()
}

function validExternalUrl(value) {
  try {
    const url = new URL(value)
    return ['http:', 'https:'].includes(url.protocol) && Boolean(url.hostname)
  } catch {
    return false
  }
}

function validateForm() {
  clearErrors()
  const title = form.title.trim()
  const summary = form.summary.trim()
  const category = categories.value.find((item) => item.id === form.categoryId)
  const disabledTagIds = new Set(tags.value.filter((tag) => tag.status === 'DISABLED').map((tag) => tag.id))

  if (!title) {
    errors.title = '请输入资源标题。'
  } else if (title.length > 200) {
    errors.title = '资源标题不能超过 200 个字符。'
  }
  if (!RESOURCE_TYPES.some((type) => type.value === form.resourceType)) {
    errors.resourceType = '请选择有效的资源类型。'
  }
  if (!category) {
    errors.categoryId = '请选择培训类别。'
  } else if (category.status !== 'ENABLED') {
    errors.categoryId = '所选培训类别已停用，请选择启用类别。'
  }
  if (summary.length > 500) {
    errors.summary = '资源摘要不能超过 500 个字符。'
  }
  if (form.durationSeconds !== '') {
    const duration = Number(form.durationSeconds)
    if (!Number.isInteger(duration) || duration < 0 || duration > 86400) {
      errors.durationSeconds = '建议学习时长必须是 0–86400 之间的整数。'
    }
  }
  if (!STORAGE_MODES.some((mode) => mode.value === form.storageMode)) {
    errors.storageMode = '请选择当前支持的存储方式。'
  } else if (form.storageMode === 'TEXT' && !form.content.trim()) {
    errors.content = '请输入资源正文。'
  } else if (form.storageMode === 'EXTERNAL_LINK' && !validExternalUrl(form.externalUrl.trim())) {
    errors.externalUrl = '请输入带有效主机名的 http 或 https 地址。'
  }
  if (form.tagIds.some((id) => disabledTagIds.has(id))) {
    errors.tagIds = '请取消已停用标签后再保存。'
  }

  return Object.keys(errors).length === 0
}

function fieldId(field) {
  return {
    title: 'training-resource-title',
    resourceType: 'training-resource-type',
    categoryId: 'training-resource-category',
    summary: 'training-resource-summary',
    durationSeconds: 'training-resource-duration',
    storageMode: 'training-resource-storage',
    content: 'training-resource-content',
    externalUrl: 'training-resource-url',
    tagIds: 'resource-tags-title'
  }[field]
}

function focusField(field) {
  document.getElementById(fieldId(field))?.focus()
}

function buildPayload() {
  return {
    resourceType: form.resourceType,
    storageMode: form.storageMode,
    categoryId: Number(form.categoryId),
    title: form.title.trim(),
    summary: form.summary.trim() || null,
    content: form.storageMode === 'TEXT' ? form.content.trim() : null,
    fileResourceId: null,
    externalUrl: form.storageMode === 'EXTERNAL_LINK' ? form.externalUrl.trim() : null,
    durationSeconds: form.durationSeconds === '' ? null : Number(form.durationSeconds),
    tagIds: form.tagIds.map(Number)
  }
}

async function loadForm() {
  if (!canManage.value) {
    return
  }

  const sequence = ++loadSequence
  loading.value = true
  loadError.value = ''
  submitError.value = ''
  try {
    const id = currentResourceId.value
    if (isEdit.value && (!Number.isInteger(Number(id)) || Number(id) <= 0)) {
      throw new Error('资源编号无效，请从资源列表重新进入。')
    }

    const requests = [listCategories(), listTags()]
    if (isEdit.value) {
      requests.push(getResource(id))
    }
    const [categoryData, tagData, resource] = await Promise.all(requests)
    if (sequence !== loadSequence) {
      return
    }
    categories.value = Array.isArray(categoryData) ? categoryData : []
    tags.value = Array.isArray(tagData) ? tagData : []
    if (resource) {
      hydrate(resource)
    } else {
      loadedResource.value = null
      resourceStatus.value = 'DRAFT'
      Object.assign(form, defaultForm())
      clearErrors()
      updateSnapshot()
    }
  } catch (error) {
    if (sequence === loadSequence) {
      loadError.value = errorMessage(error, '培训资源表单加载失败。')
    }
  } finally {
    if (sequence === loadSequence) {
      loading.value = false
    }
  }
}

function resetForm() {
  if (!initialSnapshot.value) {
    return
  }
  Object.assign(form, JSON.parse(initialSnapshot.value))
  clearErrors()
  submitError.value = ''
}

async function submitForm() {
  submitError.value = ''
  successMessage.value = ''
  if (!isEditable.value || !validateForm()) {
    await nextTick()
    focusField(Object.keys(errors)[0])
    return
  }

  saving.value = true
  try {
    const wasEdit = isEdit.value
    const saved = wasEdit
      ? await updateResource(currentResourceId.value, buildPayload())
      : await createResource(buildPayload())
    hydrate(saved)
    successMessage.value = wasEdit ? '培训资源修改已保存。' : '培训资源已保存为草稿。'
    emit('saved', saved)
    if (!wasEdit) {
      await router.replace(editPath(saved.id))
    }
  } catch (error) {
    submitError.value = errorMessage(error, '培训资源保存失败。')
  } finally {
    saving.value = false
  }
}

function confirmLeave() {
  return !isEditable.value || !isDirty.value || window.confirm('当前有未保存的更改，确定离开此页面吗？')
}

function handleBeforeUnload(event) {
  if (isEditable.value && isDirty.value) {
    event.preventDefault()
    event.returnValue = ''
  }
}

onBeforeRouteLeave(() => confirmLeave())
window.addEventListener('beforeunload', handleBeforeUnload)
onBeforeUnmount(() => window.removeEventListener('beforeunload', handleBeforeUnload))

watch([currentResourceId, canManage], ([, hasManage], previous) => {
  if (!hasManage) {
    loadSequence += 1
    loading.value = false
    return
  }
  if (!previous || previous[0] !== currentResourceId.value || !previous[1]) {
    loadForm()
  }
}, { immediate: true })
</script>

<style scoped>
.tn-error-summary {
  margin: 10px 0 0;
  padding-left: 22px;
}

.tn-error-summary a {
  color: inherit;
}

.tn-tag-fieldset {
  margin: 0;
  padding: 0;
  border: 0;
}

.tn-form-actions {
  position: sticky;
  z-index: 10;
  bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  margin-top: 22px;
  padding: 14px 16px;
  border: 1px solid var(--tn-border);
  border-radius: 12px;
  background: rgb(255 255 255 / 96%);
  box-shadow: 0 14px 35px rgb(20 62 58 / 14%);
  backdrop-filter: blur(8px);
}

.tn-unsaved-indicator {
  color: var(--tn-warning);
  font-size: 14px;
  font-weight: 650;
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

@media (max-width: 680px) {
  .tn-form-actions {
    position: static;
    align-items: stretch;
    flex-direction: column;
  }

  .tn-form-actions .tn-inline-actions {
    align-items: stretch;
    flex-direction: column-reverse;
  }
}
</style>
