<template>
  <section class="page empty-module-page">
    <div class="empty-state empty-state-large">
      <span class="empty-state-icon"><AppIcon name="shield" /></span>
      <p class="eyebrow">
        访问受限
      </p>
      <h2>当前账号没有此页面权限</h2>
      <p>当前账号无法访问这个页面，请返回工作台选择可用功能。</p>
      <RouterLink
        class="button button-primary"
        to="/"
      >
        返回角色工作台
      </RouterLink>
    </div>
  </section>
</template>

<script setup>
import AppIcon from '../components/AppIcon.vue'
</script>
