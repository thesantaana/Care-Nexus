<template>
  <section class="page page-wide admin-module">
    <header class="page-heading">
      <div>
        <p class="eyebrow">
          培训结果
        </p><h2>护工培训成绩</h2><p>每门课程最高分达到60分即通过；全部课程通过后整体培训通过。</p>
      </div><input
        v-model.trim="keyword"
        class="admin-search"
        type="search"
        placeholder="搜索护工姓名"
      >
    </header>
    <div class="admin-metrics">
      <article><strong>{{ scores.length }}</strong><span>护工人数</span></article><article><strong>{{ passedCount }}</strong><span>已通过培训</span></article><article><strong>{{ overallAverage }}</strong><span>总体平均分</span></article>
    </div>
    <div
      v-if="error"
      class="admin-alert"
    >
      {{ error }}
    </div>
    <section class="data-panel">
      <div class="table-scroll">
        <table class="data-table">
          <thead><tr><th>护工</th><th>课程成绩</th><th>平均分</th><th>通过进度</th><th>培训状态</th></tr></thead><tbody>
            <tr
              v-for="item in filtered"
              :key="item.userId"
            >
              <td><strong>{{ item.displayName }}</strong></td><td>
                <div class="score-pills">
                  <span
                    v-for="course in item.courseScores"
                    :key="course.resourceId"
                    :class="course.passed ? 'is-pass' : ''"
                  >{{ course.resourceTitle }} {{ course.bestScore }}分</span>
                </div>
              </td><td>{{ item.averageScore }}分</td><td>{{ item.passedCourseCount }}/{{ item.courseCount }}</td><td>
                <span
                  class="status-badge"
                  :class="item.trainingPassed ? 'status-published' : 'status-draft'"
                >{{ item.trainingPassed ? '已通过' : '未通过' }}</span>
              </td>
            </tr><tr v-if="!filtered.length">
              <td
                colspan="5"
                class="admin-empty-row"
              >
                暂无成绩记录
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { listCaregiverScores } from '../../api/adminTraining.js'
const scores = ref([]); const keyword = ref(''); const error = ref('')
const filtered = computed(() => scores.value.filter((item) => !keyword.value || item.displayName?.includes(keyword.value)))
const passedCount = computed(() => scores.value.filter((item) => item.trainingPassed).length)
const overallAverage = computed(() => scores.value.length ? `${(scores.value.reduce((sum, item) => sum + Number(item.averageScore || 0), 0) / scores.value.length).toFixed(1)}分` : '0分')
async function load() { try { scores.value = await listCaregiverScores() || [] } catch (e) { error.value = e.message } }
onMounted(load)
</script>
