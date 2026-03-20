import { Alert, App, Button, Form, Input, Modal, Select, Space, Switch, Table } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import {
  PropertyCreatePayload,
  PropertyItem,
  createProperty,
  fetchProperties,
  updateProperty,
  updatePropertyStatus,
} from '../api/propertyApi'
import { DEFAULT_TABLE_PAGINATION } from '../constants/tablePagination'
import { useDictOptions } from '../hooks/useDictOptions'

export function PropertyListPage() {
  const [data, setData] = useState<PropertyItem[]>([])
  const [keyword, setKeyword] = useState('')
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<PropertyItem | null>(null)
  const [form] = Form.useForm<PropertyCreatePayload>()
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { t } = useTranslation()
  const { options: businessModeOptions, labelMap: businessModeLabelMap } = useDictOptions('BUSINESS_MODE')

  const loadData = async () => {
    const propertyList = await fetchProperties()
    setData(propertyList || [])
  }

  useEffect(() => {
    loadData()
  }, [])

  const filteredData = useMemo(() => {
    if (!keyword.trim()) {
      return data
    }
    const text = keyword.trim().toLowerCase()
    return data.filter((item) => String(item.propertyCode || '').toLowerCase().includes(text)
      || String(item.propertyName || '').toLowerCase().includes(text))
  }, [data, keyword])

  const handleAdd = () => {
    setEditing(null)
    form.resetFields()
    form.setFieldValue('businessMode', businessModeOptions[0]?.value || 'HOMESTAY')
    setOpen(true)
  }

  const handleEdit = (row: PropertyItem) => {
    setEditing(row)
    form.setFieldsValue({
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
      message.success(t('property.updatedSuccess'))
    } else {
      await createProperty(payload)
      message.success(t('property.createdSuccess'))
    }
    window.dispatchEvent(new Event('hms:property-options-updated'))
    setOpen(false)
    form.resetFields()
    loadData()
  }

  const handleStatusChange = async (row: PropertyItem, checked: boolean) => {
    await updatePropertyStatus(row.id, checked ? 1 : 0)
    message.success(t('property.statusUpdatedSuccess'))
    window.dispatchEvent(new Event('hms:property-options-updated'))
    loadData()
  }

  return (
    <div>
      <Alert type="info" showIcon style={{ marginBottom: 12 }} message={t('property.infoBanner')} />
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" onClick={handleAdd}>{t('property.addButton')}</Button>
        <Input
          allowClear
          placeholder={t('property.searchPlaceholder')}
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
          { title: t('property.colCode'), dataIndex: 'propertyCode', width: 150 },
          { title: t('property.colName'), dataIndex: 'propertyName', width: 240 },
          {
            title: t('property.colBusinessMode'),
            dataIndex: 'businessMode',
            render: (value: string) => businessModeLabelMap[value] || value,
            width: 120,
          },
          { title: t('property.colCity'), dataIndex: 'city', width: 120 },
          { title: t('property.colPhone'), dataIndex: 'contactPhone', width: 140 },
          {
            title: t('property.colStatus'),
            width: 100,
            render: (_, row) => <Switch checked={row.status === 1} onChange={(checked) => handleStatusChange(row, checked)} />,
          },
          {
            title: t('property.colActions'),
            width: 160,
            render: (_, row) => (
              <Space>
                <Button type="link" onClick={() => handleEdit(row)}>{t('common.edit')}</Button>
                <Button type="link" onClick={() => navigate(`/assets/homestays/${row.id}`)}>{t('common.details')}</Button>
              </Space>
            ),
          },
        ]}
      />

      <Modal
        title={editing ? t('property.modalEditTitle') : t('property.modalAddTitle')}
        open={open}
        onOk={handleSave}
        onCancel={() => setOpen(false)}
        destroyOnClose
      >
        <Form layout="vertical" form={form} initialValues={{ businessMode: businessModeOptions[0]?.value || 'HOMESTAY' }}>
          <Form.Item name="propertyCode" label={t('property.formCode')} rules={[{ required: true, message: t('property.formCodeRequired') }]}>
            <Input disabled={!!editing} placeholder={t('property.formCodeExample')} />
          </Form.Item>
          <Form.Item name="propertyName" label={t('property.formName')} rules={[{ required: true, message: t('property.formNameRequired') }]}>
            <Input placeholder={t('property.formNameExample')} />
          </Form.Item>
          <Form.Item name="businessMode" label={t('property.formBusinessMode')} rules={[{ required: true }]}>
            <Select options={businessModeOptions} />
          </Form.Item>
          <Form.Item name="contactPhone" label={t('property.formPhone')}>
            <Input />
          </Form.Item>
          <Form.Item name="city" label={t('property.formCity')}>
            <Input />
          </Form.Item>
          <Form.Item name="address" label={t('property.formAddress')}>
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

