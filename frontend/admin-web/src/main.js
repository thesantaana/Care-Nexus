import { createApp } from 'vue'
import App from './App.vue'
import router from './router/index.js'
import { clearSession, initializeSession } from './auth/session.js'
import { UNAUTHORIZED_EVENT } from './api/request.js'
import './styles.css'

window.addEventListener(UNAUTHORIZED_EVENT, () => {
  const redirect = router.currentRoute.value.fullPath
  clearSession()
  if (router.currentRoute.value.name !== 'login') {
    router.replace({
      name: 'login',
      query: { redirect, reason: 'expired' }
    })
  }
})

async function bootstrap() {
  await initializeSession()
  createApp(App).use(router).mount('#app')
}

bootstrap()
