import { createSlice, PayloadAction } from '@reduxjs/toolkit'

interface AuthState {
  accessToken: string
  refreshToken: string
  username: string
  nickname: string
  permissions: string[]
  propertyScopes: number[]
  currentPropertyId?: number
}

const ACCESS_TOKEN_KEY = 'hms_access_token'
const REFRESH_TOKEN_KEY = 'hms_refresh_token'
const USERNAME_KEY = 'hms_username'
const NICKNAME_KEY = 'hms_nickname'
const PERMISSIONS_KEY = 'hms_permissions'
const PROPERTY_SCOPES_KEY = 'hms_property_scopes'
const CURRENT_PROPERTY_KEY = 'hms_current_property'

const getStorageValue = (key: string): string => localStorage.getItem(key) || ''

const initialState: AuthState = {
  accessToken: getStorageValue(ACCESS_TOKEN_KEY),
  refreshToken: getStorageValue(REFRESH_TOKEN_KEY),
  username: getStorageValue(USERNAME_KEY),
  nickname: getStorageValue(NICKNAME_KEY),
  permissions: JSON.parse(localStorage.getItem(PERMISSIONS_KEY) || '[]'),
  propertyScopes: JSON.parse(localStorage.getItem(PROPERTY_SCOPES_KEY) || '[]'),
  currentPropertyId: Number(localStorage.getItem(CURRENT_PROPERTY_KEY) || 0) || undefined,
}

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setAuth(state, action: PayloadAction<AuthState>) {
      state.accessToken = action.payload.accessToken
      state.refreshToken = action.payload.refreshToken
      state.username = action.payload.username
      state.nickname = action.payload.nickname
      state.permissions = action.payload.permissions
      state.propertyScopes = action.payload.propertyScopes || []
      state.currentPropertyId = action.payload.currentPropertyId

      localStorage.setItem(ACCESS_TOKEN_KEY, action.payload.accessToken)
      localStorage.setItem(REFRESH_TOKEN_KEY, action.payload.refreshToken)
      localStorage.setItem(USERNAME_KEY, action.payload.username)
      localStorage.setItem(NICKNAME_KEY, action.payload.nickname)
      localStorage.setItem(PERMISSIONS_KEY, JSON.stringify(action.payload.permissions))
      localStorage.setItem(PROPERTY_SCOPES_KEY, JSON.stringify(action.payload.propertyScopes || []))
      localStorage.setItem(CURRENT_PROPERTY_KEY, String(action.payload.currentPropertyId || ''))
    },
    clearAuth(state) {
      state.accessToken = ''
      state.refreshToken = ''
      state.username = ''
      state.nickname = ''
      state.permissions = []
      state.propertyScopes = []
      state.currentPropertyId = undefined

      localStorage.removeItem(ACCESS_TOKEN_KEY)
      localStorage.removeItem(REFRESH_TOKEN_KEY)
      localStorage.removeItem(USERNAME_KEY)
      localStorage.removeItem(NICKNAME_KEY)
      localStorage.removeItem(PERMISSIONS_KEY)
      localStorage.removeItem(PROPERTY_SCOPES_KEY)
      localStorage.removeItem(CURRENT_PROPERTY_KEY)
    },
  },
})

export const { setAuth, clearAuth } = authSlice.actions
export default authSlice.reducer
