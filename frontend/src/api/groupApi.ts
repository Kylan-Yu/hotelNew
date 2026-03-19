import { http } from './http'

export interface GroupItem {
  id: number
  groupCode: string
  groupName: string
  status: number
}

export interface GroupCreatePayload {
  groupCode: string
  groupName: string
}

export interface GroupUpdatePayload {
  groupName: string
}

export async function fetchGroups(): Promise<GroupItem[]> {
  const res = await http.get('/groups')
  return res.data.data
}

export async function createGroup(payload: GroupCreatePayload): Promise<number> {
  const res = await http.post('/groups', payload)
  return res.data.data
}

export async function updateGroup(id: number, payload: GroupUpdatePayload): Promise<void> {
  await http.put(`/groups/${id}`, payload)
}

export async function updateGroupStatus(id: number, status: number): Promise<void> {
  await http.patch(`/groups/${id}/status`, { status })
}
