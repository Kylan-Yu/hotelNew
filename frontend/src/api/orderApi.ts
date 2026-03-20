import { buildApiUrl, http } from './http'

export interface ReservationItem {
  id: number
  reservationNo: string
  propertyId: number
  propertyName: string
  roomTypeId: number
  roomTypeName: string
  contactName: string
  contactMobile: string
  guestCount: number
  checkInDate: string
  checkOutDate: string
  reservationStatus: string
  estimatedAmount: number
}

export interface OrderItem {
  id: number
  orderNo: string
  propertyId: number
  propertyName: string
  roomTypeName: string
  guestName: string
  guestMobile: string
  checkInDate: string
  checkOutDate: string
  totalAmount: number
  orderStatus: string
}

export interface StayRecordItem {
  id: number
  stayNo: string
  orderId: number
  orderNo: string
  propertyName: string
  roomNo: string
  stayType: string
  stayStatus: string
  checkInDate: string
  checkOutDate: string
}

export interface PaymentRecordItem {
  id: number
  orderId: number
  stayRecordId?: number
  paymentType: string
  paymentMethod: string
  amount: number
  paymentStatus: string
  externalTradeNo?: string
  remark?: string
}

export interface RoomStatusTimelineItem {
  nodeCode: string
  actionCode?: string
  remarkCode?: string
  remarkText?: string
  operationTime?: string
  operator?: string
  roomId?: number
  roomNo?: string
}

export interface OrderDetail {
  order: OrderItem
  stays: StayRecordItem[]
  guests: any[]
  payments: any[]
  auditLogs: any[]
  operationLogs: any[]
  roomStatusTimeline: RoomStatusTimelineItem[]
}

export async function fetchReservations(): Promise<ReservationItem[]> {
  const res = await http.get('/orders/reservations')
  return res.data.data
}

export async function createReservation(payload: any): Promise<number> {
  const res = await http.post('/orders/reservations', payload)
  return res.data.data
}

export async function generateOrder(reservationId: number): Promise<number> {
  const res = await http.post(`/orders/reservations/${reservationId}/generate-order`)
  return res.data.data
}

export async function fetchOrders(): Promise<OrderItem[]> {
  const res = await http.get('/orders')
  return res.data.data
}

export async function fetchOrderDetail(id: number): Promise<OrderDetail> {
  const res = await http.get(`/orders/${id}`)
  return res.data.data
}

export async function fetchOrderTimeline(id: number): Promise<RoomStatusTimelineItem[]> {
  const res = await http.get(`/orders/${id}/timeline`)
  return res.data.data
}

export async function fetchStays(): Promise<StayRecordItem[]> {
  const res = await http.get('/orders/stays')
  return res.data.data
}

export async function checkIn(payload: any): Promise<number> {
  const res = await http.post('/orders/stays/check-in', payload)
  return res.data.data
}

export async function checkout(payload: any): Promise<void> {
  await http.patch('/orders/stays/checkout', payload)
}

export async function changeRoom(payload: any): Promise<void> {
  await http.patch('/orders/stays/change-room', payload)
}

export async function extendStay(payload: any): Promise<void> {
  await http.patch('/orders/stays/extend', payload)
}

export async function cancelOrder(payload: any): Promise<void> {
  await http.patch('/orders/cancel', payload)
}

export async function registerGuest(payload: any): Promise<number> {
  const res = await http.post('/orders/guests', payload)
  return res.data.data
}

export async function createPayment(payload: any): Promise<number> {
  const res = await http.post('/orders/payments', payload)
  return res.data.data
}

export async function fetchPayments(): Promise<PaymentRecordItem[]> {
  const res = await http.get('/orders/payments')
  return res.data.data
}

export function orderExportUrl() {
  return buildApiUrl('/orders/export')
}

