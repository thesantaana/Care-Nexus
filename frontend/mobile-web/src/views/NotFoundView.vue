<template>
  <section class="page centered-page">
    <div class="empty-card access-card">
      <span class="empty-icon"><AppIcon name="search" /></span>
      <p class="eyebrow">
        PAGE NOT FOUND
      </p>
      <h1 tabindex="-1">
        没有找到这个页面
      </h1>
      <p>链接可能已变化，返回工作台继续使用。</p>
      <RouterLink
        class="primary-button"
        to="/workspace"
      >
        返回工作台
      </RouterLink>
    </div>
  </section>
</template>

<script setup>
import AppIcon from '../components/AppIcon.vue'
</script>
