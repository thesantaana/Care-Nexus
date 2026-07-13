import { createApp } from 'vue'
import App from './App.vue'
import router from './router/index.js'
import { acceptPortalToken, clearSession, initializeSession } from './auth/session.js'
import { clearPortalTokenFromAddress, readPortalToken, redirectToPortal } from './auth/portal.js'
import { UNAUTHORIZED_EVENT } from './api/request.js'
import './styles.css'

window.addEventListener(UNAUTHORIZED_EVENT, () => {
  clearSession()
  redirectToPortal()
})

async function bootstrap() {
  const portalToken = readPortalToken()
  if (portalToken) {
    try {
      await acceptPortalToken(portalToken)
      clearPortalTokenFromAddress()
      await router.replace('/')
    } catch {
      redirectToPortal()
      return
    }
  }
  await initializeSession()
  if (!sessionIsReady()) {
    redirectToPortal()
    return
  }
  createApp(App).use(router).mount('#app')
}

function sessionIsReady() {
  return Boolean(localStorage.getItem('care-nexus-token'))
}

bootstrap()
