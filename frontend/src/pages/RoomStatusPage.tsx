import { Card, Select, Space, Table, Tag } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { fetchRoomStatusLogs, fetchRooms } from '../api/roomApi'
import { DEFAULT_TABLE_PAGINATION } from '../constants/tablePagination'
import { useDictOptions } from '../hooks/useDictOptions'

export function RoomStatusPage() {
  const [rooms, setRooms] = useState<any[]>([])
  const [logs, setLogs] = useState<any[]>([])
  const [roomId, setRoomId] = useState<number | undefined>()
  const { labelMap: roomStatusLabelMap } = useDictOptions('ROOM_STATUS')

  const loadData = async (targetRoomId?: number) => {
    const roomList = await fetchRooms()
    setRooms(roomList || [])

    const ids = targetRoomId ? [targetRoomId] : (roomList || []).map((item) => item.id)
    const allLogs = await Promise.all(ids.map((id) => fetchRoomStatusLogs(id)))
    const merged = allLogs.flat().map((item) => {
      const room = (roomList || []).find((r) => r.id === item.roomId)
      return {
        ...item,
        roomNo: room?.roomNo,
        propertyName: room?.propertyName,
      }
    })
    setLogs(merged)
  }

  useEffect(() => {
    loadData()
  }, [])

  const filteredLogs = useMemo(() => {
    if (!roomId) return logs
    return logs.filter((item) => item.roomId === roomId)
  }, [logs, roomId])

  return (
    <Card title="房态记录">
      <Space style={{ marginBottom: 16 }}>
        <Select
          allowClear
          showSearch
          style={{ width: 260 }}
          placeholder="筛选房间"
          value={roomId}
          options={rooms.map((item) => ({ value: item.id, label: `${item.propertyName} / ${item.roomNo}` }))}
          onChange={(value) => {
            setRoomId(value)
            loadData(value)
          }}
        />
      </Space>

      <Table
        rowKey="id"
        dataSource={filteredLogs}
        columns={[
          { title: '日志ID', dataIndex: 'id', width: 90 },
          { title: '民宿', dataIndex: 'propertyName', width: 180 },
          { title: '房间号', dataIndex: 'roomNo', width: 100 },
          { title: '原房态', dataIndex: 'oldStatus', width: 140, render: (value: string) => roomStatusLabelMap[value] || value },
          {
            title: '新房态',
            dataIndex: 'newStatus',
            width: 140,
            render: (value: string) => <Tag color="blue">{roomStatusLabelMap[value] || value}</Tag>,
          },
          { title: '变更原因', dataIndex: 'reason' },
          { title: '操作人', dataIndex: 'operator', width: 100 },
          { title: '时间', dataIndex: 'createdAt', width: 180 },
        ]}
        pagination={DEFAULT_TABLE_PAGINATION}
      />
    </Card>
  )
}

