import { http } from './http'

export interface LoginPayload {
  username: string
  password: string
}

export interface LoginResult {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  userId: number
  username: string
  nickname: string
  permissions: string[]
  propertyScopes: number[]
  currentPropertyId?: number
}

function unwrapData<T>(payload: any): T | null {
  if (!payload) {
    return null
  }
  if (payload.data && typeof payload.data === 'object') {
    return payload.data as T
  }
  if (payload.code && payload.code !== 200 && payload.code !== 0) {
    return null
  }
  return payload as T
}

export async function login(payload: LoginPayload): Promise<LoginResult> {
  const res = await http.post('/auth/login', payload)
  const data = unwrapData<LoginResult>(res.data)

  if (!data || !data.accessToken) {
    throw new Error('login response invalid')
  }

  return data
}

export async function refreshToken(refreshToken: string): Promise<LoginResult> {
  const res = await http.post('/auth/refresh', { refreshToken })
  const data = unwrapData<LoginResult>(res.data)
  if (!data?.accessToken) {
    throw new Error('refresh response invalid')
  }
  return data
}

export async function logout(refreshToken?: string): Promise<void> {
  await http.post('/auth/logout', { refreshToken })
}

export async function switchProperty(propertyId: number): Promise<LoginResult> {
  const res = await http.post('/auth/switch-property', { propertyId })
  const data = unwrapData<LoginResult>(res.data)
  if (!data?.accessToken) {
    throw new Error('switch property response invalid')
  }
  return data
}
