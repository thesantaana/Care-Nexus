<template>
  <section class="page empty-module-page">
    <div class="empty-state empty-state-large">
      <span class="empty-state-icon">
        <AppIcon name="info" />
      </span>
      <p class="eyebrow">
        {{ eyebrow }}
      </p>
      <h2>{{ title }}</h2>
      <p>{{ description }}</p>
      <div
        class="planned-capabilities"
        aria-label="计划能力"
      >
        <span
          v-for="capability in capabilities"
          :key="capability"
        >{{ capability }}</span>
      </div>
      <RouterLink
        class="button button-secondary"
        to="/"
      >
        <AppIcon name="arrowLeft" />
        返回角色工作台
      </RouterLink>
    </div>
  </section>
</template>

<script setup>
import AppIcon from '../components/AppIcon.vue'

defineProps({
  eyebrow: {
    type: String,
    required: true
  },
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true
  },
  capabilities: {
    type: Array,
    default: () => []
  }
})
</script>
