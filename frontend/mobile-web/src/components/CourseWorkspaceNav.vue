<template>
  <nav class="course-workspace-nav" aria-label="课程功能">
    <RouterLink v-for="item in items" :key="item.key" :to="item.to" :class="{ active: active === item.key }">
      <AppIcon :name="item.icon" /><span>{{ item.label }}</span>
    </RouterLink>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import AppIcon from './AppIcon.vue'

const props = defineProps({ resourceId: { type: [String, Number], required: true }, active: { type: String, default: 'chapters' } })
const items = computed(() => [
  { key: 'chapters', label: '章节', icon: 'book', to: `/training/${props.resourceId}` },
  { key: 'discussions', label: '讨论', icon: 'chat', to: `/training/${props.resourceId}/discussions` },
  { key: 'assignments', label: '作业', icon: 'note', to: `/training/${props.resourceId}/assignments` },
  { key: 'exam', label: '考试', icon: 'file', to: `/training/${props.resourceId}/exam` },
  { key: 'mistakes', label: '错题集', icon: 'alert', to: `/training/${props.resourceId}/mistakes` },
  { key: 'records', label: '学习记录', icon: 'progress', to: `/training/${props.resourceId}/records` }
])
</script>

<style scoped>
.course-workspace-nav { display:grid; grid-template-columns:repeat(6,minmax(0,1fr)); gap:8px; margin:0 0 22px; padding:8px; border:1px solid #dce6e8; border-radius:8px; background:#fff; overflow:auto; }
.course-workspace-nav a { display:flex; min-width:90px; align-items:center; justify-content:center; gap:7px; min-height:46px; padding:8px 10px; border-radius:6px; color:#60737b; text-decoration:none; font-size:14px; white-space:nowrap; }
.course-workspace-nav a:hover,.course-workspace-nav a.active { background:#e9f8f5; color:#087e75; font-weight:700; }
.course-workspace-nav :deep(.app-icon) { width:19px; height:19px; }
@media(max-width:760px){.course-workspace-nav{grid-template-columns:none;grid-auto-flow:column;grid-auto-columns:max-content;justify-content:start;margin-inline:-8px;border-inline:0;border-radius:0}.course-workspace-nav a{min-width:82px}}
</style>
