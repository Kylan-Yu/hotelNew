import { buildApiUrl, http } from './http'

export async function fetchDashboard() {
  const res = await http.get('/finance-ops/dashboard')
  return res.data.data
}

export async function fetchDailyReports() {
  const res = await http.get('/finance-ops/reports/daily')
  return res.data.data
}

export async function fetchPropertyStats() {
  const res = await http.get('/finance-ops/reports/property-stats')
  return res.data.data
}

export async function fetchRefunds() {
  const res = await http.get('/finance-ops/refunds')
  return res.data.data
}

export async function fetchBillDetails() {
  const res = await http.get('/finance-ops/bills')
  return res.data.data
}

export async function fetchInvoices() {
  const res = await http.get('/finance-ops/invoices')
  return res.data.data
}

export async function fetchHousekeepingTasks() {
  const res = await http.get('/finance-ops/housekeeping/tasks')
  return res.data.data
}

export async function fetchMaintenanceTickets() {
  const res = await http.get('/finance-ops/maintenance/tickets')
  return res.data.data
}

export async function createRefund(payload: any): Promise<number> {
  const res = await http.post('/finance-ops/refunds', payload)
  return res.data.data
}

export async function createBill(payload: any): Promise<number> {
  const res = await http.post('/finance-ops/bills', payload)
  return res.data.data
}

export async function createInvoice(payload: any): Promise<number> {
  const res = await http.post('/finance-ops/invoices', payload)
  return res.data.data
}

export async function createHousekeepingTask(payload: any): Promise<number> {
  const res = await http.post('/finance-ops/housekeeping/tasks', payload)
  return res.data.data
}

export async function updateHousekeepingTaskStatus(payload: any): Promise<void> {
  await http.patch('/finance-ops/housekeeping/tasks/status', payload)
}

export async function createMaintenanceTicket(payload: any): Promise<number> {
  const res = await http.post('/finance-ops/maintenance/tickets', payload)
  return res.data.data
}

export async function updateMaintenanceTicketStatus(payload: any): Promise<void> {
  await http.patch('/finance-ops/maintenance/tickets/status', payload)
}

export function dailyReportExportUrl() {
  return buildApiUrl('/finance-ops/reports/daily/export')
}

export function propertyStatsExportUrl() {
  return buildApiUrl('/finance-ops/reports/property-stats/export')
}
