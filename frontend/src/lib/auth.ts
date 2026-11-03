export const TOKEN_STORAGE_KEY = "random-data-token"

export function authHeaders(): HeadersInit {
  const token = window.localStorage.getItem(TOKEN_STORAGE_KEY)
  return token ? { Authorization: `Bearer ${token}` } : {}
}

export function clearToken() {
  window.localStorage.removeItem(TOKEN_STORAGE_KEY)
}
