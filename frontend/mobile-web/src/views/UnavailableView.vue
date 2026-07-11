<template>
  <section class="page unavailable-page">
    <div class="page-heading">
      <p class="eyebrow">
        BACKEND PENDING
      </p>
      <h1 tabindex="-1">
        {{ title }}
      </h1>
      <p>{{ description }}</p>
    </div>

    <div class="empty-card unavailable-card">
      <span class="empty-icon"><AppIcon :name="icon" /></span>
      <div>
        <h2>暂未开放真实业务数据</h2>
        <p>{{ detail }}</p>
        <p class="honesty-note">
          当前页面不会使用 Mock 数据或伪造记录；后端接口完成后再进行真实联调。
        </p>
      </div>
      <RouterLink
        class="secondary-button"
        to="/workspace"
      >
        <AppIcon name="arrow-left" />
        返回工作台
      </RouterLink>
    </div>
  </section>
</template>

<script setup>
import AppIcon from '../components/AppIcon.vue'

defineProps({
  title: { type: String, required: true },
  description: { type: String, required: true },
  detail: { type: String, required: true },
  icon: { type: String, default: 'info' }
})
</script>
