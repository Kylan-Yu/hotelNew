import { http } from './http'

export interface OtaMappingItem {
  id: number
  propertyId: number
  propertyName: string
  channelCode: string
  mappingType: string
  localBizId: number
  channelBizId: string
  remark?: string
}

export interface OtaSyncLogItem {
  id: number
  propertyId: number
  channelCode: string
  bizType: string
  bizId: string
  syncStatus: string
  createdAt: string
}

export interface OtaCallbackLogItem {
  id: number
  channelCode: string
  eventType: string
  externalRequestNo: string
  callbackStatus: string
  processedFlag: string
  createdAt: string
  message?: string
}

export async function fetchOtaMappings(): Promise<OtaMappingItem[]> {
  const res = await http.get('/ota/mappings')
  return res.data.data
}

export async function createOtaMapping(payload: any): Promise<number> {
  const res = await http.post('/ota/mappings', payload)
  return res.data.data
}

export async function otaPushInventory(payload: any): Promise<void> {
  await http.post('/ota/sync/inventory', payload)
}

export async function otaPushPrice(payload: any): Promise<void> {
  await http.post('/ota/sync/price', payload)
}

export async function otaPullOrders(payload: any): Promise<void> {
  await http.post('/ota/sync/orders/pull', payload)
}

export async function fetchOtaSyncLogs(): Promise<OtaSyncLogItem[]> {
  const res = await http.get('/ota/sync/logs')
  return res.data.data
}

export async function fetchOtaCallbackLogs(): Promise<OtaCallbackLogItem[]> {
  const res = await http.get('/ota/callback/logs')
  return res.data.data
}
