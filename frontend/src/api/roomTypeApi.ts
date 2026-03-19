import { http } from './http'

export interface RoomTypeItem {
  id: number
  propertyId: number
  propertyName: string
  roomTypeCode: string
  roomTypeName: string
  maxGuestCount: number
  bedType?: string
  basePrice: number
  status: number
}

export interface RoomTypeCreatePayload {
  propertyId: number
  roomTypeCode: string
  roomTypeName: string
  maxGuestCount: number
  bedType?: string
  basePrice: number
}

export interface RoomTypeUpdatePayload {
  propertyId: number
  roomTypeName: string
  maxGuestCount: number
  bedType?: string
  basePrice: number
}

export async function fetchRoomTypes(): Promise<RoomTypeItem[]> {
  const res = await http.get('/room-types')
  return res.data.data
}

export async function createRoomType(payload: RoomTypeCreatePayload): Promise<number> {
  const res = await http.post('/room-types', payload)
  return res.data.data
}

export async function updateRoomType(id: number, payload: RoomTypeUpdatePayload): Promise<void> {
  await http.put(`/room-types/${id}`, payload)
}

export async function updateRoomTypeStatus(id: number, status: number): Promise<void> {
  await http.patch(`/room-types/${id}/status`, { status })
}
