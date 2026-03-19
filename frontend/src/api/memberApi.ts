import { http } from './http'

export async function fetchMembers(): Promise<any[]> {
  const res = await http.get('/member-marketing/members')
  return res.data.data
}

export async function createMember(payload: any): Promise<number> {
  const res = await http.post('/member-marketing/members', payload)
  return res.data.data
}

export async function adjustMemberPoint(payload: any): Promise<void> {
  await http.patch('/member-marketing/members/points', payload)
}

export async function fetchMemberPointLedgers(): Promise<any[]> {
  const res = await http.get('/member-marketing/points')
  return res.data.data
}

export async function fetchMemberPreferences(memberId?: number): Promise<any[]> {
  const res = await http.get('/member-marketing/preferences', { params: { memberId } })
  return res.data.data
}

export async function createMemberPreference(payload: any): Promise<number> {
  const res = await http.post('/member-marketing/preferences', payload)
  return res.data.data
}

export async function fetchCoupons(): Promise<any[]> {
  const res = await http.get('/member-marketing/coupons')
  return res.data.data
}

export async function createCoupon(payload: any): Promise<number> {
  const res = await http.post('/member-marketing/coupons', payload)
  return res.data.data
}

export async function fetchCampaigns(): Promise<any[]> {
  const res = await http.get('/member-marketing/campaigns')
  return res.data.data
}

export async function createCampaign(payload: any): Promise<number> {
  const res = await http.post('/member-marketing/campaigns', payload)
  return res.data.data
}
