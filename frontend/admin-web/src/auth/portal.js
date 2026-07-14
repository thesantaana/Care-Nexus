export const portalHomeUrl = import.meta.env.VITE_PORTAL_WEB_URL ||
  `${window.location.protocol}//${window.location.hostname}:5175`

export function redirectToPortal() {
  window.location.replace(`${portalHomeUrl}/#login`)
}

export function readPortalToken() {
  if (window.location.pathname !== '/auth-handoff') {
    return null
  }
  return new URLSearchParams(window.location.hash.slice(1)).get('token')
}

export function clearPortalTokenFromAddress() {
  window.history.replaceState(null, '', '/')
}
