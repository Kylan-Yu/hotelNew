import { http } from './http'

export interface BrandItem {
  id: number
  groupId: number
  groupName: string
  brandCode: string
  brandName: string
  status: number
}

export interface BrandCreatePayload {
  groupId: number
  brandCode: string
  brandName: string
}

export interface BrandUpdatePayload {
  groupId: number
  brandName: string
}

export async function fetchBrands(): Promise<BrandItem[]> {
  const res = await http.get('/brands')
  return res.data.data
}

export async function createBrand(payload: BrandCreatePayload): Promise<number> {
  const res = await http.post('/brands', payload)
  return res.data.data
}

export async function updateBrand(id: number, payload: BrandUpdatePayload): Promise<void> {
  await http.put(`/brands/${id}`, payload)
}

export async function updateBrandStatus(id: number, status: number): Promise<void> {
  await http.patch(`/brands/${id}/status`, { status })
}
