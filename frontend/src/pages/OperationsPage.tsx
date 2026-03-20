import { App, Form, Input, InputNumber, Modal, Select, Space, Table, Typography } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { dictText } from '../constants/businessDict'
import {
  createHousekeepingTask,
  createMaintenanceTicket,
  fetchHousekeepingTasks,
  fetchMaintenanceTickets,
  updateHousekeepingTaskStatus,
  updateMaintenanceTicketStatus,
} from '../api/financeApi'
import { fetchProperties } from '../api/propertyApi'
import { fetchRooms } from '../api/roomApi'
import { PermissionButton } from '../components/PermissionButton'
import { DEFAULT_TABLE_PAGINATION } from '../constants/tablePagination'

interface OperationsPageProps {
  view: 'housekeeping' | 'maintenance'
}

export function OperationsPage({ view }: OperationsPageProps) {
  const [housekeepingTasks, setHousekeepingTasks] = useState<any[]>([])
  const [maintenanceTickets, setMaintenanceTickets] = useState<any[]>([])
  const [properties, setProperties] = useState<any[]>([])
  const [rooms, setRooms] = useState<any[]>([])
  const [keyword, setKeyword] = useState('')

  const [filterPropertyId, setFilterPropertyId] = useState<number | undefined>()

  const [createOpen, setCreateOpen] = useState(false)
  const [statusOpen, setStatusOpen] = useState(false)
  const [currentRow, setCurrentRow] = useState<any>(null)

  const [createForm] = Form.useForm<any>()
  const [statusForm] = Form.useForm<any>()

  const { message } = App.useApp()

  const title = useMemo(() => {
    return view === 'housekeeping' ? '保洁管理' : '维修工单'
  }, [view])

  const loadData = async () => {
    const [taskList, ticketList, propertyList, roomList] = await Promise.all([
      fetchHousekeepingTasks(),
      fetchMaintenanceTickets(),
      fetchProperties(),
      fetchRooms(),
    ])
    setHousekeepingTasks(taskList || [])
    setMaintenanceTickets(ticketList || [])
    setProperties(propertyList || [])
    setRooms(roomList || [])
  }

  useEffect(() => {
    loadData()
  }, [])

  const propertyNameById = useMemo(() => {
    const map: Record<number, string> = {}
    ;(properties || []).forEach((item) => {
      map[item.id] = item.propertyName
    })
    return map
  }, [properties])

  const roomNoById = useMemo(() => {
    const map: Record<number, string> = {}
    ;(rooms || []).forEach((item) => {
      map[item.id] = item.roomNo
    })
    return map
  }, [rooms])

  const filteredHousekeeping = useMemo(() => {
    return housekeepingTasks.filter((item) => {
      if (filterPropertyId && item.propertyId !== filterPropertyId) return false
      if (keyword.trim() && !JSON.stringify(item).toLowerCase().includes(keyword.trim().toLowerCase())) return false
      return true
    })
  }, [housekeepingTasks, filterPropertyId, keyword])

  const filteredMaintenance = useMemo(() => {
    return maintenanceTickets.filter((item) => {
      if (filterPropertyId && item.propertyId !== filterPropertyId) return false
      if (keyword.trim() && !JSON.stringify(item).toLowerCase().includes(keyword.trim().toLowerCase())) return false
      return true
    })
  }, [maintenanceTickets, filterPropertyId, keyword])

  const openStatusModal = (row: any) => {
    setCurrentRow(row)
    statusForm.setFieldsValue({
      id: row.id,
      status: view === 'housekeeping' ? row.taskStatus : row.ticketStatus,
    })
    setStatusOpen(true)
  }

  return (
    <div>
      <Typography.Title level={4} style={{ marginTop: 0 }}>
        {title}
      </Typography.Title>

      <Space style={{ marginBottom: 16 }} wrap>
        <Select
          allowClear
          style={{ width: 220 }}
          placeholder="筛选民宿"
          value={filterPropertyId}
          options={properties.map((item) => ({ label: item.propertyName, value: item.id }))}
          onChange={setFilterPropertyId}
        />
        <Input.Search
          allowClear
          style={{ width: 300 }}
          placeholder="模糊搜索（房间/负责人/问题）"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
        />
        <PermissionButton permission="ops:write" type="primary" onClick={() => setCreateOpen(true)}>
          {view === 'housekeeping' ? '新建保洁任务' : '新建维修工单'}
        </PermissionButton>
      </Space>

      {view === 'housekeeping' && (
        <Table
          rowKey="id"
          dataSource={filteredHousekeeping}
          pagination={DEFAULT_TABLE_PAGINATION}
          columns={[
            { title: '任务ID', dataIndex: 'id', width: 90 },
            { title: '民宿', dataIndex: 'propertyId', width: 160, render: (value: number) => propertyNameById[value] || `民宿${value}` },
            { title: '房间号', dataIndex: 'roomId', width: 100, render: (value: number) => roomNoById[value] || value },
            { title: '任务日期', dataIndex: 'bizDate', width: 120 },
            { title: '负责人', dataIndex: 'assignee', width: 120 },
            { title: '任务状态', dataIndex: 'taskStatus', width: 120, render: (value: string) => dictText.housekeepingStatus(value) },
            { title: '备注', dataIndex: 'remark' },
            {
              title: '操作',
              width: 120,
              render: (_, row) => (
                <PermissionButton permission="ops:write" type="link" onClick={() => openStatusModal(row)}>
                  更新状态
                </PermissionButton>
              ),
            },
          ]}
        />
      )}

      {view === 'maintenance' && (
        <Table
          rowKey="id"
          dataSource={filteredMaintenance}
          pagination={DEFAULT_TABLE_PAGINATION}
          columns={[
            { title: '工单ID', dataIndex: 'id', width: 90 },
            { title: '民宿', dataIndex: 'propertyId', width: 160, render: (value: number) => propertyNameById[value] || `民宿${value}` },
            { title: '房间号', dataIndex: 'roomId', width: 100, render: (value: number) => roomNoById[value] || value },
            { title: '问题类型', dataIndex: 'issueType', width: 140 },
            { title: '问题描述', dataIndex: 'issueDescription' },
            { title: '负责人', dataIndex: 'assignee', width: 120 },
            { title: '工单状态', dataIndex: 'ticketStatus', width: 120, render: (value: string) => dictText.maintenanceStatus(value) },
            {
              title: '操作',
              width: 120,
              render: (_, row) => (
                <PermissionButton permission="ops:write" type="link" onClick={() => openStatusModal(row)}>
                  更新状态
                </PermissionButton>
              ),
            },
          ]}
        />
      )}

      <Modal
        title={view === 'housekeeping' ? '新建保洁任务' : '新建维修工单'}
        open={createOpen}
        onOk={async () => {
          const values = await createForm.validateFields()
          if (view === 'housekeeping') {
            await createHousekeepingTask(values)
            message.success('保洁任务创建成功')
          } else {
            await createMaintenanceTicket(values)
            message.success('维修工单创建成功')
          }
          setCreateOpen(false)
          createForm.resetFields()
          loadData()
        }}
        onCancel={() => setCreateOpen(false)}
      >
        <Form form={createForm} layout="vertical">
          <Form.Item name="propertyId" label="民宿" rules={[{ required: true }]}>
            <Select options={properties.map((item) => ({ label: item.propertyName, value: item.id }))} />
          </Form.Item>
          <Form.Item name="roomId" label="房间" rules={[{ required: true }]}>
            <Select options={rooms.map((item) => ({ label: `${item.roomNo} (${item.propertyName})`, value: item.id }))} />
          </Form.Item>

          {view === 'housekeeping' && (
            <>
              <Form.Item name="bizDate" label="任务日期" rules={[{ required: true }]}>
                <Input placeholder="YYYY-MM-DD" />
              </Form.Item>
              <Form.Item name="assignee" label="负责人">
                <Input />
              </Form.Item>
              <Form.Item name="remark" label="备注">
                <Input.TextArea rows={3} />
              </Form.Item>
            </>
          )}

          {view === 'maintenance' && (
            <>
              <Form.Item name="issueType" label="问题类型" rules={[{ required: true }]}>
                <Input />
              </Form.Item>
              <Form.Item name="issueDescription" label="问题描述" rules={[{ required: true }]}>
                <Input.TextArea rows={3} />
              </Form.Item>
              <Form.Item name="assignee" label="负责人">
                <Input />
              </Form.Item>
            </>
          )}
        </Form>
      </Modal>

      <Modal
        title="更新状态"
        open={statusOpen}
        onOk={async () => {
          const values = await statusForm.validateFields()
          if (view === 'housekeeping') {
            await updateHousekeepingTaskStatus(values)
            message.success('保洁任务状态已更新')
          } else {
            await updateMaintenanceTicketStatus(values)
            message.success('维修工单状态已更新')
          }
          setStatusOpen(false)
          statusForm.resetFields()
          loadData()
        }}
        onCancel={() => setStatusOpen(false)}
      >
        <Form form={statusForm} layout="vertical">
          <Form.Item name="id" label="记录ID" rules={[{ required: true }]}>
            <InputNumber disabled style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="status" label="状态" rules={[{ required: true }]}>
            <Select
              options={
                view === 'housekeeping'
                  ? [
                      { label: '待处理', value: 'TODO' },
                      { label: '进行中', value: 'DOING' },
                      { label: '已完成', value: 'DONE' },
                    ]
                  : [
                      { label: '待处理', value: 'OPEN' },
                      { label: '处理中', value: 'IN_PROGRESS' },
                      { label: '已关闭', value: 'CLOSED' },
                    ]
              }
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
