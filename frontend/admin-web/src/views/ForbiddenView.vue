<template>
  <section class="page empty-module-page">
    <div class="empty-state empty-state-large">
      <span class="empty-state-icon"><AppIcon name="shield" /></span>
      <p class="eyebrow">
        访问受限
      </p>
      <h2>当前账号没有此页面权限</h2>
      <p>页面和操作均按照服务端返回的权限代码拦截。若职责已变更，请联系管理员更新角色权限。</p>
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
