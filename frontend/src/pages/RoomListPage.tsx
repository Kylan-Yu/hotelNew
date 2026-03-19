import { App, Button, Drawer, Form, Input, Modal, Select, Space, Table, Tag } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { PropertyItem, fetchProperties } from '../api/propertyApi'
import {
  RoomCreatePayload,
  RoomItem,
  RoomStatusLogItem,
  createRoom,
  fetchRoomStatusLogs,
  fetchRooms,
  updateRoom,
  updateRoomStatus,
} from '../api/roomApi'
import { RoomTypeItem, fetchRoomTypes } from '../api/roomTypeApi'

const ROOM_STATUS_OPTIONS = [
  { label: '空净', value: 'VACANT_CLEAN', color: 'green' },
  { label: '在住', value: 'OCCUPIED', color: 'blue' },
  { label: '待清扫', value: 'VACANT_DIRTY', color: 'orange' },
  { label: '维修', value: 'MAINTENANCE', color: 'red' },
  { label: '锁房', value: 'LOCKED', color: 'purple' },
]

const roomStatusText = (status: string) => {
  const target = ROOM_STATUS_OPTIONS.find((item) => item.value === status)
  return target?.label || status
}

export function RoomListPage() {
  const [data, setData] = useState<RoomItem[]>([])
  const [properties, setProperties] = useState<PropertyItem[]>([])
  const [roomTypes, setRoomTypes] = useState<RoomTypeItem[]>([])
  const [open, setOpen] = useState(false)
  const [statusOpen, setStatusOpen] = useState(false)
  const [detailOpen, setDetailOpen] = useState(false)
  const [statusLogs, setStatusLogs] = useState<RoomStatusLogItem[]>([])
  const [editing, setEditing] = useState<RoomItem | null>(null)
  const [targetRoom, setTargetRoom] = useState<RoomItem | null>(null)
  const [selectedPropertyId, setSelectedPropertyId] = useState<number | undefined>()
  const [form] = Form.useForm<RoomCreatePayload>()
  const [statusForm] = Form.useForm<{ status: string; reason?: string }>()
  const { message } = App.useApp()

  const loadData = async () => {
    const [roomList, propertyList, roomTypeList] = await Promise.all([fetchRooms(), fetchProperties(), fetchRoomTypes()])
    setData(roomList)
    setProperties(propertyList)
    setRoomTypes(roomTypeList)
  }

  useEffect(() => {
    loadData()
  }, [])

  const filteredRoomTypes = useMemo(() => {
    if (!selectedPropertyId) {
      return roomTypes
    }
    return roomTypes.filter((item) => item.propertyId === selectedPropertyId)
  }, [roomTypes, selectedPropertyId])

  const statusColorMap = useMemo(() => {
    const map: Record<string, string> = {}
    ROOM_STATUS_OPTIONS.forEach((item) => {
      map[item.value] = item.color
    })
    return map
  }, [])

  const handleAdd = () => {
    setEditing(null)
    setSelectedPropertyId(undefined)
    form.resetFields()
    setOpen(true)
  }

  const handleEdit = (row: RoomItem) => {
    setEditing(row)
    setSelectedPropertyId(row.propertyId)
    form.setFieldsValue({
      propertyId: row.propertyId,
      roomTypeId: row.roomTypeId,
      roomNo: row.roomNo,
      floorNo: row.floorNo,
    })
    setOpen(true)
  }

  const handleSave = async () => {
    const values = await form.validateFields()
    if (editing) {
      await updateRoom(editing.id, { roomTypeId: values.roomTypeId, floorNo: values.floorNo })
      message.success('房间更新成功')
    } else {
      await createRoom(values)
      message.success('房间创建成功')
    }
    setOpen(false)
    form.resetFields()
    loadData()
  }

  const handleOpenStatus = (row: RoomItem) => {
    setTargetRoom(row)
    statusForm.setFieldsValue({ status: row.status })
    setStatusOpen(true)
  }

  const handleStatusSave = async () => {
    const values = await statusForm.validateFields()
    if (!targetRoom) {
      return
    }
    await updateRoomStatus(targetRoom.id, values)
    message.success('房态更新成功')
    setStatusOpen(false)
    statusForm.resetFields()
    loadData()
  }

  const handleOpenDetail = async (row: RoomItem) => {
    setTargetRoom(row)
    const logs = await fetchRoomStatusLogs(row.id)
    setStatusLogs(logs)
    setDetailOpen(true)
  }

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" onClick={handleAdd}>
          新增房间
        </Button>
      </Space>
      <Table
        rowKey="id"
        dataSource={data}
        columns={[
          { title: '房号', dataIndex: 'roomNo' },
          { title: '楼层', dataIndex: 'floorNo' },
          { title: '民宿', dataIndex: 'propertyName' },
          { title: '房型', dataIndex: 'roomTypeName' },
          {
            title: '房态',
            render: (_, row) => <Tag color={statusColorMap[row.status] || 'default'}>{roomStatusText(row.status)}</Tag>,
          },
          {
            title: '操作',
            render: (_, row) => (
              <Space>
                <Button type="link" onClick={() => handleEdit(row)}>
                  编辑
                </Button>
                <Button type="link" onClick={() => handleOpenStatus(row)}>
                  变更房态
                </Button>
                <Button type="link" onClick={() => handleOpenDetail(row)}>
                  详情
                </Button>
              </Space>
            ),
          },
        ]}
      />

      <Modal title={editing ? '编辑房间' : '新增房间'} open={open} onOk={handleSave} onCancel={() => setOpen(false)} destroyOnClose>
        <Form layout="vertical" form={form}>
          <Form.Item name="propertyId" label="民宿" rules={[{ required: true }]}>
            <Select
              disabled={!!editing}
              options={properties.map((p) => ({ label: `${p.propertyCode} - ${p.propertyName}`, value: p.id }))}
              onChange={(value) => {
                setSelectedPropertyId(value)
                form.setFieldValue('roomTypeId', undefined)
              }}
            />
          </Form.Item>
          <Form.Item name="roomTypeId" label="房型" rules={[{ required: true }]}>
            <Select options={filteredRoomTypes.map((rt) => ({ label: `${rt.roomTypeCode} - ${rt.roomTypeName}`, value: rt.id }))} />
          </Form.Item>
          <Form.Item name="roomNo" label="房号" rules={[{ required: true }]}> 
            <Input disabled={!!editing} />
          </Form.Item>
          <Form.Item name="floorNo" label="楼层"><Input /></Form.Item>
        </Form>
      </Modal>

      <Modal title="变更房态" open={statusOpen} onOk={handleStatusSave} onCancel={() => setStatusOpen(false)} destroyOnClose>
        <Form layout="vertical" form={statusForm}>
          <Form.Item name="status" label="目标房态" rules={[{ required: true }]}>
            <Select options={ROOM_STATUS_OPTIONS.map((item) => ({ label: item.label, value: item.value }))} />
          </Form.Item>
          <Form.Item name="reason" label="变更原因">
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>

      <Drawer title={targetRoom ? `房间详情 - ${targetRoom.roomNo}` : '房间详情'} open={detailOpen} onClose={() => setDetailOpen(false)} width={640}>
        <Table
          rowKey="id"
          dataSource={statusLogs}
          pagination={false}
          columns={[
            { title: '原状态', dataIndex: 'oldStatus', render: (value: string) => roomStatusText(value) },
            { title: '新状态', dataIndex: 'newStatus', render: (value: string) => roomStatusText(value) },
            { title: '原因', dataIndex: 'reason' },
            { title: '操作人', dataIndex: 'operator' },
            { title: '时间', dataIndex: 'createdAt' },
          ]}
        />
      </Drawer>
    </div>
  )
}
