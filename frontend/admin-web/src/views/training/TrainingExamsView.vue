<template>
  <section class="page page-wide admin-module">
    <header class="page-heading">
      <div>
        <p class="eyebrow">
          课程考核
        </p><h2>题库与考核</h2><p>每门课程维护一份独立考核，满分100分，60分及格。</p>
      </div><button
        class="button button-primary"
        type="button"
        @click="showForm = !showForm"
      >
        新建考核
      </button>
    </header>
    <form
      v-if="showForm"
      class="admin-form-panel"
      @submit.prevent="submitExam"
    >
      <label>关联课程<select
        v-model.number="form.resourceId"
        required
      ><option
        disabled
        value=""
      >请选择课程</option><option
        v-for="resource in resources"
        :key="resource.id"
        :value="resource.id"
      >{{ resource.title }}</option></select></label>
      <label>考核名称<input
        v-model.trim="form.examName"
        required
        maxlength="128"
      ></label>
      <label>及格分数<input
        value="60"
        disabled
      ></label>
      <label>最多尝试次数<input
        v-model.number="form.maxAttempts"
        type="number"
        min="1"
        max="20"
        required
      ></label>
      <div class="admin-form-actions">
        <button
          class="button button-quiet"
          type="button"
          @click="showForm = false"
        >
          取消
        </button><button
          class="button button-primary"
          :disabled="saving"
        >
          {{ saving ? '保存中…' : '保存草稿' }}
        </button>
      </div>
    </form>
    <div
      v-if="error"
      class="admin-alert"
    >
      {{ error }}
    </div>
    <section class="data-panel">
      <div class="data-panel-heading">
        <div><h3>课程考核</h3><p>共 {{ exams.length }} 份</p></div><button
          class="button button-quiet"
          @click="load"
        >
          刷新
        </button>
      </div>
      <div class="table-scroll">
        <table class="data-table">
          <thead><tr><th>课程</th><th>考核名称</th><th>题目</th><th>及格线</th><th>状态</th><th>操作</th></tr></thead><tbody>
            <tr
              v-for="exam in exams"
              :key="exam.id"
            >
              <td>{{ resourceName(exam.resourceId) }}</td><td>{{ exam.examName }}</td><td>{{ exam.questions?.length || 0 }}题</td><td>{{ exam.passScore }}分</td><td>
                <span
                  class="status-badge"
                  :class="exam.status === 'PUBLISHED' ? 'status-published' : 'status-draft'"
                >{{ exam.status === 'PUBLISHED' ? '已发布' : '草稿' }}</span>
              </td><td>
                <button
                  v-if="exam.status !== 'PUBLISHED'"
                  class="text-link"
                  @click="handlePublish(exam)"
                >
                  发布
                </button><span
                  v-else
                  class="muted"
                >已生效</span>
              </td>
            </tr><tr v-if="!loading && !exams.length">
              <td
                colspan="6"
                class="admin-empty-row"
              >
                暂无考核
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { createExam, listExams, publishExam } from '../../api/adminTraining.js'
import { listResources } from '../../api/training.js'

const exams = ref([]); const resources = ref([]); const loading = ref(false); const saving = ref(false); const showForm = ref(false); const error = ref('')
const form = reactive({ resourceId: '', examName: '', maxAttempts: 3 })
const resourceName = (id) => resources.value.find((item) => item.id === id)?.title || `课程 #${id}`
async function load() { loading.value = true; error.value = ''; try { const [examData, resourceData] = await Promise.all([listExams(), listResources({ pageNo: 1, pageSize: 100 })]); exams.value = examData || []; resources.value = resourceData?.records || [] } catch (e) { error.value = e.message } finally { loading.value = false } }
async function submitExam() { saving.value = true; error.value = ''; try { await createExam({ ...form, passScore: 60 }); showForm.value = false; form.resourceId = ''; form.examName = ''; form.maxAttempts = 3; await load() } catch (e) { error.value = e.message } finally { saving.value = false } }
async function handlePublish(exam) { if (!window.confirm(`确认发布“${exam.examName}”吗？`)) return; try { await publishExam(exam.id); await load() } catch (e) { error.value = e.message } }
onMounted(load)
</script>
