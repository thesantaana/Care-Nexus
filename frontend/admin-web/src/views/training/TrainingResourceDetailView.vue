<template>
  <section
    class="tn-page"
    aria-labelledby="resource-detail-title"
  >
    <template v-if="canView">
      <header class="tn-page-header">
        <div>
          <p class="tn-eyebrow">
            护理培训 · 资源详情
          </p>
          <h1 id="resource-detail-title">
            {{ resource?.title || '培训资源详情' }}
          </h1>
          <p class="tn-page-description">
            查看资源内容、发布状态和分类标签。管理人员可在符合状态规则时编辑、发布或下架。
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
            v-if="canEdit"
            class="tn-button tn-button--primary"
            :to="editPath"
          >
            编辑资源
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
        <span class="tn-sr-only">正在加载培训资源详情</span>
      </div>

      <div
        v-else-if="loadError"
        class="tn-panel tn-empty"
        role="alert"
      >
        <div class="tn-empty-content">
          <h2>资源详情加载失败</h2>
          <p>{{ loadError }}</p>
          <div class="tn-inline-actions">
            <button
              class="tn-button tn-button--primary"
              type="button"
              @click="loadResource"
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

      <template v-else-if="resource">
        <section
          class="tn-panel"
          aria-labelledby="resource-overview-title"
        >
          <div class="tn-panel-header">
            <div>
              <h2 id="resource-overview-title">
                资源概览
              </h2>
              <p>{{ resource.summary || '该资源暂未填写摘要。' }}</p>
            </div>
            <span
              class="tn-badge"
              :class="statusClass(resource.status)"
            >
              {{ resourceStatusLabel(resource.status) }}
            </span>
          </div>
          <div class="tn-panel-body">
            <dl class="tn-meta-list">
              <div>
                <dt>资源类型</dt>
                <dd>{{ resourceTypeLabel(resource.resourceType) }}</dd>
              </div>
              <div>
                <dt>存储方式</dt>
                <dd>{{ storageModeLabel(resource.storageMode) }}</dd>
              </div>
              <div>
                <dt>培训类别</dt>
                <dd>
                  {{ resource.category?.categoryName || '未分类' }}
                  <span
                    v-if="resource.category?.status === 'DISABLED'"
                    class="tn-muted"
                  >（已停用）</span>
                </dd>
              </div>
              <div>
                <dt>建议学习时长</dt>
                <dd>{{ durationLabel }}</dd>
              </div>
              <div>
                <dt>发布时间</dt>
                <dd>{{ formatDateTime(resource.publishedAt) }}</dd>
              </div>
              <div>
                <dt>资源编号</dt>
                <dd>#{{ resource.id }}</dd>
              </div>
            </dl>

            <div class="tn-detail-tags">
              <h3>培训标签</h3>
              <div
                v-if="resource.tags?.length"
                class="tn-tag-list"
              >
                <span
                  v-for="tag in resource.tags"
                  :key="tag.id"
                  class="tn-tag"
                >
                  {{ tag.tagName }}{{ tag.status === 'DISABLED' ? '（已停用）' : '' }}
                </span>
              </div>
              <p
                v-else
                class="tn-muted"
              >
                暂无标签
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
                学习内容
              </h2>
              <p>内容按资源存储方式安全展示。</p>
            </div>
          </div>
          <div class="tn-panel-body">
            <div
              v-if="resource.storageMode === 'TEXT'"
              class="tn-readonly-content"
            >
              {{ resource.content || '暂无正文内容。' }}
            </div>
            <div
              v-else-if="resource.storageMode === 'EXTERNAL_LINK'"
              class="tn-link-card"
            >
              <div>
                <h3>外部学习链接</h3>
                <p>{{ resource.externalUrl }}</p>
              </div>
              <a
                class="tn-button tn-button--primary"
                :href="resource.externalUrl"
                target="_blank"
                rel="noopener noreferrer"
              >
                在新窗口打开
              </a>
            </div>
            <div
              v-else
              class="tn-alert tn-alert--warning tn-local-file-note"
              role="note"
            >
              <p><strong>本地文件资源</strong></p>
              <p>文件资源 ID：{{ resource.fileResourceId || '未提供' }}。当前前端不提供文件上传或下载入口。</p>
            </div>
          </div>
        </section>

        <section
          v-if="canManage"
          class="tn-panel"
          aria-labelledby="resource-actions-title"
        >
          <div class="tn-panel-header">
            <div>
              <h2 id="resource-actions-title">
                状态操作
              </h2>
              <p>状态流转会由后端校验，并记录发布或下架操作日志。</p>
            </div>
          </div>
          <div class="tn-panel-body tn-state-action-row">
            <div>
              <p
                v-if="['DRAFT', 'OFFLINE'].includes(resource.status)"
                class="tn-muted"
              >
                发布后，该资源将对具有培训资源查看权限的护理人员可见。
              </p>
              <p
                v-else
                class="tn-muted"
              >
                下架后普通学习用户不可再访问，但历史学习记录仍会保留。
              </p>
              <p
                v-if="resource.status === 'PUBLISHED'"
                class="tn-muted"
              >
                如需修改核心内容，请先下架资源。
              </p>
            </div>
            <button
              v-if="['DRAFT', 'OFFLINE'].includes(resource.status)"
              class="tn-button tn-button--primary"
              type="button"
              @click="requestAction('publish')"
            >
              发布资源
            </button>
            <button
              v-else-if="resource.status === 'PUBLISHED'"
              class="tn-button tn-button--danger"
              type="button"
              @click="requestAction('offline')"
            >
              下架资源
            </button>
          </div>
        </section>

        <section
          v-if="canManage"
          class="tn-panel"
          aria-labelledby="resource-audit-title"
        >
          <div class="tn-panel-header">
            <div>
              <h2 id="resource-audit-title">
                维护信息
              </h2>
              <p>后端当前仅返回用户编号，不展示未提供的姓名信息。</p>
            </div>
          </div>
          <div class="tn-panel-body">
            <dl class="tn-meta-list">
              <div>
                <dt>创建人</dt>
                <dd>{{ userLabel(resource.createdBy) }}</dd>
              </div>
              <div>
                <dt>最近维护人</dt>
                <dd>{{ userLabel(resource.updatedBy) }}</dd>
              </div>
            </dl>
          </div>
        </section>
      </template>

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
        <h1 id="resource-detail-title">
          无法查看培训资源
        </h1>
        <p>当前账号缺少“培训资源查看”权限。如需访问，请联系管理员调整角色权限。</p>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import {
  getResource,
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
  errorMessage,
  formatDateTime,
  resourceStatusLabel,
  resourceTypeLabel,
  storageModeLabel
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

const route = useRoute()
const permissionSet = computed(() => new Set(resolvePermissionCodes(props.permissionCodes)))
const canManage = computed(() => permissionSet.value.has(TRAINING_MANAGE_PERMISSION))
const canView = computed(() => canManage.value || permissionSet.value.has(TRAINING_VIEW_PERMISSION))
const currentResourceId = computed(() => props.resourceId ?? route.params.id ?? null)
const resource = ref(null)
const loading = ref(false)
const loadError = ref('')
const successMessage = ref('')
let loadSequence = 0

const action = reactive({
  open: false,
  type: '',
  reason: '',
  reasonError: '',
  error: '',
  busy: false
})

const canEdit = computed(() => canManage.value &&
  resource.value?.status !== 'PUBLISHED' &&
  resource.value?.storageMode !== 'LOCAL_FILE')

const editPath = computed(() => `${resourcePath(currentResourceId.value)}/edit`)

const durationLabel = computed(() => {
  const seconds = resource.value?.durationSeconds
  if (seconds === null || seconds === undefined) {
    return '未设置'
  }
  return `${seconds} 秒`
})

const actionDescription = computed(() => {
  if (!resource.value) {
    return ''
  }
  return action.type === 'publish'
    ? `“${resource.value.title}”发布后，具有培训资源查看权限的护理人员即可访问。`
    : `“${resource.value.title}”下架后将不再向普通学习用户展示，历史学习记录不会被删除。`
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

function userLabel(user) {
  if (!user?.id) {
    return '未提供'
  }
  return user.displayName || user.username || `用户 #${user.id}`
}

async function loadResource() {
  if (!canView.value) {
    return
  }

  const id = Number(currentResourceId.value)
  if (!Number.isInteger(id) || id <= 0) {
    loadSequence += 1
    loading.value = false
    loadError.value = '资源编号无效，请从资源列表重新进入。'
    resource.value = null
    return
  }

  const sequence = ++loadSequence
  loading.value = true
  loadError.value = ''
  try {
    const data = await getResource(id)
    if (sequence === loadSequence) {
      resource.value = data
    }
  } catch (error) {
    if (sequence === loadSequence) {
      loadError.value = errorMessage(error, '培训资源详情加载失败。')
      resource.value = null
    }
  } finally {
    if (sequence === loadSequence) {
      loading.value = false
    }
  }
}

function requestAction(type) {
  if (!canManage.value || !resource.value) {
    return
  }
  Object.assign(action, {
    open: true,
    type,
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
  }
}

async function confirmAction() {
  if (!resource.value || !canManage.value) {
    return
  }
  if (action.type === 'offline' && !action.reason.trim()) {
    action.reasonError = '请输入下架原因。'
    return
  }

  action.busy = true
  action.error = ''
  try {
    resource.value = action.type === 'publish'
      ? await publishResource(resource.value.id)
      : await offlineResource(resource.value.id, action.reason.trim())
    successMessage.value = action.type === 'publish' ? '培训资源已发布。' : '培训资源已下架。'
    action.busy = false
    closeAction()
  } catch (error) {
    action.error = errorMessage(error)
  } finally {
    action.busy = false
  }
}

watch([currentResourceId, canView], ([id, hasAccess], previous) => {
  if (!hasAccess) {
    loadSequence += 1
    loading.value = false
    return
  }
  if (!previous || previous[0] !== id || !previous[1]) {
    loadResource()
  }
}, { immediate: true })
</script>

<style scoped>
.tn-detail-tags {
  margin-top: 22px;
  padding-top: 20px;
  border-top: 1px solid var(--tn-border);
}

.tn-detail-tags h3,
.tn-link-card h3 {
  margin: 0 0 10px;
  font-size: 15px;
}

.tn-detail-tags p,
.tn-link-card p {
  margin: 0;
}

.tn-link-card,
.tn-state-action-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

.tn-link-card {
  padding: 18px;
  border: 1px solid var(--tn-border);
  border-radius: 10px;
  background: var(--tn-surface-muted);
}

.tn-link-card p {
  overflow-wrap: anywhere;
  color: var(--tn-text-muted);
  line-height: 1.55;
}

.tn-state-action-row p {
  margin: 0 0 5px;
}

.tn-local-file-note {
  margin: 0;
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
  .tn-link-card,
  .tn-state-action-row {
    align-items: stretch;
    flex-direction: column;
  }
}
</style>
