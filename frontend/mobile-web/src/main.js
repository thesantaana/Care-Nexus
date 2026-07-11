import { createApp } from 'vue'
import App from './App.vue'
import { UNAUTHORIZED_EVENT } from './api/request.js'
import { router } from './router/index.js'
import { clearSession } from './session.js'
import './styles.css'

window.addEventListener(UNAUTHORIZED_EVENT, () => {
  const currentRoute = router.currentRoute.value
  clearSession()
  if (currentRoute.name !== 'login') {
    router.replace({
      name: 'login',
      query: { reason: 'expired', redirect: currentRoute.fullPath }
    })
  }
})

createApp(App).use(router).mount('#app')
