<template>
  <section class="page page-wide admin-module">
    <header class="page-heading">
      <div>
        <p class="eyebrow">
          AI辅助培训
        </p><h2>AI题目草稿审核</h2><p>AI草稿必须人工审核，通过后只进入正式题库草稿，不会自动发布。</p>
      </div><select
        v-model="status"
        class="admin-status-select"
        @change="load"
      >
        <option value="">
          全部状态
        </option><option value="DRAFT">
          待审核
        </option><option value="APPROVED">
          已通过
        </option><option value="REJECTED">
          已驳回
        </option>
      </select>
    </header>
    <div
      v-if="error"
      class="admin-alert"
    >
      {{ error }}
    </div>
    <div class="admin-card-grid">
      <article
        v-for="draft in drafts"
        :key="draft.id"
        class="admin-record-card"
      >
        <div class="admin-card-top">
          <span
            class="status-badge"
            :class="`draft-${draft.draftStatus?.toLowerCase()}`"
          >{{ statusLabel(draft.draftStatus) }}</span><small>#{{ draft.id }}</small>
        </div><h3>{{ draft.questionType === 'TRUE_FALSE' ? '判断题草稿' : '单选题草稿' }}</h3><p class="admin-draft-content">
          {{ draft.questionContent || draft.draftContent }}
        </p><div class="admin-source-list">
          <span
            v-for="source in draft.sources"
            :key="source.resourceId"
          >{{ source.resourceTitle || `资料 #${source.resourceId}` }}</span>
        </div><div
          v-if="draft.draftStatus === 'DRAFT'"
          class="admin-card-actions"
        >
          <button
            class="button button-quiet"
            @click="review(draft, 'REJECTED')"
          >
            驳回
          </button><button
            class="button button-primary"
            @click="review(draft, 'APPROVED')"
          >
            审核通过
          </button>
        </div>
      </article>
    </div>
    <p
      v-if="!loading && !drafts.length"
      class="admin-empty"
    >
      暂无AI题目草稿
    </p>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { listDrafts, reviewDraft } from '../../api/adminTraining.js'
const drafts = ref([]); const loading = ref(false); const error = ref(''); const status = ref('DRAFT')
const statusLabel = (value) => ({ DRAFT: '待审核', APPROVED: '已通过', REJECTED: '已驳回' }[value] || value)
async function load() { loading.value = true; error.value = ''; try { const data = await listDrafts({ draftStatus: status.value, pageNo: 1, pageSize: 100 }); drafts.value = data?.records || [] } catch (e) { error.value = e.message } finally { loading.value = false } }
async function review(draft, reviewStatus) { const reviewComment = window.prompt(reviewStatus === 'APPROVED' ? '审核意见（可选）' : '请输入驳回原因') ?? ''; if (reviewStatus === 'REJECTED' && !reviewComment.trim()) return; try { await reviewDraft(draft.id, { reviewStatus, reviewComment }); await load() } catch (e) { error.value = e.message } }
onMounted(load)
</script>
