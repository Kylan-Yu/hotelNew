import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios'

export const http = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})

const ACCESS_TOKEN_KEY = 'hms_access_token'
const REFRESH_TOKEN_KEY = 'hms_refresh_token'

let refreshingPromise: Promise<string | null> | null = null

http.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem(ACCESS_TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const status = error.response?.status
    const originalConfig = error.config as InternalAxiosRequestConfig & { _retry?: boolean }
    if (status !== 401 || !originalConfig || originalConfig._retry) {
      return Promise.reject(error)
    }

    originalConfig._retry = true
    const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY)
    if (!refreshToken) {
      return Promise.reject(error)
    }

    if (!refreshingPromise) {
      refreshingPromise = axios
        .post('http://localhost:8080/api/auth/refresh', { refreshToken })
        .then((res) => {
          const data = res.data.data
          localStorage.setItem(ACCESS_TOKEN_KEY, data.accessToken)
          localStorage.setItem(REFRESH_TOKEN_KEY, data.refreshToken)
          localStorage.setItem('hms_username', data.username || '')
          localStorage.setItem('hms_nickname', data.nickname || '')
          localStorage.setItem('hms_permissions', JSON.stringify(data.permissions || []))
          localStorage.setItem('hms_property_scopes', JSON.stringify(data.propertyScopes || []))
          localStorage.setItem('hms_current_property', String(data.currentPropertyId || ''))
          return data.accessToken as string
        })
        .catch(() => {
          localStorage.removeItem(ACCESS_TOKEN_KEY)
          localStorage.removeItem(REFRESH_TOKEN_KEY)
          localStorage.removeItem('hms_username')
          localStorage.removeItem('hms_nickname')
          localStorage.removeItem('hms_permissions')
          localStorage.removeItem('hms_property_scopes')
          localStorage.removeItem('hms_current_property')
          return null
        })
        .finally(() => {
          refreshingPromise = null
        })
    }

    const newToken = await refreshingPromise
    if (!newToken) {
      return Promise.reject(error)
    }
    originalConfig.headers.Authorization = `Bearer ${newToken}`
    return http.request(originalConfig)
  },
)
