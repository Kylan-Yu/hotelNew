import { Button, DatePicker, Form, Input, Select, Space, Table, Tabs } from 'antd'
import dayjs from 'dayjs'
import { useEffect, useState } from 'react'
import {
  fetchLogBrandOptions,
  fetchLogFilterContext,
  fetchLogGroupOptions,
  fetchLogPropertyOptions,
  searchAuditLogs,
  searchOperationLogs,
  type BrandOption,
  type GroupOption,
  type LogFilterContext,
  type PropertyOption,
} from '../api/logApi'
import { useAppSelector } from '../store/hooks'

type QueryForm = {
  groupId?: number
  brandId?: number
  propertyId?: number
  moduleCode?: string
  operator?: string
  timeRange?: [string, string]
}

export function LogPage() {
  const [operationData, setOperationData] = useState<any[]>([])
  const [auditData, setAuditData] = useState<any[]>([])
  const [opPage, setOpPage] = useState({ pageNo: 1, pageSize: 10, total: 0 })
  const [auPage, setAuPage] = useState({ pageNo: 1, pageSize: 10, total: 0 })
  const [groupOptions, setGroupOptions] = useState<GroupOption[]>([])
  const [brandOptions, setBrandOptions] = useState<BrandOption[]>([])
  const [propertyOptions, setPropertyOptions] = useState<PropertyOption[]>([])
  const [context, setContext] = useState<LogFilterContext>({})
  const [preferCurrent, setPreferCurrent] = useState(true)
  const [opForm] = Form.useForm<QueryForm>()
  const [auForm] = Form.useForm<QueryForm>()
  const currentPropertyId = useAppSelector((state) => state.auth.currentPropertyId)

  const toPayload = (values: QueryForm, pageNo: number, pageSize: number) => ({
    ...values,
    startTime: values?.timeRange?.[0] ? dayjs(values.timeRange[0]).format('YYYY-MM-DD HH:mm:ss') : undefined,
    endTime: values?.timeRange?.[1] ? dayjs(values.timeRange[1]).format('YYYY-MM-DD HH:mm:ss') : undefined,
    preferCurrent,
    pageNo,
    pageSize,
  })

  const loadOperation = async (pageNo = opPage.pageNo, pageSize = opPage.pageSize) => {
    const values = opForm.getFieldsValue()
    const data = await searchOperationLogs(toPayload(values, pageNo, pageSize))
    setOperationData(data.records || [])
    setOpPage({ pageNo, pageSize, total: data.total || 0 })
  }

  const loadAudit = async (pageNo = auPage.pageNo, pageSize = auPage.pageSize) => {
    const values = auForm.getFieldsValue()
    const data = await searchAuditLogs(toPayload(values, pageNo, pageSize))
    setAuditData(data.records || [])
    setAuPage({ pageNo, pageSize, total: data.total || 0 })
  }

  const loadGroups = async (preferCurrentValue = true) => {
    const groups = await fetchLogGroupOptions(preferCurrentValue)
    setGroupOptions(groups || [])
  }

  const loadBrands = async (groupId?: number, preferCurrentValue = true) => {
    const brands = await fetchLogBrandOptions(groupId, preferCurrentValue)
    setBrandOptions(brands || [])
  }

  const loadProperties = async (groupId?: number, brandId?: number, preferCurrentValue = true) => {
    const properties = await fetchLogPropertyOptions(groupId, brandId, preferCurrentValue)
    setPropertyOptions(properties || [])
  }

  const onGroupChange = async (groupId?: number) => {
    setPreferCurrent(false)
    opForm.setFieldValue('brandId', undefined)
    opForm.setFieldValue('propertyId', undefined)
    auForm.setFieldValue('brandId', undefined)
    auForm.setFieldValue('propertyId', undefined)
    await loadBrands(groupId, false)
    await loadProperties(groupId, undefined, false)
  }

  const onBrandChange = async (brandId?: number) => {
    setPreferCurrent(false)
    const groupId = opForm.getFieldValue('groupId') || auForm.getFieldValue('groupId')
    opForm.setFieldValue('propertyId', undefined)
    auForm.setFieldValue('propertyId', undefined)
    await loadProperties(groupId, brandId, false)
  }

  const resetFilters = async (form: any, onSearch: () => void) => {
    setPreferCurrent(true)
    form.resetFields()
    const nextContext = await fetchLogFilterContext()
    setContext(nextContext)

    if (nextContext?.currentGroupId) {
      form.setFieldValue('groupId', nextContext.currentGroupId)
    }
    if (nextContext?.currentBrandId) {
      form.setFieldValue('brandId', nextContext.currentBrandId)
    }
    if (nextContext?.currentPropertyId) {
      form.setFieldValue('propertyId', nextContext.currentPropertyId)
    }

    await loadGroups(true)
    await loadBrands(nextContext?.currentGroupId, true)
    await loadProperties(nextContext?.currentGroupId, nextContext?.currentBrandId, true)
    onSearch()
  }

  useEffect(() => {
    const init = async () => {
      setPreferCurrent(true)
      const nextContext = await fetchLogFilterContext()
      setContext(nextContext)
      await loadGroups(true)
      await loadBrands(nextContext?.currentGroupId, true)
      await loadProperties(nextContext?.currentGroupId, nextContext?.currentBrandId, true)

      if (nextContext?.currentGroupId) {
        opForm.setFieldValue('groupId', nextContext.currentGroupId)
        auForm.setFieldValue('groupId', nextContext.currentGroupId)
      }
      if (nextContext?.currentBrandId) {
        opForm.setFieldValue('brandId', nextContext.currentBrandId)
        auForm.setFieldValue('brandId', nextContext.currentBrandId)
      }
      if (nextContext?.currentPropertyId) {
        opForm.setFieldValue('propertyId', nextContext.currentPropertyId)
        auForm.setFieldValue('propertyId', nextContext.currentPropertyId)
      }

      loadOperation(1, 10)
      loadAudit(1, 10)
    }
    init()
  }, [currentPropertyId])

  const filterForm = (form: any, onSearch: () => void) => (
    <Form form={form} layout="inline" style={{ marginBottom: 16 }}>
      <Form.Item name="groupId" label="集团">
        <Select
          allowClear
          showSearch
          style={{ width: 160 }}
          placeholder="选择集团"
          options={groupOptions.map((item) => ({ value: item.id, label: item.groupName }))}
          onChange={onGroupChange}
          disabled={!context?.allowWideRangeSwitch && !!context?.currentGroupId}
        />
      </Form.Item>
      <Form.Item name="brandId" label="品牌">
        <Select
          allowClear
          showSearch
          style={{ width: 160 }}
          placeholder="选择品牌"
          options={brandOptions.map((item) => ({ value: item.id, label: item.brandName }))}
          onChange={onBrandChange}
          disabled={!context?.allowWideRangeSwitch && !!context?.currentBrandId}
        />
      </Form.Item>
      <Form.Item name="propertyId" label="门店">
        <Select
          allowClear
          showSearch
          style={{ width: 200 }}
          placeholder="选择门店"
          options={propertyOptions.map((item) => ({ value: item.id, label: item.propertyName }))}
          disabled={!context?.allowWideRangeSwitch && !!context?.currentPropertyId}
          onChange={() => setPreferCurrent(false)}
        />
      </Form.Item>
      <Form.Item name="moduleCode" label="模块">
        <Input allowClear />
      </Form.Item>
      <Form.Item name="operator" label="操作人">
        <Input allowClear />
      </Form.Item>
      <Form.Item name="timeRange" label="时间范围">
        <DatePicker.RangePicker showTime />
      </Form.Item>
      <Form.Item>
        <Space>
          <Button type="primary" onClick={onSearch}>
            查询
          </Button>
          <Button onClick={() => resetFilters(form, onSearch)}>重置</Button>
        </Space>
      </Form.Item>
    </Form>
  )

  return (
    <Tabs
      items={[
        {
          key: 'operation',
          label: '操作日志',
          children: (
            <>
              {filterForm(opForm, () => loadOperation(1, opPage.pageSize))}
              <Table
                rowKey="id"
                dataSource={operationData}
                columns={[
                  { title: 'ID', dataIndex: 'id', width: 80 },
                  { title: '集团ID', dataIndex: 'groupId', width: 90 },
                  { title: '品牌ID', dataIndex: 'brandId', width: 90 },
                  { title: '门店ID', dataIndex: 'propertyId', width: 90 },
                  { title: '模块', dataIndex: 'moduleCode', width: 120 },
                  { title: '操作', dataIndex: 'operation', width: 220 },
                  { title: '操作人', dataIndex: 'operator', width: 120 },
                  {
                    title: '结果',
                    dataIndex: 'successFlag',
                    width: 80,
                    render: (value: string) => (value === 'Y' ? '成功' : '失败'),
                  },
                  { title: '时间', dataIndex: 'createdAt' },
                ]}
                pagination={{
                  current: opPage.pageNo,
                  pageSize: opPage.pageSize,
                  total: opPage.total,
                  showSizeChanger: true,
                  pageSizeOptions: [10, 20, 50, 100],
                  showQuickJumper: true,
                  onChange: (page, size) => loadOperation(page, size),
                }}
              />
            </>
          ),
        },
        {
          key: 'audit',
          label: '审计日志',
          children: (
            <>
              {filterForm(auForm, () => loadAudit(1, auPage.pageSize))}
              <Table
                rowKey="id"
                dataSource={auditData}
                columns={[
                  { title: 'ID', dataIndex: 'id', width: 80 },
                  { title: '集团ID', dataIndex: 'groupId', width: 90 },
                  { title: '品牌ID', dataIndex: 'brandId', width: 90 },
                  { title: '门店ID', dataIndex: 'propertyId', width: 90 },
                  { title: '模块', dataIndex: 'moduleCode', width: 120 },
                  { title: '动作', dataIndex: 'actionType', width: 120 },
                  { title: '内容', dataIndex: 'content', width: 280 },
                  { title: '操作人', dataIndex: 'operator', width: 120 },
                  { title: '时间', dataIndex: 'createdAt' },
                ]}
                pagination={{
                  current: auPage.pageNo,
                  pageSize: auPage.pageSize,
                  total: auPage.total,
                  showSizeChanger: true,
                  pageSizeOptions: [10, 20, 50, 100],
                  showQuickJumper: true,
                  onChange: (page, size) => loadAudit(page, size),
                }}
              />
            </>
          ),
        },
      ]}
    />
  )
}
