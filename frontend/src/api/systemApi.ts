import { http } from './http'

export async function fetchSystemUsers(): Promise<any[]> {
  const res = await http.get('/system/users')
  return res.data.data
}

export async function fetchSystemRoles(): Promise<any[]> {
  const res = await http.get('/system/roles')
  return res.data.data
}

export async function fetchSystemPermissions(): Promise<any[]> {
  const res = await http.get('/system/permissions')
  return res.data.data
}

export async function fetchSystemMenus(): Promise<any[]> {
  const res = await http.get('/system/menus')
  return res.data.data
}

export async function fetchSystemDicts(): Promise<any[]> {
  const res = await http.get('/system/dicts')
  return res.data.data
}

export async function fetchSystemDictItems(dictCode: string): Promise<any[]> {
  const res = await http.get(`/system/dicts/${dictCode}/items`)
  return res.data.data
}

export async function fetchPublicDictItems(dictCode: string): Promise<any[]> {
  const res = await http.get(`/dicts/${dictCode}/items`)
  return res.data.data
}

export async function fetchSystemParams(): Promise<any[]> {
  const res = await http.get('/system/params')
  return res.data.data
}

export async function createSystemUser(payload: any): Promise<number> {
  const res = await http.post('/system/users', payload)
  return res.data.data
}

export async function updateSystemUser(id: number, payload: any): Promise<void> {
  await http.put(`/system/users/${id}`, payload)
}

export async function createSystemRole(payload: any): Promise<number> {
  const res = await http.post('/system/roles', payload)
  return res.data.data
}

export async function updateSystemRole(id: number, payload: any): Promise<void> {
  await http.put(`/system/roles/${id}`, payload)
}

export async function createSystemDict(payload: any): Promise<number> {
  const res = await http.post('/system/dicts', payload)
  return res.data.data
}

export async function updateSystemDict(dictCode: string, payload: any): Promise<void> {
  await http.put(`/system/dicts/${dictCode}`, payload)
}

export async function createSystemDictItem(dictCode: string, payload: any): Promise<number> {
  const res = await http.post(`/system/dicts/${dictCode}/items`, payload)
  return res.data.data
}

export async function updateSystemDictItem(id: number, payload: any): Promise<void> {
  await http.put(`/system/dict-items/${id}`, payload)
}

export async function createSystemParam(payload: any): Promise<number> {
  const res = await http.post('/system/params', payload)
  return res.data.data
}

export async function updateSystemParam(paramKey: string, payload: any): Promise<void> {
  await http.put(`/system/params/${encodeURIComponent(paramKey)}`, payload)
}
