<template>
  <main class="standalone-state">
    <div class="empty-state empty-state-large">
      <span class="empty-state-icon"><AppIcon name="search" /></span>
      <p class="eyebrow">
        404
      </p>
      <h1>没有找到这个页面</h1>
      <p>地址可能已变化，或者该入口不属于当前 PC 工作台。</p>
      <RouterLink
        class="button button-primary"
        to="/"
      >
        返回首页
      </RouterLink>
    </div>
  </main>
</template>

<script setup>
import AppIcon from '../components/AppIcon.vue'
</script>
