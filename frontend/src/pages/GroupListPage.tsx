import { App, Button, Form, Input, Modal, Space, Switch, Table } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { createGroup, fetchGroups, GroupCreatePayload, GroupItem, updateGroup, updateGroupStatus } from '../api/groupApi'
import { DEFAULT_TABLE_PAGINATION } from '../constants/tablePagination'

export function GroupListPage() {
  const [data, setData] = useState<GroupItem[]>([])
  const [keyword, setKeyword] = useState('')
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<GroupItem | null>(null)
  const [form] = Form.useForm<GroupCreatePayload>()
  const { message } = App.useApp()

  const loadData = async () => {
    const list = await fetchGroups()
    setData(list)
  }

  useEffect(() => {
    loadData()
  }, [])

  const filteredData = useMemo(() => {
    if (!keyword.trim()) {
      return data
    }
    const text = keyword.toLowerCase()
    return data.filter((item) => {
      return (
        String(item.groupCode || '').toLowerCase().includes(text)
        || String(item.groupName || '').toLowerCase().includes(text)
      )
    })
  }, [data, keyword])

  const handleAdd = () => {
    setEditing(null)
    form.resetFields()
    setOpen(true)
  }

  const handleEdit = (row: GroupItem) => {
    setEditing(row)
    form.setFieldsValue({ groupCode: row.groupCode, groupName: row.groupName })
    setOpen(true)
  }

  const handleSave = async () => {
    const values = await form.validateFields()
    if (editing) {
      await updateGroup(editing.id, { groupName: values.groupName })
      message.success('集团更新成功')
    } else {
      await createGroup(values)
      message.success('集团创建成功')
    }
    setOpen(false)
    form.resetFields()
    loadData()
  }

  const handleStatusChange = async (row: GroupItem, checked: boolean) => {
    await updateGroupStatus(row.id, checked ? 1 : 0)
    message.success('状态更新成功')
    loadData()
  }

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" onClick={handleAdd}>
          新增集团
        </Button>
        <Input.Search
          allowClear
          placeholder="搜索集团编码 / 集团名称"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          style={{ width: 300 }}
        />
      </Space>
      <Table
        rowKey="id"
        dataSource={filteredData}
        pagination={DEFAULT_TABLE_PAGINATION}
        columns={[
          { title: '集团编码', dataIndex: 'groupCode' },
          { title: '集团名称', dataIndex: 'groupName' },
          {
            title: '状态',
            dataIndex: 'status',
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

      <Modal
        title={editing ? '编辑集团' : '新增集团'}
        open={open}
        onOk={handleSave}
        onCancel={() => setOpen(false)}
        destroyOnClose
      >
        <Form layout="vertical" form={form}>
          <Form.Item name="groupCode" label="集团编码" rules={[{ required: true }]}>
            <Input disabled={!!editing} />
          </Form.Item>
          <Form.Item name="groupName" label="集团名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
