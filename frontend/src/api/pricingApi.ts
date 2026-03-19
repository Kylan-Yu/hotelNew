import { http } from './http'

export interface PricePlanItem {
  id: number
  propertyId: number
  propertyName: string
  roomTypeId: number
  roomTypeName: string
  bizDate: string
  salePrice: number
  sellableInventory: number
  overbookLimit: number
  priceTag?: string
}

export interface PriceRuleItem {
  id: number
  propertyId: number
  ruleType: string
  ruleName: string
  dateScope?: string
  weekdayMask?: string
  adjustType: string
  adjustValue: number
  priorityNo: number
  enabledFlag: number
}

export interface InventoryItem {
  id: number
  propertyId: number
  propertyName: string
  roomTypeId: number
  roomTypeName: string
  bizDate: string
  totalInventory: number
  occupiedInventory: number
  availableInventory: number
  warning?: boolean
}

export async function fetchPricePlans(): Promise<PricePlanItem[]> {
  const res = await http.get('/pricing/plans')
  return res.data.data
}

export async function upsertPricePlan(payload: any): Promise<void> {
  await http.post('/pricing/plans', payload)
}

export async function fetchPriceRules(): Promise<PriceRuleItem[]> {
  const res = await http.get('/pricing/rules')
  return res.data.data
}

export async function createPriceRule(payload: any): Promise<number> {
  const res = await http.post('/pricing/rules', payload)
  return res.data.data
}

export async function fetchInventories(): Promise<InventoryItem[]> {
  const res = await http.get('/pricing/inventories')
  return res.data.data
}

export async function adjustInventory(payload: any): Promise<void> {
  await http.patch('/pricing/inventories/adjust', payload)
}
