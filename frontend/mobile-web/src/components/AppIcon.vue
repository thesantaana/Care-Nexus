<template>
  <svg
    class="app-icon"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.8"
    stroke-linecap="round"
    stroke-linejoin="round"
    aria-hidden="true"
  >
    <template v-if="name === 'home'">
      <path d="m3 10 9-7 9 7" />
      <path d="M5 9v11h14V9" />
      <path d="M9 20v-6h6v6" />
    </template>
    <template v-else-if="name === 'book'">
      <path d="M4 5.5A2.5 2.5 0 0 1 6.5 3H11v16H6.5A2.5 2.5 0 0 0 4 21.5z" />
      <path d="M20 5.5A2.5 2.5 0 0 0 17.5 3H13v16h4.5a2.5 2.5 0 0 1 2.5 2.5z" />
    </template>
    <template v-else-if="name === 'progress'">
      <path d="M4 19V9" />
      <path d="M10 19V5" />
      <path d="M16 19v-7" />
      <path d="M22 19H2" />
    </template>
    <template v-else-if="name === 'orders'">
      <path d="M6 3h12v18H6z" />
      <path d="M9 7h6M9 11h6M9 15h4" />
    </template>
    <template v-else-if="name === 'user'">
      <circle
        cx="12"
        cy="8"
        r="4"
      />
      <path d="M4.5 21a7.5 7.5 0 0 1 15 0" />
    </template>
    <template v-else-if="name === 'heart'">
      <path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21l7.8-7.5 1.1-1.1a5.5 5.5 0 0 0-.1-7.8z" />
    </template>
    <template v-else-if="name === 'services'">
      <path d="M12 22s8-4 8-11V5l-8-3-8 3v6c0 7 8 11 8 11z" />
      <path d="M9 12h6M12 9v6" />
    </template>
    <template v-else-if="name === 'search'">
      <circle
        cx="11"
        cy="11"
        r="7"
      />
      <path d="m20 20-4-4" />
    </template>
    <template v-else-if="name === 'filter'">
      <path d="M4 5h16M7 12h10M10 19h4" />
    </template>
    <template v-else-if="name === 'arrow-left'">
      <path d="m15 18-6-6 6-6" />
    </template>
    <template v-else-if="name === 'arrow-right'">
      <path d="m9 18 6-6-6-6" />
    </template>
    <template v-else-if="name === 'external'">
      <path d="M15 3h6v6" />
      <path d="m10 14 11-11" />
      <path d="M18 13v7a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V7a1 1 0 0 1 1-1h7" />
    </template>
    <template v-else-if="name === 'clock'">
      <circle
        cx="12"
        cy="12"
        r="9"
      />
      <path d="M12 7v5l3 2" />
    </template>
    <template v-else-if="name === 'refresh'">
      <path d="M20 7v5h-5" />
      <path d="M4 17v-5h5" />
      <path d="M6.1 8A7 7 0 0 1 18 6l2 6M4 12l2 6a7 7 0 0 0 11.9-2" />
    </template>
    <template v-else-if="name === 'logout'">
      <path d="M10 17l5-5-5-5M15 12H3" />
      <path d="M14 3h5a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-5" />
    </template>
    <template v-else-if="name === 'eye'">
      <path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6S2 12 2 12z" />
      <circle
        cx="12"
        cy="12"
        r="2.5"
      />
    </template>
    <template v-else-if="name === 'eye-off'">
      <path d="m3 3 18 18" />
      <path d="M10.6 6.1A9.5 9.5 0 0 1 12 6c6.5 0 10 6 10 6a17 17 0 0 1-2.1 2.8M6.2 6.2C3.5 8 2 12 2 12s3.5 6 10 6a9.8 9.8 0 0 0 3.1-.5" />
    </template>
    <template v-else-if="name === 'check'">
      <path d="m5 12 4 4L19 6" />
    </template>
    <template v-else-if="name === 'alert'">
      <path d="M12 3 2.5 20h19z" />
      <path d="M12 9v4M12 17h.01" />
    </template>
    <template v-else-if="name === 'info'">
      <circle
        cx="12"
        cy="12"
        r="9"
      />
      <path d="M12 11v6M12 7h.01" />
    </template>
    <template v-else-if="name === 'file'">
      <path d="M6 2h8l4 4v16H6z" />
      <path d="M14 2v5h5M9 13h6M9 17h6" />
    </template>
    <template v-else>
      <path d="M4 13h4l2-6 4 10 2-4h4" />
    </template>
  </svg>
</template>

<script setup>
defineProps({
  name: {
    type: String,
    default: 'pulse'
  }
})
</script>
