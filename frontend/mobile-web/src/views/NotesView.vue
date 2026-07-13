<template>
  <section class="page learning-library-page">
    <div class="library-title-row">
      <div>
        <p class="eyebrow">
          PERSONAL NOTES
        </p>
        <h1 tabindex="-1">
          学习笔记
        </h1>
        <p>整理培训中的重点内容，笔记仅保存在当前设备和账号下。</p>
      </div>
    </div>

    <div
      v-if="notes.length"
      class="notes-grid"
    >
      <article
        v-for="note in notes"
        :key="note.resourceId"
        class="note-card"
      >
        <div class="note-card-heading">
          <span class="note-icon"><AppIcon name="note" /></span>
          <time>{{ formatDate(note.updatedAt) }}</time>
        </div>
        <h2>{{ note.title || `培训资料 #${note.resourceId}` }}</h2>
        <p>{{ note.content }}</p>
        <RouterLink :to="`/training/${note.resourceId}`">
          继续学习 <AppIcon name="arrow-right" />
        </RouterLink>
      </article>
    </div>
    <div
      v-else
      class="empty-card notes-empty"
    >
      <span class="empty-icon"><AppIcon name="note" /></span>
      <div><h2>还没有学习笔记</h2><p>打开任意培训资料，即可记录学习重点。</p></div>
      <RouterLink
        class="primary-button"
        to="/training"
      >
        浏览课程
      </RouterLink>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import AppIcon from '../components/AppIcon.vue'
import { learningLibrary, loadLearningLibrary } from '../learningStore.js'

const notes = computed(() => Object.values(learningLibrary.notes).sort((a, b) => b.updatedAt.localeCompare(a.updatedAt)))

function formatDate(value) {
  return new Intl.DateTimeFormat('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }).format(new Date(value))
}

onMounted(loadLearningLibrary)
</script>
