import { http } from './http'

export interface LogQueryPayload {
  groupId?: number
  brandId?: number
  propertyId?: number
  moduleCode?: string
  operator?: string
  startTime?: string
  endTime?: string
  preferCurrent?: boolean
  pageNo: number
  pageSize: number
}

export interface GroupOption {
  id: number
  groupName: string
}

export interface BrandOption {
  id: number
  groupId: number
  brandName: string
}

export interface PropertyOption {
  id: number
  groupId: number
  brandId: number
  propertyName: string
}

export interface LogFilterContext {
  currentPropertyId?: number
  currentBrandId?: number
  currentGroupId?: number
  allowWideRangeSwitch?: boolean
}

export async function searchOperationLogs(payload: LogQueryPayload) {
  const res = await http.post('/logs/operation/search', payload)
  return res.data.data
}

export async function searchAuditLogs(payload: LogQueryPayload) {
  const res = await http.post('/logs/audit/search', payload)
  return res.data.data
}

export async function fetchLogFilterContext(): Promise<LogFilterContext> {
  const res = await http.get('/logs/options/context')
  return res.data.data
}

export async function fetchLogGroupOptions(preferCurrent = true): Promise<GroupOption[]> {
  const res = await http.get('/logs/options/groups', { params: { preferCurrent } })
  return res.data.data
}

export async function fetchLogBrandOptions(groupId?: number, preferCurrent = true): Promise<BrandOption[]> {
  const res = await http.get('/logs/options/brands', { params: { groupId, preferCurrent } })
  return res.data.data
}

export async function fetchLogPropertyOptions(groupId?: number, brandId?: number, preferCurrent = true): Promise<PropertyOption[]> {
  const res = await http.get('/logs/options/properties', { params: { groupId, brandId, preferCurrent } })
  return res.data.data
}
