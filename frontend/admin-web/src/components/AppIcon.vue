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
    <path
      v-for="path in paths"
      :key="path"
      :d="path"
    />
  </svg>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  name: {
    type: String,
    required: true
  }
})

const icons = {
  home: ['M3 10.5 12 3l9 7.5', 'M5 9.5V21h14V9.5', 'M9 21v-7h6v7'],
  book: ['M4 4.5A2.5 2.5 0 0 1 6.5 2H11v18H6.5A2.5 2.5 0 0 0 4 22V4.5Z', 'M20 4.5A2.5 2.5 0 0 0 17.5 2H13v18h4.5A2.5 2.5 0 0 1 20 22V4.5Z'],
  tag: ['M20 12 12 20l-9-9V4h7l10 8Z', 'M7.5 8h.01'],
  shield: ['M12 22s8-4 8-11V5l-8-3-8 3v6c0 7 8 11 8 11Z', 'm9 12 2 2 4-4'],
  menu: ['M4 6h16', 'M4 12h16', 'M4 18h16'],
  logout: ['M10 17l5-5-5-5', 'M15 12H3', 'M14 3h5a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-5'],
  plus: ['M12 5v14', 'M5 12h14'],
  edit: ['M12 20h9', 'M16.5 3.5a2.1 2.1 0 0 1 3 3L8 18l-4 1 1-4L16.5 3.5Z'],
  eye: ['M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6S2 12 2 12Z', 'M12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z'],
  publish: ['m5 12 4 4L19 6'],
  pause: ['M8 5v14', 'M16 5v14'],
  refresh: ['M20 7h-5V2', 'M20 7a8 8 0 1 0 2 5'],
  search: ['M21 21l-4.35-4.35', 'M19 11a8 8 0 1 1-16 0 8 8 0 0 1 16 0Z'],
  close: ['M6 6l12 12', 'M18 6 6 18'],
  chevron: ['m9 18 6-6-6-6'],
  arrowLeft: ['M19 12H5', 'm12 19-7-7 7-7'],
  external: ['M14 3h7v7', 'M10 14 21 3', 'M21 14v5a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5'],
  info: ['M12 22a10 10 0 1 0 0-20 10 10 0 0 0 0 20Z', 'M12 11v6', 'M12 7h.01'],
  exam: ['M6 3h12v18H6z', 'M9 8h6', 'M9 12h6', 'm9 16 1.5 1.5L15 14'],
  spark: ['m12 3 1.4 4.1L17.5 8.5l-4.1 1.4L12 14l-1.4-4.1-4.1-1.4 4.1-1.4L12 3Z', 'm18 14 .8 2.2L21 17l-2.2.8L18 20l-.8-2.2L15 17l2.2-.8L18 14Z'],
    chart: ['M4 20V10', 'M10 20V4', 'M16 20v-7', 'M22 20H2'],
    upload: ['M12 16V4', 'm7 9 5-5 5 5', 'M5 20h14']
}

const paths = computed(() => icons[props.name] || icons.info)
</script>
