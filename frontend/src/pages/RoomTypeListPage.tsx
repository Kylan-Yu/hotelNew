import { App, Button, Form, Input, InputNumber, Modal, Select, Space, Switch, Table } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { PropertyItem, fetchProperties } from '../api/propertyApi'
import {
  RoomTypeCreatePayload,
  RoomTypeItem,
  createRoomType,
  fetchRoomTypes,
  updateRoomType,
  updateRoomTypeStatus,
} from '../api/roomTypeApi'
import { DEFAULT_TABLE_PAGINATION } from '../constants/tablePagination'

export function RoomTypeListPage() {
  const [data, setData] = useState<RoomTypeItem[]>([])
  const [properties, setProperties] = useState<PropertyItem[]>([])
  const [keyword, setKeyword] = useState('')
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<RoomTypeItem | null>(null)
  const [form] = Form.useForm<RoomTypeCreatePayload>()
  const { message } = App.useApp()

  const loadData = async () => {
    const [roomTypeList, propertyList] = await Promise.all([fetchRoomTypes(), fetchProperties()])
    setData(roomTypeList)
    setProperties(propertyList)
  }

  useEffect(() => {
    loadData()
  }, [])

  const filteredData = useMemo(() => {
    if (!keyword.trim()) {
      return data
    }
    const text = keyword.trim().toLowerCase()
    return data.filter((item) => {
      return (
        String(item.roomTypeCode || '').toLowerCase().includes(text) ||
        String(item.roomTypeName || '').toLowerCase().includes(text)
      )
    })
  }, [data, keyword])

  const handleAdd = () => {
    setEditing(null)
    form.resetFields()
    setOpen(true)
  }

  const handleEdit = (row: RoomTypeItem) => {
    setEditing(row)
    form.setFieldsValue({
      propertyId: row.propertyId,
      roomTypeCode: row.roomTypeCode,
      roomTypeName: row.roomTypeName,
      maxGuestCount: row.maxGuestCount,
      bedType: row.bedType,
      basePrice: row.basePrice,
    })
    setOpen(true)
  }

  const handleSave = async () => {
    const values = await form.validateFields()
    if (editing) {
      await updateRoomType(editing.id, {
        propertyId: values.propertyId,
        roomTypeName: values.roomTypeName,
        maxGuestCount: values.maxGuestCount,
        bedType: values.bedType,
        basePrice: values.basePrice,
      })
      message.success('房型更新成功')
    } else {
      await createRoomType(values)
      message.success('房型创建成功')
    }
    setOpen(false)
    form.resetFields()
    loadData()
  }

  const handleStatusChange = async (row: RoomTypeItem, checked: boolean) => {
    await updateRoomTypeStatus(row.id, checked ? 1 : 0)
    message.success('状态更新成功')
    loadData()
  }

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" onClick={handleAdd}>
          新增房型
        </Button>
        <Input
          allowClear
          placeholder="搜索房型编码 / 房型名称"
          style={{ width: 320 }}
          value={keyword}
          onChange={(event) => setKeyword(event.target.value)}
        />
      </Space>
      <Table
        rowKey="id"
        dataSource={filteredData}
        pagination={DEFAULT_TABLE_PAGINATION}
        columns={[
          { title: '房型编码', dataIndex: 'roomTypeCode' },
          { title: '房型名称', dataIndex: 'roomTypeName' },
          { title: '所属门店', dataIndex: 'propertyName' },
          { title: '可住人数', dataIndex: 'maxGuestCount' },
          { title: '床型', dataIndex: 'bedType' },
          { title: '基础价', dataIndex: 'basePrice' },
          {
            title: '状态',
            render: (_, row) => <Switch checked={row.status === 1} onChange={(checked) => handleStatusChange(row, checked)} />,
          },
          {
            title: '操作',
            render: (_, row) => (
              <Button type="link" onClick={() => handleEdit(row)}>
                编辑
              </Button>
            ),
          },
        ]}
      />

      <Modal title={editing ? '编辑房型' : '新增房型'} open={open} onOk={handleSave} onCancel={() => setOpen(false)} destroyOnClose>
        <Form layout="vertical" form={form} initialValues={{ maxGuestCount: 2, basePrice: 0 }}>
          <Form.Item name="propertyId" label="所属门店" rules={[{ required: true }]}>
            <Select options={properties.map((p) => ({ label: `${p.propertyCode} - ${p.propertyName}`, value: p.id }))} />
          </Form.Item>
          <Form.Item name="roomTypeCode" label="房型编码" rules={[{ required: true }]}> 
            <Input disabled={!!editing} />
          </Form.Item>
          <Form.Item name="roomTypeName" label="房型名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="maxGuestCount" label="最大入住人数" rules={[{ required: true }]}>
            <InputNumber min={1} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="bedType" label="床型"><Input /></Form.Item>
          <Form.Item name="basePrice" label="基础价格" rules={[{ required: true }]}>
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
