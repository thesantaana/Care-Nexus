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
    <section class="course-score-section">
      <div class="course-score-heading">
        <div><p class="eyebrow">课程维度</p><h3>课程成绩概览</h3></div>
        <span>按课程查看参考与通过情况</span>
      </div>
      <div class="course-score-grid">
        <article
          v-for="course in courseOverview"
          :key="course.resourceId"
          class="course-score-card"
        >
          <img :src="course.coverUrl" :alt="`${course.resourceTitle}课程封面`">
          <div class="course-score-content">
            <div class="course-score-title"><div><span>培训文章</span><h4>{{ course.resourceTitle }}</h4></div><strong>{{ course.averageScore }}分</strong></div>
            <div class="course-score-progress"><i :style="{ width: `${course.passRate}%` }" /></div>
            <div class="course-score-stats">
              <span><b>{{ course.participantCount }}</b> 人参考</span>
              <span><b>{{ course.passedCount }}</b> 人通过</span>
              <span><b>{{ course.passRate }}%</b> 通过率</span>
            </div>
          </div>
        </article>
      </div>
    </section>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { listCaregiverScores } from '../../api/adminTraining.js'
import { listResources } from '../../api/training.js'
const scores = ref([]); const resources = ref([]); const keyword = ref(''); const error = ref('')
const filtered = computed(() => scores.value.filter((item) => !keyword.value || item.displayName?.includes(keyword.value)))
const passedCount = computed(() => scores.value.filter((item) => item.trainingPassed).length)
const overallAverage = computed(() => scores.value.length ? `${(scores.value.reduce((sum, item) => sum + Number(item.averageScore || 0), 0) / scores.value.length).toFixed(1)}分` : '0分')
const courseOverview = computed(() => {
  const resourceMap = new Map(resources.value.map((item) => [Number(item.id), item]))
  const groups = new Map()
  scores.value.forEach((caregiver) => (caregiver.courseScores || []).forEach((course) => {
    const key = Number(course.resourceId)
    const group = groups.get(key) || { resourceId: key, resourceTitle: course.resourceTitle, scores: [], passedCount: 0 }
    group.scores.push(Number(course.bestScore || 0))
    if (course.passed) group.passedCount += 1
    groups.set(key, group)
  }))
  return [...groups.values()].map((group) => {
    const resource = resourceMap.get(group.resourceId)
    const participantCount = group.scores.length
    return {
      ...group,
      participantCount,
      averageScore: participantCount ? (group.scores.reduce((sum, score) => sum + score, 0) / participantCount).toFixed(1) : '0.0',
      passRate: participantCount ? Math.round(group.passedCount * 100 / participantCount) : 0,
      coverUrl: resource?.coverUrl || '/assets/default-course-cover.png'
    }
  })
})
async function load() { try { const [scoreData, resourceData] = await Promise.all([listCaregiverScores(), listResources({ status: 'PUBLISHED', pageNo: 1, pageSize: 100 })]); scores.value = scoreData || []; resources.value = resourceData?.records || [] } catch (e) { error.value = e.message } }
onMounted(load)
</script>

<style scoped>
.course-score-section { margin-top: 28px; }
.course-score-heading { display: flex; align-items: end; justify-content: space-between; margin-bottom: 14px; }
.course-score-heading h3 { margin: 3px 0 0; font-size: 22px; color: #102f3e; }
.course-score-heading > span { color: #617987; font-size: 13px; }
.course-score-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16px; }
.course-score-card { display: grid; grid-template-columns: 172px 1fr; min-height: 168px; overflow: hidden; background: #fff; border: 1px solid #d8e5e9; border-radius: 14px; box-shadow: 0 12px 30px rgba(31, 73, 87, .08); }
.course-score-card img { width: 100%; height: 100%; min-height: 168px; object-fit: cover; background: #e9f1f3; }
.course-score-content { display: grid; align-content: center; gap: 18px; padding: 20px; }
.course-score-title { display: flex; align-items: start; justify-content: space-between; gap: 16px; }
.course-score-title span { color: #16816f; font-size: 12px; font-weight: 700; }
.course-score-title h4 { margin: 5px 0 0; color: #123545; font-size: 18px; }
.course-score-title strong { color: #087f6c; font-size: 24px; white-space: nowrap; }
.course-score-progress { height: 7px; overflow: hidden; background: #e5eeef; border-radius: 999px; }
.course-score-progress i { display: block; height: 100%; background: linear-gradient(90deg, #0e7c6b, #55b99c); border-radius: inherit; }
.course-score-stats { display: flex; gap: 22px; color: #647b87; font-size: 12px; }
.course-score-stats b { color: #173c4b; font-size: 15px; }
@media (max-width: 1000px) { .course-score-grid { grid-template-columns: 1fr; } }
@media (max-width: 600px) { .course-score-card { grid-template-columns: 112px 1fr; } .course-score-stats { gap: 10px; flex-wrap: wrap; } }
</style>
