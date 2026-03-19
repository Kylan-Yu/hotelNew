import { http } from './http'

export interface RoomItem {
  id: number
  propertyId: number
  propertyName: string
  roomTypeId: number
  roomTypeName: string
  roomNo: string
  floorNo?: string
  status: string
}

export interface RoomStatusLogItem {
  id: number
  roomId: number
  oldStatus: string
  newStatus: string
  reason?: string
  operator?: string
  createdAt: string
}

export interface RoomCreatePayload {
  propertyId: number
  roomTypeId: number
  roomNo: string
  floorNo?: string
}

export interface RoomUpdatePayload {
  roomTypeId: number
  floorNo?: string
}

export interface RoomStatusUpdatePayload {
  status: string
  reason?: string
}

export async function fetchRooms(): Promise<RoomItem[]> {
  const res = await http.get('/rooms')
  return res.data.data
}

export async function createRoom(payload: RoomCreatePayload): Promise<number> {
  const res = await http.post('/rooms', payload)
  return res.data.data
}

export async function updateRoom(id: number, payload: RoomUpdatePayload): Promise<void> {
  await http.put(`/rooms/${id}`, payload)
}

export async function updateRoomStatus(id: number, payload: RoomStatusUpdatePayload): Promise<void> {
  await http.patch(`/rooms/${id}/status`, payload)
}

export async function fetchRoomStatusLogs(id: number): Promise<RoomStatusLogItem[]> {
  const res = await http.get(`/rooms/${id}/status-logs`)
  return res.data.data
}
