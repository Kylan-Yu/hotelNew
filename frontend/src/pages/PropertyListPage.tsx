import { Alert, App, Button, Collapse, Form, Input, Modal, Select, Space, Switch, Table, Typography } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { BrandItem, fetchBrands } from '../api/brandApi'
import { GroupItem, fetchGroups } from '../api/groupApi'
import {
  PropertyCreatePayload,
  PropertyItem,
  createProperty,
  fetchProperties,
  updateProperty,
  updatePropertyStatus,
} from '../api/propertyApi'

export function PropertyListPage() {
  const [data, setData] = useState<PropertyItem[]>([])
  const [groups, setGroups] = useState<GroupItem[]>([])
  const [brands, setBrands] = useState<BrandItem[]>([])
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<PropertyItem | null>(null)
  const [selectedGroupId, setSelectedGroupId] = useState<number | undefined>()
  const [form] = Form.useForm<PropertyCreatePayload>()
  const navigate = useNavigate()
  const { message } = App.useApp()

  const loadData = async () => {
    const [propertyList, groupList, brandList] = await Promise.all([fetchProperties(), fetchGroups(), fetchBrands()])
    setData(propertyList || [])
    setGroups(groupList || [])
    setBrands(brandList || [])
  }

  useEffect(() => {
    loadData()
  }, [])

  const filteredBrands = useMemo(() => {
    if (!selectedGroupId) {
      return brands
    }
    return brands.filter((item) => item.groupId === selectedGroupId)
  }, [brands, selectedGroupId])

  const handleAdd = () => {
    setEditing(null)
    setSelectedGroupId(undefined)
    form.resetFields()
    form.setFieldValue('businessMode', 'HOMESTAY')
    setOpen(true)
  }

  const handleEdit = (row: PropertyItem) => {
    setEditing(row)
    setSelectedGroupId(row.groupId)
    form.setFieldsValue({
      groupId: row.groupId,
      brandId: row.brandId,
      propertyCode: row.propertyCode,
      propertyName: row.propertyName,
      businessMode: row.businessMode,
      contactPhone: row.contactPhone,
      city: row.city,
      address: row.address,
    })
    setOpen(true)
  }

  const handleSave = async () => {
    const values = await form.validateFields()
    const payload = {
      groupId: values.groupId || undefined,
      brandId: values.brandId || undefined,
      propertyCode: values.propertyCode,
      propertyName: values.propertyName,
      businessMode: values.businessMode,
      contactPhone: values.contactPhone,
      province: values.province,
      city: values.city,
      district: values.district,
      address: values.address,
    }
    if (editing) {
      await updateProperty(editing.id, payload)
      message.success('民宿更新成功')
    } else {
      await createProperty(payload)
      message.success('民宿创建成功')
    }
    setOpen(false)
    form.resetFields()
    loadData()
  }

  const handleStatusChange = async (row: PropertyItem, checked: boolean) => {
    await updatePropertyStatus(row.id, checked ? 1 : 0)
    message.success('状态更新成功')
    loadData()
  }

  return (
    <div>
      <Alert
        type="info"
        showIcon
        style={{ marginBottom: 12 }}
        message="新增民宿不再要求集团/品牌，系统按独立民宿建档；如需组织化管理，可在“高级组织信息（选填）”中补充。"
      />
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" onClick={handleAdd}>
          新增民宿
        </Button>
      </Space>
      <Table
        rowKey="id"
        dataSource={data}
        columns={[
          { title: '民宿编码', dataIndex: 'propertyCode', width: 140 },
          { title: '民宿名称', dataIndex: 'propertyName', width: 220 },
          {
            title: '组织归属',
            width: 240,
            render: (_, row) => (
              <Typography.Text>
                {row.groupName || '独立民宿'} / {row.brandName || '无品牌'}
              </Typography.Text>
            ),
          },
          {
            title: '经营模式',
            dataIndex: 'businessMode',
            render: (value: string) => (value === 'HOMESTAY' ? '民宿模式' : '酒店模式'),
            width: 120,
          },
          { title: '城市', dataIndex: 'city', width: 120 },
          { title: '联系电话', dataIndex: 'contactPhone', width: 140 },
          {
            title: '状态',
            width: 100,
            render: (_, row) => <Switch checked={row.status === 1} onChange={(checked) => handleStatusChange(row, checked)} />,
          },
          {
            title: '操作',
            width: 160,
            render: (_, row) => (
              <Space>
                <Button type="link" onClick={() => handleEdit(row)}>
                  编辑
                </Button>
                <Button type="link" onClick={() => navigate(`/assets/homestays/${row.id}`)}>
                  详情
                </Button>
              </Space>
            ),
          },
        ]}
      />

      <Modal
        title={editing ? '编辑民宿' : '新增民宿'}
        open={open}
        onOk={handleSave}
        onCancel={() => setOpen(false)}
        destroyOnClose
      >
        <Form layout="vertical" form={form} initialValues={{ businessMode: 'HOMESTAY' }}>
          <Form.Item name="propertyCode" label="民宿编码" rules={[{ required: true, message: '请输入民宿编码' }]}>
            <Input disabled={!!editing} placeholder="例如: HS_SH_001" />
          </Form.Item>
          <Form.Item name="propertyName" label="民宿名称" rules={[{ required: true, message: '请输入民宿名称' }]}>
            <Input placeholder="例如: 静栖里·外滩店" />
          </Form.Item>
          <Form.Item name="businessMode" label="经营模式" rules={[{ required: true }]}> 
            <Select options={[{ label: '民宿', value: 'HOMESTAY' }, { label: '酒店', value: 'HOTEL' }]} />
          </Form.Item>
          <Form.Item name="contactPhone" label="联系电话">
            <Input />
          </Form.Item>
          <Form.Item name="city" label="城市">
            <Input />
          </Form.Item>
          <Form.Item name="address" label="地址">
            <Input />
          </Form.Item>

          <Collapse
            ghost
            items={[
              {
                key: 'org',
                label: '高级组织信息（选填）',
                children: (
                  <>
                    <Form.Item name="groupId" label="所属集团（可选）">
                      <Select
                        allowClear
                        placeholder="不填则按独立民宿管理"
                        options={groups.map((g) => ({ label: `${g.groupCode} - ${g.groupName}`, value: g.id }))}
                        onChange={(value) => {
                          setSelectedGroupId(value)
                          if (!value) {
                            form.setFieldValue('brandId', undefined)
                          }
                        }}
                      />
                    </Form.Item>
                    <Form.Item name="brandId" label="所属品牌（可选）">
                      <Select
                        allowClear
                        placeholder="可不填"
                        options={filteredBrands.map((b) => ({ label: `${b.brandCode} - ${b.brandName}`, value: b.id }))}
                      />
                    </Form.Item>
                  </>
                ),
              },
            ]}
          />
        </Form>
      </Modal>
    </div>
  )
}
