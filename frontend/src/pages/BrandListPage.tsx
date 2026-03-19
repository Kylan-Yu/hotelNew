import { App, Button, Form, Input, Modal, Select, Space, Switch, Table } from 'antd'
import { useEffect, useState } from 'react'
import { BrandCreatePayload, BrandItem, createBrand, fetchBrands, updateBrand, updateBrandStatus } from '../api/brandApi'
import { GroupItem, fetchGroups } from '../api/groupApi'

export function BrandListPage() {
  const [data, setData] = useState<BrandItem[]>([])
  const [groups, setGroups] = useState<GroupItem[]>([])
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<BrandItem | null>(null)
  const [form] = Form.useForm<BrandCreatePayload>()
  const { message } = App.useApp()

  const loadData = async () => {
    const [brandList, groupList] = await Promise.all([fetchBrands(), fetchGroups()])
    setData(brandList)
    setGroups(groupList)
  }

  useEffect(() => {
    loadData()
  }, [])

  const handleAdd = () => {
    setEditing(null)
    form.resetFields()
    setOpen(true)
  }

  const handleEdit = (row: BrandItem) => {
    setEditing(row)
    form.setFieldsValue({
      groupId: row.groupId,
      brandCode: row.brandCode,
      brandName: row.brandName,
    })
    setOpen(true)
  }

  const handleSave = async () => {
    const values = await form.validateFields()
    if (editing) {
      await updateBrand(editing.id, { groupId: values.groupId, brandName: values.brandName })
      message.success('品牌更新成功')
    } else {
      await createBrand(values)
      message.success('品牌创建成功')
    }
    setOpen(false)
    form.resetFields()
    loadData()
  }

  const handleStatusChange = async (row: BrandItem, checked: boolean) => {
    await updateBrandStatus(row.id, checked ? 1 : 0)
    message.success('状态更新成功')
    loadData()
  }

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" onClick={handleAdd}>
          新增品牌
        </Button>
      </Space>
      <Table
        rowKey="id"
        dataSource={data}
        columns={[
          { title: '品牌编码', dataIndex: 'brandCode' },
          { title: '品牌名称', dataIndex: 'brandName' },
          { title: '所属集团', dataIndex: 'groupName' },
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

      <Modal
        title={editing ? '编辑品牌' : '新增品牌'}
        open={open}
        onOk={handleSave}
        onCancel={() => setOpen(false)}
        destroyOnClose
      >
        <Form layout="vertical" form={form}>
          <Form.Item name="groupId" label="所属集团" rules={[{ required: true }]}> 
            <Select
              options={groups.map((g) => ({ label: `${g.groupCode} - ${g.groupName}`, value: g.id }))}
              showSearch
              optionFilterProp="label"
            />
          </Form.Item>
          <Form.Item name="brandCode" label="品牌编码" rules={[{ required: true }]}> 
            <Input disabled={!!editing} />
          </Form.Item>
          <Form.Item name="brandName" label="品牌名称" rules={[{ required: true }]}> 
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
