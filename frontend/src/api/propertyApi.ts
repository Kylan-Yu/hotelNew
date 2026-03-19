import { http } from './http'

export interface PropertyItem {
  id: number
  groupId?: number
  groupName?: string
  brandId?: number
  brandName?: string
  propertyCode: string
  propertyName: string
  businessMode: string
  contactPhone?: string
  city?: string
  address?: string
  status: number
}

export interface PropertyCreatePayload {
  groupId?: number
  brandId?: number
  propertyCode: string
  propertyName: string
  businessMode: string
  contactPhone?: string
  province?: string
  city?: string
  district?: string
  address?: string
}

export interface PropertyUpdatePayload {
  groupId?: number
  brandId?: number
  propertyName: string
  businessMode: string
  contactPhone?: string
  province?: string
  city?: string
  district?: string
  address?: string
}

export async function fetchProperties(): Promise<PropertyItem[]> {
  const res = await http.get('/properties')
  return res.data.data
}

export async function createProperty(payload: PropertyCreatePayload): Promise<number> {
  const res = await http.post('/properties', payload)
  return res.data.data
}

export async function updateProperty(id: number, payload: PropertyUpdatePayload): Promise<void> {
  await http.put(`/properties/${id}`, payload)
}

export async function updatePropertyStatus(id: number, status: number): Promise<void> {
  await http.patch(`/properties/${id}/status`, { status })
}
