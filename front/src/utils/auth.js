const AUTH_TOKEN_KEY = 'vlive-auth-token'
const CURRENT_USER_KEY = 'vlive-current-user'
const CURRENT_USER_ID_KEY = 'vlive-current-user-id'

export function getAuthToken() {
  if (typeof window === 'undefined') return null
  return window.localStorage.getItem(AUTH_TOKEN_KEY) || window.sessionStorage.getItem(AUTH_TOKEN_KEY)
}

export function setAuthToken(token, persist = true) {
  if (typeof window === 'undefined') return

  const primaryStore = persist ? window.localStorage : window.sessionStorage
  const secondaryStore = persist ? window.sessionStorage : window.localStorage

  primaryStore.setItem(AUTH_TOKEN_KEY, token)
  secondaryStore.removeItem(AUTH_TOKEN_KEY)
}

export function clearAuthToken() {
  if (typeof window === 'undefined') return

  window.localStorage.removeItem(AUTH_TOKEN_KEY)
  window.sessionStorage.removeItem(AUTH_TOKEN_KEY)
  window.localStorage.removeItem(CURRENT_USER_KEY)
  window.sessionStorage.removeItem(CURRENT_USER_KEY)
  window.localStorage.removeItem(CURRENT_USER_ID_KEY)
  window.sessionStorage.removeItem(CURRENT_USER_ID_KEY)
}

export function setCurrentUser(username, persist = true) {
  if (typeof window === 'undefined') return

  const primaryStore = persist ? window.localStorage : window.sessionStorage
  const secondaryStore = persist ? window.sessionStorage : window.localStorage

  primaryStore.setItem(CURRENT_USER_KEY, username)
  secondaryStore.removeItem(CURRENT_USER_KEY)
export function getCurrentUser() {
  if (typeof window === 'undefined') return null
  return window.localStorage.getItem(CURRENT_USER_KEY) || window.sessionStorage.getItem(CURRENT_USER_KEY)
}

export function setCurrentUserId(userId, persist = true) {
  if (typeof window === 'undefined') return

  const primaryStore = persist ? window.localStorage : window.sessionStorage
  const secondaryStore = persist ? window.sessionStorage : window.localStorage

  primaryStore.setItem(CURRENT_USER_ID_KEY, String(userId))
  secondaryStore.removeItem(CURRENT_USER_ID_KEY)
}

export function getCurrentUserId() {
  if (typeof window === 'undefined') return null
  const userId = window.localStorage.getItem(CURRENT_USER_ID_KEY) || window.sessionStorage.getItem(CURRENT_USER_ID_KEY)
  return userId ? parseInt(userId) : null
} if (typeof window === 'undefined') return null
  return window.localStorage.getItem(CURRENT_USER_KEY) || window.sessionStorage.getItem(CURRENT_USER_KEY)
}

